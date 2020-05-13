package com.example.dchinch.snapsnack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class Display2 extends AppCompatActivity {


    int num_labels = 5
    ImageView img;
    Uri uri;
    CollapsingToolbarLayout ctl;
    TextView prob,recipe;
    FloatingActionButton fab;
    float [] outputs = new float [num_labels];
    String [] labels = new String[num_labels];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        outputs = getIntent().getFloatArrayExtra("predictions");
        labels = getIntent().getStringArrayExtra("labels");
        uri = getIntent().getParcelableExtra("uri");

        img = findViewById(R.id.expandedImage);
        try {
            Bitmap recvImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            img.setImageBitmap(recvImage);

        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //collapsing toolbar
        ctl = findViewById(R.id.toolbar_layout);

        String disp = labels[0] + " ("+Math.floor(outputs[0]*10000)/100+ "%)";
        ctl.setTitle(disp);

        prob = findViewById(R.id.prob);
        recipe = findViewById(R.id.recipe);

        String probs = "";
        // for displaying remaining probabilities in %
        for(int j=1; j<num_labels - 1;j++)
        {
            probs += labels[j]+": "+String.format("%.6f",outputs[j]*100)+" %\n";
        }
        probs += labels[num_labels - 1]+": "+String.format("%.6f",outputs[num_labels-1]*100)+" %\n";
        prob.setText(probs);

        switch(labels[0])
        {

          case "Vada Pav":
            mp = MediaPlayer.create(Display2.this, R.raw.vada_pav); recipe.setText(R.string.vada_pav);
            break;
          case "Misal Pav":
            mp = MediaPlayer.create(Display2.this, R.raw.misal_pav); recipe.setText(R.string.misal_pav);
            break;
          case "Pohe":
            mp = MediaPlayer.create(Display2.this, R.raw.pohe);
            recipe.setText(R.string.pohe);
            break;
          case "Sabudana Vada":
            mp = MediaPlayer.create(Display2.this, R.raw.sabudana_vada);
            recipe.setText(R.string.sabu_wada);
            break;
          case "Modak":
            mp = MediaPlayer.create(Display2.this, R.raw.modak);
            recipe.setText(R.string.modak);
            break;
          }

          fab = findViewById(R.id.fab);

          //floating action button for audio
          fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
              mp.start();
            }});
    }
}
