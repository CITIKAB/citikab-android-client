package com.trioangle.gofer.GladePay.request;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.trioangle.gofer.GladePay.Charge;
import com.trioangle.gofer.GladePay.exceptions.ChargeException;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;

import java.util.HashMap;

import javax.inject.Inject;


/**
 * Charge Request Body
 */
public class CardChargeRequestBody extends BaseRequestBody {

    private static final String FIELD_CLIENT_FIRSTNAME = "firstname";
    private static final String FIELD_CLIENT_LASTNAME = "lastname";
    private static final String FIELD_LAST4 = "last4";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_AMOUNT = "amount";
    private static final String FIELD_REFERENCE = "reference";
    private static final String FIELD_METADATA = "metadata";
    private static final String FIELD_CURRENCY = "currency";
    private static final String FIELD_CARD_NO = "card_no";

    @SerializedName(FIELD_LAST4)
    private  String last4;

    @SerializedName(FIELD_CLIENT_FIRSTNAME)
    private  String firstname;

    @SerializedName(FIELD_CLIENT_LASTNAME)
    private  String lastname;

    @SerializedName(FIELD_AMOUNT)
    private  String amount;

    @SerializedName(FIELD_REFERENCE)
    private  String reference;


    private  String expiry_year;

    private  String expiry_month;
    private  String cvc;
    private String pin;

    @SerializedName(FIELD_METADATA)
    private String metadata;

    @SerializedName(FIELD_CURRENCY)
    private String currency;

    @SerializedName(FIELD_EMAIL)
    private String email;


    @SerializedName(FIELD_CARD_NO)
    private String card_no;

    public @Inject
    SessionManager sessionManager;

    private HashMap<String, String> additionalParameters;

    public CardChargeRequestBody() {
        AppController.getAppComponent().inject(this);
    }

    public CardChargeRequestBody(Charge charge) {
        AppController.getAppComponent().inject(this);
        if(charge == null) {
            throw new ChargeException("Please Provide A Valid And NonNull Charge");
        }
        this.setDeviceId();
        this.last4 = charge.getCard().getLast4digits();
        this.email = charge.getEmail();
        this.amount = Double.toString(charge.getAmount());
        this.reference = charge.getReference();
        this.metadata = charge.getMetadata();
        this.additionalParameters = charge.getAdditionalParameters();
        String tempName = charge.getCard().getName() == null? "" : charge.getCard().getName();
        String[] names_part = tempName.split(" ");
        if (names_part.length > 1) {
            this.firstname = names_part[0];
            this.lastname = names_part[1];
        } else {
            this.firstname = names_part[0];
            this.lastname = "";
        }
        System.out.println("FirstName"+this.firstname);
        this.card_no = charge.getCard().getNumber();
        this.expiry_month = Integer.toString(charge.getCard().getExpiryMonth());
        this.expiry_year = Integer.toString(charge.getCard().getExpiryYear());
        this.cvc = charge.getCard().getCvc();
      //  this.pin= String.valueOf(charge.getCard().getPin());
       // addPin(charge.getCard().getPin())

    }

    public CardChargeRequestBody addPin(String pin) {
//        this.handle = Crypto.encrypt(pin);
        this.pin = pin;
        return this;
    }


    /**
     *
     * @return
     */
    @Override
    public JsonObject getInitiateParamsJsonObjects(){

        Gson gson2 = new Gson();
        System.out.println("getLanguageCode"+sessionManager.getLanguageCode());

        JsonObject step1_initiate_jsonob = new JsonObject();
        step1_initiate_jsonob.addProperty("action", "initiate");
        step1_initiate_jsonob.addProperty("paymentType", "card");
        step1_initiate_jsonob.addProperty("is_recurring", true);
        System.out.println("Firstname"+sessionManager.getFirstName());
//        step1_initiate_jsonob.addProperty("amount", 10000);

      /*  step1_initiate_jsonob.addProperty("country", "NG");
        step1_initiate_jsonob.addProperty("currency", sessionManager.getCurrencyCode());
*/
        JsonObject userJson = new JsonObject();
        userJson.addProperty("firstname", this.firstname);
        userJson.addProperty("lastname", this.lastname);

      //  userJson.addProperty("email", this.email);
      /*  userJson.addProperty("ip", "192.168.33.10");
        userJson.addProperty("fingerprint", "cccvxbxbxb");*/

        if(amount != null) {
            step1_initiate_jsonob.addProperty("amount", this.amount);
        }else{
            throw new ChargeException("Charge Card Amount Required");
        }

//        userJson.addProperty("firstname", this.firstname);
//        userJson.addProperty("lastname", this.lastname);


        if(email != null) {
            userJson.addProperty("email", this.email);
        }else{
            throw new ChargeException("Email Has To Be Provided");
        }
        JsonObject cardJson = new JsonObject();

//        cardJson.addProperty("card_no", "5438898014560229");
//        cardJson.addProperty("expiry_month", "09");
//        cardJson.addProperty("expiry_year", "19");
//        cardJson.addProperty("ccv", "798");
//        cardJson.addProperty("pin", "3310");

        cardJson.addProperty("card_no", this.card_no);
        cardJson.addProperty("expiry_month", this.expiry_month);
        cardJson.addProperty("expiry_year", this.expiry_year);
        cardJson.addProperty("ccv", this.cvc);
     //   System.out.println("Pin :"+);
      //  cardJson.addProperty("pin", this.pin);
        step1_initiate_jsonob.add("user", userJson);
        step1_initiate_jsonob.add("card", cardJson);
        return step1_initiate_jsonob;
    }




