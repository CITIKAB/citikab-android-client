package com.trioangle.gofer.GladePay.utils;

public class StringUtilities {
    /**
     * Method to sanitize and remove non-numeric characters from card number
     * @param card_number - String of card number
     * @return card number with only numeric characters
     */
    public static String sanitizeCardNumber(String card_number) {
        return card_number.replaceAll( "[^\\d]", "" );
    }

    public static boolean isEmpty(String value) {
        if( value == null || value.length() < 1 || value.equalsIgnoreCase("null") )
            return true;
        else
            return false;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        if(charSequence == null || isEmpty(charSequence.toString()))
            return true;
        else
            return false;
    }

    /**
     * Method to nullifystring empty String.
     *
     * @param value - A string we want to set to null if it is empty
     * @return null if a value is empty or null, otherwise, returns the value
     */
    public static String nullifystring(String value) {
        if (isEmpty(value)) {
            return null;
        }
        return value;
    }
}
