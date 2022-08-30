package com.vinorsoft.microservices.core.notarization.util.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

public class DataHelper {

    public enum SortSqlQuery {
        ASC,
        DESC
    }

    /**
     * Return a ramdom big interger
     * 
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    public static BigInteger RandomBigInteger(BigInteger rangeStart, BigInteger rangeEnd) {

        Random rand = new Random();
        int scale = rangeEnd.toString().length();
        String generated = "";
        for (int i = 0; i < rangeEnd.toString().length(); i++) {
            generated += rand.nextInt(10);
        }
        BigDecimal inputRangeStart = new BigDecimal("0").setScale(scale, RoundingMode.FLOOR);
        BigDecimal inputRangeEnd = new BigDecimal(
                String.format("%0" + (rangeEnd.toString().length()) + "d", 0).replace('0', '9'))
                .setScale(scale, RoundingMode.FLOOR);
        BigDecimal outputRangeStart = new BigDecimal(rangeStart).setScale(scale, RoundingMode.FLOOR);
        BigDecimal outputRangeEnd = new BigDecimal(rangeEnd).add(new BigDecimal("1")).setScale(scale,
                RoundingMode.FLOOR); // Adds one to the output range to correct rounding

        // Calculates: (generated - inputRangeStart) / (inputRangeEnd - inputRangeStart)
        // * (outputRangeEnd - outputRangeStart) + outputRangeStart
        BigDecimal bd1 = new BigDecimal(new BigInteger(generated)).setScale(scale, RoundingMode.FLOOR)
                .subtract(inputRangeStart);
        BigDecimal bd2 = inputRangeEnd.subtract(inputRangeStart);
        BigDecimal bd3 = bd1.divide(bd2, RoundingMode.FLOOR);
        BigDecimal bd4 = outputRangeEnd.subtract(outputRangeStart);
        BigDecimal bd5 = bd3.multiply(bd4);
        BigDecimal bd6 = bd5.add(outputRangeStart);

        BigInteger returnInteger = bd6.setScale(0, RoundingMode.FLOOR).toBigInteger();
        returnInteger = (returnInteger.compareTo(rangeEnd) > 0 ? rangeEnd : returnInteger); // Converts number to the
                                                                                            // end of output range if
                                                                                            // it's over it. This is to
                                                                                            // correct rounding.
        return returnInteger;
    }

    /**
     * Return a random big deciamal
     * 
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    public static BigDecimal RandomBigDecimal(BigDecimal rangeStart, BigDecimal rangeEnd) {
        BigDecimal randomBigDecimal = rangeStart
                .add(new BigDecimal(Math.random()).multiply(rangeEnd.subtract(rangeStart)));
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Convert a byte array to Byte array
     * 
     * @param bytesPrim
     * @return
     */
    public static Byte[] ToByteArray(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        Arrays.setAll(bytes, n -> bytesPrim[n]);
        return bytes;
    }

    /**
     * Serialize an object to byte array
     * 
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] SerializeObject(Object obj) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
        return bytes;
    }

    /**
     * Deserialize byte array to object
     * 
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object DeserializeBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bytesIn);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    /**
     * Convert string on Camelcase style to Uppercase style, use for query string to
     * database by convention
     * 
     * @param source
     * @return
     */
    public static String ConvertCamelCaseToUpperCase(String source) {
        StringBuilder builder = new StringBuilder();
        for (Character character : source.toCharArray()) {
            if (!builder.isEmpty() && Character.isUpperCase(character)) {
                builder.append("_");
            }
            builder.append(Character.toUpperCase(character));
        }
        return builder.toString();
    }

    /**
     * Print stacktrace of exception to logging
     * 
     * @param ex
     * @return
     */
    public static String GetStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}