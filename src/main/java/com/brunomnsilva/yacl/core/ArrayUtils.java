package com.brunomnsilva.yacl.core;

/**
 * A utility class for working with arrays.
 *
 * @author brunomnsilva
 */
public class ArrayUtils {

    /**
     * Returns the string representation of an array of doubles, where each double is displayed with a specified precision.
     * @param arr the array to convert to a string representation
     * @param precision the precision of each value
     * @return the string representation of the array
     */
    public static String toString(double[] arr, int precision) {
        // The same type of output of java's Arrays.toString, but with precision
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatDouble(arr[i], precision));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String formatDouble(double value, int precision) {
        String format = "%." + precision + "f";
        return String.format(format, value);
    }

}
