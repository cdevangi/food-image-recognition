# Food Image Recognition using Deep Learning

The project helps you classify food images using Deep Convolutional Neural Networks.

## Getting Started

The project essentially has two types of code:
  1.  **build-model.py** trains a DCNN model and freezes(saves) it to be used in the android code.
  2.  **android_app/SnapSnack** is an android application that recognizes the food item when the user either clicks an image or uploads one.
  
  For building the model and just a command-line usage, **build-model.py** should be enough.
  
## Prerequisites

1.  [Anaconda](https://www.anaconda.com/products/individual) 

     Once installed, open the anaconda command prompt and create a tensorflow environment
     
     ```
     conda create -n tensorflow_env tensorflow
     conda activate tensorflow_env
     ```
     
     TensorFlow is now installed and ready for use.
     
     For more help with TensorFlow in Anaconda, click [here](https://www.anaconda.com/blog/tensorflow-in-anaconda).
     
 ## Understanding the Code
 
 ### build-model.py
 
 This is a Python code that does the following:
 
  1. **Prepares image data for training**  
     Performs Histogram Equilization and White Balance on the input image data
     The model performs better on preprocessed images as compared to raw images.  
       
  2. **Trains a DCNN using training data**  
     The number of the layers in the DCNN can vary depending on various factors like size, quality and variety of training data. The best way is when you try and tweak to find the optimal number.  
     
  3. **Freezes the trained model for use in Android app**  
     Freezing is simply saving the model. And what does a model comprise of? The variables and the weights along each connection when we finalize on the model which can then be directly used in applications without having to train the model in that application. This makes the target application extremely light weight
     
  4. **Tests the model**  
    
  See more about freezing models [here](https://medium.com/@prasadpal107/saving-freezing-optimizing-for-inference-restoring-of-tensorflow-models-b4146deb21b5).
 
