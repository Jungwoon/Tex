package com.aile.tex;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by JW on 16. 4. 13..
 */
public class InputDialog extends Dialog {
    private static final String LOG_TAG1 = "InputDialog";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    private Context mContext;
    private Button btnAccommodation;
    private Button btnFood;
    private Button btnTransportation;
    private Button btnShopping;
    private Button btnSightseeing;
    private Button btnEtc;
    private Button btnConfirm;
    private Button btnEdit;
    private Button btnDelete;

    private TextView txtInputArriveCurrency;
    private EditText editInputArriveValue;
    private EditText editMemo;

    private TextView txtInputDepartCurrency;
    private TextView txtInputDepartValue;

    private String departCurrency;
    private String arriveCurrency;

    // edit & delete
    private String idx;
    private String category;
    private String arrive_currency;
    private String arrive_price;
    private String depart_currency;
    private String depart_price;
    private String date;
    private String memo;

    private boolean editFlag = false;

    public InputDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    // 생성자 오버로딩으로 Edit Mode일땐 따로 생성함
    public InputDialog(Context context, String idx, String category, String arrive_currency, String arrive_price, String depart_currency, String depart_price, String date, String memo) {
        super(context);
        this.mContext = context;

        this.idx = idx;
        this.category = category;
        this.arrive_currency = arrive_currency;
        this.arrive_price = arrive_price;
        this.depart_currency = depart_currency;
        this.depart_price = depart_price;
        this.date = date;
        this.memo = memo;

        editFlag = true;
    }

