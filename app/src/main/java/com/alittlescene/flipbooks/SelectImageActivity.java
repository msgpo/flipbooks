package com.alittlescene.flipbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.alittlescene.flipbooks.R;

import java.io.File;
import java.io.InputStream;

public class SelectImageActivity extends ActionBarActivity {
    private final static int RESULT_SELECT_IMAGE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "SelectImageActivity";

    String photoPath;
    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);


        Bitmap img = null;

        try{
            //Pick Image From Gallery
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_SELECT_IMAGE);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RESULT_SELECT_IMAGE:

                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    try{
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        //return Image Path to the Main Activity
                        Intent returnFromGalleryIntent = new Intent();
                        returnFromGalleryIntent.putExtra("PATH_TO_PIC",picturePath);
                        setResult(RESULT_OK,returnFromGalleryIntent);
                        finish();
                    }catch(Exception e){
                        e.printStackTrace();
                        Intent returnFromGalleryIntent = new Intent();
                        setResult(RESULT_CANCELED, returnFromGalleryIntent);
                        finish();
                    }
                }else{
                    Log.i(TAG, "RESULT_CANCELED");
                    Intent returnFromGalleryIntent = new Intent();
                    setResult(RESULT_CANCELED, returnFromGalleryIntent);
                    finish();
                }
                break;
        }
    }

}
