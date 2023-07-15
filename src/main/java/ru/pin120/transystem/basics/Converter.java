package ru.pin120.transystem.basics;

import java.math.BigDecimal;

public class Converter {
    public static Integer tryValueOfInteger(String value){
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e){
            return null;
        }
    }

    public static int tryParseInt(String value){
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e){
            return -1;
        }
    }

    public static BigDecimal tryConvertToBigDecimal(String value){
        try{
            return new BigDecimal(value);
        }catch (NumberFormatException e){
            return null;
        }
    }
}
