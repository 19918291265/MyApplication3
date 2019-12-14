package com.example.myapplication3;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication3.Util.SharedPreferencesUtil;
import com.example.myapplication3.Util.Util;

public class SafeActivity extends AppCompatActivity implements View.OnClickListener {

    private String account_id;
    private EditText cipher_old;
    private EditText cipher_1;
    private EditText cipher_2;
    private String cipher;
    private Button change_do;
    private SharedPreferencesUtil check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_safe);

        initUI();




        change_do.setOnClickListener(this);
    }

    private void initUI() {

        cipher_old = findViewById(R.id.et_1);
        cipher_1 = findViewById(R.id.et_2);
        cipher_2 = findViewById(R.id.et_3);
        change_do = findViewById(R.id.change);
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id = check.getAccountId();
    }


    public void onClick(View v) {

        String cipher_compare = cipher_old.getText().toString();
        String cipher_a = cipher_1.getText().toString();
        String cipher_b = cipher_2.getText().toString();

        MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(SafeActivity.this);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

        Cursor cursor = database.query("user", new String[]{"cipher"}, "account_id=?", new String[]{account_id}, null, null, null);

        if (cursor.moveToFirst()) {
            cipher = cursor.getString(cursor.getColumnIndex("cipher"));
        }
        cursor.close();
        database.close();

        if (cipher_old.length()==0)
        { Toast.makeText(this, "请输入原密码！", Toast.LENGTH_SHORT).show(); return;}
        else if (cipher_a.length()<4||cipher_b.length()<4)
        { Toast.makeText(this, "新密码至少四位！", Toast.LENGTH_SHORT).show(); return;}
        else if (!Util.isEmpty(cipher_a)||!Util.isEmpty(cipher_b))
        {Toast.makeText(this, "密码中输入了非法字符！", Toast.LENGTH_SHORT).show(); return;}
        else if (!cipher.equals(cipher_compare))
        { Toast.makeText(this, "原密码不正确", Toast.LENGTH_SHORT).show();return; }
        else if (!cipher_a.equals(cipher_b))
        {Toast.makeText(this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show(); return;}
        else
        {
            MyDataBaseHelper dataBaseHelper1 = new MyDataBaseHelper(SafeActivity.this);
            SQLiteDatabase database1 = dataBaseHelper1.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("cipher",cipher_a );//第一个"name" 是字段名字  第二个是对应字段的数据
            database1.update("user", values, "account_id=?", new String[]{account_id});
            Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
            database1.close();
        }


    }
}
