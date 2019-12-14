package com.example.myapplication3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication3.Adapter.MyAdapter;
import com.example.myapplication3.Adapter.MyAdapter2;
import com.example.myapplication3.Util.SharedPreferencesUtil;
import com.example.myapplication3.Util.TimeThreadUtil;
import com.example.myapplication3.Util.TimeUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimeListener {
//, MyViewPager.OnViewPagerTouchListener, ViewPager.OnPageChangeListener

    private RecyclerView recyclerView;
    private SharedPreferencesUtil check;
    private ImageView imageView;
    private TextView mouth;
    private TextView day;
    private String getValue = null;
    private TimeUtil timeUtil;
    private String KEY;
    private static final String TAG = "MainActivity";

    int a = 0;
    //统计下载了几张图片
    int n = 0;
    //统计当前viewpager轮播到第几页
    int p = 0;
    private ViewPager vp;
    //准备好三张网络图片的地址
    private String imageUrl[];
    //装载下载图片的集合
    private List<ImageView> data;
    //控制图片是否开始轮播的开关,默认关的
    private boolean isStart = false;
    //开始图片轮播的线程
    private MyThread t;
    //存放代表viewpager播到第几张的小圆点
    private LinearLayout ll_tag;
    //存储小圆点的一维数组
    private ImageView tag[];
    private Handler mHandler;
    private  String image_show;
    private String sdate;
    private String sdate1;

//
//    static {
//
//
//        sPics.add(R.mipmap.pic1);
//        sPics.add(R.mipmap.pic2);
//        sPics.add(R.mipmap.pic3);
//    }

    //    private Handler mHandler;
    private boolean mIsTouch = false;
    private LinearLayout mPointContainer;

    List<Map<String, Object>> list = new ArrayList<>();
    private Handler handler;


    //    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
    }

    @Override
    protected void onStart() {
        super.onStart();


        initUI();



        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://news-at.zhihu.com/api/3/stories/latest");
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
                    showResponse3(response.toString());
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

//        mHandler = new Handler();

        imageView.setOnClickListener(this);

        timeUtil = TimeUtil.getInstance();
        new TimeThreadUtil(this).start();
//        getValue = sharedPreferences.getString(KEY,"");




        String account_id = check.getAccountId();
        MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(MainActivity.this);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

        Cursor cursor = database.query("user", new String[]{"name", "picture"}, "account_id = ?", new String[]{account_id}, null, null, null);
        if (cursor.moveToFirst()) {
//            name_show = cursor.getString(cursor.getColumnIndex("name"));
            final String str_picture = cursor.getString(cursor.getColumnIndex("picture"));
//            name.setText(name_show + "");

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (check.isPicture()) {
                        Bitmap bitmap = stringToBitmap(str_picture);
                        imageView.setImageBitmap(bitmap);
                    }
                }

            };

//        SharedPreferencesUtil sp;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0);


        }

    }
    public Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void initUI() {
        imageView = findViewById(R.id.to_load);
        mouth = findViewById(R.id.date_month);
        day = findViewById(R.id.date_day);
        recyclerView = findViewById(R.id.recyclerView);

        final RefreshLayout
                refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
//设置 Footer 为 球脉冲 样式
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        check = SharedPreferencesUtil.getInstance(getApplicationContext());


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                list.clear();
                onStart();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader reader = null;
                        try {

                            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
                            Date date=new Date();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_MONTH, a);
                            date = calendar.getTime();
                            sdate=(new SimpleDateFormat("yyyyMMdd")).format(date);


                            SimpleDateFormat sdf1=new SimpleDateFormat("MM月dd日");
                            Date date1=new Date();
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTime(date1);
                            calendar1.add(Calendar.DAY_OF_MONTH, a);
                            date1 = calendar1.getTime();
                            sdate1 =(new SimpleDateFormat("MM月dd日")).format(date1);


//                            str = getTime(a);
                            URL url = new URL("http://news.at.zhihu.com/api/1.2/stories/before/"+sdate);

                            a--;
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
                            re_showResponse(response.toString(),sdate1);


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



                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

    }

    private static String getTime(int x){
        Calendar  calendar =Calendar. getInstance();
        calendar.add( Calendar. DATE, x);
        Date date= calendar.getTime();
        return date.toString();
    }

    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

    @Override
    public void onGetDateTime() {
        mouth.setText(timeUtil.getCurrentDate_1());
        day.setText(timeUtil.getCurrentDate_2());
    }

    @Override
    public void onClick(View v) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (check.isLogin()) {
                    Intent intent = new Intent(MainActivity.this,MyselfActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }


            }

        };

