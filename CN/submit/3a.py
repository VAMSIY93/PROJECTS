#to count the number of objects in HAR file

import json
import argparse
import urlparse
from datetime import datetime, timedelta


def main(harfile_path):
    harfile = open(harfile_path)
    harfile_json = json.loads(harfile.read())
    object=0
    #total number of objects downloaded:
    for entry in harfile_json['log']['entries']:
        if 'mimeType' in entry['response']['content']:
            mimetype = entry['response']['content']['mimeType']
            object=object+1
    print "Number of objects are: " , object
    
    totobjects=0
    urllist=[]
    #print "|\t\tDomain\t\t\t|\t\tObjects Downloaded\t\t\t|\t\t\tSize"
    '''
    for entry in harfile_json['log']['entries']:
       if 'url' in entry['request']:
          url=entry['request']['url']
          hostname = (urlparse.urlparse(url).hostname)
          domtotal=0
          if(urllist.count(hostname)==0):
            i=0
            urllist.append(hostname)
            for data in harfile_json['log']['entries']:
                if 'url' in data['request']:
                  url1=data['request']['url']
                  hostname1 = (urlparse.urlparse(url1).hostname)
                  if hostname==hostname1:
                    if 'mimeType' in data['response']['content']:
                      i=i+1
                      totobjects+=1
                    domhead=data['response']['headersSize']
                    dombody=data['response']['bodySize']
                    if(dombody==-1):
                      dombody=0
                    if(domhead==-1):
                      domhead=0
                    domtotal=domtotal+(domhead+dombody)
            totalsize+=domtotal 
            print "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            print hostname,"\n-----------------------------\n \tNo.of.Objects Downloaded : ",i," \n\t Size of Objects Downloaded :",domtotal
            #print "|",hostname,"\t\t\t\t|\t\t\t\t",i,"\t\t\t|\t\t\t",domtotal
    print "\n\nTotal No of Objects Downloaded : ",totobjects," \t Total Size of Objects Downloaded : ",totalsize        
    '''
    #total object of each type  
    urllist=[]
    a=0
    with open("conndown.txt","a") as file:
              file.truncate()
              file.write("DOWNLOAD TREE FOR OBJECTS USING CONNECTIONID :\n")
              file.write("--------------------------------------------\n\n\n\n")
    with open("objtree.txt","a") as file:
      file.write("OBJECT TREE FOR USING PAGE REFERRER :\n")
      file.write("--------------------------------------------\n\n\n\n")
    count=0;
    x=1;
    objnodeid=1
    avgGoodPutDomain=[]
    hostlist=[]
    hostmimelist=[]
    hosturllist=[]
    totobjects=0
    totalsize=0
    for entry in harfile_json['log']['entries']:
       if 'url' in entry['request']:
          url=entry['request']['url']
          hostname = (urlparse.urlparse(url).hostname)
          #connection=entry['connection']
          if(count==0):
            pageStartTime=entry['startedDateTime']
            count+=1
          domtotal=0
          con_obj_size=0
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
          if(urllist.count(hostname)==0):
            i=0
            urllist.append(hostname)
            for data in harfile_json['log']['entries']:
                if 'url' in data['request']:
                  url1=data['request']['url']
                  hostname1 = (urlparse.urlparse(url1).hostname)
                  if hostname==hostname1:
                    if(hostlist.count(hostname1)==0):
                      hostlist.append(hostname1)
                      host=hostname1
                      hostmimelist.append([url1])
                      #hosturllist.append([url1])
                      #hosturllist[hostlist.index(hostname1)].append(url1)
                    else:
                      #hostlist.append(hostname1)
                      hostmimelist[hostlist.index(hostname1)].append(url1)
                      #hosturllist.append([url1])
                      #hosturllist[hostlist.index(hostname1)].append(url1)
                    if 'mimeType' in data['response']['content']:
                      i=i+1
                      totobjects+=1
                    domhead=data['response']['headersSize']
                    dombody=data['response']['bodySize']
                    if(dombody==-1):
                      dombody=0
                    if(domhead==-1):
                      domhead=0
                    contentsize=contentsize+dombody+domhead
                    domtotal+=(domhead+dombody)
                    connection=data['connection']
                    for o in range(len(data['request']['headers'])):
                      if((data['request']['headers'][o]['name']=='referer') or (data['request']['headers'][o]['name']=='Referer')):
                        objref.append(data['request']['headers'][o]['value'])
                        a+=1
                      
                    if connlist.count(connection)==0:
                      connlist.append(connection)
                      connmimelist.append([url1])
                      connestablish.append(data['timings']['connect'])
                      connwait.append([data['timings']['wait']])
                      connreceive.append([data['timings']['receive']])
                      connsend.append([data['timings']['send']])
                      tottime=(data['timings']['wait'])+(data['timings']['receive'])+(data['timings']['send'])
                      conntotal.append([tottime])
                      #con_obj_size=con_obj_size+dombody+domhead     #edit required
                      #print data['response']['bodySize'] , "    "
                      mytime=datetime.strptime(data['startedDateTime'][12:23],"%H:%M:%S.%f")
                      connStartTime.append([mytime])
                      mytime1=mytime+timedelta(milliseconds=tottime)
                      connEndTime.append([mytime1])
                      connobjlist.append(1)
                      if(timeforDNS<data['timings']['dns']):
                        timeforDNS = data['timings']['dns']
                      connsizelist.append([dombody+domhead])
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
                      #con_obj_size=con_obj_size+dombody+domhead     #edit required
                      connobjlist[connlist.index(connection)]+=1
                      connsizelist[connlist.index(connection)].append(dombody+domhead)
                      connmimelist[connlist.index(connection)].append(url1)
                    
            #print "\n"
            #print "----------------------------------------------------------------------------------------------------------------------------"
            #print hostname,":",i," | size :",domtotal,"|  No.of.connections opened: ",len(connlist) , "Host Number:", x, \
                  #"| Time For DNS: ",timeforDNS
            totalsize+=domtotal 
            with open("hostsizegraph.csv","a") as hostsizegraphfile:
              hostsizegraphfile.write(host)
              hostsizegraphfile.write(",")
              hostsizegraphfile.write(str(domtotal))
              hostsizegraphfile.write("\n")
            
            print "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
            print hostname,"\n-----------------------------\n\t\tNo.of.Objects Downloaded : ",i," \n\t\tSize of Objects Downloaded : ",domtotal,\
                  "\n\t\tNo.of.TCP Connections Opened : ",len(connlist)
            with open("conndown.txt","a") as file:
              file.write("HostName : ")
              file.write(hostname)
              file.write("\n---------------\n")
            for k in range(len(connlist)):
              #print "No.of.objects downloaded are:",connobjlist[k]
              #print "Size of Objects downloaded are:",connsizelist[k]
              maxGoodPut.append(max(connsizelist[k])/connreceive[k][connsizelist[k].index(max(connsizelist[k]))])
              avgGoodPut.append(domtotal/sum(connreceive[k]))
              connActiveTime.append(max(connEndTime[k])-min(connStartTime[k]))
              activeper.append(sum(conntotal[k])/(connActiveTime[k].total_seconds()*1000))
              idleper.append(1-activeper[k])
              print "Connection : ",connlist[k],"\n\t\tNo.of.Objects in this Connection : ",connobjlist[k],\
                    "\n\t\tSize of the Objects Downloaded in this connection : ",sum(connsizelist[k])
              #print "Connection",connlist[k]," : \n Establishment time : ",connestablish[k]," | Waiting Time:",sum(connwait[k]),\
              #" | Receiving Time:",sum(connreceive[k])," | Sending Time:",sum(connsend[k]) ,"\n | Connection Active Time:",connActiveTime[k],\
              #" | Active % :",activeper[k] , " | Idle % :",idleper[k] , " | Avg GoodPut :",avgGoodPut[k],"\n | Max GoodPut: " ,maxGoodPut[k]
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
                  file.write(objref[k+j])
                  file.write("\n\n")
                  objnodeid+=1
            avgGoodPutDomain.append(sum(avgGoodPut)/len(avgGoodPut))
            lastObjEndTime=max(connEndTime[len(connlist)-1])  
    print "\tTotal Size of all the objects Downloaded is : ",totalsize
    zipped=zip(hostlist,hostmimelist)
    #for k in range(len(hostlist)):
    #  with open("tuple.txt","a") as file:
    #    file.write(zipped[k])
    #  print zipped[k]
    #  print "\n\n"
    #for 
    #for k in range of 
    #for k in range(len(hosturllist)):
     # print hosturllist[k]
    with open("tupletree.txt","a") as file: 
      for t in zipped:
        file.write('#'+' '.join(str(s) for s in t)+ '\n\n')
    mytime=datetime.strptime(pageStartTime[12:23],"%H:%M:%S.%f")
    #mytime1=datetime.strptime(lastObjEndTime[12:23],"%H:%M:%S.%f")    
    '''
    print "\nPage Load Time :",(lastObjEndTime-mytime).total_seconds()*1000 ,"ms"
    print 
    print "Average Good Put of Network is:",sum(avgGoodPutDomain)/len(avgGoodPutDomain)
    if max(avgGoodPutDomain)<(sum(avgGoodPutDomain)/len(avgGoodPutDomain)):
      print "Yes It was Utilised."
    else:
      print "No It was Not Utilised."
    print "TotalSize"
    print totalsize
    '''
    mimelist=[]
    mimecount=[]
    for entry in harfile_json['log']['entries']:
        if 'mimeType' in entry['response']['content']:
          mimetype=entry['response']['content']['mimeType']
          if (mimelist.count(mimetype)==0):
            #print mimetype.split('/')[1]
            mimelist.append(mimetype)
            mimecount.append(1);
          else:
            mimecount[mimelist.index(mimetype)]+=1
    for k in range(len(mimelist)):
      print mimelist[k], "     :",mimecount[k]
    

    
if __name__ == '__main__':
    argparser = argparse.ArgumentParser(
        prog='parsehar',
        description='Parse .har files into comma separated values (csv).')
    argparser.add_argument('harfile', type=str, nargs=1,
                        help='path to harfile to be processed.')
    args = argparser.parse_args()

    main(args.harfile[0])                
                  
                  
                
                
