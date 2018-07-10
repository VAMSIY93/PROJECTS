from flask import *
import socket

import csv
import time
import datetime
import pyDes


app = Flask(__name__)

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

@app.route('/authenticate', methods = ['POST'])
def authenticate():
	uid = request.form['uid']
	psw = request.form['psw']
	
	valid = False
	with open('user.csv') as file:
		spamreader = csv.reader(file, delimiter=',')
		for row in spamreader:
			if row[0]==uid:
				valid = True
				pwd=row[1]
				break

	password = pwd
	if valid==True:
		send('AS','login '+uid)
		
		serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		serversocket.bind(('localhost', 8070))
		serversocket.listen(5)
		connection, address = serversocket.accept()
		buf = connection.recv(256)
		plain = ''
		if len(buf) > 0:
			if len(pwd)<8:
				pwd = pwd.ljust(8, '0')
			else:
				pwd = pwd[:8]

			key = pyDes.des(pwd, pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
			plain = key.decrypt(buf)
			f1 = open('plain.txt','w')
			f1.write(plain)
			f1.close()

			f2 = open('uid.txt','w')
			f2.write(uid)
			f2.close()

			msg = plain.split(' ')
			print 'Ticket TGS:  '+msg[4]
			if password==psw:
				return redirect('/showLinks')
			else:
				return redirect('/')

		else:
			return redirect('/')
	else:
		return redirect('/')		


@app.route('/getticket')
def getticket():
	
	f2 = open('plain.txt','r')
	plain = f2.read()
	f2.close()
	
	IP = socket.gethostbyname(socket.gethostname())
	ADC = IP + ':8070'
	ts = time.time()
	tStamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
	recvd = plain.split(' ')
	f3 = open('uid.txt','r')
	uid = f3.read()
	f3.close()
	msg = uid+ ' ' + ADC + ' ' + tStamp
	
	key = pyDes.des(recvd[0], pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
	auth = key.encrypt(msg)
	msg = '1toy' + recvd[4] + 'toy' + auth
	send('TGS', msg)

	#Recieve Response
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind(('localhost', 8070))
	serversocket.listen(5)
	connection, address = serversocket.accept()
	buf = connection.recv(256)
	if len(buf) > 0:
		key = pyDes.des("CLNT$TGS", pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
		plain2 = key.decrypt(buf)
		

	recvd = plain2.split(' ')
	print 'Ticket Tv : '+recvd[3]
	ts = time.time()
	tStamp = datetime.datetime.fromtimestamp(ts).strftime('%H:%M:%S')
	msg = uid + ' ' + ADC + ' ' + tStamp
	key = pyDes.des(recvd[0], pyDes.CBC, "\0\0\0\0\0\0\0\0", pad=None, padmode=pyDes.PAD_PKCS5)
	auth = key.encrypt(msg)
	msg = recvd[3] + ' ' + auth
	#send('server', msg)

	return redirect('/congrats')


@app.route('/congrats')
def congrats():
	return render_template('final.html')

@app.route('/showLinks')
def showLinks():
	return render_template('menu.html')

@app.route('/')
@app.route('/<name>')
def home(name=None):
	return render_template('login.html',name= name)

if __name__ == '__main__':
	app.run(debug=False,host='localhost')