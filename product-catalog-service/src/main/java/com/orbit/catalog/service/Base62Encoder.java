package com.orbit.catalog.service;

public final class Base62Encoder {

    private static final String ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE =62;
    private Base62Encoder(){}

    public static String encode(long number){ //75
        if(number == 0){
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        long n = number; //75
        while(n >0){
            sb.append(ALPHABET.charAt((int)(n % BASE))); //75%62 = 13
           // sb = C1
            n /= BASE; //  75/62 = 1
        }
        return sb.reverse().toString(); // 1C

    }

}
