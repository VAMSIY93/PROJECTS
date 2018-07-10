import socket
import csv
import time
import datetime
import pyDes

def send(dest, msg):
	if dest=='client':
		clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		clientsocket.connect(('localhost', 8070))
		clientsocket.send(msg)


if __name__ == "__main__":
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind(('localhost', 8080))
	serversocket.listen(5) 
	
	while True:
		connection, address = serversocket.accept()
		buf = connection.recv(256)
		if len(buf) > 0:
			print buf
			msg = buf.split(' ')			
			key = pyDes.des("$TGS001$", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
			tgs = key.decrypt(msg[1])
			tkt = tgs.split(' ')
			key = pyDes.des(tkt[0], pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
			plain = key.decrypt(msg[2])
			clnt = plain.split(' ')

			#ckeck ID's
			if clnt[0]==tkt[1] and clnt[1]==tkt[2]:
				ID = True
			else:
				ID = False

			#check lifetime
			print 'time: '+clnt[2]+'  CLNT:'+ plain+'  TKT:'+tgs
			as_tsp = tkt[4].split(':')
			clnt_tsp = clnt[2].split(':')
			minutes = (int(clnt_tsp[0]) - int(as_tsp[0]))*60 + int(clnt_tsp[1]) - int(as_tsp[1])
			if minutes<=int(tkt[5]):
				life = True
			else:
				life = False


			print ID
			print life
			if ID==True and life==True:
				ts = time.time()
				tStamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
				text = 'CLNTSRVR ' + clnt[0] + ' ' + clnt[1] + ' SERVER0001 ' + tStamp + ' 10'
				key = pyDes.des("SERVER01", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
				tktV = key.encrypt(text)
				text = 'CLNTSRVR SERVER0001 '+tStamp+' '+tktV
				key = pyDes.des("CLNT$TGS", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
				data = key.encrypt(text)
				print 'data: '+data
				send('client', data)
