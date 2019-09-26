package com.aile.tex;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class History extends Fragment {
    private static final String LOG_TAG1 = "History";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.e(LOG_TAG1, LOG_TAG2, "History onCreateView()");

        View view = inflater.inflate(R.layout.fragment_history, null);
        ArrayList<MyItem> arItem = new ArrayList<>();
        MyItem myItem;

        try {
            DBHelper mHelper = new DBHelper(getContext());
            SQLiteDatabase db = mHelper.getReadableDatabase();

            String query = String.format("SELECT category, arrive_currency, arrive_price, depart_currency, depart_price, date, memo FROM %s ORDER BY idx DESC;", Common.EXPENSE_TABLE);

            // 쿼리를 실행하고 거기에 대한 결과를 cursor에 넣음
            Cursor cursor = db.rawQuery(query, null);

            while(cursor.moveToNext()) {
                switch(Integer.parseInt(cursor.getString(0))) {
                    case 0 : // 숙박
                        myItem = new MyItem(R.drawable.ic_hotel_active, getString(R.string.category_accommodation), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        arItem.add(myItem);
                        break;

                    case 1 : // 음식
                        myItem = new MyItem(R.drawable.ic_restaurant_active, getString(R.string.category_food), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        arItem.add(myItem);
                        break;

                    case 2 : // 교통
                        myItem = new MyItem(R.drawable.ic_train_active, getString(R.string.category_transportation), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        arItem.add(myItem);
                        break;

                    case 3 : // 쇼핑
                        myItem = new MyItem(R.drawable.ic_shopping_cart_active, getString(R.string.category_shopping), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        arItem.add(myItem);
                        break;

                    case 4 : // 관광
                        myItem = new MyItem(R.drawable.ic_map_active, getString(R.string.category_sightseeing), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        arItem.add(myItem);
                        break;

                    case 5 : // 그 외
                        myItem = new MyItem(R.drawable.ic_more_horiz_active, getString(R.string.category_etc), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                        arItem.add(myItem);
                        break;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        MyListAdapter MyAdapter = new MyListAdapter(getContext(), R.layout.listitem, arItem);
        final ListView MyList = (ListView)view.findViewById(R.id.list);
        MyList.setAdapter(MyAdapter);

        // 수정을 위한 LongClick 부분
        MyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                editItem(position);
                return false;
            }
        });

        return view;
    }

    private class MyItem{
        int Icon;
        String Item;
        String Dest;
        String DestVal;
        String Depart;
        String DepartVal;
        String Date;
        String Memo;

        MyItem(int aIcon, String aItem, String aDest, String aDestVal, String aDepart, String aDepartVal, String aDate, String aMemo){
            Icon = aIcon;
            Item = aItem;
            Dest = aDest;
            DestVal = aDestVal;
            Depart = aDepart;
            DepartVal = aDepartVal;
            Date = aDate;
            Memo = aMemo;
        }
    }

    private class MyListAdapter extends BaseAdapter {
        Context maincon;
        LayoutInflater Inflater;
        ArrayList<MyItem> arSrc;
        int layout;

        public MyListAdapter(Context context, int alayout, ArrayList<MyItem> aarSrc){
            maincon = context;
            Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arSrc = aarSrc;
            layout = alayout;
        }

        public int getCount(){
            return arSrc.size();
        }

        public String getItem(int position){
            return arSrc.get(position).Item;
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            final int pos = position;
            if(convertView == null){
                convertView = Inflater.inflate(layout, parent, false);
            }

            ImageView img = (ImageView)convertView.findViewById(R.id.listImg);
            img.setImageResource(arSrc.get(position).Icon);

            TextView item = (TextView)convertView.findViewById(R.id.listCategory);
            item.setText(arSrc.get(position).Item);

            TextView dest = (TextView)convertView.findViewById(R.id.listArriveCurrency);
            dest.setText(arSrc.get(position).Dest);

            TextView destVal = (TextView)convertView.findViewById(R.id.listArriveValue);
            destVal.setText(arSrc.get(position).DestVal);

            TextView depart = (TextView)convertView.findViewById(R.id.listDepartCurrency);
            depart.setText(arSrc.get(position).Depart);

            TextView departVal = (TextView)convertView.findViewById(R.id.listDepartValue);
            departVal.setText(arSrc.get(position).DepartVal);

            TextView date = (TextView)convertView.findViewById(R.id.listDate);
            date.setText(arSrc.get(position).Date);

            TextView memo = (TextView)convertView.findViewById(R.id.listMemo);
            memo.setText(arSrc.get(position).Memo);

            return convertView;
        }
    }

    private void editItem(int index) {
        try {
            DBHelper mHelper = new DBHelper(getContext());
            SQLiteDatabase db = mHelper.getReadableDatabase();

            String query = String.format("SELECT idx, category, arrive_currency, arrive_price, depart_currency, depart_price, date, memo FROM %s ORDER BY idx DESC;", Common.EXPENSE_TABLE);

            // 쿼리를 실행하고 거기에 대한 결과를 cursor에 넣음
            Cursor cursor = db.rawQuery(query, null);

            String idx = "";
            String category = "";
            String arrive_currency = "";
            String arrive_price = "";
            String depart_currency = "";
            String depart_price = "";
            String date = "";
            String memo = "";

            int chkIndex = 0;

            while(cursor.moveToNext()) {
                if (chkIndex == index) {
                    idx = cursor.getString(0);
                    category = cursor.getString(1);
                    arrive_currency = cursor.getString(2);
                    arrive_price = cursor.getString(3);
                    depart_currency = cursor.getString(4);
                    depart_price = cursor.getString(5);
                    date = cursor.getString(6);
                    memo = cursor.getString(7);
                }
                chkIndex++;
            }

            cursor.close();
            db.close();
            mHelper.close();

            InputDialog inputDialog = new InputDialog(getContext(), idx, category, arrive_currency, arrive_price, depart_currency, depart_price, date, memo);
            inputDialog.show();

            inputDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    MainActivity.bypassOnResume(getContext());
                    MainActivity.adapterRefresh();
                }
            });
        }
        catch(Exception e) { e.printStackTrace(); }
    }
}