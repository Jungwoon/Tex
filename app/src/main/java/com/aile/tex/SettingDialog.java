package com.aile.tex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by JW on 16. 4. 13..
 */
public class SettingDialog extends Dialog {
    private static final String LOG_TAG1 = "SettingDialog";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    Context mContext;
    Spinner spinnerDepartCountry;
    Spinner spinnerArriveCountry;
    EditText editTotalExpense;
    Button btnConfirm;
    Button btnInit;

    TextView nativeChangeExpense;

    String strTempDepart;
    String strTempArrive;

    String departCountry; // 출발국
    String departCurrency; // 출발국 화폐
    int departIndex; // 출발국 index

    String arriveCountry; // 도착국
    String arriveCurrency; // 도착국 화폐
    int arriveIndex; // 도착국 index

    TextView txtDepartCurrency;
    TextView txtArriveCurrency;

    SharedPreferences prefSettings;

    int cnt;

    public SettingDialog(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);

        prefSettings = mContext.getSharedPreferences(Common.PREF_SETTINGS, 4);

        // 출발 스피너
        spinnerDepartCountry = (Spinner)findViewById(R.id.setDepartCountry);
        spinnerDepartCountry.setOnItemSelectedListener(mOnItemSeletedLister);

        // 도착 스피너
        spinnerArriveCountry = (Spinner)findViewById(R.id.setArriveCountry);
        spinnerArriveCountry.setOnItemSelectedListener(mOnItemSeletedLister);

        // 만약 나라 설정을 했었으면 나라 변경 못하게, 막음
        if(prefSettings.getBoolean(Common.APP_INIT, false)) {
            spinnerDepartCountry.setEnabled(false);
            spinnerArriveCountry.setEnabled(false);
        }
        else {
            spinnerDepartCountry.setEnabled(true);
            spinnerArriveCountry.setEnabled(true);
        }

        // 여행 경비
        editTotalExpense = (EditText)findViewById(R.id.editTotalExpense);
        editTotalExpense.addTextChangedListener(textWatcher);

        // 출발국 환율
        nativeChangeExpense = (TextView)findViewById(R.id.txtTransExpense);

        // 스피너에서 나라가 바뀌면 환율도 같이 바뀌도록 하기 위한 부분
        txtDepartCurrency = (TextView)findViewById(R.id.txtDepartCurrency);
        txtArriveCurrency = (TextView)findViewById(R.id.txtArriveCurrency);

        // 이미 등록을 했었는지 확인하는 부분
        int tempDepartIndex = prefSettings.getInt(Common.DEPART_INDEX, -1);
        Logger.e(LOG_TAG1, LOG_TAG2, "tempDepartIndex : " + tempDepartIndex);

        int tempArriveIndex = prefSettings.getInt(Common.ARRIVE_INDEX, -1);
        Logger.e(LOG_TAG1, LOG_TAG2, "tempArriveIndex : " + tempArriveIndex);

        int tempTotalExpense = prefSettings.getInt(Common.TOTAL_EXPENSE, -1);
        Logger.e(LOG_TAG1, LOG_TAG2, "tempArriveIndex : " + tempArriveIndex);

        // 값이 있으면, 값이 없으면 -1을 가져오기 때문
        if(!(tempDepartIndex < 0) && !(tempArriveIndex < 0)) {
            spinnerDepartCountry.setSelection(tempDepartIndex);
            spinnerArriveCountry.setSelection(tempArriveIndex);

            if(!(tempTotalExpense < 0)) {
                Calculation mCalculation = new Calculation(mContext);
                editTotalExpense.setText(tempTotalExpense+"");
                nativeChangeExpense.setText(Util.makeStringComma(mCalculation.Convert((long)tempTotalExpense)+""));
            }
        }
        // 초기화 버튼
        btnInit = (Button)findViewById(R.id.btnSettingInit);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(getContext())
                            .setMessage(R.string.setting_init_comment)
                            .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        initTable(); // 지출내역 제거

                                        prefSettings.edit().putBoolean(Common.APP_INIT, false).apply();
                                        prefSettings.edit().putInt(Common.DEPART_INDEX, -1).apply();
                                        prefSettings.edit().putInt(Common.ARRIVE_INDEX, -1).apply();

                                        prefSettings.edit().putString(Common.DEPART_COUNTRY, "South Korea").apply();
                                        prefSettings.edit().putString(Common.ARRIVE_COUNTRY, "America").apply();

                                        prefSettings.edit().putString(Common.DEPART_CURRENCY, "KRW").apply();
                                        prefSettings.edit().putString(Common.ARRIVE_CURRENCY, "USD").apply();

                                        prefSettings.edit().putInt(Common.TOTAL_EXPENSE, -1).apply();
                                        prefSettings.edit().putInt(Common.SPEND_EXPENSE, 0).apply();

                                        prefSettings.edit().putString(Common.LEFT_DEPART_EXPENSE, "0").apply();
                                        prefSettings.edit().putString(Common.LEFT_ARRIVE_EXPENSE, "0").apply();

