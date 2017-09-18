package com.shynar.photo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    Bitmap image;
    int REQUEST_CODE = 1;
    int RESULT_STORE_FILE = 2;
    public File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        imageFile = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        ImageView imOpen = (ImageView) findViewById(R.id.imageView);
        imOpen.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_STORE_FILE);
            }
        });
    }

    public void MakePhoto(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri tempuri = Uri.fromFile(imageFile);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, tempuri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void OpenPhoto(){

    }

    Bitmap resizePhoto(File f){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        image = BitmapFactory.decodeFile(f.toString(), options);


        int maxWidth = 720;
        if (options.outWidth > maxWidth){
            options.inSampleSize = options.outWidth / maxWidth;
        }
        options.inJustDecodeBounds = false;

        Bitmap bmp = BitmapFactory.decodeFile(f.toString(), options);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        File file=null;
        if (requestCode == RESULT_STORE_FILE){
            if (Activity.RESULT_OK==-1) {
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                file = new File(cursor.getString(idx));

            }
        }
        if (requestCode == REQUEST_CODE) {
            if (Activity.RESULT_OK == -1) {
                if (imageFile.exists()) {

                    file = imageFile;

                }
            }
        }
        if (file!=null){
            image = resizePhoto(file);
            String endStr   = file.toString().substring(file.toString().lastIndexOf('/')+1);
            File picFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), endStr);
            if (picFile !=null) {
                try {
                    FileOutputStream fos = new FileOutputStream(picFile);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    image.compress(Bitmap.CompressFormat.PNG,1,fos);

                } catch (FileNotFoundException e){
                } catch (Exception e) {

                }
            } else{
                Toast.makeText(this,"ne sushetvuet", Toast.LENGTH_SHORT).show();
            }

        }

    }

}

