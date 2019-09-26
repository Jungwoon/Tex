package com.aile.tex;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG1 = "MainActivity";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    // Drawer Head
    static TextView txtDepartInfo;
    static TextView txtArriveInfo;

    // Drawer Summary
    static TextView txtTotalArriveCurrency;
    static TextView txtTotalArriveValue;
    static TextView txtTotalDepartCurrency;
    static TextView txtTotalDepartValue;

    static TextView txtSpendArriveCurrency;
    static TextView txtSpendArriveValue;
    static TextView txtSpendDepartCurrency;
    static TextView txtSpendDepartValue;

    static TextView txtLeftArriveCurrency;
    static TextView txtLeftArriveValue;
    static TextView txtLeftDepartCurrency;
    static TextView txtLeftDepartValue;

    // Drawer Currency
    static TextView txtCurrencyArriveCurrency;
    static TextView txtCurrencyArriveValue;

    static TextView txtCurrencyDepartCurrency;
    static TextView txtCurrencyDepartValue;

    static TextView txtUpdateDate; // Drawer Update Time

    public ViewPager viewPager;
    public TabLayout tabLayout;
    public static PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_dashboard)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_history)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Drawer Head
        txtDepartInfo = findViewById(R.id.departInfo);
        txtArriveInfo = findViewById(R.id.arriveInfo);

        // Drawer Summary
        txtTotalArriveCurrency = findViewById(R.id.totalArriveCurrency);
        txtTotalArriveValue = findViewById(R.id.totalArriveValue);
        txtTotalDepartCurrency = findViewById(R.id.totalDepartCurrency);
        txtTotalDepartValue = findViewById(R.id.totalDepartValue);

        txtSpendArriveCurrency = findViewById(R.id.spendArriveCurrency);
        txtSpendArriveValue = findViewById(R.id.spendArriveValue);
        txtSpendDepartCurrency = findViewById(R.id.spendDepartCurrency);
        txtSpendDepartValue = findViewById(R.id.spendDepartValue);

        txtLeftArriveCurrency = findViewById(R.id.leftArriveCurrency);
        txtLeftArriveValue = findViewById(R.id.leftArriveValue);
        txtLeftDepartCurrency = findViewById(R.id.leftDepartCurrency);
        txtLeftDepartValue = findViewById(R.id.leftDepartValue);

        // Drawer Currency
        txtCurrencyArriveCurrency = findViewById(R.id.currencyArriveCurrency);
        txtCurrencyArriveValue = findViewById(R.id.currencyArriveValue);

        txtCurrencyDepartCurrency = findViewById(R.id.currencyDepartCurrency);
        txtCurrencyDepartValue = findViewById(R.id.currencyDepartValue);

        txtUpdateDate = findViewById(R.id.updateDate);

        viewPager = findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        final FloatingActionButton fabInput = findViewById(R.id.action_input);
        fabInput.setIcon(R.drawable.ic_add);
        fabInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputDialog inputDialog = new InputDialog(MainActivity.this);
                inputDialog.show();

                // 해당 다이얼로그가 Dismiss() 상태가 되는 시점을 확인해 notifyDataSetChanged()를 호출
                // Fragment를 다시 갱신한다
                inputDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onResume();
                        adapterRefresh();
                    }
                });
            }
        });

        final FloatingActionButton fabSettings = findViewById(R.id.action_settings);
        fabSettings.setIcon(R.drawable.ic_settings);
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingDialog settingDialog = new SettingDialog(MainActivity.this);
                settingDialog.show();

                // 해당 다이얼로그가 Dismiss() 상태가 되는 시점을 확인해 notifyDataSetChanged()를 호출
                // Fragment를 다시 갱신한다
                settingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onResume();
                        adapterRefresh();
                    }
                });
            }
        });

        final FloatingActionButton fabRefresh = findViewById(R.id.action_refresh);
        fabRefresh.setIcon(R.drawable.ic_refresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkNetworkStatus()) {
                    // 환율 업데이트 해주는 부분
                    UpdateExchangeRate task = new UpdateExchangeRate();
                    task.execute();
                } else {
                    Toast.makeText(MainActivity.this, getText(R.string.toast_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.e(LOG_TAG1, LOG_TAG2, "onResume()");

        // 만약 설치 후 처음으로 열었으면
        if (!getSharedPreferences(Common.PREF_SETTINGS, MODE_MULTI_PROCESS).getBoolean(Common.APP_INIT, false)) {
            // 기본 환율정보 업데이트부터 하고
            initExchangeRate();

            // 세팅 다이얼로그를 띄워주고
            SettingDialog settingDialog = new SettingDialog(MainActivity.this);
            settingDialog.show();

            settingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                    adapterRefresh();
                }
            });
        } else {
            bypassOnResume(getBaseContext());
        }
    }

    // Fragment를 다시 그리기 위한 부분
    public static void adapterRefresh() {
        Logger.e(LOG_TAG1, LOG_TAG2, "adapterRefresh()");
        adapter.notifyDataSetChanged();
    }

    // 다른 클래스로부터 바로 onResume를 호출할 수 없기 때문에 우회하기 위해서 만든 부분
    public static void bypassOnResume(Context context) {
        Calculation mCalculation = new Calculation(context);

        SharedPreferences prefUpdate = context.getSharedPreferences(Common.PREF_UPDATE, MODE_MULTI_PROCESS);
        SharedPreferences prefSetting = context.getSharedPreferences(Common.PREF_SETTINGS, MODE_MULTI_PROCESS);

        // 데이터베이스로부터 사용한 금액 가져오기
        try {
            DBHelper mHelper = new DBHelper(context);
            SQLiteDatabase db = mHelper.getReadableDatabase();

            String query = String.format("SELECT arrive_price FROM %s;", Common.EXPENSE_TABLE);

            // 쿼리를 실행하고 거기에 대한 결과를 cursor에 넣음
            Cursor cursor = db.rawQuery(query, null);

            int totalExpense = 0;

            while (cursor.moveToNext()) {
                totalExpense += Integer.parseInt(cursor.getString(0).replaceAll(",", ""));
            }

            prefSetting.edit().putInt(Common.SPEND_EXPENSE, totalExpense).apply();

            cursor.close();
            db.close();
            mHelper.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String departCountry = prefSetting.getString(Common.DEPART_COUNTRY, "South Korea");
        String departCurrency = prefSetting.getString(Common.DEPART_CURRENCY, "KRW");

        String arriveCountry = prefSetting.getString(Common.ARRIVE_COUNTRY, "America");
        String arriveCurrency = prefSetting.getString(Common.ARRIVE_CURRENCY, "USD");

        String updateDate = prefUpdate.getString(Common.UPDATE_TIME, "default");

        int totalExpense = prefSetting.getInt(Common.TOTAL_EXPENSE, 0);
        int spendExpense = prefSetting.getInt(Common.SPEND_EXPENSE, 0);

        /**
         * 여기서부터는 Drawer의 값들 갱신해주는 부분
         */

        // Drawer Head
        txtDepartInfo.setText(departCountry + "/" + departCurrency);
        txtArriveInfo.setText(arriveCountry + "/" + arriveCurrency);

        // Drawer Summary
        txtTotalArriveCurrency.setText(arriveCurrency);
        txtTotalArriveValue.setText(Util.makeStringComma(totalExpense + ""));

        txtTotalDepartCurrency.setText(departCurrency);
        txtTotalDepartValue.setText(Util.makeStringComma(mCalculation.Convert(totalExpense) + ""));

        txtSpendArriveCurrency.setText(arriveCurrency);
        txtSpendDepartCurrency.setText(departCurrency);

        if (spendExpense == 0) {
            txtSpendArriveValue.setText("0");
            txtSpendDepartValue.setText("0");
        } else {
            txtSpendArriveValue.setText(Util.makeStringComma(spendExpense + ""));
            txtSpendDepartValue.setText(Util.makeStringComma(mCalculation.Convert(spendExpense) + ""));
        }

        txtLeftArriveCurrency.setText(arriveCurrency);
        txtLeftArriveValue.setText(Util.makeStringComma((totalExpense - spendExpense) + ""));
        prefSetting.edit().putString(Common.LEFT_ARRIVE_EXPENSE, Util.makeStringComma((totalExpense - spendExpense) + "")).apply();

        txtLeftDepartCurrency.setText(departCurrency);
        txtLeftDepartValue.setText(Util.makeStringComma(mCalculation.Convert((totalExpense - spendExpense)) + ""));
        prefSetting.edit().putString(Common.LEFT_DEPART_EXPENSE, Util.makeStringComma(mCalculation.Convert((totalExpense - spendExpense)) + "")).apply();

        // Drawer Summary
        txtCurrencyArriveCurrency.setText(arriveCurrency);
        txtCurrencyDepartCurrency.setText(departCurrency);

        // Drawer Currency
        // 베트남과 일본은 100 단위로 보여줌
        if (arriveCurrency.equals("VND") || arriveCurrency.equals("JPY")) {
            txtCurrencyArriveValue.setText("100");
            txtCurrencyDepartValue.setText(Util.makeStringComma(mCalculation.Convert(100) + ""));
        } else {
            txtCurrencyArriveValue.setText("1");
            txtCurrencyDepartValue.setText(Util.makeStringComma(mCalculation.Convert(1) + ""));
        }

        txtUpdateDate.setText(updateDate);
    }

    /**
     * YQL로부터 환율을 업데이트하고 Progress Bar를 보여주는 부분
     */
    // TODO 네트워크 타지 못하는 경우 확인할 수 있도록
    public class UpdateExchangeRate extends AsyncTask<Integer, Integer, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        // 작업시작, ProgressDialog 객체를 생성하고 시작합니다
        protected void onPreExecute() {
            String[] currency = getResources().getStringArray(R.array.currency);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            asyncDialog.setMessage(getText(R.string.progress_update));
            asyncDialog.setMax(currency.length); // ProgressBar의 최대 숫자를 지정
            asyncDialog.setCanceledOnTouchOutside(false); // Dialog 바깥을 눌러도 종료 안되게 변경

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        // 진행중, ProgressDialog 의 진행 정도를 표현해 줍니다.
        protected Void doInBackground(Integer... params) {
            try {
                String[] currency = getResources().getStringArray(R.array.currency);
                String[] defaultRate = getResources().getStringArray(R.array.currency_default_rate);
                SharedPreferences prefUpdate = getSharedPreferences(Common.PREF_UPDATE, MODE_MULTI_PROCESS);
                SharedPreferences prefExchange = getSharedPreferences(Common.PREF_EXCHANGE, MODE_MULTI_PROCESS);

                for (int i = 0; i < currency.length; i++) {
                    String requestUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%3D%22" + currency[i] + "USD%22&format=xml&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

                    String currencyRate = updateExchangeRateFromYql(requestUrl);
                    Logger.e(LOG_TAG1, LOG_TAG2, "GetCurrencyRate : " + currency[i] + "/" + "USD : " + currencyRate);

                    // 서버로부터 잘 가져왔으면 preference에 넣어줌
                    // !currencyRate.equals("0.0000") 베트남 같은 경우 단위가 너무 낮아서 예외처리
                    if (!currencyRate.equals("") && !currencyRate.equals("0.0000")) {
                        prefExchange.edit().putString(currency[i], currencyRate).apply();
                    }
                    // 가져오지 못한 경우에는 평균 환율을 넣어줌
                    else {
                        String tempRate = defaultRate[i].substring(6, defaultRate[i].length());
                        Logger.e(LOG_TAG1, LOG_TAG2, "tempRate : " + tempRate);
                        prefExchange.edit().putString(currency[i], tempRate).apply();
                    }
                    publishProgress(i);
                }

                // 업데이트를 해주고 업데이트한 시간을 Preference에 저장한다
                prefUpdate.edit().putString(Common.UPDATE_TIME, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTimeInMillis()) + "").commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        // doInBackground에 publishProgress 메서드로 넘겨준 값을 업데이트 함
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            asyncDialog.setProgress(values[0]);
        }

        @Override
        // 종료, ProgressDialog 종료 기능을 구현합니다.
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            asyncDialog.dismiss();
            Toast.makeText(MainActivity.this, getText(R.string.toast_update_done), Toast.LENGTH_SHORT).show();
            bypassOnResume(getApplicationContext());
            adapterRefresh();
        }
    }

    public String updateExchangeRateFromYql(String requestUrl) {
        try {
            URL url = new URL(requestUrl);

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            boolean isName = false;
            boolean isRate = false;
            boolean isDate = false;
            boolean isTime = false;

            String name = "";
            String rate = "";
            String date = "";
            String time = "";

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    // parser가 시작 태그를 만나면 실행
                    case XmlPullParser.START_TAG:
                        // KRW/USD 같이 환율 비교 나라가 나옴
                        if (parser.getName().equals("Name")) {
                            isName = true;
                        }

                        // 환율 정보
                        if (parser.getName().equals("Rate")) {
                            isRate = true;
                        }

                        // 요청 날짜
                        if (parser.getName().equals("Date")) {
                            isDate = true;
                        }

                        // 요청 시간
                        if (parser.getName().equals("Time")) {
                            isTime = true;
                        }
                        break;

                    //parser가 내용에 접근했을때
                    case XmlPullParser.TEXT:
                        // isName이 true일 때 태그의 내용을 저장.
                        if (isName) {
                            name = parser.getText();
                            isName = false;
                        }

                        // isRate가 true일 때 태그의 내용을 저장.
                        if (isRate) {
                            rate = parser.getText();
                            isRate = false;
                        }

                        // isDate가 true일 때 태그의 내용을 저장.
                        if (isDate) {
                            date = parser.getText();
                            isDate = false;
                        }

                        // isTime true일 때 태그의 내용을 저장.
                        if (isTime) {
                            time = parser.getText();
                            isTime = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                parserEvent = parser.next();
            }

            return rate;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void initExchangeRate() {
        try {
            String[] currency = getResources().getStringArray(R.array.currency);
            String[] defaultRate = getResources().getStringArray(R.array.currency_default_rate);
            SharedPreferences prefUpdate = getSharedPreferences(Common.PREF_UPDATE, MODE_MULTI_PROCESS);
            SharedPreferences prefExchange = getSharedPreferences(Common.PREF_EXCHANGE, MODE_MULTI_PROCESS);

            for (int i = 0; i < currency.length; i++) {
                String tempRate = defaultRate[i].substring(6);
                Logger.e(LOG_TAG1, LOG_TAG2, "tempRate : " + tempRate);
                prefExchange.edit().putString(currency[i], tempRate).apply();
            }

            // 업데이트를 해주고 업데이트한 시간을 Preference에 저장한다
            prefUpdate.edit().putString(Common.UPDATE_TIME, "default").apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 인터넷 가능 여부 체크 및 메시지 */
    private boolean checkNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}