package com.github.donovan_dead.Utils;

public class DoubleParser {
    public static void ParseDouble(String s, double min, double max, double[] values, int index, double null_val){
        try {
            values[index] = Double.parseDouble(s);

            if(values[index] < min || values[index] > max) throw new Exception("Value parsed is out of bound.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            values[index] = null_val;
        }
    }

    public static void ParseDouble(String s, double[] values, int index, double null_val){
      try {
            values[index] = Double.parseDouble(s);
        } catch (Exception e) {
            System.out.println("Error parsing the string: - " + s + " -  To double.");
            values[index] = null_val;
        }
    }
}
