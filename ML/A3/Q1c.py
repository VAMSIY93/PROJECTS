import os
import sys
import re
import csv
import pandas as pd
import numpy as np
import pdb
import math
import copy
import matplotlib.pyplot as plt

atb_list = ['Pclass','Sex','Age','SibSp','Parch','Ticket','Fare','Embarked','Cabin_a','Cabin_b']
atb_split = {'Pclass':3,'Sex':2,'Age':2,'SibSp':2,'Parch':2,'Ticket':2,'Fare':2,'Embarked':4,'Cabin_a':8,'Cabin_b':2}
split_cnt = [3,2,2,2,2,2,2,4,8,2]
atb_type = {'Pclass':'Discrete','Sex':'Discrete','Age':'Numerical','SibSp':'Numerical','Parch':'Numerical','Ticket':'Numerical','Fare':'Numerical','Embarked':'Discrete','Cabin_a':'Discrete','Cabin_b':'Numerical'}
max_atb_count = {'Pclass':0,'Sex':0,'Age':0,'SibSp':0,'Parch':0,'Ticket':0,'Fare':0,'Embarked':0,'Cabin_a':0,'Cabin_b':0}
atb_treshold = {'Pclass':[],'Sex':[],'Age':[],'SibSp':[],'Parch':[],'Ticket':[],'Fare':[],'Embarked':[],'Cabin_a':[],'Cabin_b':[]}
present_med = {'Pclass':[],'Sex':[],'Age':[],'SibSp':[],'Parch':[],'Ticket':[],'Fare':[],'Embarked':[],'Cabin_a':[],'Cabin_b':[]}

train_data = ""
validation_data = ""
test_data = ""

num_nodes = []
num_nodes_BFS = []
accur_count = [""]*3
accur_BFS = [""]*3
for i in range(0,3):
    accur_count[i]=[]
    accur_BFS[i]=[]

class Node:
    def __init__(self):
        self.split_atb = ""
        self.splitSize = 2
        self.value = ["",""]
        self.child = ["",""]
        self.parent = ""
        self.output = None
        self.children = 0
        self.median = ""

    def getParent(self):
        return self.parent

    def getChild(self,i):
        return self.child[i]

    def getSplitValue(self,i):
        return self.value[i]

    def getSplitAttribute(self):
        return self.split_atb

    def getSplitSize(self):
        return self.splitSize 

    def getOutput(self):
        return self.output 

    def getChildren(self):
        return self.children   

    def getMedian(self):
        return self.median

    def setSplitAttribute(self,attrib):
        self.split_atb = attrib

    def setSplitValue(self,i,value):
        if i<2:
            self.value[i] = value
        else:
            self.value.append(value)    

    def setParent(self,parent):
        self.parent = parent

    def setSplitSize(self,size):
        self.splitSize = size   

    def setMedian(self,value):
        self.median = value

    def setChild(self,i,child):
        if i<2:
            self.child[i] = child
        else:
            self.child.append(child)      

    def setOutput(self,op):
        self.output = op 

    def addChild(self):
        self.children = self.children + 1      

    def reduceChildren(self):
        self.children = self.children - 1         


