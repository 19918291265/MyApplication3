package com.example.myapplication3;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PopupWindow extends AppCompatActivity implements View.OnClickListener {

    private TextView camera;
    private TextView album;
    private TextView cancel;
    private String aByte = null;
    public static final int CHOOSE_PHOTO = 2;
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String account_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pop_up);

        Intent intent = getIntent();
        account_id = intent.getStringExtra("id");

        initUI();

        camera.setOnClickListener(this);
        album.setOnClickListener(this);
        cancel.setOnClickListener(this);


    }

    private void initUI() {
        camera = findViewById(R.id.open_camera);
        album = findViewById(R.id.open_album);
        cancel = findViewById(R.id.cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_camera:
            {
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(PopupWindow.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                }
                else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);



                MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(PopupWindow.this);
                SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

                ContentValues values = new ContentValues();
                values.put("picture",aByte);
                database.update("user", values, "account_id=?", new String[]{account_id});
                database.close();

                Toast.makeText(PopupWindow.this, "上传成功！", Toast.LENGTH_SHORT).show();

                finish();
                break;
            }





            case R.id.open_album:

                if (ContextCompat.checkSelfPermission(PopupWindow.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PopupWindow.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();

                }

                    MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(PopupWindow.this);
                    SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("picture",aByte);
                    database.update("user", values, "account_id=?", new String[]{account_id});
                    database.close();
                Toast.makeText(PopupWindow.this, "上传成功！", Toast.LENGTH_SHORT).show();

                finish();
                break;


            case R.id.cancel:
                finish();
                break;
            default:
                break;
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast.makeText(OpenShopActivity.this,"onActivityResult1",Toast.LENGTH_SHORT).show();
//        Toast.makeText(RegisterActivity.this,String.valueOf(requestCode),Toast.LENGTH_SHORT).show();
        Log.e("TAG", String.valueOf(requestCode));
        switch (requestCode){
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT>= 19){
                        handleImageOnKitKat(data);
                    }
                    else {
                        handleImageBoforeKitKat(data);
                    }
                }
                break;

            case TAKE_PHOTO:{
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        aByte = bitmapToString(bitmap);

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
                }
                else {
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
            aByte = bitmapToString(bitmap);


        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }

    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imgBytes = baos.toByteArray();
        return imgBytes;
    }

    public String bitmapToString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string= Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

}
