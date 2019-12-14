package com.example.myapplication3.Util;

public class Util {
private static final String cipher = "[0-9a-zA-Z]+";
private static final String price1 = "^(([1-9]\\d*)|0)(\\.\\d+)?$";

public static boolean isEmpty(String value){return value.matches(cipher);}
public static boolean isPrice_1(String value){return value.matches(price1);}

}
