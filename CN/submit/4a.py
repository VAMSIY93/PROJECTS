import json
import argparse
import urlparse
from datetime import datetime, timedelta
import subprocess
import socket
import time
import sys
import math
import numpy as np
import matplotlib.pyplot as plt


def fetch_host_data(hostname, obj_list):
	x = 0
	foldername = str(r)	

	bashCommand = "mkdir "+foldername
	process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE)
	output = process.communicate()[0]	
	global r
	r = r+1
	max_conn_time = 0
	for k in range(len(obj_list)):
		tot_time=0
		for j in range(len(obj_list[k])):		
			url = obj_list[k][j]
			#print url,'\n'
			httpReq = 'GET '+url+' HTTP/1.0\n\n'
			time_start =  int(round(time.time() * 1000))
			mysock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			mysock.connect(('proxy62.iitd.ernet.in', 3128))
			mysock.send(httpReq)
	
			count = 0
			picture = "";
			while True:
			    data = mysock.recv(5120)
			    time_end = int(round(time.time() * 1000))	 
			    if ( len(data) < 1 ) : break
			    # time.sleep(0.25)
			    count = count + len(data)
			    #print 'length of data'	
			    #print len(data),count
			    picture = picture + data
			tot_time = tot_time+(time_end - time_start)
			i = -1
			t=0
	 		c = url[i]
			while(c!='/' and i>-100):
				if(c=='.'):
					t = i
				i = i-1
				c=url[i]
	
			if t>-6 and t<0:
				filename = url[i+1:]
			else:
				filename = str(x)+".txt"
				x = x+1

			#print filename,'filename'
			pos = picture.find("\r\n\r\n");
			picture = picture[pos+4:]
			with open(filename,"w") as file:
				file.write(picture)
				file.close()		
	
			bashCommand = "mv "+filename+" "+foldername
			#print bashCommand
			process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE)
			output = process.communicate()[0]
			mysock.close()
		
		if (max_conn_time<tot_time):
			max_conn_time = tot_time
	global page_time
	page_time = page_time + max_conn_time

def plotGraph(browser, program):
	n_groups = 1

	br_time = (browser)
	br_name = (2)

	pr_time = (program)
	pr_name = (1)

	#fig, ax = plt.subplots()

	index = np.arange(n_groups)
	bar_width = 0.1

	opacity = 0.5
	error_config = {'ecolor': '0.1'}

	rects1 = plt.bar(index,br_time,bar_width,alpha=opacity,color='b',yerr=br_name,error_kw=error_config,label='Browser Page Load Time')

	rects2 = plt.bar(index+bar_width,pr_time,bar_width,alpha=opacity,color='r',yerr=pr_name,error_kw=error_config,label='Program Page Load  Time')

	plt.xlabel('Item')
	plt.ylabel('Time')
	plt.title('COMP. OF BROWSER AND PROGRAM PLT')	
	#plt.xticks(index + bar_width, ('A'))
	plt.legend()

	plt.tight_layout()
	plt.show()


