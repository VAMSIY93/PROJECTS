import csv 
import sys
from graphviz import Digraph
import graphviz

#dot = Digraph(comment='The Round Table')
dot =graphviz.Graph(format='svg')
dot  #doctest: +ELLIPSIS
f = open('graph.csv')
reader = csv.reader(f)
length=0

adjMatrix =[]

for row in reader:
	#print row
	adjMatrix.append(row)
	length = length +1

for i in range(0,length):
	dot.node(str(i))

for i in range(0,length):
	for j in range(0,i):
		
		if((adjMatrix[i][j] != ' 0')):
			if(adjMatrix[i][j] != '0'):
				dot.edge(str(i),str(j), label=adjMatrix[i][j])
			


print(dot.source)  # doctest: +NORMALIZE_WHITESPACE
dot.render('test-output/round-table.gv', view=True)
f.close()
