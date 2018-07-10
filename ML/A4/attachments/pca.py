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



if __name__ == "__main__":

	start_time = time.time()

	train = genfromtxt('train.csv', delimiter=',')
	train_label = train[:,0]
	train = train[:,1:147]

	test = genfromtxt('testfile.csv', delimiter=',')
	test = test[:,1:147]

	validation1 = genfromtxt('validation_1.csv', delimiter=',')
	validation1_label = validation1[:,0]
	validation1 = validation1[:,1:147]

	validation2 = genfromtxt('validation_2.csv', delimiter=',')
	validation2_label = validation2[:,0]
	validation2 = validation2[:,1:147]

	validation3 = genfromtxt('validation_3.csv', delimiter=',')
	validation3_label = validation3[:,0]
	validation3 = validation3[:,1:147]

	pca = PCA(n_components=50)
	pca_train = pca.fit_transform(train)
	pca_test = pca.fit_transform(test)

	pca_valid1 = pca.fit_transform(validation1)
	pca_valid2 = pca.fit_transform(validation2)
	pca_valid3 = pca.fit_transform(validation3)

	clf = svm.SVC(C=1.0, class_weight=None, coef0=0.0, degree=5, gamma='auto', kernel='rbf',
    max_iter=-1, probability=False, random_state=None, shrinking=True,
    tol=0.001, verbose=False)

	clf.fit(pca_train,train_label)
	joblib.dump(clf,'models-svmG/save_model_pca.pk1')

	accuracy_v1 = clf.score(pca_valid1,validation1_label)
	accuracy_v2 = clf.score(pca_valid2,validation2_label)
	accuracy_v3 = clf.score(pca_valid3,validation3_label)

	prediction = clf.predict(pca_test)
	np.savetxt('prediction-pca-svm.txt',prediction)

	print 'V1 Accuracy:',accuracy_v1
	print 'V2 Accuracy:',accuracy_v2
	print 'V3 Accuracy:',accuracy_v3

	end_time = time.time()
	print (end_time-start_time)
