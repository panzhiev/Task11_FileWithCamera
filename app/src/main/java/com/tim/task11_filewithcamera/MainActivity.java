package com.tim.task11_filewithcamera;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;//задаем REQUEST CODE для того чтобы распознать какой интент нам вернется
    private Button btnSelect;
    private ImageView ivImage;
    private String userChooserTask;
    public String TAG = "MY_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelect = (Button) findViewById(R.id.btn_select_photo);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.iv_image);
    }

    private void selectImage() {
        final CharSequence[] items = { "Take photo", "Choose From Library", "Cancel"}; //создаем массив айтемов для выбора необходимого

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add photo!");//задаем название диалога
        builder.setItems(items, new DialogInterface.OnClickListener() {//задаем диалогу наши items и вешаем на них слушателя
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(items[item].equals("Take photo"))//сравниваем какой item нам пришел
                {
                    userChooserTask = "Take Photo";
                    cameraIntent();//вызываем метод, который обрабатывает интент камеры
                }else if (items[item].equals("Choose From Library")){
                    galleryIntent();//вызываем метод, который обрабатывает интент галереи
                } else if (items[item].equals("Cancel"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");//задаем тип файлов, которые может принимать интент(все файлы image)
        intent.setAction(Intent.ACTION_GET_CONTENT);//интент для получение файлов выборка по image/*
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)//если действие с вызванной активности прошло успешно и интент возвращает нам результат
        {
            if (requestCode == SELECT_FILE)//определем чему равен пришедший requestCode
            {
                onSelectFromGalleryResult(data);//если равен 1, тогда запускаем этот метод и передаем ему intent
                Log.d(TAG, "onSelectFromGalleryResult workiing");
            }else if (requestCode == REQUEST_CAMERA)
            {
                onCameraImageResult(data);
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data){

        Bitmap bm = null;
        if (data != null){
            Log.d(TAG, "data != null");
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                // получаем системный контент провайдер и передаем ему uri - нашего интента
                Log.d(TAG,"" + data.getData());
            } catch (IOException e){
                Log.d(TAG,"" + "EXCEPTION!!!");
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
        Log.d(TAG, "Image should be in view from Gallery");
    }

    private void onCameraImageResult(Intent data)
    {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File (Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        ivImage.setImageBitmap(bitmap);
        Log.d(TAG, "Image should be in view from camera");
    }
}
