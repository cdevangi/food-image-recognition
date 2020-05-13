package com.example.dchinch.snapsnack;

import android.graphics.Bitmap;
import android.content.res.AssetManager;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Vector;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * Created by dchinch on 3/19/18.
 */

public class ClassifyImage extends MainActivity {

  /*
*	Meaning of the variables and constants used in this program:
*
*	labels – stores the class labels i.e. the food item names
*	outputs – probabilities of each food item
*	INPUT_SIZE – size of the input to the DCNN
*	INPUT_NAME – input node name in the frozen graph of the model
*	OUTPUT_NAME – output node name in the frozen graph model
*	MODEL_FILE – name of the file storing the frozen model
*	LABEL_FILE – name of the file storing class labels
*/

    public Vector<String> labels = new Vector<String>();
    public float[] outputs;
    protected static final int INPUT_SIZE = 150;               //same as used when training model
    protected static final String INPUT_NAME = "input/X";
    protected static final String OUTPUT_NAME = "FullyConnected_1/Softmax";
    protected static final String MODEL_FILE = "file:///android_asset/frozen_model_food.pb";
    protected static final String LABEL_FILE = "file:///android_asset/labels.txt";


    public void Classify(AssetManager am,Bitmap bitmap) {
        String actualFilename = LABEL_FILE.split("file:///android_asset/")[1];
        System.out.println(actualFilename);
        BufferedReader br = null;
        AssetManager assetManager = am;

        try {

            br = new BufferedReader(new InputStreamReader(assetManager.open(actualFilename)));
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
                System.out.println("Reading lables from label file");
            }
            br.close();

        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!", e);
        }


        TensorFlowInferenceInterface inferenceInterface;

        String model = MODEL_FILE;

        //create a new inference interface for the model ‘model’ passed as a parameter
        inferenceInterface = new TensorFlowInferenceInterface(assetManager, model);

        System.out.println("Inference Interface object created");

        final int numClasses = 5;

        String [] outputNames = new String[]{OUTPUT_NAME};
        int [] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        float [] floatValues = new float[INPUT_SIZE * INPUT_SIZE * 3];
        outputs = new float[numClasses];

        //get the input bitmap values in the array ‘intValues’
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        //convert the packed pixel values back to R,G and B components
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3 + 0] = (val & 0xFF);
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF);
            floatValues[i * 3 + 2] = (val >> 16) & 0xFF;
        }

        //feed the inference interface with input node name 'INPUT_NAME' and our input image as 150 * 150 * 3 matrix
        inferenceInterface.feed(INPUT_NAME, floatValues, 1, INPUT_SIZE, INPUT_SIZE, 3);

        //run the inference interface for the given output node names
        inferenceInterface.run(outputNames);

        //fetch the output from output node 'OUTPUT_NAME' in the array 'outputs'
        inferenceInterface.fetch(OUTPUT_NAME, outputs);


        /*
        Code for sorting the output and label vectors in descending order based on output values
        */

        for(int i=0;i < outputs.length; i++)
        {
            for(int j=1; j < outputs.length - i; j++)
            {
                if(outputs[j-1] < outputs[j])
                {
                    //swapping output values
                    float temp;
                    temp = outputs[j-1];
                    outputs[j-1] = outputs[j];
                    outputs[j] = temp;

                    //swapping corresponding labels
                    String ltemp;
                    ltemp = labels.get(j-1);
                    labels.set(j-1, labels.get(j));
                    labels.set(j, ltemp);
                }

            }
        }

    }
}