class DecisionTree:
    def __init__(self):
        self.root = Node()
        self.node_count = 1

    def calculateEntropy(self,data,attrib):
        splitSize = atb_split.get(attrib)
        ones = [0]*splitSize
        zeros = [0]*splitSize
        probO = [0]*splitSize
        probZ = [0]*splitSize
        H = [0]*splitSize
        tot=len(data[attrib])
        H_final=0.0
        new_data = [0]*splitSize
        p = [0]*splitSize

        if splitSize==2 and attrib!='Sex':
            med = np.median(data[attrib])
            new_data[0] = data[data[attrib]<=med]
            new_data[1] = data[data[attrib]>med]
            for i in range(0,splitSize):
                zeros[i] = len(new_data[i][new_data[i]['Survived']==0])
                ones[i] = len(new_data[i][new_data[i]['Survived']==1])

        elif splitSize==2 and attrib=='Sex':
            for i in range(0,splitSize):
                new_data[i] = data[data[attrib]==i]
                zeros[i] = len(new_data[i][new_data[i]['Survived']==0])
                ones[i] = len(new_data[i][new_data[i]['Survived']==1])

        elif splitSize==3 and attrib=='Pclass':
            for i in range(0,splitSize):
                new_data[i] = data[data[attrib]==(i+1)]
                zeros[i] = len(new_data[i][new_data[i]['Survived']==0])
                ones[i] = len(new_data[i][new_data[i]['Survived']==1])

        elif splitSize==4 and attrib=='Embarked':            
            new_data[0] = data[data[attrib]=='C']
            new_data[1] = data[data[attrib]=='Q']
            new_data[2] = data[data[attrib]=='S']
            new_data[3] = data[data[attrib]=='0']

            for i in range(0,splitSize):
                zeros[i] = len(new_data[i][new_data[i]['Survived']==0])
                ones[i] = len(new_data[i][new_data[i]['Survived']==1])

        else:
            new_data[0] = data[data[attrib]=='A']
            new_data[1] = data[data[attrib]=='B']
            new_data[2] = data[data[attrib]=='C']
            new_data[3] = data[data[attrib]=='D']
            new_data[4] = data[data[attrib]=='E']            
            new_data[5] = data[data[attrib]=='F']
            new_data[6] = data[data[attrib]=='G']
            new_data[7] = data[data[attrib]=='0']

            for i in range(0,splitSize):
                zeros[i] = len(new_data[i][new_data[i]['Survived']==0])
                ones[i] = len(new_data[i][new_data[i]['Survived']==1])
       
        for i in range(0,splitSize):
            if ones[i]==0 or zeros[i]==0:
                p[i] = 0.0
                H[i] = 0.0
            else:       
                probO[i] = float(ones[i])/(ones[i]+zeros[i])
                probZ[i] = float(zeros[i])/(ones[i]+zeros[i])
                p[i] = float(ones[i] + zeros[i])/tot            
                H[i] = -1*(probO[i]*(math.log(probO[i],2)) + probZ[i]*(math.log(probZ[i],2)))

            H_final = H_final+ p[i]*H[i]

        return H_final                
        

    def getSplitAttribute(self,data,prntEntropy,atb_comp):
        max_IG = 0
        max_entropy = 0.0
        max_atb = None
        for i in range(0,10):
            entropy = self.calculateEntropy(data,atb_list[i])
            IG =prntEntropy - entropy   
            if IG>max_IG:
                max_IG=IG
                max_atb=atb_list[i]
                max_entropy = entropy

        return max_atb,max_entropy    

    def splitNode(self,data,attrib,prntNode,prntEntropy,atb_comp):
        splitSize = atb_split.get(attrib)
        tot_dat = len(data[attrib])
        new_data = [0]*splitSize

        if splitSize==2 and attrib!='Sex':
            med = np.median(data[attrib])
            new_data[0] = data[data[attrib]<=med]
            new_data[1] = data[data[attrib]>med]
            prntNode.setSplitValue(0,'<=')
            prntNode.setSplitValue(1,'>')
            prntNode.setMedian(med)    

        elif splitSize==2 and attrib=='Sex':
            for i in range(0,splitSize):
                new_data[i] = data[data['Sex']==i]
            prntNode.setSplitValue(0,'0')
            prntNode.setSplitValue(1,'1')
            prntNode.setMedian(None)

        elif splitSize==3 and attrib=='Pclass':
            for i in range(0,splitSize):
                new_data[i] = data[data['Pclass']==(i+1)]
            prntNode.setSplitValue(0,'1')
            prntNode.setSplitValue(1,'2')
            prntNode.setSplitValue(2,'3')
            prntNode.setMedian(None)    

        elif splitSize==4 and attrib=='Embarked':
            new_data[0] = data[data['Embarked']=='C']
            new_data[1] = data[data['Embarked']=='Q']
            new_data[2] = data[data['Embarked']=='S']
            new_data[3] = data[data['Embarked']=='0']

            prntNode.setSplitValue(0,'C')
            prntNode.setSplitValue(1,'Q')
            prntNode.setSplitValue(2,'S')
            prntNode.setSplitValue(3,'0')
            prntNode.setMedian(None)
        else:
            new_data[0] = data[data['Cabin_a']=='A']
            new_data[1] = data[data['Cabin_a']=='B']
            new_data[2] = data[data['Cabin_a']=='C']
            new_data[3] = data[data['Cabin_a']=='D']
            new_data[4] = data[data['Cabin_a']=='E']
            new_data[5] = data[data['Cabin_a']=='F']
            new_data[6] = data[data['Cabin_a']=='G']
            new_data[7] = data[data['Cabin_a']=='0']
            prntNode.setSplitValue(0,'A')
            prntNode.setSplitValue(1,'B')
            prntNode.setSplitValue(2,'C')                    
            prntNode.setSplitValue(3,'D')
            prntNode.setSplitValue(4,'E')
            prntNode.setSplitValue(5,'F')
            prntNode.setSplitValue(6,'G')
            prntNode.setSplitValue(7,'0')
            prntNode.setMedian(None)            

        prntNode.setSplitAttribute(attrib)  
        node = [""]*splitSize
        prntNode.setSplitSize(splitSize)

        atb_comp[attrib] += 1
        if splitSize==2 and attrib!='Sex':
            present_med[attrib].append(med)

        for i in range(0,splitSize):
            self.node_count = self.node_count + 1            
            if len(new_data[i]['Survived'])==0:
                one = len(data[data['Survived']==1])
                zero = len(data[data['Survived']==0])
                node[i] = Node()
                node[i].setParent(prntNode)
                prntNode.setChild(i,node[i])
                prntNode.addChild()
                if one<=zero:
                    node[i].setSplitAttribute('0')
                    node[i].setSplitSize(1)
                    node[i].setOutput('0')                    
                else:
                    node[i].setSplitAttribute('1')
                    node[i].setSplitSize(1)
                    node[i].setOutput('1')    
            else:    
                node[i] = Node()
                prntNode.setChild(i,node[i])
                prntNode.addChild()
                node[i].setParent(prntNode)
                self.growTree(new_data[i],node[i],atb_comp)

        if splitSize==2 and attrib!='Sex':
            if atb_comp[attrib]>max_atb_count[attrib]:
                max_atb_count[attrib] = atb_comp[attrib]
                atb_treshold[attrib] = copy.deepcopy(present_med[attrib])
            n = present_med[attrib].pop()
    
        atb_comp[attrib] -= 1      
          
    def growTree(self,data,prntNode,atb_comp):
        tot_dat = len(data['Survived'])       
        ones = len(data[data['Survived']==1])
        zeros = len(data[data['Survived']==0])

        if ones<=zeros:
            prntNode.setOutput('0')
        else:
            prntNode.setOutput('1')

        if ones==0:
            prntNode.setSplitAttribute('0')
            prntNode.setSplitSize(1)                  
        elif zeros==0:
            prntNode.setSplitAttribute('1')
            prntNode.setSplitSize(1)
        else:            
            po = float(ones)/(ones+zeros)
            pz = float(zeros)/(ones+zeros)
            entropy = -1*(po*(math.log(po,2)) + pz*(math.log(pz,2))) 
            attrib,attrib_entropy = self.getSplitAttribute(data,entropy,atb_comp)
            
            if attrib!=None: 
                self.splitNode(data,attrib,prntNode,entropy,atb_comp)
            else:                                    
                if ones<=zeros:
                    prntNode.setSplitAttribute('0')
                    prntNode.setSplitSize(1)                    
                else:
                    prntNode.setSplitAttribute('1')
                    prntNode.setSplitSize(1) 

    def BFS(self):
        Output = temp = []
        no_of_nodes = 1
        Output.append(self.root)
        for value in Output:
            splitSize = value.getSplitSize()
            for i in range(0,splitSize):
                no_of_nodes = no_of_nodes + 1
                if (value.child[i].getSplitAttribute())!='0' and (value.child[i].getSplitAttribute())!='1':                                        
                    Output.append(value.child[i])

        print 'Number of Nodes: ',no_of_nodes
        print 'Number of Internal Nodes: ',len(Output)
        
        return Output  

    def calcBFSAccuracies(self):
        dTree_2= copy.deepcopy(self)
        Output = dTree_2.BFS()
        for node in reversed(Output):
            if node!=dTree_2.root:
                parent = node.getParent()
                node.setParent("")
                children = parent.getChildren()
                for i in range(0,children):
                    if parent.child[i]==node:
                        break

                parent.setChild(i,"")
                parent.reduceChildren()
                children = node.getChildren()
                dTree_2.node_count = dTree_2.node_count - (children+1)
                dTree_2.saveAllAccuracies()

    def saveAllAccuracies(self):
        num_nodes_BFS.append(self.node_count)
        accuracy = self.getAccuracies(train_data)
        accur_BFS[0].append(accuracy)
        accuracy = self.getAccuracies(validation_data)
        accur_BFS[1].append(accuracy)
        accuracy = self.getAccuracies(test_data)
        accur_BFS[2].append(accuracy)

    def getAccuracies(self,data):
        count = 0
        for i in range(0,len(data['Survived'])):
            value = self.root
            while(value.getChildren()>0):
                attrib = value.getSplitAttribute()
                splitSize = value.getChildren()
                for j in range(0,splitSize):
                    flag1=False
                    flag2=False
                    if value.child[j]!="":
                        if attrib=='Embarked' or attrib=='Cabin_a':
                            if (str(value.getSplitValue(j)) is str((data[attrib][i]))):                        
                                value = value.child[j]
                                flag1=True
                            elif j==(splitSize-1):
                                flag2=True
                        elif attrib=='Pclass' or attrib=='Sex':
                            if (str(value.getSplitValue(j))) is str(int(data[attrib][i])):
                                value = value.child[j]
                                flag1=True
                            elif j==(splitSize-1):
                                flag2=True
                        else:
                            if float(data[attrib][i]) <= value.getMedian():                        
                                value = value.child[0]
                                flag1=True
                            elif value.child[1]!="":
                                value = value.child[1]
                                flag1=True
                            else:
                                flag2=True
                    else:
                        flag2=True

                    if(flag1 or flag2):
                        break

                if(flag2):
                    break
                                          
            if str(value.getOutput()) is str(data['Survived'][i]):
                count = count + 1

        accuracy = (float(count)*100)/(len(data['Survived']))
    
        return accuracy
        

    def calculateAccuracies(self,data):
        count = 0
        for i in range(0,len(data['Survived'])):
            value = self.root
            while(value.getSplitAttribute()!='0' and value.getSplitAttribute()!='1'):
                attrib = value.getSplitAttribute()
                splitSize = value.getSplitSize()

                for j in range(0,splitSize): 
                    if attrib=='Embarked' or attrib=='Cabin_a':    
                        if (str(value.getSplitValue(j)) is str((data[attrib][i]))):                        
                            value = value.child[j]
                            break
                    elif attrib=='Pclass' or attrib=='Sex':
                        if (str(value.getSplitValue(j)) is str(int(data[attrib][i]))):                        
                            value = value.child[j]
                            break
                    else:        
                        if float(data[attrib][i]) <= value.getMedian():
                            value = value.child[0]
                            break
                        else:
                            value = value.child[1]
                            break

            if str(value.getSplitAttribute()) is str(data['Survived'][i]):
                count = count + 1

        accuracy = (float(count)*100)/(len(data['Survived']))
        return accuracy                        


    def  buildDT(self,data):
        atb_comp = {'Pclass':0,'Sex':0,'Age':0,'SibSp':0,'Parch':0,'Ticket':0,'Fare':0,'Embarked':0,'Cabin_a':0,'Cabin_b':0}
        self.growTree(data,self.root,atb_comp)


