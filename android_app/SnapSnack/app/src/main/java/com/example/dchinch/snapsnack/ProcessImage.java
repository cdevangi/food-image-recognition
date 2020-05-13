package com.example.dchinch.snapsnack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileNotFoundException;

/**
 * Created by dchinch on 4/2/18.
 */

public class ProcessImage{

    public int[] histogram(int[] y)
    {
        int [] pixNum = new int [256];
        int size = y.length;

        for(int c=0; c< 256; c++)
        {
            int sum = 0;
            for(int m =0; m<size; m++)
            {
                if (y[m] == c)
                    sum++;
            }

            pixNum[c] = sum;
        }

        return  pixNum;
    }

    public int[] getCDF(int hist[])
    {
        int [] cdf = new int[256];
        int cum = 0;

        for(int c =0 ;c<256;c++)
        {
            cum+=hist[c];
            cdf[c] = cum;
        }

        return  cdf;
    }

    public int getMinCDF(int[] cdf)
    {
        int minCDF = 257;
        for(int c=0; c<256;c++)
        {
            if(cdf[c] < minCDF && cdf[c]!=0)
                minCDF = cdf[c];
        }

        return minCDF;
    }

    public int getMaxCDF(int[] cdf)
    {
        int maxCDF = 0;
        for(int c=0; c<256;c++)
        {
            if(cdf[c] > maxCDF)
                maxCDF = cdf[c];
        }

        return maxCDF;
    }

    public float[] equalize(int [] cdf, int size)
    {
        int min = getMinCDF(cdf);
        float e[] = new float[256];

        for(int c =0 ; c<256; c++)
        {
            e[c] = (float) ((((float) cdf[c] - min) / (float) size) * 255);
        }

        for(int c = 0; c<256; c++)
        {
            if (e[c] < 0)
                e[c] = 0;
            if (e[c] > 255)
                e[c] = 255;
        }

        return  e;
    }


    public Bitmap histogramEqualize(Bitmap image) {

        Bitmap img = Bitmap.createScaledBitmap(image,150,150,true);
        int SIZE = 150 * 150;

        int [] intValues = new int[SIZE];
        int [] r = new int[SIZE];
        int [] g = new int[SIZE];
        int [] b = new int[SIZE];
        int [] alpha = new int[SIZE];

        int [] y = new int[SIZE];
        int [] cb = new int[SIZE];
        int [] cr = new int[SIZE];

        img.getPixels(intValues, 0, 150, 0, 0, 150, 150);

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];

            b[i] = (val & 0xFF);
            g[i] = ((val >> 8) & 0xFF);
            r[i] = (val >> 16) & 0xFF;
            alpha[i] = (val >> 24) & 0xFF;


            y[i] = (int)(0.299*r[i] + 0.587 * g[i] + 0.114 * b[i]);
            cb[i] = (int) (128 - 0.169 * r[i] - 0.331 * g[i] + 0.500 * b[i]);
            cr[i] = (int) (128 + 0.500 * r[i] - 0.419 * g[i] - 0.081 * b[i]);
        }

        int [] histogram = new int[256];
        histogram = histogram(y);

        int [] cdf = new int[256];
        cdf = getCDF(histogram);

        float [] ecdf = new float[256];
        ecdf = equalize(cdf, SIZE);

        int [] newr = new int[SIZE];
        int [] newg = new int[SIZE];
        int [] newb = new int[SIZE];

        float [] newy = new float[SIZE];

        int counter=0;
        for(int c=0; c < SIZE; c++ )
        {
            newy[counter] = ecdf[(int) y[counter]];
            counter++;
        }
         //Calculate new rgb values from new y and old cb, cr

        for (int c=0; c < SIZE; c++ )
        {
            newr[c] = (int) (newy[c] + 1.403 * (cr[c] - 128));
            newg[c] = (int) (newy[c] - 0.344 * (cb[c] - 128)- 0.714 * (cr[c] - 128) );
            newb[c] = (int) (newy[c] + 1.773 * (cb[c] - 128));
        }

        Bitmap newImg = Bitmap.createBitmap(150,150, Bitmap.Config.ARGB_8888);

        int [] newIntVals = new int[SIZE];

        for(int c =0; c < intValues.length; c++)
        {
            newIntVals[c] = alpha[c] << 24 | newr[c] << 16 | newg[c] << 8 | newb[c] ;

        }

        newImg.setPixels(newIntVals,0, 150, 0, 0, 150, 150);

        return newImg;

    }

}
