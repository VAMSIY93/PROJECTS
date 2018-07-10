import json
import argparse
import urlparse
from datetime import datetime, timedelta



def main(harfile_path):
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
  maxGoodPutDomain=[]
  hostlist=[]
  hosturllist=[]
  hostmimelist=[]
  totalsize=0
  objParentId=[]
  objref=[]
  parallelconn=[]
  print "\n\n\t\t\t\t\tTCP CONNECTION DETAILS OF EACH DOMAIN\t\t\t\t\t"
  print "\t\t\t\t~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\t\t\t"
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
                      hostmimelist.append(url1)
                      #print hostlist
                      #print hostmimelist
                      if objref.count(data['request']['headers'][o]['value'])==0:
                        objref.append(data['request']['headers'][o]['value'])
                      objParentId.append(objref.index(data['request']['headers'][o]['value']))
                    
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
                    connsizelist.append([dombody+domhead])
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
                    connsizelist[connlist.index(connection)].append(dombody+domhead )
                    connmimelist[connlist.index(connection)].append(url1)
          
          hostmimelist.append(connmimelist)
          parallelconnhelp=[] 
          #print connStartTime
          #print connEndTime
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
          print "============================================================================================================================================================"
          #print "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
          print hostname
          print "~~~~~~~~~~~~~~~~~~~~~~  \n\tMax # of TCP Connections opened: ",len(connlist) ,"\t|\tMax # of Conn Simultaneously opened per doamin: ",max(parallelconnhelp),"\t|\tTime For DNS: ",timeforDNS
          #print connStartTime
          with open("tcpconntime.csv","a") as tcpconnfile:
            tcpconnfile.write("Conn "+ str(connlist[k]))
            tcpconnfile.write(",")
            tcpconnfile.write(str(connestablish[k]))
            tcpconnfile.write("\n")
          with open("tcpsendtime.csv","a") as tcpsendfile:
            tcpsendfile.write("Conn "+ str(connlist[k])) 
            tcpsendfile.write(",")
            tcpsendfile.write(str(sum(connsend[k])))
            tcpsendfile.write("\n")
          with open("tcprecvtime.csv","a") as tcprecvfile:
            tcprecvfile.write("Conn "+ str(connlist[k])) 
            tcprecvfile.write(",")
            tcprecvfile.write(str(sum(connreceive[k])))
            tcprecvfile.write("\n")
          with open("tcpwaittime.csv","a") as tcpwaitfile:
            tcpwaitfile.write("Conn "+ str(connlist[k])) 
            tcpwaitfile.write(",")
            tcpwaitfile.write(str(sum(connwait[k])))
            tcpwaitfile.write("\n")
          
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
            print "Connection",connlist[k]," : \n\t\tEstablishment time : ",connestablish[k],"ms\t|\t Waiting Time:",sum(connwait[k]),\
            "ms\t|\tReceiving Time:",sum(connreceive[k]),"ms\t|\t Sending Time:",sum(connsend[k]) ,"ms\n\t\t| Connection Active Time:",connActiveTime[k].total_seconds()*1000,\
            "ms\t|\tActive % :",activeper[k]*100 , "\t\t|\tIdle % :",idleper[k]*100 , "\n\t\t|\tAvg GoodPut :",avgGoodPut[k],"B/ms|\tMax GoodPut: " ,maxGoodPut[k],"B/ms"
            with open("conndown.txt","a") as file:
              file.write("Conn"+str(k))
              file.write("\n------\n")
            for l in range(len(connmimelist[k])):
              #print connmimelist[k][l]
              with open("conndown.txt","a")as file:
                file.write(connmimelist[k][l])
                file.write("\n\n")
            with open("tcpactivetime.csv","a") as tcpactivefile:
              tcpactivefile.write("Conn "+ str(connlist[k])) 
              tcpactivefile.write(",")
              tcpactivefile.write(str(connActiveTime[k].total_seconds()))
              tcpactivefile.write("\n")
            with open("tcpavgput.csv","a") as tcpavgputfile:
              tcpavgputfile.write("Conn "+ str(connlist[k])) 
              tcpavgputfile.write(",")
              tcpavgputfile.write(str(avgGoodPut[k]))
              tcpavgputfile.write("\n")
            with open("tcpmaxput.csv","a") as tcpmaxputfile:
              tcpmaxputfile.write("Conn "+ str(connlist[k])) 
              tcpmaxputfile.write(",")
              tcpmaxputfile.write(str(maxGoodPut[k]))
              tcpmaxputfile.write("\n")

          maxGoodPutDomain.append(max(maxGoodPut))
          avgGoodPutDomain.append(sum(avgGoodPut)/len(avgGoodPut))
          lastObjEndTime=max(connEndTime[len(connlist)-1]) 
  #print len(objParentId) 
  with open("objtree.csv","w") as file1:
    for j in range(len(objParentId)):
      file1.write("OBJ"+str(j)+"  :")
      file1.write("\n---------\n")
      file1.write(str(hostmimelist[j]))
      file1.write("\nParent ID:")
      file1.write(str(objParentId[j]))
      file1.write("\n\n")
  with open("hostconnlist.txt","w") as file:
    for k in hostmimelist:
      file.write(' '.join(str(s) for s in k))
      file.write("\n\n\n")        
        #for t in zipped:
        #file.write('#'+' '.join(str(s) for s in t)+ '\n\n')
    
  zipped=zip(hostlist,hostmimelist)
  '''
  print "\n\n"
  for k in range(len(zipped)):
    print zipped[k]
    print "\n\n"
  '''
  mytime=datetime.strptime(pageStartTime[12:23],"%H:%M:%S.%f")
  #mytime1=datetime.strptime(lastObjEndTime[12:23],"%H:%M:%S.%f")    
  print "\nPage Load Time :",(lastObjEndTime-mytime).total_seconds()*1000 ,"ms"
  print 
  print "Average Good Put of Network is:",sum(avgGoodPutDomain)/len(avgGoodPutDomain)
  #print maxGoodPutDomain
  print "Max of maximum Gooput of Network is:",max(maxGoodPutDomain)
  #if max(avgGoodPutDomain)<(sum(avgGoodPutDomain)/len(avgGoodPutDomain)):
  #  print "Yes It was Utilised."
  #else:
  #  print "No It was Not Utilised."
  with open("tupletree.txt","a") as file: 
    for t in zipped:
      file.write('#'+' '.join(str(s) for s in t)+ '\n\n')
  


   
    
if __name__ == '__main__':
    argparser = argparse.ArgumentParser(
        prog='parsehar',
        description='Parse .har files into comma separated values (csv).')
    argparser.add_argument('harfile', type=str, nargs=1,
                        help='path to harfile to be processed.')
    args = argparser.parse_args()

    main(args.harfile[0])                
