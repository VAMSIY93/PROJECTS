import os
import sys
import re
import csv
import pandas as pd
import numpy as np
import pdb
import math
import random
import matplotlib.pyplot as plt
from collections import Counter

mapping = {0:'rec.autos',1:'rec.motorcycles',2:'rec.sport.baseball',3:'rec.sport.hockey',4:'talk.politics.guns',5:'talk.politics.mideast',6:'talk.politics.misc',7:'talk.religion.misc'}
rev_mapping = {'rec.autos':0,'rec.motorcycles':1,'rec.sport.baseball':2,'rec.sport.hockey':3,'talk.politics.guns':4,'talk.politics.mideast':5,'talk.politics.misc':6,'talk.religion.misc':7}

def train(data,vocabulary):
	sum_group = [0]*8
	group_words = [0]*8
	res_sum = [0]*8
	cnt_list = [""]*8
	no_vocab = len(vocabulary)
	for i in range(0,8):
		cnt_list[i] = Counter()
	group_counter = {'rec.autos':cnt_list[0],'rec.motorcycles':cnt_list[1],'rec.sport.baseball':cnt_list[2],'rec.sport.hockey':cnt_list[3],'talk.politics.guns':cnt_list[4],'talk.politics.mideast':cnt_list[5],'talk.politics.misc':cnt_list[6],'talk.religion.misc':cnt_list[7]}

	sample = np.matrix(np.zeros((8,no_vocab)))
	probMatrix = np.matrix(np.zeros((8,no_vocab)))

	for j in range(0,8):
		key = mapping[j]
		for i in range(0,4):			
			group_counter[key] = group_counter[key] + data[i][key]

	for i in range(0,8):
		key = mapping[i]
		cnt = group_counter[key]
		for j in vocabulary:
			k = vocabulary[j] - 1
			sample[i,k] = cnt[j]
			sum_group[i] = sum_group[i] + sample[i,k]
			if sample[i,k]!=0:
				group_words[i] = group_words[i] + 1

	for i in range(0,8):
		res_sum[i] = sum_group[i] + group_words[i]			
				
	sample = sample + 1
	sample = sample.astype(float)
	for i in range(0,8):
		for j in range(0,no_vocab):
			probMatrix[i,j] = 	sample[i,j]/res_sum[i]

	return probMatrix		

def test(test_data,probMatrix,vocabulary,counts,confusion,perform):
	no_vocab = len(vocabulary)
	correct = 0
	tot = 0

	length = len(test_data)
	counts = np.matrix(counts)
	counts = np.transpose(counts)
	counts = counts + 1
	arr = np.matrix(np.zeros((8,1)))
	tot = counts.sum()
	counts=counts.astype(float)

	for i in range(0,length):
		argmax = counts/tot
		cnt = Counter()
		words = test_data[i][1]
		for w in words:
			cnt[w] += 1

		for key in cnt:
			index = vocabulary[key] - 1
			prob = 	probMatrix[:,index]
			logProb = np.log2(prob)
			argmax = np.add(argmax,logProb)
					
		maxVal = argmax[0,0]
		maxInd = 0	

		for j in range(1,8):
			if maxVal<argmax[j,0]:
				maxInd = j
				maxVal = argmax[j,0]	

		if perform==1:		
			actual = rev_mapping[test_data[i][0]]
			confusion[actual][maxInd] += 1

		if test_data[i][0]==mapping[maxInd]:
			correct = correct + 1


	accuracy = (float(correct)*100)/length
	return accuracy,confusion


def performSplit(Input_Data,size):
	train_test_data = [""]*size
	ran = size/4
	data = [""]*4
	group_counts = [0]*8
	rlist = random.sample(xrange(1445),ran)	
	cnt_list = [""]*4	
	for i in range(0,4):
		cnt_list[i] = [""]*8
		for j in range(0,8):
			cnt_list[i][j] = Counter()
	
	for i in range(0,4):
		data[i] = {'rec.autos':cnt_list[i][0],'rec.motorcycles':cnt_list[i][1],'rec.sport.baseball':cnt_list[i][2],'rec.sport.hockey':cnt_list[i][3],'talk.politics.guns':cnt_list[i][4],'talk.politics.mideast':cnt_list[i][5],'talk.politics.misc':cnt_list[i][6],'talk.religion.misc':cnt_list[i][7]}	

	count = 0	
	for i in range(0,4):
		for j in rlist:
			key = Input_Data[i][j][0]
			words = Input_Data[i][j][1]
			train_test_data[count] = Input_Data[i][j]
			count = count + 1
			cnt = Counter()
			for w in words:
				cnt[w] += 1

			data[i][key] = data[i][key] + cnt
			index = rev_mapping[key]
			group_counts[index] = group_counts[index] + 1

	return data,train_test_data,group_counts

def predictRandomAccuracies(Input_Data):
	correct = 0
	tot = 0
	for i in range(0,5):
		length = len(Input_Data[i])
		tot = tot + length
		for j in range(0,length):
			index = random.randint(0,7)
			if mapping[index] == Input_Data[i][j][0]:
				correct = correct + 1

	accuracy = (float(correct)*100)/tot
	return accuracy					


