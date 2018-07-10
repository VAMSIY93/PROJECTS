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

num = 0
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

        if splitSize==2:
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
        max_entropy = 0
        max_atb = atb_list[0]
        for i in range(0,10):
            entropy = self.calculateEntropy(data,atb_list[i])
            if entropy>0:
                IG = prntEntropy - entropy
            else:
                IG = 0.0        
            if IG>max_IG:
                max_IG=IG
                max_atb=atb_list[i]
                max_entropy = entropy
        
        if max_IG==0.0:
            for i in atb_list:
                if atb_comp[i]==False:
                    max_atb = i
                    break

        return max_atb,max_entropy    

    def splitNode(self,data,attrib,prntNode,prntEntropy,atb_comp):
        splitSize = atb_split.get(attrib)
        tot_dat = len(data[attrib])
        new_data = [0]*splitSize

        if splitSize==2:
            for i in range(0,splitSize):
                new_data[i] = data[data[attrib]==i]
            prntNode.setSplitValue(0,'0')
            prntNode.setSplitValue(1,'1')    

        elif splitSize==3 and attrib=='Pclass':
            for i in range(0,splitSize):
                new_data[i] = data[data['Pclass']==(i+1)]
            prntNode.setSplitValue(0,'1')
            prntNode.setSplitValue(1,'2')
            prntNode.setSplitValue(2,'3')    

        elif splitSize==4 and attrib=='Embarked':
            new_data[0] = data[data['Embarked']=='C']
            new_data[1] = data[data['Embarked']=='Q']
            new_data[2] = data[data['Embarked']=='S']
            new_data[3] = data[data['Embarked']=='0']

            prntNode.setSplitValue(0,'C')
            prntNode.setSplitValue(1,'Q')
            prntNode.setSplitValue(2,'S')
            prntNode.setSplitValue(3,'0')
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

        prntNode.setSplitAttribute(attrib)
        atb_comp[attrib]=True  
        node = [""]*splitSize
        prntNode.setSplitSize(splitSize)  
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

        atb_comp[attrib]=False      
          
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
            
            if atb_comp[attrib]==False: 
                self.splitNode(data,attrib,prntNode,entropy,atb_comp)
            else:                                    
                if ones<=zeros:
                    prntNode.setSplitAttribute('0')
                    prntNode.setSplitSize(1)                    
                else:
                    prntNode.setSplitAttribute('1')
                    prntNode.setSplitSize(1)  

    def BFS(self):
        Output = []
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

    def getSubTreeSize(self,node):
        if node.getSplitAttribute()=='0' or node.getSplitAttribute()=='1':
            return 1
        else:
            num = 1
            splitSize = node.getSplitSize()
            for i in range(0,splitSize):
                num = num + self.getSubTreeSize(node.child[i])

            return num

    def pruneDT(self,init_accuracy):
        dTree_2= copy.deepcopy(self)
        Output = dTree_2.BFS() 
        length = len(Output)    
        prunedAccuracies = [0]*length

        Flag = True
        while(Flag):
            index = 0
            length = len(Output)
            prunedAccuracies = [0]*length            
            for node in Output:
                if node!=dTree_2.root:
                    index = index + 1
                    parent = node.getParent()
                    node.setParent("")
                    children = parent.getSplitSize()
            
                    for i in range(0,children):
                        if parent.child[i]==node:
                            break

                    parent.child[i]=""
                    parent.reduceChildren()
                    grand_children = node.getChildren()

                    new_accuracy = dTree_2.getAccuracies(validation_data)
                    prunedAccuracies[index] = new_accuracy

                    parent.child[i]=node
                    node.setParent(parent)
                    parent.addChild()

            max_accuracy = init_accuracy
            index = 0
            for i in range(0,length):
                if max_accuracy<=prunedAccuracies[length-i-1]:
                    max_accuracy = prunedAccuracies[length-i-1]
                    index = length-i-1
                
            if index>0:
                node = Output[index]
                parent = node.getParent()
                node.setParent("")
                children = parent.getSplitSize()
                
                for i in range(0,children):
                    if parent.child[i]==node:
                        break

                new_node = Node()
                new_node.setSplitAttribute(parent.getOutput())
                new_node.setOutput(parent.getOutput())
                new_node.setSplitSize(1)
                parent.reduceChildren()
                parent.child[i]=new_node
                new_node.setParent(parent)
                
                subTree = dTree_2.getSubTreeSize(node)
                dTree_2.node_count = dTree_2.node_count - subTree + 1
                parent.addChild()
                Output = dTree_2.BFS()
                init_accuracy = max_accuracy
            else:
                Flag=False

            dTree_2.saveAllAccuracies()

        print 'Number of Nodes after Pruning: ',dTree_2.node_count
        print 'Final Accuracy: ',init_accuracy
        print dTree_2.calculateAccuracies(train_data)
        print dTree_2.calculateAccuracies(validation_data)
        print dTree_2.calculateAccuracies(test_data)

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
                    else:        
                        if (str(value.getSplitValue(j)) is str(int(data[attrib][i]))):                        
                            value = value.child[j]
                            break     

            if str(value.getSplitAttribute()) is str(data['Survived'][i]):
                count = count + 1

        accuracy = (float(count)*100)/(len(data['Survived']))
        return accuracy

    def saveAllAccuracies(self):
        num_nodes.append(self.node_count)
        accuracy = self.getAccuracies(train_data)
        accur_count[0].append(accuracy)
        accuracy = self.getAccuracies(validation_data)
        accur_count[1].append(accuracy)
        accuracy = self.getAccuracies(test_data)
        accur_count[2].append(accuracy)

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
                        else:
                            if (str(value.getSplitValue(j)) is str(int(data[attrib][i]))):                        
                                value = value.child[j]
                                flag1=True
                            elif j==(splitSize-1):
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


    def  buildDT(self,data):
        atb_comp = {'Pclass':False,'Sex':False,'Age':False,'SibSp':False,'Parch':False,'Ticket':False,'Fare':False,'Embarked':False,'Cabin_a':False,'Cabin_b':False}
        self.growTree(data,self.root,atb_comp)


