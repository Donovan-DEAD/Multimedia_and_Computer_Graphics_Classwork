package com.github.donovan_dead.Utilities;

public class MathFuncs {
     public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        long eps = 1;

        while (b > eps) {
            long temp = b;
            b = a % b;  
            a = temp;
        }
        return a;
    }

    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(a * b) / gcd(a, b);
    }

}