if __name__ == "__main__":

    dTree = DecisionTree()

    train = pd.read_csv('train.csv') 
    validation = pd.read_csv('validation.csv')
    test = pd.read_csv('test.csv')   
    
    for i in range(0,len(train)):
        if train['Sex'][i]=="female":
            train['Sex'][i]=0
        else:
            train['Sex'][i]=1

    for i in range(0,len(validation)):
        if validation['Sex'][i]=="female":
            validation['Sex'][i]=0
        else:
            validation['Sex'][i]=1

    for i in range(0,len(test)):
        if test['Sex'][i]=='female':
            test['Sex'][i]=0
        else:
            test['Sex'][i]=1

    train_data = train
    validation_data = validation
    test_data = test

    dTree.buildDT(train)

    train_accur = dTree.calculateAccuracies(train)
    validation_accur = dTree.calculateAccuracies(validation)        
    test_accur = dTree.calculateAccuracies(test)

    dTree_2 = copy.deepcopy(dTree)
    dTree_2.calcBFSAccuracies()

    num_nodes_BFS.insert(0,dTree.node_count)
    accur_BFS[0].insert(0,train_accur)
    accur_BFS[1].insert(0,validation_accur)
    accur_BFS[2].insert(0,test_accur)

    print 'Train Data Accuracy:',train_accur
    print 'Validation Data Accuracy:',validation_accur
    print 'Test Data Accuracy:',test_accur

    print('Tresholds: ')
    print(max_atb_count)
    print(atb_treshold)

    plt.plot(num_nodes_BFS,accur_BFS[2],color='blue',label='TestData')
    plt.plot(num_nodes_BFS,accur_BFS[0],color='green',label='TrainData')
    plt.plot(num_nodes_BFS,accur_BFS[1],color='red',label='ValidationData')
    plt.legend(loc='upper left')
    plt.axis([0,320,50,110])
    plt.ylabel('----Accuracy------>')
    plt.xlabel('----Number of Nodes------>')
    plt.show()
    