if __name__ == "__main__":

	train_data = [""]*4
	data = [""]*5
	Input_Data = [""]*5
	cnt_list = [""]*5
	count_list = [""]*5
	for i in range(0,5):
		Input_Data[i] = [""]*1446
		cnt_list[i] = [""]*8
		count_list[i] = [0]*8
	vocabulary = []
	size = [0]*5

	for i in range(0,5):
		for j in range(0,8):
			cnt_list[i][j] = Counter()
	
	for i in range(0,5):
		data[i] = {'rec.autos':cnt_list[i][0],'rec.motorcycles':cnt_list[i][1],'rec.sport.baseball':cnt_list[i][2],'rec.sport.hockey':cnt_list[i][3],'talk.politics.guns':cnt_list[i][4],'talk.politics.mideast':cnt_list[i][5],'talk.politics.misc':cnt_list[i][6],'talk.religion.misc':cnt_list[i][7]}
	
	fp = open('20ng-rec_talk.txt')
	f = open('sample.txt')
	for line in fp:
		words = line.split()
				
		i = random.randint(0,4)
		while size[i]>1445:
			i = (i+1)%5			

		key = words.pop(0)
		index = rev_mapping[key]
		tup = (key,words)
		Input_Data[i][size[i]] = tup	
		cnt1 = Counter()	
		for w in words:			
			cnt1[w] += 1

		data[i][key] = data[i][key] + cnt1
		count_list[i][index] = count_list[i][index] + 1 
		vocabulary = list(set(vocabulary) | set(words))
		size[i] = size[i] + 1
		a=i
	fp.close()	
	length = len(vocabulary)

	for i in range(0,4):
		train_data[i] = data[i]

	N = length
	int_list = [i+1 for i in xrange(N)] 
	vocab = dict(zip(vocabulary,int_list))
	counts = [""]

	random_accuracy = predictRandomAccuracies(Input_Data)

	confusion = [""]*8
	conf = [""]*8
	for i in range(0,8):
		confusion[i] = [0]*8
		conf[i] = [0]*8

	train_main_accur = 0.0	
	sum_accuracy = 0.0
	class_accuracies = [0]*5
	new_count_list = [""]*4
	inputData = [""]*4
	for i in range(0,5):
		k=0
		for j in range(0,5):
			if i!=j:
				train_data[k] = data[j]
				inputData[k] = Input_Data[j]
				new_count_list[k] = count_list[j]
				k = k + 1
			else:
				test_data = Input_Data[i]
				counts = count_list[i]

		probMatrix = train(train_data,vocab)
		class_accuracies[i],confusion = test(test_data,probMatrix,vocab,counts,confusion,1)
		for j in range(0,4):
			train_acc,conf = test(inputData[j],probMatrix,vocab,new_count_list[j],conf,0)
			train_main_accur = train_main_accur + train_acc
		sum_accuracy = sum_accuracy + class_accuracies[i]
	final_accuracy = sum_accuracy/5
	train_main_accur = train_main_accur/20	

	avg_accuracy = [0]*5
	indi_accuracies = [0]*5
	new_Input_Data = [""]*4
	train_accuracies = [0]*5 
	for i in range(0,5):
		indi_accuracies[i] = [0]*5
	for l in range(0,5):
		sum_accuracy = 0.0
		for i in range(0,5):
			k=0
			for j in range(0,5):
				if i!=j:
					train_data[k] = data[j]
					new_Input_Data[k] = Input_Data[j]
					k = k + 1
				else:
					test_data = Input_Data[i]
					counts = count_list[i]

			train_data,train_test_data,group_counts = performSplit(new_Input_Data,1000*(l+1))		
			probMatrix = train(train_data,vocab)
			indi_accuracies[l][i],conf = test(test_data,probMatrix,vocab,counts,conf,0)
			train_acc,conf = test(train_test_data,probMatrix,vocab,group_counts,conf,0)
			train_accuracies[l] = train_accuracies[l] + train_acc
			sum_accuracy = sum_accuracy + indi_accuracies[l][i]

		train_accuracies[l] = train_accuracies[l]/5	
		avg_accuracy[l] = sum_accuracy/5		

	xvalues = [1000,2000,3000,4000,5000,5784]	

	avg_accuracy.append(final_accuracy)
	train_accuracies.append(train_main_accur)

	print('Random Accuracy:')
	print(random_accuracy)	

	print('Final Accuracies:')	
	print(indi_accuracies)
	print(avg_accuracy)

	print('Main Accuracies:')
	print(class_accuracies)
	print(final_accuracy)	

	print('Train Accuracies:')
	print(train_accuracies)
	print(train_main_accur)

	print 'Confusion Matrix:',confusion
	print 'Conf:',conf

	plt.plot(xvalues,avg_accuracy,color='blue',label='TestData')
	plt.plot(xvalues,train_accuracies,color='green',label='TrainData')
	plt.legend(loc='upper left')
	plt.axis([1000,6000,80,110])
	plt.ylabel('----Accuracy------>')
	plt.xlabel('----Number of Documents------>')
	plt.show()