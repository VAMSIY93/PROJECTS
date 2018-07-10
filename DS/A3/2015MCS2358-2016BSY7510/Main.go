package main

import (
	"fmt"
	"time"
	"math/rand"
	"strconv"
	"strings"
	"crypto/sha1"
	"sync"
)

type Node struct {
	nodeId int
	alive bool
	stop bool
	txn int
	clock int
	list map[string]string
	txn_list []string
	txn_clock []int
}

func (node Node) insertToList(contents []string) Node {

	var length = len(node.txn_list)
	var recvd_clock, _ = strconv.Atoi(contents[2])

	if length==0 {
		node.txn_list = append(node.txn_list, contents[3])
		node.txn_clock = append(node.txn_clock, node.clock)
	} else {
		node.txn_list = append(node.txn_list, node.txn_list[length-1])
		node.txn_clock = append(node.txn_clock, node.txn_clock[length-1])				
	}

	for i:=length;i>=0;i-- {
		if node.txn_clock[i]<recvd_clock {
			node.txn_list[i] = contents[3]
			node.txn_clock[i] = recvd_clock
			break
		} else if node.txn_clock[i]==recvd_clock {
			var recvd_id, _ = strconv.Atoi(contents[1])
			var splits []string
			splits = strings.Split(node.txn_list[i], "-")
			var list_id, _ = strconv.Atoi(splits[0])
					
			if recvd_id>=list_id {
				node.txn_list[i] = contents[3]
				node.txn_clock[i] = recvd_clock
				break
			} else {
				if i>0 {
					node.txn_list[i] = node.txn_list[i-1]
					node.txn_clock[i] = node.txn_clock[i-1]
				}	
			}
		} else {
			if i>0 {
				node.txn_list[i] = node.txn_list[i-1]
				node.txn_clock[i] = node.txn_clock[i-1]
			}	
		}
	}

	return node
}	

func (node Node) processStart(channel [10]chan string, contents []string, n int) Node {
	//fmt.Println("Processing start at: ",node.nodeId)
	var mutex = &sync.Mutex{}

	var num = rand.Intn(3)
	if num%4==0 {
		msg := "abort "+strconv.Itoa(node.nodeId)+" "+strconv.Itoa(node.clock)+" "+contents[3]
		var ind, _ = strconv.ParseInt(contents[1], 10, 0)
		mutex.Lock()
		channel[ind]<-msg
		mutex.Unlock()
		node.clock = node.clock + 1
	} else {
		msg := "commit "+strconv.Itoa(node.nodeId)+" "+strconv.Itoa(node.clock)+" "+contents[3]
		var ind, _ = strconv.ParseInt(contents[1], 10, 0)
		mutex.Lock()
		channel[ind]<-msg
		mutex.Unlock()
		node.clock = node.clock + 1

		//SAVE TRANSACTION FOR FUTURE WITH TIMEOUT LATER
	}

	return node
}

func (node Node) processAbort(channel [10]chan string, contents []string, n int) Node {
	//fmt.Println("ABORT at: ",node.nodeId)

	if node.list[contents[3]]!="" {
		delete(node.list, contents[3])
	}

	return node
}

func (node Node) processCommit(channel [10]chan string, contents []string, n int) Node {
	var nodes []string
	var mutex = &sync.Mutex{}

	if node.list[contents[3]]!="" {
		//fmt.Println("COMMITING AT: ",node.nodeId)
		var value = node.list[contents[3]]
		nodes = strings.Split(value, ",")
		var cnt, _ = strconv.ParseInt(nodes[2], 10, 0)
		delete(node.list, contents[3])

		if cnt==0 {
			value = contents[0]+","+contents[1]+",1"
			node.list[contents[3]] = value
		} else {
			//SAVE IN ORDER
			node = node.insertToList(contents)
		}

		for i:=0;i<n;i++ {
			msg := "accept "+strconv.Itoa(node.nodeId)+" "+strconv.Itoa(node.clock)+" "+contents[3]
			if i!=node.nodeId {
				mutex.Lock()
				channel[i]<-msg
				mutex.Unlock()
			}
		}
		node.clock = node.clock + 1
	}

	return node
}

func (node Node) processAccept(channel [10]chan string, contents []string, n int) Node {
	//fmt.Println("Accepting at: ",node.nodeId)
	
	//Arranging in Ascending order
	node = node.insertToList(contents)

	return node		
}

func (node Node) processRequest(channel [10]chan string, contents []string, n int) Node {
	fmt.Println(contents)

	return node
}

