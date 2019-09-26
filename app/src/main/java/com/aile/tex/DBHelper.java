package com.aile.tex;

/**
 * Created by JW on 15. 10. 9..
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB관련해서 Table생성해주는 부분
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG1 = "DBHelper";
    private static final String LOG_TAG2 = Common.LOG_TAG_STRING;

    private static final int DATABASE_VERSION = 8;

    DBHelper(Context context){
        super(context, Common.DATABASE_NAME, null, DATABASE_VERSION);
    }

    //DB가 없으면 onCreate를 생성
    public void onCreate(SQLiteDatabase db){
        Logger.d(LOG_TAG1, LOG_TAG2, "onCreate()");

        try {

            // Setting Table
            db.execSQL("CREATE TABLE " + Common.CURRENCY_TABLE + " (" +
                            "idx integer primary key autoincrement, " +
                            "currency varchar(50) not null, " +
                            "rate varchar(50) not null);"
            );

            // Expense Table
            db.execSQL("CREATE TABLE " + Common.EXPENSE_TABLE + " (" +
                            "idx integer primary key autoincrement, " +
                            "category varchar(50) not null, " +
                            "arrive_currency varchar(50) not null, " +
                            "arrive_price varchar(50) not null, " +
                            "depart_currency varchar(50) not null, " +
                            "depart_price varchar(50) not null, " +
                            "date varchar(15) not null, " +
                            "memo varchar(50));"
            );
        }
        catch(Exception e) { ; }

    }

    //DB를 업그레이드할 때 호출된다. 기존 테이블을 삭제하고 새로 만들거나 ALTER TABLE로 스키마를 수정한다.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Logger.d(LOG_TAG1, LOG_TAG2, "onUpgrade()");
        db.execSQL("DROP TABLE IF EXISTS " + Common.CURRENCY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Common.EXPENSE_TABLE);
        onCreate(db);
    }
}
