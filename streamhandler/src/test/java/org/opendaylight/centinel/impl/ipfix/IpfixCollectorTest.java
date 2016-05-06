/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.ipfix;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class IpfixCollectorTest {
    IPFIXHeaderDAO parser;
    IpfixCollectorFactory factory = new IpfixCollectorFactory();
    IpfixTemplateHandlerTest templTest = new IpfixTemplateHandlerTest();
    private boolean errorWhileParsing = false;

    @Test
    public void parseHeader() {
        try {
            errorWhileParsing = true;
            parser = IpfixHeaderHandlerTest.parse(factory.inputArrayHeaderCorrectLengthParse());
        } catch (Exception e) {
            errorWhileParsing = false;
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseHeaderForLengthLessThanSixteen() { // for ipfix header
                                                        // length <16
        try {
            errorWhileParsing = true;
            parser = IpfixHeaderHandlerTest.parse(factory.inputArrayHeaderWrongLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseTemplateSet() {
        try {
            errorWhileParsing = true;
            templTest.parse(factory.inputArrayTemplateCorrectLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseTemplateSetForLessThanFour() {
        try {
            errorWhileParsing = true;
            templTest.parse(factory.inputArrayTemplateWrongLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseOptionsTemplateSet() {
        try {
            errorWhileParsing = true;
            IpfixOptionTemplateHandlerTest.parse(factory.inputArrayOptionsTemplateCorrectLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseOptionsTemplateSetLessThanSix() {
        try {
            errorWhileParsing = true;
            IpfixOptionTemplateHandlerTest.parse(factory.inputArrayOptionsTemplateWrongLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseDataSet() {
        try {
            errorWhileParsing = true;
            IpfixDataSetHandlerTest.parse(factory.inputArrayOptionsDataSetCorrectLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

    @Test
    public void parseDataSetLessThanLength() {
        try {
            errorWhileParsing = true;
            IpfixDataSetHandlerTest.parse(factory.inputArrayOptionsDataSetWrongLengthParse());
        } catch (Exception e) {
        }
        assertTrue(errorWhileParsing);
    }

}
