package com.trioangle.gofer.GladePay;


import android.content.Context;
import android.widget.Toast;

import com.trioangle.gofer.GladePay.utils.CardUtilities;
import com.trioangle.gofer.GladePay.utils.StringUtilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * The class for the card model. Has utility methods for validating the card.\n
 * Best initialized with the Card.CardBuilder class
 *
 * @author Light Chinaka on 8/10/18.
 */
public class Card implements Serializable {

    //declare fields in a typical card
    /**
     * List of cardTypes
     */
    private static final List<CardType> cardTypesList = Arrays.asList(
            new Visa(),
            new MasterCard(),
            new AmericanExpress(),
            new DinersClub(),
            new Jcb(),
            new Verve(),
            new Discover()
    );

    /**
     * Name on the card
     */
    private String name;
    /**
     * Card number
     */
    private String number;
    /**
     * CVC number
     */
    private String cvc;
    /**
     * Expiry month
     */
    private Integer expiryMonth;
    /**
     * Expiry year
     */
    private Integer expiryYear;

    /**
     * Country of the bank
     */
    private String addressCountry;
    private String country;

    private Integer pin;
    /**
     * Type of card
     */
    private String type;
    private String last4digits;

    /**
     * Private constructor for a Card object, using a CardBuilder;
     *
     * @param builder - a builder with which to build the card object
     */
    private Card(CardBuilder builder) {
        this.number = StringUtilities.nullifystring(builder.number);
        this.expiryMonth = builder.expiryMonth;
        this.expiryYear = builder.expiryYear;
        this.cvc = StringUtilities.nullifystring(builder.cvc);

        this.pin= builder.pin;

        this.name = StringUtilities.nullifystring(builder.name);

        this.country = StringUtilities.nullifystring(builder.country);
        this.type = getType();
        this.last4digits = builder.last4digits;
    }

    /**
     * Public constructor for a Card object using field values:
     *
     * @param number            - card number
     * @param expiryMonth       - card expiry month
     * @param expiryYear        - card expiry year
     * @param cvc               - card CVC
     * @param country           - card country
     */
    public Card(String number, Integer expiryMonth, Integer expiryYear, String cvc, String name, String country) {
        // use builder to set first 5 required fields
        CardBuilder intermediate = new CardBuilder(number, expiryMonth, expiryYear, cvc);
        this.number = intermediate.number;
        this.expiryMonth = intermediate.expiryMonth;
        this.expiryYear = intermediate.expiryYear;
        this.cvc = intermediate.cvc;
        this.last4digits = intermediate.last4digits;

        this.name = StringUtilities.nullifystring(name);

        this.country = StringUtilities.nullifystring(country);

        this.type = getType();
    }


    /**
     * Public constructor for a Card object using field values:
     *
     * @param number      - card number
     * @param expiryMonth - card expiry month
     * @param expiryYear  - card expiry year
     * @param cvc         - card CVC
     * @param name        - card name
     */
    public Card(String number, Integer expiryMonth, Integer expiryYear, String cvc, String name) {
        this(number, expiryMonth, expiryYear, cvc, name, "NGN");
    }

    /**
     * Public constructor for a Card object using field values:
     *
     * @param number      - card number
     * @param expiryMonth - card expiry month
     * @param expiryYear  - card expiry year
     * @param cvc         - card CVC
     */
    public Card(String number, Integer expiryMonth, Integer expiryYear, String cvc) {
        this(number, expiryMonth, expiryYear, cvc, "", null);
    }

    /**
     * Validates the Card object
     *
     * @return True if card is valid, false otherwise
     */
    /*public boolean isValid() {
        if (cvc == null) {
            return false;
        } else if (number == null) {
            return false;
        } else if (expiryMonth == null) {
            return false;
        } else if (expiryYear == null) {
            return false;
        } else {
            System.out.println("validNumber"+validNumber()+"validDate"+validExpiryDate()+"validCVC"+validCVC(c));
            return validNumber() && validExpiryDate() && validCVC(c);
        }
    }*/

