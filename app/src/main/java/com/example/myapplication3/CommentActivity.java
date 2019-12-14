package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.Adapter.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    List<Map<String, Object>> list = new ArrayList<>();


    private RecyclerView recyclerView_long;
    private RecyclerView recyclerView_short;
    private String new_id;
    private TextView comment_1;
    private TextView comment_2;
    private String comments_sh;
    private String comments_lo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comment);
        Intent intent = getIntent();
         new_id = intent.getStringExtra("id_news");

        initUI();

     new Thread(new Runnable() {
        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                Intent intent = getIntent();
                String new_id = intent.getStringExtra("id_news");
                URL url = new URL("https://news-at.zhihu.com/api/3/story-extra/" + new_id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                InputStream in = connection.getInputStream();
                //读取刚刚获取的输入流
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    Intent intent = getIntent();
                    String new_id = intent.getStringExtra("id_news");
                    URL url = new URL("https://news-at.zhihu.com/api/4/story/" + new_id + "/long-comments" );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //读取刚刚获取的输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse2(response.toString());


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

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    Intent intent = getIntent();
                    String new_id = intent.getStringExtra("id_news");
                    URL url = new URL("https://news-at.zhihu.com/api/4/story/" + new_id + "/short-comments");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //读取刚刚获取的输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse3(response.toString());


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

    private void showResponse3(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String avatar = jsonObject1.getString("avatar");
                String author = jsonObject1.getString("author");
                String content = jsonObject1.getString("content");


                Map map = new HashMap();


                map.put("avatar",avatar);
                map.put("author",author);
                map.put("content",content);

                list.add(map);//检查到list了

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    recyclerView_long.setAdapter(new MyAdapter(CommentActivity.this, list));//绑定适配器

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showResponse2(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String avatar = jsonObject1.getString("avatar");
                String author = jsonObject1.getString("author");
                String content = jsonObject1.getString("content");

                Map map = new HashMap();

                map.put("avatar",avatar);
                map.put("author",author);
                map.put("content",content);

                list.add(map);//检查到list了
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    recyclerView_long.setAdapter(new MyAdapter(CommentActivity.this, list));//绑定适配器

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {

        recyclerView_long = findViewById(R.id.recyclerView_long);
        recyclerView_short = findViewById(R.id.recyclerView_short);
        recyclerView_long.setLayoutManager(new LinearLayoutManager(CommentActivity.this));//垂直排列
        recyclerView_short.setLayoutManager(new LinearLayoutManager(CommentActivity.this));//垂直排列
        comment_1 = findViewById(R.id.tv_1);
        comment_2 = findViewById(R.id.tv_2);

    }

    @Override
    public void onClick(View v) {

    }

    public void showResponse(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);

//                String id = jsonObject.getString("id");
//                String url = jsonObject.getString("url");
//                String images = jsonObject.getString("images");
//                String thumbnail = jsonObject1.getString("thumbnail");

            comments_lo = jsonObject.getString("long_comments");

            comments_sh = jsonObject.getString("short_comments");

            comment_1.setText(comments_sh + "条短评");

            comment_2.setText(comments_lo + "条长评");


//                Map map = new HashMap();
//
//                map.put("news_id",id);
//                map.put("check",1);
//                map.put("url",url);
//                map.put("images",images);
////                map.put("thumbnail",thumbnail);
//                map.put("title", title);
//
//                list.add(map);//检查到list了

            } catch (JSONException ex) {
            ex.printStackTrace();
        }

        runOnUiThread(new Runnable() {
                @Override
                public void run() {



                }
            });

        }
    }

