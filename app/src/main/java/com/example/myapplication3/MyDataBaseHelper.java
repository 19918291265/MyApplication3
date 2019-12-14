package com.example.myapplication3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    public MyDataBaseHelper(Context context){
        super(context,"database",null,4);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //下面这句是简单的SQL语句,创建数据库的表
        String TABLE_USER = "create table user(account_id INTEGER PRIMARY KEY autoincrement,account text" +
                ",cipher text,name text,picture text);";
        sqLiteDatabase.execSQL(TABLE_USER);

        String TABLE_COLLECT = "create table collect(collect_id INTEGER PRIMARY KEY autoincrement,account_id text,news_id text,url text,image_url text,title text);";
        sqLiteDatabase.execSQL(TABLE_COLLECT);
    }




    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //更新数据库版本时才会执行onUpgrade
        sqLiteDatabase.execSQL("drop table if exists user");
        onCreate(sqLiteDatabase);
    }
}