func (node Node) processStop(channel [10]chan string) Node {
	//if <-channel[node.nodeId]=="" {
		//time.Sleep(time.Second*2)
	//}
	fmt.Println(node.nodeId," reached")

	var final string
	//if <-channel[node.nodeId]=="" {
		var length = len(node.txn_clock)
		for i:=0;i<length;i++ {
			final = final+node.txn_list[i]+":"
		}

		fmt.Println(node.nodeId,": ",final)
		Hash := sha1.New()
		Hash.Write([]byte(final))
		bs := Hash.Sum(nil)

		fmt.Printf("%x: ",bs)
	//}

	return node	
}

func (node Node) performTransaction(channel [10]chan string, n int) Node {
	var num = rand.Intn(100)

	if num%100==0 {
		//fmt.Println("generating Txn at: ",node.nodeId)
		var node1 = rand.Intn(n)
		for ;true; {
			if node1==node.nodeId {
				node1 = rand.Intn(n)
			} else {
				break
			}
		}

		var node2 = rand.Intn(n)
		for ;true; {
			if node2==node.nodeId {
				node2 = rand.Intn(n)
			} else {
				break
			}
		}	

		var tid = strconv.Itoa(node.nodeId)+"-"+strconv.Itoa(node.txn+1)
		node.txn++
		node.clock++
		var txn_nodes = strconv.Itoa(node1)+","+strconv.Itoa(node2)+",0"
		node.list[tid] = txn_nodes

		//SENDING
		var mutex = &sync.Mutex{}

		msg := "start "+strconv.Itoa(node.nodeId)+" "+strconv.Itoa(node.clock)+" "+tid
		//fmt.Println("final msg: ",msg," to ",node1)
		mutex.Lock()
		channel[node1]<-msg
		mutex.Unlock()
		msg = "start "+strconv.Itoa(node.nodeId)+" "+strconv.Itoa(node.clock)+" "+tid
		//fmt.Println("final msg: ",msg," to ",node2)
		mutex.Lock()
		channel[node2]<-msg
		mutex.Unlock()
	}

	return node
}

func (node Node) run(channel [10]chan string, n int) {

	//VARIABLES DECLARED
	var loops = 0
	var contents = make([]string, 0, 4)
	var mutex = &sync.Mutex{}
	var done = false
	
	for ;true; {
		if node.alive==true {


			mutex.Lock()
			select {
				case msg:=<-channel[node.nodeId]:
					mutex.Unlock()
					if node.nodeId==3 {
						fmt.Println(msg)
					}
					contents = strings.Split(msg, " ")
					
					//UPDATE CLOCK
					if msg!="stop" {
						var recvd_clock, _ = strconv.ParseInt(contents[2], 0, 8) 
						var recvd = int(recvd_clock)
						if node.clock<recvd {
							node.clock = recvd+1
						} else {
							node.clock = node.clock+1
						}
					}
						
					//PROCESSING MESSAGE
					if contents[0]=="start" {
						node = node.processStart(channel, contents, n)
					} else if contents[0]=="abort" {
						node = node.processAbort(channel, contents, n)
					} else if contents[0]=="commit" {
						node = node.processCommit(channel, contents, n)
					} else if contents[0]=="accept" {
						node = node.processAccept(channel, contents, n)
					} else if contents[0]=="request" {
						node = node.processRequest(channel, contents, n)
					} else if contents[0]=="stop" {
						node.stop=true
						fmt.Println(node.nodeId," reached")
					} else {
						fmt.Println("Wrong Input at: ",node.nodeId," ",contents[0])
					}
				default:
					mutex.Unlock()
					if node.stop==false {
						node = node.performTransaction(channel, n)	
						time.Sleep(time.Second*1)
					} else {
						node = node.processStop(channel)
						done = true
					}
			}
		}
		
		if done==true {
			break
		}
		loops++
	}
}


func main() {

	//Variables and Data
	var n,t int
	var mutex = &sync.Mutex{}

	fmt.Print("Enter Number of Nodes: ")
	fmt.Scanf("%d",&n)	

	//Variables
	var channel = [10]chan string{}
	
	for i:=0;i<n;i++ {
		channel[i] = make(chan string, 20000)
	}

	//Threads
	var nodeList = [10]Node{}
	for i:=0;i<n;i++ {
		node := Node{i, true, false, 0, 0, make(map[string]string), make([]string,0,1), make([]int, 0, 1)}
		nodeList[i] = node
		go node.run(channel, n)
	}
	
	fmt.Print("Enter 1 You want to end: ")
	fmt.Scanf("%d",&t)


	if t==1 {
		for i:=0;i<n;i++ {
			mutex.Lock()
			channel[i]<-"stop"
			mutex.Unlock()
		}
		fmt.Println("Finished")
	}

	time.Sleep(time.Second*5)
}