def preProcessData(train):
    leng = len(train['Age'])

    for i in range(0,leng):
        if train['Age'][i]<=med_age:
            train['Age'][i]=0
        else:
            train['Age'][i]=1

        if train['Sex'][i]=="female":
            train['Sex'][i]=0
        else:
            train['Sex'][i]=1

        if train['SibSp'][i]<=med_sib:
            train['SibSp'][i]=0
        else:
            train['SibSp'][i]=1

        if train['Parch'][i]<=med_par:
            train['Parch'][i]=0
        else:
            train['Parch'][i]=1

        if train['Ticket'][i]<=med_tkt:
            train['Ticket'][i]=0
        else:
            train['Ticket'][i]=1

        if train['Fare'][i]<=med_far:
            train['Fare'][i]=0
        else:
            train['Fare'][i]=1

        if train['Cabin_b'][i]<=med_cab:
            train['Cabin_b'][i]=0
        else:
            train['Cabin_b'][i]=1 

    return train        
        

if __name__ == "__main__":

    dTree = DecisionTree()
    dTree_2 = DecisionTree()
    train = pd.read_csv('train.csv') 
    validation = pd.read_csv('validation.csv')
    test = pd.read_csv('test.csv')   
    
    med_age = np.median(train['Age'])
    med_sib = np.median(train['SibSp'])
    med_par = np.median(train['Parch'])
    med_tkt = np.median(train['Ticket'])
    med_far = np.median(train['Fare'])
    med_cab = np.median(train['Cabin_b'])

    train = preProcessData(train)
    validation = preProcessData(validation)
    test = preProcessData(test)
    train_data = train
    validation_data = validation
    test_data = test

    dTree.buildDT(train)

    train_accur = dTree.calculateAccuracies(train)
    validation_accur = dTree.calculateAccuracies(validation)        
    test_accur = dTree.calculateAccuracies(test)

    num_nodes.insert(0,dTree.node_count)
    accur_count[0].insert(0,train_accur)
    accur_count[1].insert(0,validation_accur)
    accur_count[2].insert(0,test_accur)

    dTree.pruneDT(validation_accur)

    print 'Train Data Accuracy:',train_accur
    print 'Validation Data Accuracy:',validation_accur
    print 'Test Data Accuracy:',test_accur

    plt.plot(num_nodes,accur_count[2],color='blue',label='TestData')
    plt.plot(num_nodes,accur_count[0],color='green',label='TrainData')
    plt.plot(num_nodes,accur_count[1],color='red',label='ValidationData')
    plt.legend(loc='upper left')
    plt.axis([0,420,50,110])
    plt.ylabel('----Accuracy------>')
    plt.xlabel('----Number of Nodes------>')
    plt.show()