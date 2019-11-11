package com.trioangle.gofer.sidebar.payment;

/**
 * @author Trioangle Product Team
 * @version 1.5
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.payment
 * @category Model
 */


public class PromoAmountModel {

    private String promo_id;
    private String promo_code;
    private String promo_amount;
    private String promo_exp;


    public PromoAmountModel() {

    }

    public PromoAmountModel(
            String promo_amount,
            String promo_id,
            String promo_code,
            String promo_exp) {

        this.promo_amount = promo_amount;
        this.promo_id = promo_id;
        this.promo_code = promo_code;
        this.promo_exp = promo_exp;

    }

    /**
     * Getter and setter for promo code details method
     */

    public String getPromoAmount() {

        return promo_amount;
    }

    public void setPromoAmount(String promo_amount) {
        this.promo_amount = promo_amount;
    }

    public String getPromoId() {
        return promo_id;
    }

    public void setPromoId(String promo_id) {
        this.promo_id = promo_id;
    }

    public String getPromoCode() {
        return promo_code;
    }

    public void setPromoCode(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getPromoExp() {
        return promo_exp;
    }

    public void setPromoExp(String promo_exp) {
        this.promo_exp = promo_exp;
    }
}