                                        // APP_INIT의 값이 true로 바뀔때까지 대기하기 위해서
                                        cnt = 0;
                                        while(true) {
                                            cnt++;
                                            Logger.e(LOG_TAG1, LOG_TAG2, "cnt : " + cnt);

                                            Thread.sleep(100);
                                            if(!prefSettings.getBoolean(Common.APP_INIT, true)) {
                                                break;
                                            }

                                            // 1초까지만 기다림
                                            if(cnt == 10) {
                                                break;
                                            }
                                        }
                                    }
                                    catch(Exception e) {
                                        e.printStackTrace();
                                    }

                                    dismiss(); // 다이얼로그를 닫고
                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            }).show();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 확인 버튼
        btnConfirm = (Button)findViewById(R.id.btnSettingConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Country
                    prefSettings.edit().putString(Common.DEPART_COUNTRY, departCountry).apply();
                    prefSettings.edit().putString(Common.ARRIVE_COUNTRY, arriveCountry).apply();

                    // Currency
                    prefSettings.edit().putString(Common.DEPART_CURRENCY, departCurrency).apply();
                    prefSettings.edit().putString(Common.ARRIVE_CURRENCY, arriveCurrency).apply();

                    // Index
                    prefSettings.edit().putInt(Common.DEPART_INDEX, departIndex).apply();
                    prefSettings.edit().putInt(Common.ARRIVE_INDEX, arriveIndex).apply();

                    // Expense
                    prefSettings.edit().putInt(Common.TOTAL_EXPENSE, Integer.parseInt(editTotalExpense.getText().toString())).apply();

                    // 처음이 아니기 때문에 true로 바꿔줌
                    prefSettings.edit().putBoolean(Common.APP_INIT, true).apply();

                    // Preference 갱신하는데 시간이 조금 걸리기 때문에 0.6초 쉬어줌

                    // APP_INIT의 값이 true로 바뀔때까지 대기하기 위해서
                    cnt = 0;
                    while(true) {
                        cnt++;
                        Logger.e(LOG_TAG1, LOG_TAG2, "cnt : " + cnt);

                        Thread.sleep(100);
                        if(prefSettings.getBoolean(Common.APP_INIT, false)) {
                            Logger.e(LOG_TAG1, LOG_TAG2, "APP_INIT is true");
                            break;
                        }

                        // 1초까지만 기다림
                        if(cnt == 10) {
                            Logger.e(LOG_TAG1, LOG_TAG2, "cnt is 10");
                            break;
                        }
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                dismiss();
            }
        });
    }

    // 스피너의 선택 메뉴가 바뀌면 이벤트 처리해주는 부분
    private AdapterView.OnItemSelectedListener mOnItemSeletedLister = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // substring으로 나라와 화폐를 나누는 부분
            strTempDepart = spinnerDepartCountry.getSelectedItem().toString();
            departCurrency = strTempDepart.substring(strTempDepart.length()-3, strTempDepart.length());
            departCountry = strTempDepart.substring(0, strTempDepart.length()-6);
            departIndex = spinnerDepartCountry.getSelectedItemPosition();

            // substring으로 나라와 화폐를 나누는 부분
            strTempArrive = spinnerArriveCountry.getSelectedItem().toString();
            arriveCurrency = strTempArrive.substring(strTempArrive.length()-3, strTempArrive.length());
            arriveCountry = strTempArrive.substring(0, strTempArrive.length()-6);
            arriveIndex = spinnerArriveCountry.getSelectedItemPosition();

            // 스피너에서 출발국과 도착국이 바뀌면 환율 정보도 같이 바뀌는 부분
            txtDepartCurrency.setText(departCurrency);
            txtArriveCurrency.setText(arriveCurrency);

            Log.e("JW TEST", "Selected Depart Country: " + departCountry);
            Log.e("JW TEST", "Selected Arriv Country: " + arriveCountry);

            Log.e("JW TEST", "Selected Depart Currency: " + departCurrency);
            Log.e("JW TEST", "Selected Arrive Currency: " + arriveCurrency);
            Log.e("JW TEST", "=========================== ");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // EidtText가 눌릴때마다 감지하는 부분
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Calculation mCalculation = new Calculation(mContext, departCurrency, arriveCurrency);

            try {
                // 입력이 되었을때만 입력
                if(editTotalExpense.length() > 0) {
                    Long changeValue = mCalculation.Convert(Long.parseLong(editTotalExpense.getText().toString()));

                    // editText로 받은 숫자에 해당 환율을 곱해서 천 단위로 보여줌
                    nativeChangeExpense.setText(Util.makeStringComma(changeValue.toString()));
                }
                else {
                    // 입력이 안되었으면 0으로
                    nativeChangeExpense.setText("0");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initTable() {
        try {
            DBHelper mHelper = new DBHelper(mContext);

            //SQLite에 쓸 수 있게 만듦
            SQLiteDatabase db = mHelper.getWritableDatabase();

            String query = String.format("DELETE FROM %s;", Common.EXPENSE_TABLE);

            //만들어진 Query가 정상적인지 확인하는 부분
            Logger.e(LOG_TAG1, LOG_TAG2, "EditDialog Query : " + query);

            //쿼리 실행
            db.execSQL(query);

            //다 썼으니 닫아줌
            mHelper.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}