    public JsonObject getParamsJsonObjects(String txnRef,String pin)
    {
        JsonObject step1_initiate_jsonob = new JsonObject();
        step1_initiate_jsonob.addProperty("action", "charge");
        step1_initiate_jsonob.addProperty("paymentType", "card");
        step1_initiate_jsonob.addProperty("txnRef", txnRef);
        step1_initiate_jsonob.addProperty("auth_type", "PIN");
        step1_initiate_jsonob.addProperty("is_recurring", true);
        JsonObject userJson = new JsonObject();
        userJson.addProperty("firstname", sessionManager.getFirstName());
        userJson.addProperty("lastname",sessionManager.getLastName());
        userJson.addProperty("email", this.email);

        JsonObject cardJson = new JsonObject();
        cardJson.addProperty("card_no", this.card_no);
        cardJson.addProperty("expiry_month", this.expiry_month);
        cardJson.addProperty("expiry_year", this.expiry_year);
        cardJson.addProperty("ccv", this.cvc);
           System.out.println("Pin :"+this.pin);

      /*  userJson.addProperty("ip", "192.168.33.10");
        userJson.addProperty("fingerprint", "cccvxbxbxb");*/
        if(amount != null) {
            step1_initiate_jsonob.addProperty("amount", this.amount);
        }else{
            throw new ChargeException("Charge Card Amount Required");
        }
        if(this.pin !=null)
        {
            cardJson.addProperty("pin", this.pin);
        }else{
            throw new ChargeException("PIN Required");
        }
        step1_initiate_jsonob.add("user", userJson);
        step1_initiate_jsonob.add("card", cardJson);
        return step1_initiate_jsonob;
    }
    public JsonObject getParamsJsonObjects(String token)
    {
        System.out.println("token"+token);
        JsonObject step1_initiate_jsonob = new JsonObject();
        step1_initiate_jsonob.addProperty("action", "charge");
        step1_initiate_jsonob.addProperty("paymentType", "token");
        step1_initiate_jsonob.addProperty("token", token);
        step1_initiate_jsonob.addProperty("is_recurring", true);
        JsonObject userJson = new JsonObject();
        userJson.addProperty("firstname", sessionManager.getFirstName());
        userJson.addProperty("lastname",sessionManager.getLastName());

        userJson.addProperty("email", this.email);
      /*  userJson.addProperty("ip", "192.168.33.10");
        userJson.addProperty("fingerprint", "cccvxbxbxb");*/
        if(amount != null) {
            step1_initiate_jsonob.addProperty("amount", this.amount);
        }else{
            throw new ChargeException("Charge Card Amount Required");
        }
        step1_initiate_jsonob.add("user", userJson);
        return step1_initiate_jsonob;
    }
    /**
     *
     * @param action
     * @param txnRef
     * @param otp
     * @return
     */
    public JsonObject getParamsJsonObjects(String action, String txnRef, String otp){

        if(action.equalsIgnoreCase("validate")){
            JsonObject validate_jsonob = new JsonObject();

            validate_jsonob.addProperty("action", "validate");
            validate_jsonob.addProperty("txnRef", txnRef);
            validate_jsonob.addProperty("otp",otp);

            return validate_jsonob;

        }else if(action.equalsIgnoreCase("verify")){
            JsonObject verify_jsonob = new JsonObject();

            verify_jsonob.addProperty("action", "verify");
            verify_jsonob.addProperty("txnRef", txnRef);

            return verify_jsonob;
        }else{
            return null;
        }
    }

   /* Card Payments need 2 authentications before the transaction is  completed. if your response is Respone
    {"status":202,"txnRef":"GP3127375220191017U","apply_auth":"PIN"}. to proceed you need*/


   /* Charge Action request payload

    At this point, you will pass the pin that was entered by the customer, and txnRef from the initiate action.
    {
        "action":"charge",
            "paymentType":"card",
            "user": {
        "firstname":"John",
                "lastname":"Doe",
                "email":"johndoe@test.com",
                "ip":"192.168.33.10",
                "fingerprint": "cccvxbxbxb"
    },
        "card":{
        "card_no":"5399830000000008",
                "expiry_month":"05",
                "expiry_year":"30",
                "ccv":"100",
                "pin":"3310"
    },
        "amount":"50",
            "country": "NG",
            "currency": "NGN",
            "txnRef":"GP98885125720191017V",
            "auth_type":"PIN"
    }
*/
}
