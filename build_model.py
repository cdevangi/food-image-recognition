import cv2
import numpy as np
import os
from tqdm import tqdm

DIR = '/home/group5/Group5/' #location of input data set
PROCESSED_DIR = DIR + 'Processed'

imageTypes = ['TrainData','ValidData','TestData']

def process_images(folder):
    Dir = os.path.join(DIR,folder)
    for filename in tqdm(os.listdir(Dir)):
        img = cv2.imread(os.path.join(Dir,filename))

        b,g,r = cv2.split(img)

        #Perform White balance
        def wb(channel, perc):
            mi, ma = (np.percentile(channel, perc), np.percentile(channel,100.0-perc))
            channel = np.uint8(np.clip((channel-mi)*255.0/(ma-mi), 0, 230))
            return channel

        imgWB = np.dstack([wb(channel, 2) for channel in (b, g, r)] )
        #white balance done

        #Perform Histogram Equilization
        ycbcr = cv2.cvtColor(imgWB,cv2.COLOR_BGR2YCrCb )
        y ,cb, cr = cv2.split(ycbcr)
        y1 = cv2.equalizeHist(y)
        ycbcrE = cv2.merge((y1,cb,cr))
        imgE = cv2.cvtColor(ycbcrE,cv2.COLOR_YCrCb2BGR )

        new = os.path.join(NEW,folder)
        newf = os.path.join(new,filename)
        #histogram equalization done

        cv2.imwrite(newf,imgE)

for imageType in imageTypes:
    process_images(i)

import cv2
import numpy as np
import os
from random import shuffle
from tqdm import tqdm

TRAIN_DIR = PROCESSED_DIR + 'TrainData'
VALID_DIR = PROCESSED_DIR + 'ValidData'
TEST_DIR = PROCESSED_DIR + 'TestData'
IMG_SIZE = 150
LR = 1e-3

MODEL_NAME = 'food_recognition_-{}-{}.model'.format(LR, 'conv')

food_items = ['vadapav', 'misalpav', 'sabudanawada', 'modak', 'pohe']

def label_img(img):
    #Image name example: 'vadapav.4.jpg'
    label = img.split('.')[-3]
    label_array = [0 for in range(len(food_items))]

    try: label_array[food_items.index(label)] = 1
    except ValueError: print("") #do nothing

    return label_array

def create_train_data():
    training_data = []
    for img in tqdm(os.listdir(TRAIN_DIR)):
        label = label_img(img)
        path = os.path.join(TRAIN_DIR, img)
        img = cv2.resize(cv2.imread(path), (IMG_SIZE, IMG_SIZE))
        training_data.append([np.array(img), np.array(label)])
    shuffle(training_data)
    np.save('trainData.npy', training_data)
    return training_data

def create_valid_data():
    validation_data = []
    for img in tqdm(os.listdir(VALID_DIR)):
        label = label_img(img)
        path = os.path.join(VALID_DIR, img)
        img = cv2.resize(cv2.imread(path), (IMG_SIZE, IMG_SIZE))
        validation_data.append([np.array(img), np.array(label)])
    shuffle(validation_data)
    np.save('validData.npy', validation_data)
    return validation_data

def process_test_data():
    testing_data = []
    for img in tqdm(os.listdir(TEST_DIR)):
        path = os.path.join(TEST_DIR, img)
        name = img.split('.')[-3]

        try: img_num = food_items.index(name) + 1
        except ValueError: img_num = -1

        img = cv2.resize(cv2.imread(path), (IMG_SIZE, IMG_SIZE))
        testing_data.append([np.array(img), img_num])
    np.save('testData.npy', testing_data)
    return testing_data

train_data = create_train_data()
valid_data = create_valid_data()

import tflearn
import os
from tflearn.layers.conv import conv_2d, max_pool_2d
from tflearn.layers.core import input_data, dropout, fully_connected
from tflearn.layers.estimator import regression
import tensorflow as tf

