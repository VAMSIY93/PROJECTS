import socket
import time
import datetime
import pyDes

def send(dest, msg):
	if dest=='client':
		clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		clientsocket.connect(('localhost', 8070))
		clientsocket.send(msg)
	elif dest=='AS':
		clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		clientsocket.connect(('localhost', 8090))
		clientsocket.send(msg)
	elif dest=='TGS':
		clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		clientsocket.connect(('localhost', 8080))
		clientsocket.send(msg)
	else:
		print 'Not Sent'

if __name__ == "__main__":
	#1st request
	msg = raw_input('Enter your input: ')
	send('AS', msg)


	#1st recv
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind(('localhost', 8070))
	serversocket.listen(5)
	connection, address = serversocket.accept()
	buf = connection.recv(256)
	plain = ''
	if len(buf) > 0:
		print buf
		key = pyDes.des("VAMSI000", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
		plain = key.decrypt(buf)
		print plain


	#2nd request
	IP = socket.gethostbyname(socket.gethostname())
	ADC = IP + ':8070'
	ts = time.time()
	tStamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
	recvd = plain.split(' ')
	msg = '2015MCS2358 '+ ADC + ' ' + tStamp
	key = pyDes.des(recvd[0], pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
	auth = key.encrypt(msg)
	msg = '1 ' + recvd[4] + ' ' + auth
	send('TGS', msg)
	print 'Successfull sent'


	#2nd recieve
	connection, address = serversocket.accept()
	buf = connection.recv(256)
	if len(buf) > 0:
		print 'RECIEVED'
		key = pyDes.des("CLNT$TGS", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
		plain = key.decrypt(buf)
		print plain


	#3rd request
	recvd = plain.split(' ')
	ts = time.time()
	tStamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
	msg = '2015MCS2358 ' + ADC + ' ' + tStamp
	key = pyDes.des(recvd[0], pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
	auth = key.encrypt(msg)
	msg = recvd[3] + ' ' + auth
	send('server', msg)


