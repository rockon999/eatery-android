package com.example.jc.eatery_android;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.CafeteriaModel;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    ImageView cafeImage;
    TextView cafeLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        ArrayList<CafeteriaModel> cafeList = null;
        String cafeName = (String) intent.getSerializableExtra("locName");
        cafeLoc = (TextView)findViewById(R.id.ind_name);
        cafeLoc.setText(cafeName);

        cafeName = "@drawable/" + convertName(cafeName);
        Log.i("TAG 4", cafeName);

        cafeList = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("testData");

        cafeImage = (ImageView) findViewById(R.id.ind_image);
        int imageRes = getResources().getIdentifier(cafeName, null, getPackageName());
        cafeImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
                imageRes, 400, 400));



    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String convertName(String str) {
        if (str.equals("104West!")) return "west";

        str = str.replaceAll("[&\']", "");
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("é", "e");
        str = str.toLowerCase();
        return str;
    }
}