def main(harfile_path):
  global r
  r = 1
  global page_time
  page_time = 0	
	
  max_no_conn = int(sys.argv[2])
  max_no_objects = int(sys.argv[3])	

  harfile = open(harfile_path)
  harfile_json = json.loads(harfile.read())
  object=0
  with open("conndown.txt","a") as file:
    file.truncate()
    file.write("DOWNLOAD TREE FOR OBJECTS USING CONNECTIONID :\n")
    file.write("--------------------------------------------\n\n\n\n")
  with open("objtree.txt","a") as file:
    file.write("OBJECT TREE FOR USING PAGE REFERRER :\n")
    file.write("--------------------------------------------\n\n\n\n")
  count=0;
  urllist1=[]
  objnodeid=0
  avgGoodPutDomain=[]
  hostlist=[]
  hostmimelist=[]
  hosturllist=[]
  totalsize=0
  parallelconn=[]
 # print "\n\n\t\t\t\t\tTCP CONNECTION DETAILS OF EACH DOMAIN\t\t\t\t\t"
  #print "\t\t\t\t~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\t\t\t"
  for entry in harfile_json['log']['entries']:
     if 'url' in entry['request']:
        url=entry['request']['url']
        hostname = (urlparse.urlparse(url).hostname)
        #connection=entry['connection']
        if(count==0):
          pageStartTime=entry['startedDateTime']
          count+=1
        domtotal=0
        contentsize=0
        connobjlist=[]
        connsizelist=[]
        connlist=[]
        connmimelist=[]
        connestablish=[]
        connwait=[]
        connreceive=[]
        connsend=[]
        conntotal=[]
        connStartTime=[]
        connEndTime=[]
        connActiveTime=[]
        activeper=[]
        idleper=[]
        avgGoodPut=[]
        maxGoodPut=[]
        objref=[]
        timeforDNS=0;
        if(urllist1.count(hostname)==0):
          i=0
          urllist1.append(hostname)
          for data in harfile_json['log']['entries']:
              if 'url' in data['request']:
                url1=data['request']['url']
                hostname1 = (urlparse.urlparse(url1).hostname)
                if hostname==hostname1:
                  if(hostlist.count(hostname1)==0):
                    hostlist.append(hostname1)
                    host=hostname1
                    #hostmimelist.append([url1])
                    #hosturllist.append([url1])
                    #hosturllist[hostlist.index(hostname1)].append(url1)
                  '''
                  else:
                    #hostlist.append(hostname1)
                    hostmimelist[hostlist.index(hostname1)].append(url1)
                    #hosturllist.append([url1])
                    #hosturllist[hostlist.index(hostname1)].append(url1)
                  '''
                  if 'mimeType' in data['response']['content']:
                    i=i+1
                  domhead=data['response']['headersSize']
                  dombody=data['response']['bodySize']
                  if(dombody==-1):
                    dombody=0
                  if(domhead==-1):
                    domhead=0
                  contentsize=contentsize+dombody
                  domtotal=domtotal+(domhead+dombody)
                  connection=data['connection']
                  for o in range(len(data['request']['headers'])):
                    if((data['request']['headers'][o]['name']=='referer') or (data['request']['headers'][o]['name']=='Referer')):
                      objref.append(data['request']['headers'][o]['value'])
                      
                    
                  if connlist.count(connection)==0:
                    connlist.append(connection)
                    connmimelist.append([url1])
                    connestablish.append(data['timings']['connect'])
                    connwait.append([data['timings']['wait']])
                    connreceive.append([data['timings']['receive']])
                    connsend.append([data['timings']['send']])
                    tottime=(data['timings']['wait'])+(data['timings']['receive'])+(data['timings']['send'])
                    conntotal.append([tottime])
                    #print data['response']['bodySize'] , "    "
                    mytime=datetime.strptime(data['startedDateTime'][12:23],"%H:%M:%S.%f")
                    connStartTime.append([mytime])
                    mytime1=mytime+timedelta(milliseconds=tottime)
                    connEndTime.append([mytime1])
                    connobjlist.append(1)
                    connsizelist.append([domhead+dombody])
                    if(timeforDNS<data['timings']['dns']):
                      timeforDNS = data['timings']['dns']

                  else:
                    #print data['response']['bodySize'] , "    "
                    #connStartTime[connlist.index(connection)].append(data['startedDateTime'])
                    #connestablish[connlist.index(connection)].append(data['timings']['connect'])
                    connwait[connlist.index(connection)].append(data['timings']['wait'])
                    connreceive[connlist.index(connection)].append((data['timings']['receive']))
                    connsend[connlist.index(connection)].append(data['timings']['send'])
                    tottime=(data['timings']['wait'])+(data['timings']['receive'])+(data['timings']['send'])
                    conntotal[connlist.index(connection)].append(tottime)
                    mytime=datetime.strptime(data['startedDateTime'][12:23],"%H:%M:%S.%f")
                    connStartTime[connlist.index(connection)].append(mytime)
                    mytime1=mytime+timedelta(milliseconds=tottime)
                    connEndTime[connlist.index(connection)].append(mytime1)
                    connobjlist[connlist.index(connection)]+=1
                    connsizelist[connlist.index(connection)].append(dombody+domhead)
                    connmimelist[connlist.index(connection)].append(url1)
                    
          hostmimelist.append(connmimelist)
          parallelconnhelp=[] 
          for k in range(len(connlist)):
            init=min(connStartTime[k])
            final=max(connEndTime[k])
            count=0
            for l in range(len(connlist)):
              for m in range(len(connEndTime[l])):
                #if(l!=k):
                if (init<=connStartTime[l][m]<=final)or (init <= connEndTime[l][m] <= final):
                  count+=1
                  break
            parallelconnhelp.append(count)
          #parallelconn.append(max(parallelconnhelp))
          #print "Max No of Connections Opened Per Each Domain:",max(parallelconn)
          '''
          print "============================================================================================================================================================"
          #print "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
          print hostname
          print "~~~~~~~~~~~~~~~~~~~~~~  \n\tMax # of TCP Connections opened: ",len(connlist) ,"\t|\tMax # of Conn Simultaneously opened per doamin: ",max(parallelconnhelp),"\t|\tTime For DNS: ",timeforDNS
          #print connStartTime
          '''
          with open("conndown.txt","a") as file:
            file.write("HostName : ")
            file.write(hostname)
            file.write("\n---------------\n")
          for k in range(len(connlist)):
            #print "No.of.objects downloaded are:",connobjlist[k]
            #print "Size of Objects downloaded are:",connsizelist[k]
            #print max(connEndTime[k])-min(connStartTime[k])
            maxGoodPut.append(max(connsizelist[k])/connreceive[k][connsizelist[k].index(max(connsizelist[k]))])
            avgGoodPut.append(sum(connsizelist[k])/sum(connreceive[k]))
            connActiveTime.append(max(connEndTime[k])-min(connStartTime[k]))
            activeper.append(sum(conntotal[k])/(connActiveTime[k].total_seconds()*1000))
            idleper.append(1-activeper[k])
            '''
            print "Connection",connlist[k]," : \n\t\tEstablishment time : ",connestablish[k],"ms\t|\t Waiting Time:",sum(connwait[k]),\
            "ms\t|\tReceiving Time:",sum(connreceive[k]),"ms\t|\t Sending Time:",sum(connsend[k]) ,"ms\n\t\t| Connection Active Time:",connActiveTime[k].total_seconds()*1000,\
            "ms\t|\tActive % :",activeper[k]*100 , "\t\t|\tIdle % :",idleper[k]*100 , "\n\t\t|\tAvg GoodPut :",avgGoodPut[k],"B/ms|\tMax GoodPut: " ,maxGoodPut[k],"B/ms"
            '''
            with open("conndown.txt","a") as file:
              file.write("Conn"+str(k))
              file.write("\n------\n")
            for l in range(len(connmimelist[k])):
              #print connmimelist[k][l]
              with open("conndown.txt","a")as file:
                file.write(connmimelist[k][l])
                file.write("\n\n")
            for j in range(len(connmimelist[k])):
              with open("objtree.txt","a") as file:
                file.write("OBJ"+str(objnodeid)+"  :")
                file.write(connmimelist[k][j])
                file.write("\n---------")
                file.write("\nParent ID:")
                #file.write(objref[k+j])
                file.write("\n\n")
                objnodeid+=1
          avgGoodPutDomain.append(sum(avgGoodPut)/len(avgGoodPut))
          lastObjEndTime=max(connEndTime[len(connlist)-1])  
  with open("hostconnlist.txt","w") as file:
    for k in hostmimelist:
      file.write(' '.join(str(s) for s in k))
      file.write("\n\n\n")        
        #for t in zipped:
        #file.write('#'+' '.join(str(s) for s in t)+ '\n\n')
    
  zipped=zip(hostlist,hostmimelist)
  print "\n\n"
  for k in range(len(zipped)):#range(5): 
    y = zipped[k]
    fetch_host_data(y[0],y[1])	
    #print zipped[k]
    #print "\n\n"
    	
  mytime=datetime.strptime(pageStartTime[12:23],"%H:%M:%S.%f")
  #mytime1=datetime.strptime(lastObjEndTime[12:23],"%H:%M:%S.%f")    
  print "\nBrowser Page Load Time :",(lastObjEndTime-mytime).total_seconds()*1000 ,"ms"
  browser = (lastObjEndTime-mytime).total_seconds()*1000 
  bro_pt = int(browser)
  bro_pt = bro_pt/1000
  #print "\nAverage Good Put of Network is:",sum(avgGoodPutDomain)/len(avgGoodPutDomain)
  print "\n"
  with open("tupletree.txt","a") as file: 
    for t in zipped:
      file.write('#'+' '.join(str(s) for s in t)+ '\n\n')
  
  print "Program Page Load Time:",page_time,'ms'
  page_time = page_time/1000	
  plotGraph(bro_pt,page_time)	 

   
    
if __name__ == '__main__':
    argparser = argparse.ArgumentParser(
        prog='parsehar',
        description='Parse .har files into comma separated values (csv).')
    argparser.add_argument('harfile', type=str, nargs=3,
                        help='path to harfile to be processed.')
    args = argparser.parse_args()

    main(args.harfile[0])            

