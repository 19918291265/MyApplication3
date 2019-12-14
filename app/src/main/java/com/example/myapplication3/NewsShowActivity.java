package com.example.myapplication3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication3.Util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewsShowActivity extends AppCompatActivity implements View.OnClickListener {

    private String id;
    private String url;
    private String title;
    private String image_url;
    private RelativeLayout comment;
    private RelativeLayout collect;
    private SharedPreferencesUtil check;
    private String account_id;

    List<Map<String,Object>> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        url = intent.getStringExtra("url");
        image_url = intent.getStringExtra("images");
        title = intent.getStringExtra("title");

        initUI();

        comment.setOnClickListener(this);
        collect.setOnClickListener(this);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://news-at.zhihu.com/api/3/story/"+ id );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //读取刚刚获取的输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
//                    line= reader.readLine();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();

    }

    private void initUI() {
        collect = findViewById(R.id.collect);
        comment = findViewById(R.id.comment);
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id = check.getAccountId();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.collect:
                if (check.isLogin()) {

                    MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(NewsShowActivity.this);//实例化一个对象
                    SQLiteDatabase database = dataBaseHelper.getReadableDatabase();//打开数据库\
                        database.execSQL("insert into collect(account_id,news_id,url,image_url,title) values('" + account_id + "','" + id + "','" + url + "','" + image_url + "','" + title + "');");
                        database.close();
                        Toast.makeText(this, "收藏成功！", Toast.LENGTH_SHORT).show();

                        break;
                    }

                    else {
                        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                        break;
                    }

            case R.id.comment:
                Intent intent = new Intent(NewsShowActivity.this,CommentActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
                break;

        }
    }
    private void showResponse(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
                String share_url = jsonObject.getString("share_url");


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}