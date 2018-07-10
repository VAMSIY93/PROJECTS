import os
import sys
import re
import csv
import pandas as pd
import numpy as np
import pdb
import math
import copy
import time
from numpy import genfromtxt
from sklearn import svm
from sklearn import datasets
from sklearn.externals import joblib
import pickle
from sklearn.decomposition import PCA
from sklearn.metrics import accuracy_score



if __name__ == "__main__":

	start_time = time.time()

	train = genfromtxt('train.csv', delimiter=',')
	train_label = train[:,0]
	train = train[:,1:147]
	(trn,x) = train.shape

	test = genfromtxt('testfile.csv', delimiter=',')
	testID = test[:,0]
	test = test[:,1:147]

	validation1 = genfromtxt('validation_1.csv', delimiter=',')
	validation1_label = validation1[:,0]
	validation1 = validation1[:,1:147]
	(v1,x) = validation1.shape

	validation2 = genfromtxt('validation_2.csv', delimiter=',')
	validation2_label = validation2[:,0]
	validation2 = validation2[:,1:147]
	(v2,x) = validation2.shape

	validation3 = genfromtxt('validation_3.csv', delimiter=',')
	validation3_label = validation3[:,0]
	validation3 = validation3[:,1:147]
	(v3,x) = validation3.shape

	new_train1 = train[0:20000]
	new_train2 = train[20000:35000]
	new_train3 = train[35000:50000]

	train_label1 = train_label[0:20000]
	train_label2 = train_label[20000:35000]
	train_label3 = train_label[35000:50000]

	clf1 = svm.SVC(C=5,kernel='poly')
	clf1.fit(new_train1,train_label1)
	joblib.dump(clf1,'models-svmG/save1_split1')

	print 'Completed 1'
	clf2 = svm.SVC(C=5,kernel='poly')
	clf2.fit(new_train2,train_label2)
	joblib.dump(clf2,'models-svmG/save1_split2')

	print 'Completed 2'
	clf3 = svm.SVC(C=5,kernel='poly') 
	clf3.fit(new_train3,train_label3)
	joblib.dump(clf3,'models-svmG/save1_split3')

	print 'Calculating Accuracies'

	pred1_v3 = clf1.predict(validation3)
	pred2_v3 = clf2.predict(validation3)
	pred3_v3 = clf3.predict(validation3)

	pred_sum = pred1_v3 + pred2_v3 + pred3_v3
	final_prediction = pred_sum>1
	accuracy_v3 = accuracy_score(final_prediction,validation3_label)

	pred1_v1 = clf1.predict(validation1)
	pred2_v1 = clf2.predict(validation1)
	pred3_v1 = clf3.predict(validation1)

	pred_sum = pred1_v1 + pred2_v1 + pred3_v1
	final_prediction = pred_sum>1
	accuracy_v1 = accuracy_score(final_prediction,validation1_label)

	pred1_v2 = clf1.predict(validation2)
	pred2_v2 = clf2.predict(validation2)
	pred3_v2 = clf3.predict(validation2)

	pred_sum = pred1_v2 + pred2_v2 + pred3_v2
	final_prediction = pred_sum>1
	accuracy_v2 = accuracy_score(final_prediction,validation2_label)	

	pred1_v3 = clf1.predict(test)
	pred2_v3 = clf2.predict(test)
	pred3_v3 = clf3.predict(test)

	pred_sum = pred1_v3 + pred2_v3 + pred3_v3
	final_prediction = pred_sum>1

	fid = open('prediction5-split.csv','w')
	fid.write(str("ID,TARGET")+'\n')
	for i in range(len(final_prediction)):
		fid.write(str(int(testID[i]))+","+str(int(final_prediction[i]))+'\n');
	fid.close()

	print 'V1 Accuracy:',accuracy_v1
	print 'V2 Accuracy:',accuracy_v2
	print 'V3 Accuracy:',accuracy_v3

	end_time = time.time()
	print (end_time-start_time)