    private int categoryIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_dialog);

        txtInputArriveCurrency = (TextView)findViewById(R.id.inputArriveCurrency);
        editInputArriveValue = (EditText)findViewById(R.id.inputArriveValue);
        editInputArriveValue.addTextChangedListener(textWatcher);

        txtInputDepartCurrency = (TextView)findViewById(R.id.inputDepartCurrency);
        txtInputDepartValue = (TextView)findViewById(R.id.inputDepartValue);

        // 메모
        editMemo = (EditText)findViewById(R.id.editMemo);

        // 확인 버튼
        btnConfirm = (Button)findViewById(R.id.btnInputConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 넣어주는 부분
                try {
                    int category = categoryIdx;
                    String arrive_currency = arriveCurrency;
                    String arrive_price = Util.makeStringComma(editInputArriveValue.getText().toString());
                    String depart_currency = departCurrency;
                    String depart_price = txtInputDepartValue.getText().toString();
                    String date = new SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault()).format(Calendar.getInstance().getTimeInMillis());
                    String memo = editMemo.getText().toString();

                    Logger.e(LOG_TAG1, LOG_TAG2, "editInputArriveValue.getText().toString() : " + editInputArriveValue.getText().toString());

                    if (editInputArriveValue.getText().toString().equals("")) {
                        Toast.makeText(getContext(), getContext().getText(R.string.toast_input_price), Toast.LENGTH_SHORT).show();
                    } else if (category < 0) {
                        Toast.makeText(getContext(), getContext().getText(R.string.toast_input_category), Toast.LENGTH_SHORT).show();
                    } else {
                        DBHelper mHelper = new DBHelper(mContext);

                        //SQLite에 쓸 수 있게 만듦
                        SQLiteDatabase db = mHelper.getWritableDatabase();

                        String query = String.format("INSERT INTO %s (category, arrive_currency, arrive_price, depart_currency, depart_price, date, memo)" +
                                        " VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                                Common.EXPENSE_TABLE, category, arrive_currency, arrive_price, depart_currency, depart_price, date, memo);

                        //만들어진 Query가 정상적인지 확인하는 부분
                        Logger.e(LOG_TAG1, LOG_TAG2, "InputDialog Query : " + query);

                        //쿼리 실행
                        db.execSQL(query);

                        //다 썼으니 닫아줌
                        mHelper.close();

                        // 다이얼로그를 닫아줌
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 수정 버튼
        btnEdit = (Button)findViewById(R.id.btnEditConfirm);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // DB에 넣어주는 부분
                try {
                    int category = categoryIdx;
                    String arrive_currency = arriveCurrency;
                    String arrive_price = Util.makeStringComma(editInputArriveValue.getText().toString());
                    String depart_currency = departCurrency;
                    String depart_price = txtInputDepartValue.getText().toString();
                    String date = new SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault()).format(Calendar.getInstance().getTimeInMillis());
                    String memo = editMemo.getText().toString();

                    Logger.e(LOG_TAG1, LOG_TAG2, "editInputArriveValue.getText().toString() : " + editInputArriveValue.getText().toString());

                    if (editInputArriveValue.getText().toString().equals("")) {
                        Toast.makeText(getContext(), getContext().getText(R.string.toast_input_price), Toast.LENGTH_SHORT).show();
                    }
                    else if (category < 0) {
                        Toast.makeText(getContext(), getContext().getText(R.string.toast_input_category), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        DBHelper mHelper = new DBHelper(mContext);

                        //SQLite에 쓸 수 있게 만듦
                        SQLiteDatabase db = mHelper.getWritableDatabase();

                        String query = String.format("UPDATE %s  SET category = '%s', arrive_currency = '%s', arrive_price = '%s', depart_currency = '%s', depart_price = '%s', date = '%s', memo = '%s' WHERE idx = '%s';",
                                Common.EXPENSE_TABLE, category, arrive_currency, arrive_price, depart_currency, depart_price, date, memo, idx);

                        //만들어진 Query가 정상적인지 확인하는 부분
                        Logger.e(LOG_TAG1, LOG_TAG2, "EditDialog Query : " + query);

                        //쿼리 실행
                        db.execSQL(query);

                        //다 썼으니 닫아줌
                        mHelper.close();

                        // 다이얼로그를 닫아줌
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 삭제 버튼
        btnDelete = (Button)findViewById(R.id.btnDeleteConfirm);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // DB에 넣어주는 부분
                try {
                    DBHelper mHelper = new DBHelper(mContext);

                    //SQLite에 쓸 수 있게 만듦
                    SQLiteDatabase db = mHelper.getWritableDatabase();

                    String query = String.format("DELETE FROM %s WHERE idx = '%s';", Common.EXPENSE_TABLE, idx);

                    //만들어진 Query가 정상적인지 확인하는 부분
                    Logger.e(LOG_TAG1, LOG_TAG2, "InputDialog Query : " + query);

                    //쿼리 실행
                    db.execSQL(query);

                    //다 썼으니 닫아줌
                    mHelper.close();

                    // 다이얼로그를 닫아줌
                    dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        SharedPreferences prefSetting = mContext.getSharedPreferences(Common.PREF_SETTINGS, 4);
        departCurrency = prefSetting.getString(Common.DEPART_CURRENCY, "");
        arriveCurrency = prefSetting.getString(Common.ARRIVE_CURRENCY, "");

        txtInputArriveCurrency.setText(arriveCurrency);
        txtInputDepartCurrency.setText(departCurrency);

        // 숙박 버튼
        btnAccommodation = (Button) findViewById(R.id.btnAccommodation);
        btnAccommodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAccommodation.setSelected(true);
                btnFood.setSelected(false);
                btnTransportation.setSelected(false);
                btnShopping.setSelected(false);
                btnSightseeing.setSelected(false);
                btnEtc.setSelected(false);

                categoryIdx = 0; // 숙박
            }
        });

        // 음식 버튼
        btnFood = (Button) findViewById(R.id.btnFood);
        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAccommodation.setSelected(false);
                btnFood.setSelected(true);
                btnTransportation.setSelected(false);
                btnShopping.setSelected(false);
                btnSightseeing.setSelected(false);
                btnEtc.setSelected(false);

                categoryIdx = 1; // 음식
            }
        });

        // 교통 버튼
        btnTransportation = (Button) findViewById(R.id.btnTransportation);
        btnTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAccommodation.setSelected(false);
                btnFood.setSelected(false);
                btnTransportation.setSelected(true);
                btnShopping.setSelected(false);
                btnSightseeing.setSelected(false);
                btnEtc.setSelected(false);

                categoryIdx = 2; // 교통
            }
        });

        // 쇼핑 버튼
        btnShopping = (Button) findViewById(R.id.btnShopping);
        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAccommodation.setSelected(false);
                btnFood.setSelected(false);
                btnTransportation.setSelected(false);
                btnShopping.setSelected(true);
                btnSightseeing.setSelected(false);
                btnEtc.setSelected(false);

                categoryIdx = 3; // 쇼핑
            }
        });

        // 관광 버튼
        btnSightseeing = (Button) findViewById(R.id.btnSightseeing);
        btnSightseeing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAccommodation.setSelected(false);
                btnFood.setSelected(false);
                btnTransportation.setSelected(false);
                btnShopping.setSelected(false);
                btnSightseeing.setSelected(true);
                btnEtc.setSelected(false);

                categoryIdx = 4; // 관광
            }
        });

        // 그 외 버튼
        btnEtc = (Button) findViewById(R.id.btnEtc);
        btnEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAccommodation.setSelected(false);
                btnFood.setSelected(false);
                btnTransportation.setSelected(false);
                btnShopping.setSelected(false);
                btnSightseeing.setSelected(false);
                btnEtc.setSelected(true);

                categoryIdx = 5; // 그 외
            }
        });

        // ======================= 윗 부분은 필요한 초기화 부분 단계 ===========================

        // 수정 및 삭제 모드
        if (editFlag) {
            btnConfirm.setVisibility(View.GONE);

            btnAccommodation.setSelected(false);
            btnFood.setSelected(false);
            btnTransportation.setSelected(false);
            btnShopping.setSelected(false);
            btnSightseeing.setSelected(false);
            btnEtc.setSelected(false);

            switch(Integer.parseInt(category)) {
                case 0 :
                    btnAccommodation.setSelected(true);
                    categoryIdx = 0;
                    break;
                case 1 :
                    btnFood.setSelected(true);
                    categoryIdx = 1;
                    break;
                case 2 :
                    btnTransportation.setSelected(true);
                    categoryIdx = 2;
                    break;
                case 3 :
                    btnShopping.setSelected(true);
                    categoryIdx = 3;
                    break;
                case 4 :
                    btnSightseeing.setSelected(true);
                    categoryIdx = 4;
                    break;
                case 5 :
                    btnEtc.setSelected(true);
                    categoryIdx = 5;
                    break;
            }

            txtInputArriveCurrency.setText(arrive_currency);
            editInputArriveValue.setText(Long.parseLong(arrive_price.replaceAll(",",""))+"");
            editInputArriveValue.setSelection((Long.parseLong(arrive_price.replaceAll(",",""))+"").length());

            txtInputDepartCurrency.setText(depart_currency);
            txtInputDepartValue.setText(depart_price);

            editMemo.setText(memo);
        }
        // 추가 부분에서는 Edit 버튼과 Delete 버튼을 안보이게 한다
        else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

    }

    // EidtText가 눌릴때마다 감지하는 부분
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Calculation mCalculation = new Calculation(mContext);

            try {
                // 입력이 되었을때만 입력
                if (editInputArriveValue.length() > 0) {
//                    Long changeValue = Long.parseLong(editTotalExpense.getText().toString()) * 1000;
                    Long changeValue = mCalculation.Convert(Long.parseLong(editInputArriveValue.getText().toString()));

                    // editText로 받은 숫자에 해당 환율을 곱해서 천 단위로 보여줌
                    txtInputDepartValue.setText(Util.makeStringComma(changeValue.toString()));
                } else {
                    // 입력이 안되었으면 0으로
                    txtInputDepartValue.setText("0");
                }
            } catch (Exception e) {
                Logger.e(LOG_TAG1, LOG_TAG2, "error : " + e);
                e.printStackTrace();
            }
        }
    };
}