tf.reset_default_graph()
filters = [32, 64]
NUM_LAYERS = 8

convnet = input_data(shape=[None, IMG_SIZE, IMG_SIZE, 3], name='input')

for layer in range(NUM_LAYERS):
    convnet = conv_2d(convnet, filters[layer % 2], 2, activation='relu')
    convnet = max_pool_2d(convnet, 2)

convnet = fully_connected(convnet, 1024, activation='relu')
convnet = dropout(convnet, 0.8)

convnet = fully_connected(convnet, 5, activation='softmax')
convnet = regression(convnet, optimizer='adam', learning_rate=LR, loss='categorical_crossentropy', name='targets')

sess=tf.Session()

model = tflearn.DNN(convnet, tensorboard_dir='log', session=sess)
if os.path.exists('{}.meta'.format(MODEL_NAME)):
    model.load(MODEL_NAME)
    print('model loaded!')

train = train_data
test = valid_data
X = np.array([i[0] for i in train]).reshape(-1, IMG_SIZE, IMG_SIZE, 3)
Y = [i[1] for i in train]

test_x = np.array([i[0] for i in test]).reshape(-1, IMG_SIZE, IMG_SIZE, 3)
test_y = [i[1] for i in test]


model.fit({'input': X}, {'targets': Y}, n_epoch=45, validation_set=({'input': test_x}, {'targets': test_y}),
    snapshot_step=500, show_metric=True, run_id=MODEL_NAME)

model.save(MODEL_NAME)

saver = tf.train.Saver()
model_directory='model_files/'
if not os.path.exists(model_directory):
        os.makedirs(model_directory)
#saving the graph
tf.train.write_graph(sess.graph_def, model_directory, 'savegraph.pbtxt')

saver.save(sess, 'model_files/model.ckpt')

'''
Freeze graph using command line:

python /home/group5/anaconda3/envs/tensorflow/lib/python3.6/site-packages/tensorflow/python/tools/freeze_graph.py --input_graph /home/group5/Group5/model_files/savegraph.pbtxt \ --input_checkpoint /home/group5/Group5/model_files/model.ckpt \ --output_graph /home/group5/Group5/model_files/frozen_model_food.pb \ --output_node_names FullyConnected_1/Softmax

(run in the tensorflow environment)
'''

from tensorflow.python.tools import freeze_graph
# library for optmising inference
from tensorflow.python.tools import optimize_for_inference_lib

output_graph_def = optimize_for_inference_lib.optimize_for_inference(
        sess.graph_def,
        ["input/X"], # an array of the input node(s)
        ["FullyConnected_1/Softmax"], # an array of output nodes
        tf.float32.as_datatype_enum)

output_optimized_graph_name = 'model_files/optimized_inference_model_'+MODEL_NAME+'.pb'

with tf.gfile.GFile(output_optimized_graph_name, "wb") as f:
            f.write(output_graph_def.SerializeToString())
g = tf.GraphDef()
##checking frozen graph
g.ParseFromString(open(output_optimized_graph_name, 'rb').read())

g1==g #shoud be false

import matplotlib.pyplot as plt

test_data = process_test_data()

truePositives=0
fig = plt.figure()

for num, data in enumerate(test_data):
    img_num = data[1]
    img_data = data[0]

    y = fig.add_subplot(5, 6, num + 1)
    orig = img_data
    data = img_data.reshape(IMG_SIZE, IMG_SIZE, 3)

    model_out = np.argmax(model.predict([data])[0])

    if img_num == (model_out + 1) : truePositives += 1
    plot_label = food_items[model_out]

    y.imshow(orig)
    plt.title(str_label)
    y.axes.get_xaxis().set_visible(False)
    y.axes.get_yaxis().set_visible(False)

plt.show()

accuracy = truePositives/len(test_data)

print ("True positives: " + str(truePositives))
print ("Accuracy: " + str(accuracy))
