package com.trioangle.gofer.GladePay.apiui;


import com.trioangle.gofer.GladePay.Card;

public class CardUsageSingleton {
    private static CardUsageSingleton instance = new CardUsageSingleton();
    private Card card = null;

    private CardUsageSingleton() {
    }

    public static CardUsageSingleton getInstance() {
        return instance;
    }

    public Card getCard() {
        return card;
    }

    public CardUsageSingleton setCard(Card card) {
        this.card = card;
        return this;
    }


}