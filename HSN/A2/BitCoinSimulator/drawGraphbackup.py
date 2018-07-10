import csv 
import sys
from graphviz import Digraph
import graphviz
import time

dot = Digraph(comment='The Round Table')
  #doctest: +ELLIPSIS


n=15
for i in range(0,n):
	dot = graphviz.Graph(format='svg')
	dot
	filenm = "node-"+str(i)+".csv"
	f = open(filenm)
	reader = csv.reader(f)
	print(filenm)
	for row in reader:
		if (row[1] != 'null'):
			dot.node(str(row[0]))
			dot.node(str(row[1]))		
			dot.edge(str(row[0]),str(row[1]), label=row[3])

	print(dot.source)  # doctest: +NORMALIZE_WHITESPACE
	

	label = "label ="+"node"+str(i)
	dot.body.append(label)
	opfilenm = "node"+str(i)+".gv"
	f.close()
	time.sleep(6)
	opfilenm = "node"+str(i)+".gv"
	dot.render(opfilenm, view=True)
	time.sleep(6)



	
