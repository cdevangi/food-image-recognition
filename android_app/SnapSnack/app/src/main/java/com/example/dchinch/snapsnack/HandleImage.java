package com.example.dchinch.snapsnack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.util.Vector;

/**
 * Created by dchinch on 4/2/18.
 */

public class HandleImage extends Activity {

    ImageView showImage;
    Button predict;
    ClassifyImage ci =new ClassifyImage();
    Uri uri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bitmap recvImage = null;
        System.out.println("Activity started");

        showImage = findViewById(R.id.img);
        predict = findViewById(R.id.predict);

        Intent i = getIntent();     // get intent from “MainActivity”
        uri = i.getParcelableExtra("imageUri");   // get file uri from intent

        try {
            recvImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

            /* resize the bitmap to size 150 * 150 which is the input size for our model */
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(recvImage, 150,150,true);
            /* call the Classify function by passing the input scaled image */
            ci.Classify(getAssets(),scaledBitmap);


        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        predict.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                float [] outputs = ci.outputs;
                Vector <String> labels = ci.labels;

                String [] larray = labels.toArray(new String[5]);

                /* Pass the results of classification to the next activity using intent */
                Intent i = new Intent(HandleImage.this, Display2.class);
                i.putExtra("predictions",ci.outputs);
                i.putExtra("labels",ci.labels.toArray(new String[5]));
                i.putExtra("uri",uri);
                startActivity(i);

            }});

        //resize image to display
        Bitmap resized = Bitmap.createScaledBitmap(recvImage, 850, 1050, true);
        showImage.setImageBitmap(resized);
    }
}
