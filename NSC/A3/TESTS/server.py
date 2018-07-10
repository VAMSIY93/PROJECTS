import socket
import csv

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversocket.bind(('localhost', 8090))
serversocket.listen(5) 

while True:
	connection, address = serversocket.accept()
	buf = connection.recv(128)
	if len(buf) > 0:
		print buf
		msg = buf.split(' ')
		print msg[0]
		valid = False
		if msg[0]=='login':
			with open('user.csv') as file:
				spamreader = csv.reader(file, delimiter=',')
				for row in spamreader:
					if row[0]==msg[1] and row[1]==msg[2]:
						valid = True
						break
		if valid==True:
			print 'sending Ticket'

