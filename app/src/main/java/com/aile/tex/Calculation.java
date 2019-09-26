package com.aile.tex;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JW on 16. 5. 1..
 */
public class Calculation {
    private static final String LOG_TAG1 = "Calculation";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    SharedPreferences pref;
    String departCurrency;
    String arriveCurrency;

    float departRate;
    float arriveRate;

    Calculation(Context mContext) {
        pref = mContext.getSharedPreferences(Common.PREF_SETTINGS, 4);
        departCurrency = pref.getString(Common.DEPART_CURRENCY, "");
        arriveCurrency = pref.getString(Common.ARRIVE_CURRENCY, "");

        Logger.e(LOG_TAG1, LOG_TAG2, "departCurrency : " + departCurrency);
        Logger.e(LOG_TAG1, LOG_TAG2, "arriveCurrency : " + arriveCurrency);

        SharedPreferences prefExchange = mContext.getSharedPreferences(Common.PREF_EXCHANGE, 4);
        departRate = Float.parseFloat(prefExchange.getString(departCurrency, "0"));
        arriveRate = Float.parseFloat(prefExchange.getString(arriveCurrency, "0"));

        Logger.e(LOG_TAG1, LOG_TAG2, "departRate : " + departRate);
        Logger.e(LOG_TAG1, LOG_TAG2, "arriveRate : " + arriveRate);
    }

    // 첫 세팅때 가져오는 부분
    Calculation(Context mContext, String departCurrency, String arriveCurrency) {
        Logger.e(LOG_TAG1, LOG_TAG2, "departCurrency : " + departCurrency);
        Logger.e(LOG_TAG1, LOG_TAG2, "arriveCurrency : " + arriveCurrency);

        SharedPreferences prefExchange = mContext.getSharedPreferences(Common.PREF_EXCHANGE, 4);
        departRate = Float.parseFloat(prefExchange.getString(departCurrency, "0"));
        arriveRate = Float.parseFloat(prefExchange.getString(arriveCurrency, "0"));

        Logger.e(LOG_TAG1, LOG_TAG2, "departRate : " + departRate);
        Logger.e(LOG_TAG1, LOG_TAG2, "arriveRate : " + arriveRate);

    }

    // 도착국 -> 출발국 환산
    long Convert(long price) {
        float temp1 = price * arriveRate;
        float temp2 = 1 / departRate;

        Logger.e(LOG_TAG1, LOG_TAG2, "TEST : " + (int)(temp1 * temp2));

        return (long)(temp1 * temp2);
    }

    // 출발국 -> 도착국 환산
    public long Revert(long price) {
        float temp1 = price * departRate;
        float temp2 = 1 / arriveRate;

        Logger.e(LOG_TAG1, LOG_TAG2, "TEST : " + (int)(temp1 * temp2));

        return (long)(temp1 * temp2);
    }
}