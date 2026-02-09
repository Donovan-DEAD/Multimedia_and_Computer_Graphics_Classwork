package com.github.donovan_dead.Utils;

/**
 * Utility class for parsing String values into double-precision floating-point numbers.
 * Provides overloaded methods to handle parsing with or without specified bounds,
 * and a default value for invalid inputs.
 */
public class DoubleParser {

    /**
     * Parses a String into a double and stores it in a provided array at a specific index.
     * It validates if the parsed value falls within the specified minimum and maximum bounds.
     * If parsing fails or the value is out of bounds, a default null value is used.
     *
     * @param s The String to parse.
     * @param min The minimum allowed value (inclusive).
     * @param max The maximum allowed value (inclusive).
     * @param values The array where the parsed double will be stored.
     * @param index The index in the 'values' array to store the result.
     * @param null_val The default value to use if parsing fails or the value is out of bounds.
     */
    public static void ParseDouble(String s, double min, double max, double[] values, int index, double null_val){
        try {
            values[index] = Double.parseDouble(s);

            if(values[index] < min || values[index] > max) throw new Exception("Value parsed is out of bound.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            values[index] = null_val;
        }
    }


    /**
     * Parses a String into a double and stores it in a provided array at a specific index.
     * If parsing fails, a default null value is used. This overload does not perform bounds checking.
     *
     * @param s The String to parse.
     * @param values The array where the parsed double will be stored.
     * @param index The index in the 'values' array to store the result.
     * @param null_val The default value to use if parsing fails.
     */
    public static void ParseDouble(String s, double[] values, int index, double null_val){
      try {
            values[index] = Double.parseDouble(s);
        } catch (Exception e) {
            System.out.println("Error parsing the string: - " + s + " -  To double.");
            values[index] = null_val;
        }
    }
}
