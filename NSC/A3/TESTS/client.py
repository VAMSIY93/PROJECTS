import socket
import time

#msg = raw_input('Enter your input: ')
clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientsocket.connect(('localhost', 8090))
msg = 'login 2015MCS2358 VAMSI'
clientsocket.send(msg)

time.sleep(2)
msg2 = 'login 2015MCS2347 MILAN'
clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientsocket.connect(('localhost', 8090))
clientsocket.send(msg2)