//        SharedPreferencesUtil sp;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0);



        }

    private void showResponse2(final String string){
    try {
        JSONObject jsonObject = new JSONObject(string);
        JSONArray jsonArray = jsonObject.getJSONArray("top_stories");

        imageUrl = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                String id = jsonObject1.getString("id");
//                String url = jsonObject1.getString("url");
             image_show = jsonObject1.getString("image");
//                String thumbnail = jsonObject1.getString("thumbnail");
//            String title = jsonObject1.getString("title");


          imageUrl[i] = image_show;



//                Map map = new HashMap();

//                map.put("news_id", id);
//                map.put("url", url);
//                map.put("images", images);
//                map.put("thumbnail",thumbnail);
//                map.put("title", title);
//

//            list.add(map);//检查到list了
        }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                            init();

                }
            });

    } catch (JSONException e) {
        e.printStackTrace();
    }



}

    private void init() {

        mHandler=new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch(msg.what){
                    case 0:
                        n++;
                        Bitmap bitmap=(Bitmap) msg.obj;
                        ImageView iv=new ImageView(MainActivity.this);
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv.setImageBitmap(bitmap);
                        //把图片添加到集合里
                        data.add(iv);
                        //当接收到第三张图片的时候，设置适配器,
                        if(n==imageUrl.length){
                            vp.setAdapter(new MyAdapter2(data,MainActivity.this));
                            //创建小圆点
                            creatTag();
                            //把开关打开
                            isStart=true;
                            t=new MyThread();
                            //启动轮播图片线程
                            t.start();

                        }
                        break;
                    case 1:
                        //接受到的线程发过来的p数字
                        int page=(Integer) msg.obj;
                        vp.setCurrentItem(page);

                        break;

                }
            };
        };







        // TODO Auto-generated method stub
        vp=(ViewPager) findViewById(R.id.looper_pager);
        ll_tag=(LinearLayout) findViewById(R.id.points_container);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                //把当前的页数赋值给P
                p=position;
                //得到当前图片的索引,如果图片只有三张，那么只有0，1，2这三种情况
                int currentIndex=(position%imageUrl.length);
                for(int i=0;i<tag.length;i++){
                    if(i==currentIndex){
                        tag[i].setBackgroundResource(R.drawable.white_circle);
                    }else{
                        tag[i].setBackgroundResource(R.drawable.white_circle);
                    }
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //这个switch语句我注掉了，我觉得这个语句没有问题啊，可是为什么加上以下语句，当用手拉一次viewpager的时候，轮播就停止了，再也恢复不过来了?有人知道吗
                //switch(state){
                //当页面被手指拉动的时候，暂停轮播
                //case ViewPager.SCROLL_STATE_DRAGGING:
                //  isStart=false;
                //  break;
                //当手指拉完松开或者页面自己翻到下一页静止的时候,开始轮播
                //case ViewPager.SCROLL_STATE_IDLE:
                //  isStart=true;

                //  break;
                //
                //}
            }
        });
        //构造一个存储照片的集合
        data=new ArrayList<ImageView>();
        //从网络上把图片下载下来
        for(int i=0;i<imageUrl.length;i++){
            getImageFromNet(imageUrl[i]);

        }




    }

    private void getImageFromNet(final String imagePath) {
        // TODO Auto-generated method stub
        new Thread(){
            public void run() {
                try {
                    URL url=new URL(imagePath);
                    HttpURLConnection con=(HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(10*1000);
                    InputStream is=con.getInputStream();
                    //把流转换为bitmap
                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    Message message=new Message();
                    message.what=0;
                    message.obj=bitmap;
                    //把这个bitmap发送到hanlder那里去处理
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            };
        }.start();

    }
    //控制图片轮播
    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(isStart){
                Message message=new Message();
                message.what=1;
                message.obj=p;
                mHandler.sendMessage(message);
                try {
                    //睡眠3秒,在isStart为真的情况下，一直每隔三秒循环
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                p++;
            }
        }
    }

    protected void creatTag() {
        tag=new ImageView[imageUrl.length];
        for(int i=0;i<imageUrl.length;i++){

            tag[i]=new ImageView(MainActivity.this);
            //第一张图片画的小圆点是白点
            if(i==0){
                tag[i].setBackgroundResource(R.drawable.black_background);
            }else{
                //其它的画灰点
                tag[i].setBackgroundResource(R.drawable.white_circle);
            }
            //设置上下左右的间隔
            tag[i].setPadding(10, 10, 10, 10);
            tag[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //添加到viewpager底部的线性布局里面
            ll_tag.addView(tag[i]);
        }

    }

    private void re_showResponse(final String string,final String date) {

    try {
        JSONObject jsonObject = new JSONObject(string);
        JSONArray jsonArray = jsonObject.getJSONArray("news");
        for (int i = -1; i < jsonArray.length(); i++) {

            if (i == -1){
                Map map1 = new HashMap();
                map1.put("date",date);
                map1.put("check","0");
                list.add(map1);//检查到list了
            }

            else {

            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String id = jsonObject1.getString("id");
            String url = jsonObject1.getString("url");
            String images = jsonObject1.getString("image");
            String hint = jsonObject1.getString("hint");
            String title = jsonObject1.getString("title");


            Map map = new HashMap();



                map.put("check",1);
                map.put("news_id",id);
                map.put("url",url);
                map.put("images",images);
                map.put("hint",hint);
                map.put("title", title);
                list.add(map);
            }



        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));//垂直排列
                recyclerView.setAdapter(new MyAdapter(MainActivity.this, list));//绑定适配器
            }
        });

    } catch (JSONException e) {
        e.printStackTrace();
    }


}

    private void showResponse3(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String url = jsonObject1.getString("url");
                String images = jsonObject1.getString("images");
                String hint = jsonObject1.getString("hint");
                String title = jsonObject1.getString("title");


                Map map = new HashMap();

                map.put("news_id",id);
                map.put("check",1);
                map.put("url",url);
                map.put("images",images);
                map.put("hint",hint);
                map.put("title", title);

                list.add(map);//检查到list了
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));//垂直排列
                    recyclerView.setAdapter(new MyAdapter(MainActivity.this, list));//绑定适配器

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


