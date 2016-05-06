/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.ipfix;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import org.junit.Test;

public class ConvertUtilTest {

    private boolean errorWhileConversion = false;
    IpfixCollectorFactory factory = new IpfixCollectorFactory();

    @Test
    public void intToByteConversionValueMoreThan0() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.intToByte(factory.inputIntToByteConvesionMoreThan0());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void shortToByteConversionMoreThan0() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.shortToByte(factory.inputShortToByteConvesionMoreThan0());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void twoBytesToIntegerCorrectConversionLessThan2() {
        try {
            errorWhileConversion = false;
            CoverterUtilHandlerTest.twoBytesToInteger(factory.inputTwoBytesToIntegerCorrectConvesionLessThan2());
        } catch (Exception e) {
            errorWhileConversion = true;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void twoBytesToIntegerCorrectConversionMoreThan2() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.twoBytesToInteger(factory.inputTwoBytesToIntegerCorrectConvesionMoreThan2());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void intToTwoBytesCorrectConversion() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.intToTwoBytes(factory.inputIntToTwoBytesCorrectConvesion());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void longToFourBytesCorrectConversion() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.longToFourBytes(factory.inputlongToFourBytesCorrectConvesion());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void longToSixBytesCorrectConversion() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.longToSixBytes(factory.inputlongToSixBytesCorrectConvesion(10));
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void BigIntegerToEightBytesConversionLengthLessThanEight() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest
                    .BigIntegerToEightBytes(factory.inputBigIntegerToEightBytesConversionLengthLessThanEight());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void BigIntegerToEightBytesConversionLengthMoreThanEight() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest
                    .BigIntegerToEightBytes(factory.inputBigIntegerToEightBytesConversionLengthMoreThanEight());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void oneByteToIntegerCorrectConversion() {
        try {
            errorWhileConversion = true;
            byte data = 5;
            CoverterUtilHandlerTest.oneByteToInteger(data);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void oneByteToShortCorrectConversion() {
        try {
            errorWhileConversion = true;
            byte data = 5;
            CoverterUtilHandlerTest.oneByteToShort(data);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void fourBytesToLongConversionLessThan4() {
        try {
            errorWhileConversion = false;
            CoverterUtilHandlerTest.fourBytesToLong(factory.inputfourBytesToLongConversionLessThan4());
        } catch (Exception e) {
            errorWhileConversion = true;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void fourBytesToLongConversionGreaterThan4() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.fourBytesToLong(factory.inputfourBytesToLongConversionGreaterThan4());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void sixBytesToLongConversionLessThan6() {
        try {
            errorWhileConversion = false;
            CoverterUtilHandlerTest.sixBytesToLong(factory.inputsixBytesToLongConversionLessThan6());
        } catch (Exception e) {
            errorWhileConversion = true;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void sixBytesToLongConversionMoreThan6() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.sixBytesToLong(factory.inputsixBytesToLongConversionMoreThan6());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void eightBytesToBigIntegerConversionLessThan8() {
        try {
            errorWhileConversion = false;
            CoverterUtilHandlerTest.eightBytesToBigInteger(factory.inputeightBytesToBigIntegerConversionLessThan8());
        } catch (Exception e) {
            errorWhileConversion = true;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void eightBytesToBigIntegerConversionMoreThan8() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.eightBytesToBigInteger(factory.inputeightBytesToBigIntegerConversionMoreThan8());
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void dumpBytes() {
        try {
            errorWhileConversion = true;
            byte[] data = { 1, 3, 5, 6, 7, 7 };
            CoverterUtilHandlerTest.dumpBytes(data);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void prependZeroIfNeededForMacAddressForNull() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.prependZeroIfNeededForMacAddress(null);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void prependZeroIfNeededForMacAddressForZero() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.prependZeroIfNeededForMacAddress("");
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void prependZeroIfNeededForMacAddressForOne() {
        try {
            errorWhileConversion = true;
            CoverterUtilHandlerTest.prependZeroIfNeededForMacAddress("a");
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void isConfiguredForNull() {
        try {
            errorWhileConversion = true;
            Inet4Address address = null;
            CoverterUtilHandlerTest.isConfigured(address);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void isConfiguredForValue() {
        try {
            errorWhileConversion = true;
            byte[] addressBytes = new byte[] { 74, 125, (byte) 224, 19 };
            Inet4Address address = (Inet4Address) Inet4Address.getByAddress(addressBytes);
            CoverterUtilHandlerTest.isConfigured(address);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void isConfiguredforinet4AddressNull() {
        try {
            errorWhileConversion = true;
            Inet4Address address = null;
            CoverterUtilHandlerTest.isConfigured(address);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void isConfiguredforinet4AddressForValue() {
        try {
            errorWhileConversion = true;
            byte[] addressBytes = new byte[] { 74, 125, (byte) 224, 19 };
            Inet4Address address = (Inet4Address) Inet4Address.getByAddress(addressBytes);
            CoverterUtilHandlerTest.isConfigured(address);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

    @Test
    public void isConfiguredforinet6AddressNull() {
        try {
            errorWhileConversion = true;
            Inet6Address address = null;
            CoverterUtilHandlerTest.isConfigured(address);
        } catch (Exception e) {
            errorWhileConversion = false;
        }
        assertTrue(errorWhileConversion);
    }

}
