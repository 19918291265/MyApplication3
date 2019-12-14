package com.example.myapplication3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication3.Util.SharedPreferencesUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyselfActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout collect;
    private RelativeLayout change;
    private RelativeLayout safe;
    private RelativeLayout exit;

    private TextView name;
    private ImageView picture_show;
    private ImageButton mback;
    private String account_id;
    private Bitmap bitmap;
    private EditText edit;
    private Dialog mCameraDialog;
    private String name_show;
    private String str_picture;
    private String aByte;
    public static final int CHOOSE_PHOTO = 2;
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private SharedPreferencesUtil check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_myself);
//        if (!check.isLogin()) {
//            Intent intent = getIntent();
//            account_id = intent.getStringExtra("id");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initUI();

        collect.setOnClickListener(this);
        change.setOnClickListener(this);
        safe.setOnClickListener(this);
        exit.setOnClickListener(this);
        picture_show.setOnClickListener(this);
        mback.setOnClickListener(this);


        MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(MyselfActivity.this);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

        Cursor cursor = database.query("user", new String[]{"name", "picture"}, "account_id = ?", new String[]{account_id}, null, null, null);
        if (cursor.moveToFirst()) {
            name_show = cursor.getString(cursor.getColumnIndex("name"));
            str_picture = cursor.getString(cursor.getColumnIndex("picture"));
            name.setText(name_show + "");












            final Handler handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (check.isPicture()) {
                        bitmap = stringToBitmap(str_picture);
                        picture_show.setImageBitmap(bitmap);
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


    private void initUI() {
        change = findViewById(R.id.change);
        collect = findViewById(R.id.collect);
        safe = findViewById(R.id.safe);
        exit = findViewById(R.id.exit);
        name = findViewById(R.id.name);
        picture_show = findViewById(R.id.picture);
        mback = findViewById(R.id.back);
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        account_id  = check.getAccountId();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect:
                Intent intent_1 = new Intent(MyselfActivity.this, CollectActivity.class);
//                intent_1.putExtra("id", account_id);
                startActivity(intent_1);
                break;
            case R.id.change:

                LayoutInflater factory = LayoutInflater.from(MyselfActivity.this);//提示框
                final View view = factory.inflate(R.layout.layout_change, null);//这里必须是final的
                edit = view.findViewById(R.id.name);//获得输入框对象

                new AlertDialog.Builder(MyselfActivity.this)
                        .setTitle("请输入新的昵称")//提示框标题
                        .setView(view)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //事件
                                        if (edit.getText().toString().length() == 0) {
                                            Toast.makeText(MyselfActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else if (edit.getText().toString().length() > 8) {
                                            Toast.makeText(MyselfActivity.this, "昵称最多输入8个字符！", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(MyselfActivity.this);
                                            SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
                                            ContentValues values = new ContentValues();
                                            values.put("name", edit.getText().toString());//第一个"name" 是字段名字  第二个是对应字段的数据
                                            database.update("user", values, "account_id=?", new String[]{account_id});
                                            database.close();
                                            onResume();
                                        }


                                    }
                                }).setNegativeButton("取消", null).create().show();

                break;

            case R.id.safe:
                Intent intent_3 = new Intent(MyselfActivity.this, SafeActivity.class);
                intent_3.putExtra("id", account_id);
                startActivity(intent_3);
                break;

            case R.id.exit:
                check.setLogin(false);
                Intent intent_4 = new Intent(MyselfActivity.this, LoginActivity.class);
                startActivity(intent_4);

                break;

            case R.id.picture:
                setDialog();
                break;


            case R.id.open_album:

                if (ContextCompat.checkSelfPermission(MyselfActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyselfActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }

                Toast.makeText(this, aByte + "", Toast.LENGTH_SHORT).show();
                break;

            case R.id.open_camera:
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(MyselfActivity.this, "com.example.cameraalbumtest.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);

                break;


            case R.id.cancel:
                mCameraDialog.cancel();
                break;
            case R.id.back:

                Intent intent_5 = new Intent(MyselfActivity.this, MainActivity.class);
                startActivity(intent_5);
        }
    }

    public void alert_edit(View view) {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入消息")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        Toast.makeText(getApplicationContext(), et.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消", null).show();
    }


    private void setDialog() {
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_pop_up, null);
        //初始化视图
        root.findViewById(R.id.open_album).setOnClickListener(this);
        root.findViewById(R.id.open_camera).setOnClickListener(this);
        root.findViewById(R.id.cancel).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.DialogAnimation); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast.makeText(OpenShopActivity.this,"onActivityResult1",Toast.LENGTH_SHORT).show();
//        Toast.makeText(RegisterActivity.this,String.valueOf(requestCode),Toast.LENGTH_SHORT).show();

        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBoforeKitKat(data);
                    }
                }
                break;

            case TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                        Bitmap bitmap1 = compressImage(bitmap);

                        aByte = bitmapToString(bitmap1);

                        MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(MyselfActivity.this);
                        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("picture", aByte);//第一个"name" 是字段名字  第二个是对应字段的数据
                        database.update("user", values, "account_id=?", new String[]{account_id});
                        check.setPicture(true);
                        database.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            default:
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBoforeKitKat(Intent data) {
        Uri uri = data.getData();
        String iamgePath = getImagePath(uri, null);
        displayImage(iamgePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {

        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            Bitmap bitmap1 = compressImage(bitmap);

            aByte = bitmapToString(bitmap1);

            MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(MyselfActivity.this);
            SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("picture", aByte);//第一个"name" 是字段名字  第二个是对应字段的数据
            database.update("user", values, "account_id=?", new String[]{account_id});
            check.setPicture(true);
            database.close();

        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }

    }

    public String bitmapToString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
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
    //对bitmap进行质量压缩
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}