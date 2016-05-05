/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the utility class to perform the conversions requir
 *
 */
public class ConverterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConverterUtil.class);

    /**
     * This function converts integer to one byte
     * 
     * @param value
     *            int type
     * @return value
     * @throws Exception
     *             Exception
     */
    public static final byte intToByte(int value) throws Exception {
        if ((value > Math.pow(2, 15)) || (value < 0)) {
            throw new Exception("Integer value " + value + " is larger than 2^15");
        }
        return (byte) (value & 0xFF);
    }

    /**
     * This function converts short to one byte.
     * 
     * @param value
     *            byte type
     * @return value
     * @throws Exception
     *             Exception
     */
    public static final byte shortToByte(short value) throws Exception {
        if ((value > Math.pow(2, 15)) || (value < 0)) {
            throw new Exception("Integer value " + value + " is larger than 2^15");
        }
        return (byte) (value & 0xFF);
    }

    /**
     * This function converts integer to two bytes
     * 
     * @param value
     *            int type
     * @return value int type
     * @throws Exception
     *             Exception
     */
    public static final byte[] intToTwoBytes(int value) throws Exception {
        byte[] result = new byte[2];
        if ((value > Math.pow(2, 31)) || (value < 0)) {
            throw new Exception("Integer value " + value + " is larger than 2^31");
        }
        result[0] = (byte) ((value >>> 8) & 0xFF);
        result[1] = (byte) (value & 0xFF);
        return result;
    }

    /**
     * This function converts longToFourBytes
     * 
     * @param value
     *            long value
     * @return value byte value
     * @throws Exception
     *             Exception
     */
    public static final byte[] longToFourBytes(long value) throws Exception {
        byte[] result = new byte[4];
        result[0] = (byte) ((value >>> 24) & 0xFF);
        result[1] = (byte) ((value >>> 16) & 0xFF);
        result[2] = (byte) ((value >>> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }

    /**
     * This function converts long to six bytes.
     * 
     * @param value
     *            long type
     * @return value byte type
     * @throws Exception
     *             Exception
     */
    public static final byte[] longToSixBytes(long value) throws Exception {
        byte[] result = new byte[6];
        result[0] = (byte) ((value >>> 40) & 0xFF);
        result[1] = (byte) ((value >>> 32) & 0xFF);
        result[2] = (byte) ((value >>> 24) & 0xFF);
        result[3] = (byte) ((value >>> 16) & 0xFF);
        result[4] = (byte) ((value >>> 8) & 0xFF);
        result[5] = (byte) (value & 0xFF);
        return result;
    }

    /**
     * This function converts big integer to eight bytes.
     * 
     * @param value
     *            BigInteger type
     * @return value byte type
     * @throws Exception
     *             Exception
     */
    public static final byte[] BigIntegerToEightBytes(BigInteger value) throws Exception {
        byte[] result = new byte[8];
        byte[] tmp = value.toByteArray();
        if (tmp.length > 8) {
            System.arraycopy(tmp, tmp.length - 8, result, 0, 8);
        } else {
            System.arraycopy(tmp, 0, result, 8 - tmp.length, tmp.length);
        }
        return result;
    }

    /**
     * This function converts one byte to integer.
     * 
     * @param value
     *            byte type
     * @return value int type
     * @throws Exception
     *             Exception
     */
    public static final int oneByteToInteger(byte value) throws Exception {
        return (int) value & 0xFF;
    }

    /**
     * This function converts one byte to short.
     * 
     * @param value
     *            byte
     * @return value short
     * @throws Exception
     *             Exception
     */
    public static final short oneByteToShort(byte value) throws Exception {
        return (short) (value & 0xFF);
    }

    /**
     * This function converts two bytes to integer.
     * 
     * @param value
     *            byte
     * @return value int
     * @throws Exception
     *             Exception
     */
    public static final int twoBytesToInteger(byte[] value) throws Exception {
        if (value.length < 2) {
            throw new Exception("Byte array too short!");
        }
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        return (temp0 << 8) + temp1;
    }

    /**
     * This function converts four bytes to long.
     * 
     * @param value
     *            byte
     * @return value long
     * @throws Exception
     *             Exception
     */
    public static final long fourBytesToLong(byte[] value) throws Exception {
        if (value.length < 4) {
            throw new Exception("Byte array too short!");
        }
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        return (((long) temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
    }

    /**
     * This function converts six bytes to long
     * 
     * @param value
     *            byte
     * @return value long
     * @throws Exception
     *             Exception
     */
    public static final long sixBytesToLong(byte[] value) throws Exception {
        if (value.length < 6) {
            throw new Exception("Byte array too short!");
        }
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        int temp4 = value[4] & 0xFF;
        int temp5 = value[5] & 0xFF;
        return ((((long) temp0) << 40) + (((long) temp1) << 32) + (((long) temp2) << 24) + (temp3 << 16) + (temp4 << 8)
                + temp5);
    }

    /**
     * This function converts eighth bytes to big integer.
     * 
     * @param value
     *            byte
     * @return value BigInteger
     * @throws Exception
     *             Exception
     */
    public static final BigInteger eightBytesToBigInteger(byte[] value) throws Exception {
        if (value.length < 8) {
            throw new Exception("Byte array too short!");
        }

        BigInteger bInt = new BigInteger(1, value);
        return bInt;
    }

    /**
     * @param data
     *            data
     * @return String String
     */
    public static final String dumpBytes(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (byte b : data) {
            i++;
            sb.append(String.valueOf(b));
            if (i < data.length)
                sb.append(", ");
            if ((i % 15) == 0)
                sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @param string
     *            String
     * @return string String
     */
    public static String prependZeroIfNeededForMacAddress(String string) {
        if (string == null)
            return "00";
        if (string.length() == 0)
            return "00";
        if (string.length() == 1)
            return "0" + string;
        return string;
    }

    /**
     * @param inet4Address
     *            inet4Address
     * @return boolean boolean
     */
    public static boolean isConfigured(Inet4Address inet4Address) {
        if (inet4Address == null)
            return false;
        try {
            if (inet4Address.equals(Inet4Address.getByName("0.0.0.0")))
                return false;
        } catch (UnknownHostException e) {
            LOG.debug("UnknownHostException", e);
            return false;
        }
        return true;
    }

    /**
     * @param inet6Address
     *            inet6Address
     * @return boolean boolean
     */
    public static boolean isConfigured(Inet6Address inet6Address) {
        if (inet6Address == null)
            return false;
        try {
            if (inet6Address.equals(Inet6Address.getByName("0:0:0:0:0:0:0:0")))
                return false;
        } catch (UnknownHostException e) {
            LOG.debug("unknown host exception", e);
            return false;
        }
        return true;
    }
}