    public boolean isValid(Context c) {
        if (cvc == null) {
            Toast.makeText(c, "please Enter valid cvc", Toast.LENGTH_SHORT).show();
            return false;
        } else if (number == null) {
            Toast.makeText(c, "please Enter valid cardnumber", Toast.LENGTH_SHORT).show();
            return false;
        } else if (expiryMonth == null) {
            Toast.makeText(c, "please Enter valid expiryMonth", Toast.LENGTH_SHORT).show();
            return false;
        } else if (expiryYear == null) {
            Toast.makeText(c, "please Enter valid expiryYear", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            System.out.println("validNumber"+validNumber(c)+"validDate"+validExpiryDate(c)+"validCVC"+validCVC(c));
            return validNumber(c) && validExpiryDate(c) && validCVC(c);
        }
    }


    /**
     * Method that validates the CVC or CVV of the card object
     *
     * @return true if the cvc is valid
     */


    public boolean validPin()
    {
        System.out.println("pin"+pin);
       if(pin==0 || pin>4)
       {
           return false;
       }
       return true;

    }
    public boolean validCVC(Context c) {
        //validate cvc
        if (StringUtilities.isEmpty(cvc)) {
           // Toast.makeText(c, "please enter valid cvc", Toast.LENGTH_SHORT).show();
            return false;
        }
        String cvcValue = cvc.trim();

        boolean validLength = ((type == null && cvcValue.length() >= 3 && cvcValue.length() <= 4) ||
                (CardType.AMERICAN_EXPRESS.equals(type) && cvcValue.length() == 4) ||
                (!CardType.AMERICAN_EXPRESS.equals(type) && cvcValue.length() == 3));

        if(!validLength &&!CardUtilities.isWholePositiveNumber(cvcValue) )
        {
            Toast.makeText(c, "please enter valid cvc", Toast.LENGTH_SHORT).show();
        }
        return !(!CardUtilities.isWholePositiveNumber(cvcValue) || !validLength);
    }

    /**
     * Method that validates the card number of the card object
     *
     * @return true if the card number is valid
     */
    public boolean validNumber(Context c) {
        if (StringUtilities.isEmpty(number))
        {
          //  Toast.makeText(c, "please enter valid card number", Toast.LENGTH_SHORT).show();
            return false;
        }


        //remove all non 0-9 characters
        String formattedNumber = number.trim().replaceAll("[^0-9]", "");

        // Verve card needs no other validation except that it matched pattern
        if (formattedNumber.matches(CardType.PATTERN_VERVE)) {
            return true;
        }

        //check if formattedNumber is empty or card isn't a whole positive number or isn't Luhn-valid
        if (StringUtilities.isEmpty(formattedNumber)
                || !CardUtilities.isWholePositiveNumber(number)
                || !isValidLuhnNumber(number)) {
            System.out.println("card invalid "+"please enter valid card number");
          //  Toast.makeText(c, "please enter valid card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        //check type for lengths
        if (CardType.AMERICAN_EXPRESS.equals(type)) {
            return formattedNumber.length() == CardType.MAX_LENGTH_AMERICAN_EXPRESS;
        } else if (CardType.DINERS_CLUB.equals(type)) {
            return formattedNumber.length() == CardType.MAX_LENGTH_DINERS_CLUB;
        } else {
            return formattedNumber.length() == CardType.MAX_LENGTH_NORMAL;
        }
    }

    /**
     * Validates the number against Luhn algorithm https://de.wikipedia.org/wiki/Luhn-Algorithmus#Java
     *
     * @param number - number to validate
     * @return true if the number is Luhn-valid
     */
    private boolean isValidLuhnNumber(String number) {
        int sum = 0;
        int length = number.trim().length();

        //iterate through from the rear
        for (int i = 0; i < length; i++) {
            char c = number.charAt(length - 1 - i);

            //check if character is a digit before parsing it
            if (!Character.isDigit(c)) {
                return false;
            }

            int digit = Integer.parseInt(c + "");

            //if it's odd, multiply by 2
            if (i % 2 == 1)
                digit *= 2;

            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }

    /**
     * Method that validates the expiry date of the card
     *
     * @return true if the expiry date is valid or hasn't been passed, false otherwise
     */
    public boolean validExpiryDate(Context c) {
        System.out.println();
        //validate month and year
        if((expiryMonth == null || expiryYear == null) && CardUtilities.isNotExpired(expiryYear, expiryMonth) && CardUtilities.isValidMonth(expiryMonth))
        {
            Toast.makeText(c, "please enter valid ExpiryDate", Toast.LENGTH_SHORT).show();
            return false;
        }
        return !(expiryMonth == null || expiryYear == null) && CardUtilities.isNotExpired(expiryYear, expiryMonth) && CardUtilities.isValidMonth(expiryMonth);
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }


    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Method that returns the type of the card
     *
     * @return a String representation of the card type detected. You can use this to improve the experience on your form,
     * to display the card type logo, while the user is entering the card number e.g MasterCard, Discover, Visa etc.
     */
    public String getType() {
        //if type is empty and the number isn't empty
        if (StringUtilities.isEmpty(type) && !StringUtilities.isEmpty(number)) {
            for (CardType cardType : cardTypesList) {
                if (cardType.matches(number)) {
                    return cardType.toString();
                }
            }
            return CardType.UNKNOWN;
        }
        return type;
    }



    public void setType(String type) {
        this.type = type;
    }

    public String getLast4digits() {
        return last4digits;
    }


    /**
     * Builder class to build a card;
     */
    public static class CardBuilder {
        private String name;
        private String number;
        private String cvc;
        private Integer expiryMonth;
        private Integer expiryYear;
        private String country;
        private String type;
        private String last4digits;
        private Integer pin;

        /**
         * Public constructor for the builder, with important fields set
         *
         * @param number      - Card number
         * @param expiryMonth - card Expiry Month
         * @param expiryYear  - card Expiry Year
         * @param cvc         - Card CVC
         */
        public CardBuilder(String number, Integer expiryMonth, Integer expiryYear, String cvc) {
            this.setNumber(number);
            this.expiryMonth = expiryMonth;
            this.expiryYear = expiryYear;
            this.cvc = cvc;
        }

        public CardBuilder setNumber(String number) {
            this.number = StringUtilities.sanitizeCardNumber(number);
            if (number.length() == 4) {
                last4digits = number;
            } else if (number.length() > 4) {
                last4digits = number.substring(number.length() - 4);
            } else {
                // whatever is appropriate in this case
                last4digits = number;
            }
            return this;
        }

     /*   public CardBuilder setPin(String pin)
        {
            this.pin=pin;
            return  this;
        }*/

        public CardBuilder setName(String name) {
            this.name = name;
            return this;
        }


        public CardBuilder setCountry(String country) {
            this.country = country;
            return this;
        }

        public CardBuilder setType(String type) {
            this.type = type;
            return this;
        }


        public Card build() {
            return new Card(this);
        }
    }

    public static abstract class CardType {
        //card types
        public static final String VISA = "Visa";
        public static final String MASTERCARD = "MasterCard";
        public static final String AMERICAN_EXPRESS = "American Express";
        public static final String DINERS_CLUB = "Diners Club";
        public static final String DISCOVER = "Discover";
        public static final String JCB = "JCB";
        public static final String VERVE = "VERVE";
        public static final String UNKNOWN = "Unknown";
        //lengths for some cards
        public static final int MAX_LENGTH_NORMAL = 16;
        public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
        public static final int MAX_LENGTH_DINERS_CLUB = 14;
        //source of these regex patterns http://stackoverflow.com/questions/72768/how-do-you-detect-credit-card-type-based-on-number
        public static final String PATTERN_VISA = "^4[0-9]{12}(?:[0-9]{3})?$";
        public static final String PATTERN_MASTERCARD = "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$";
        public static final String PATTERN_AMERICAN_EXPRESS = "^3[47][0-9]{13}$";
        public static final String PATTERN_DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";
        public static final String PATTERN_DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{12}$";
        public static final String PATTERN_JCB = "^(?:2131|1800|35[0-9]{3})[0-9]{11}$";
        public static final String PATTERN_VERVE = "^((506(0|1))|(507(8|9))|(6500))[0-9]{12,15}$";

        public abstract boolean matches(String card);

        @Override
        public abstract String toString();
    }

    /**
     * Private clsas for VISA card
     */
    private static class Visa extends CardType {
        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_VISA);
        }

        @Override
        public String toString() {
            return VISA;
        }
    }

    private static class MasterCard extends CardType {
        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_MASTERCARD);
        }

        @Override
        public String toString() {
            return MASTERCARD;
        }
    }

    private static class AmericanExpress extends CardType {
        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_AMERICAN_EXPRESS);
        }

        @Override
        public String toString() {
            return AMERICAN_EXPRESS;
        }
    }

    private static class DinersClub extends CardType {
        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_DINERS_CLUB);
        }

        @Override
        public String toString() {
            return DINERS_CLUB;
        }
    }

    private static class Discover extends CardType {

        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_DISCOVER);
        }

        @Override
        public String toString() {
            return DISCOVER;
        }
    }

    private static class Jcb extends CardType {

        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_JCB);
        }

        @Override
        public String toString() {
            return JCB;
        }
    }

    private static class Verve extends CardType {

        @Override
        public boolean matches(String card) {
            return card.matches(PATTERN_VERVE);
        }

        @Override
        public String toString() {
            return VERVE;
        }
    }
}