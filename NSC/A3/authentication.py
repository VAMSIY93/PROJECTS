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
	serversocket.bind(('localhost', 8090))
	serversocket.listen(5) 
	
	while True:
		connection, address = serversocket.accept()
		buf = connection.recv(256)
		if len(buf) > 0:
			print buf
			msg = buf.split(' ')
			valid = False
			if msg[0]=='login':
				with open('user.csv') as file:
					spamreader = csv.reader(file, delimiter=',')
					for row in spamreader:
						if row[0]==msg[1]:
							valid = True
							pwd = row[1]
							break
			if valid==True:
				print 'sending Ticket'

				IP = socket.gethostbyname(socket.gethostname())
				ADC = IP + ':8070'
				ts = time.time()
				tStamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
				tkt = 'CLNTTGS1 ' + msg[1] + ' ' + ADC + ' TGS001 ' + tStamp + ' 10'
				key = pyDes.des("$TGS001$", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
				TktTgs = key.encrypt(tkt)

				if len(pwd)<8:
					pwd = pwd.ljust(8, '0')
				else:
					pwd = pwd[:8]
				key = pyDes.des(pwd, pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
				text = 'CLNTTGS1 TGS001 ' + tStamp + ' 10 ' + TktTgs
				cipher = key.encrypt(text)
				print cipher
				send('client', cipher)
			else:
				send('client' 'Invalid UserID')
