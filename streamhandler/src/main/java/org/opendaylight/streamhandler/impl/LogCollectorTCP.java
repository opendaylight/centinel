/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Sumit kapoor
 * This class collects syslog messages over TCP
 */

public class LogCollectorTCP {
    private static final Logger log = LoggerFactory.getLogger(LogCollectorTCP.class);
    CommonServices commonServices = CommonServices.getInstance();

    final List<ClientHandler> clientHandler = Collections.synchronizedList(new ArrayList<ClientHandler>());

    private final int keepAliveTime = (int) TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS);
    private final int corePoolSize = 70;
    private final int maxPoolSize = 200;
    private final int blockinQueueSize = 200;
    private final KafkaProducer<Long, String> producer;

    private Properties props = null;

    BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(blockinQueueSize);
    StreamThreadPoolExecutor executor = new StreamThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
            TimeUnit.MILLISECONDS, blockingQueue);

    volatile boolean socketOpen = false;
    public int syslogPort;

    public LogCollectorTCP() {
        props = new Properties();
        props.put("bootstrap.servers", KafkaProperties.zkConnect);
        props.put("client.id", "StreamProducer");
        props.put("key.serializer", LongSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);

        this.syslogPort = Integer.parseInt(commonServices.syslogPort);
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable kafkaStreamProducer, ThreadPoolExecutor executor) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage(), e);
                }
                executor.execute(kafkaStreamProducer);
            }
        });
    }

    SocketReader socketReader;
    ServerSocket serverSocket = null;
    Object serverSocketLock = new Object();

    class SocketReader extends Thread {
        @Override
        public void run() {
            while (socketOpen) {
                ServerSocket socket = null;
                synchronized (serverSocketLock) {
                    socket = serverSocket;
                }

                if (socket == null || socket.isClosed()) {
                    return;
                }

                try {
                    Socket conn = socket.accept();
                    new ClientHandler(conn).start();
                } catch (Exception e) {
                    log.error("Exception while connecting " + e.getMessage(), e);
                }
            }
        }
    }

    class ClientHandler extends Thread {
        private Socket conn = null;

        ClientHandler(Socket conn) {
            clientHandler.add(this);
            this.conn = conn;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while (((line = br.readLine()) != null) && (line.length() != 0) && socketOpen) {
                    executor.execute(
                            new KafkaStreamProducer(KafkaProperties.syslogTopic, false, line, "syslog", producer));
                }
            } catch (IOException e) {
                log.error("IOException on socket from run " + e.getMessage(), e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (IOException e) {
                        log.error("IO Exception while closing connection from un-secured Logcollector " + ":: "
                                + e.getMessage());
                    }
                }
            }
            clientHandler.remove(this);
        }
    }

    /**
     * This starts collection of logs by creating server sockets and starting
     * client threads.
     */
    public void start() {
        executor.prestartAllCoreThreads();
        synchronized (serverSocketLock) {
            if (!socketOpen) {
                socketOpen = true;
            }
            if (serverSocket == null) {
                try {
                    serverSocket = new ServerSocket(syslogPort);
                    serverSocket.setReuseAddress(true);
                } catch (IOException e) {
                    log.error("Exception while connecting " + e.getMessage(), e);
                }
            }
        }
        socketReader = new SocketReader();
        socketReader.start();
    }

    /**
     * This stops collection of logs by closing server sockets and client
     * threads.
     */
    public void stop() {
        executor.shutdown();
        synchronized (serverSocketLock) {
            if (socketOpen) {
                socketOpen = false;
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    serverSocket = null;
                } catch (IOException e) {
                    log.error("Exception while closing socket " + e.getMessage(), e);
                }
            }
        }
        if (clientHandler.size() != 0) {
            List<ClientHandler> clients = new ArrayList<ClientHandler>(clientHandler);
            for (ClientHandler client : clients) {
                try {
                    client.join();
                } catch (InterruptedException e) {
                    log.error("Intruppting clientHandler threads", e);
                }
            }
        }
        try {
            if (socketReader != null) {
                socketReader.join();
                socketReader = null;
            }
        } catch (InterruptedException e) {
            log.error("Intruppting socketReader threads", e);
        }
    }
}
