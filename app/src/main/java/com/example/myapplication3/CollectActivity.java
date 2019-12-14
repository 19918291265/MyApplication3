package com.example.myapplication3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.Adapter.MyAdapter_collect;
import com.example.myapplication3.Util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private String account_id;
    private SharedPreferencesUtil check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_collect);

        initUI();


        recyclerView = findViewById(R.id.recyclerView);

        MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(CollectActivity.this);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        List<Map<String, Object>> list = new ArrayList<>();

        Cursor cursor = database.query("collect",new String[]{"news_id","url","title","image_url"},"account_id=?", new String[]{account_id},null,null,null);

        if (cursor.moveToFirst()){
            String id = cursor.getString(cursor.getColumnIndex("news_id"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", id);
            map1.put("url",url);
            map1.put("title",title);
            map1.put("image_url",image_url);
            list.add(map1);
            while (cursor.moveToNext()) {
                String id1 = cursor.getString(cursor.getColumnIndex("news_id"));
                String url1 = cursor.getString(cursor.getColumnIndex("url"));
                String title1 = cursor.getString(cursor.getColumnIndex("title"));
                String image_url1 = cursor.getString(cursor.getColumnIndex("image_url"));
                Map<String, Object> map = new HashMap<>();
                map.put("id", id1);
                map.put("url",url1);
                map.put("title",title1);
                map.put("image_url",image_url1);
                list.add(map);
            }
        }
        cursor.close();
        database.close();
        recyclerView.setLayoutManager(new LinearLayoutManager(CollectActivity.this));//垂直排列
        recyclerView.setAdapter(new MyAdapter_collect(CollectActivity.this, list));//绑定适配器


    }

    private void initUI() {
        recyclerView = findViewById(R.id.recyclerView);
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id = check.getAccountId();
    }


    public void onClick(View v) {

    }
}
