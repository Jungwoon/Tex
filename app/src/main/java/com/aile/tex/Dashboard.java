package com.aile.tex;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_MULTI_PROCESS;

public class Dashboard extends Fragment {
    private static final String LOG_TAG1 = "Dashboard";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    View fragmentDashboardView;
    TextView txtArriveCurrency;
    TextView txtArriveValue;

    TextView txtDepartCurrency;
    TextView txtDepartValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.e(LOG_TAG1, LOG_TAG2, "Dashboard onCreateView()");
        fragmentDashboardView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtArriveCurrency = fragmentDashboardView.findViewById(R.id.dashboardArriveCurrency);
        txtArriveValue = fragmentDashboardView.findViewById(R.id.dashboardArriveValue);

        txtDepartCurrency = fragmentDashboardView.findViewById(R.id.dashboardDepartCurrency);
        txtDepartValue = fragmentDashboardView.findViewById(R.id.dashboardDepartValue);

        SharedPreferences prefSetting = getContext().getSharedPreferences(Common.PREF_SETTINGS, MODE_MULTI_PROCESS);

        // 값이 세팅이 안되어 있으면 그냥 USD로 세팅
        String arriveCurrency = prefSetting.getString(Common.ARRIVE_CURRENCY, "USD");
        String arriveLeftExpense = prefSetting.getString(Common.LEFT_ARRIVE_EXPENSE, "0");

        // 값이 세팅이 안되어 있으면 그냥 KRW로 세팅
        String departCurrency = prefSetting.getString(Common.DEPART_CURRENCY, "KRW");
        String departLeftExpense = prefSetting.getString(Common.LEFT_DEPART_EXPENSE, "0");

        txtArriveCurrency.setText(arriveCurrency);
        txtArriveValue.setText(arriveLeftExpense);

        txtDepartCurrency.setText(departCurrency);
        txtDepartValue.setText(departLeftExpense);

        return fragmentDashboardView;
    }
}