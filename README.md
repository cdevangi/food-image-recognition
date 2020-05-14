# Food Image Recognition using Deep Learning

The project helps you classify food images using Deep Convolutional Neural Networks.

## Getting Started

The project essentially has two types of code:
  1.  **build-model.py** trains a DCNN model using your training data and freezes it to be used in the android code.
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
 
    1. Trains DCNN using training data
    2. Freezes the trained model for use in Android app
    3. Tests the model
 
