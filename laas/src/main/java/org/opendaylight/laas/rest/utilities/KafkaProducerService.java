/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.rest.utilities;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Sumit kapoor
 * This class publishes stream messages to given topic
 */

public class KafkaProducerService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaProducer<Long, String> producer;
    private final String topic;
    private final Boolean isAsync;
    private final Long key;
    private String message = null;

    public KafkaProducerService(String topic, Boolean isAsync, Long key, String message) {
        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaProperties.zkConnect);
        props.put("client.id", "StreamProducer");
        props.put("key.serializer", LongSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
        this.topic = topic;
        this.isAsync = isAsync;
        this.key = key;
        this.message = message;
    }

    public KafkaProducerService(String topic, Boolean isAsync, String message, String type,
            KafkaProducer<Long, String> producer) {

        synchronized (producer) {
            this.producer = producer;
        }

        this.message = message;
        this.topic = topic;
        this.isAsync = isAsync;
        this.key = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord<>(topic, key, message), new StreamCallBack(startTime, key, message));
        } else { // Send synchronously
            try {
                producer.send(new ProducerRecord<>(topic, key, message)).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}

/*
 * @author Sumit kapoor This class will provide access point to the common
 * services for handling KAFKA cluster.
 */
class StreamCallBack implements Callback {
    private static final Logger log = LoggerFactory.getLogger(StreamCallBack.class);
    private final long startTime;
    private final Long key;
    private final String message;

    public StreamCallBack(long startTime, Long key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling
     * of request completion. This method will be called when the record sent to
     * the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata
     *            The metadata for the record that was sent (i.e. the partition
     *            and offset). Null if an error occurred.
     * @param exception
     *            The exception thrown during processing of this record. Null if
     *            no error occurred.
     */
    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            log.error(exception.getLocalizedMessage(), exception);

        }
    }
}