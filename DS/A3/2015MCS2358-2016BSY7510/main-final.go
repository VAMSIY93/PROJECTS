package main

import (
	"fmt"
	//"container/list"
	"math/rand"
	"sync"
	"time"
        //"sync/atomic" 
        //"hash/fnv"
        //"crypto/sha1"
        //"strconv" 
     )



type Enum int

/** States */
const (
  STARTED Enum = 0 /** for node initiating tx **/
  READY = 1 /** for node receving tx **/ 
  AWAITING_REPLY = 2 /** for node waiting replies from members **/
  COMMITED = 3 
  ABORTED = 4
)

/** Protocol messages */
const(
 VOTE_REQ = 0
 VOTE_COMMIT = 1
 VOTE_ABORT = 2
 COMMIT = 3
 ABORT = 4
 COMMIT_ACK = 5
)

const(
 PING Enum = 0
 PONG = 1
)

type txMsg struct {
     txID  int
     msgType int
     timestamp int
     senderID int /** thread ID */ 
}

type pingMsg struct {
     msg string
     nodeID int /** node ID */ 
}

type txObj struct {
     txID  int /** tx ID **/
     state Enum
     coordinatorID int /** Coordinator node id which initiated the transaction */
     member1ID int /** ID of other client involved in tx **/
     member2ID int /** ID of other client involved in tx **/
     member1Voted bool /** Has member 1 voted ? **/
     member2Voted bool /** has member 2 voted ? **/
     votesNeeded int /** The count of votes needed to commit the transaction **/ 
}

type bcastMsg struct {
     txID  int /** tx ID **/
}


type Node struct {
     id int
     txMap map[int]*txObj /* txmap of all the ongoing transactions */
     txList []int /** List of all globally Committed transactions**/
     alive bool
     timestamp int /** timestamp maintained by evernode **/
     txCount int /** global txCount seen by node **/ 

     
}


/**
 * Declaration of global variables 
 **/
var wg sync.WaitGroup 
var twoPhaseMsgCh []chan txMsg
var pingCh []chan pingMsg
var bcastCh []chan bcastMsg
//var paxosCh []chan paxosMsg
var global_tx_count int /** Terminate the simulation when tx count is 1/10th of node count **/
var max_tx_count int/** Max transactions that can take place **/
var max_dead_count int /** Max count of the node that can die **/ 
var global_dead_count int
var nodeCount int
var mutex sync.Mutex
var bcast_mutex sync.Mutex
var nodes []Node
/*******************************************************************
* NAME :           recvMsg
*
* DESCRIPTION :    Receives Message from channels for a given input node
*
* INPUTS :
*       PARAMETERS:
*           *Node    Pointer to Node Struct 
* WORKING :       
* OUTPUTS :
*       PARAMETERS:
*           
*       GLOBALS :
*            None
*       RETURN :
*            Type:   int                    Error code:
*            Values:          
*
/*******************************************************************/
func broadcastTx(bcast_msg bcastMsg) {
   
   bcast_mutex.Lock()
    fmt.Println("Broadcasting tx \n",bcast_msg);
    for i:=0;i<nodeCount;i++ {	
	 
       if(nodes[i].alive==true){
         bcastCh[i]<-bcast_msg } }//End of for
    bcast_mutex.Unlock()
   
}/* End of broadcastTx*/



func recvMsg(node *Node) {
  
  var new_tx_obj *txObj 
  var tx_msg_out txMsg /** Variable to send the trnsaction message to other node **/
  var bcast_msg bcastMsg 

  select {
       case tx_msg_recv := <-twoPhaseMsgCh[node.id]:
        fmt.Println("Node",node.id,"received message", tx_msg_recv)
        
        tx_obj,tx_present := node.txMap[tx_msg_recv.txID]
        
        if(tx_present){
          
          fmt.Println("Node",node.id,"found existing tx object")
         
                
          switch(tx_msg_recv.msgType){
          
            case VOTE_ABORT:
             fmt.Printf("Node %d Aborting Tx %d \n",node.id,tx_msg_recv.txID)
             tx_obj.state = ABORTED
             break

            case VOTE_COMMIT: 
               tx_obj.votesNeeded = tx_obj.votesNeeded -1;
                if(tx_msg_recv.senderID==tx_obj.member1ID) {
                   tx_obj.member1Voted = true; }
                if(tx_msg_recv.senderID==tx_obj.member2ID) {
                   tx_obj.member2Voted = true; }

               /*
                * Send a COMMIT message only when you have received
                * COMMIT_ACK from both members 
                */
               if(tx_obj.votesNeeded==0){            
               /** Send 2Phase COMMIT/ABORT to the nodes **/
               /** Creating Transaction Message **/
               tx_msg_out.txID = tx_msg_recv.txID  
               tx_msg_out.msgType = COMMIT
               tx_msg_out.timestamp = node.timestamp 
               tx_msg_out.senderID = node.id        
               
                /** Sending commit tx_msg to member1 **/ 
                twoPhaseMsgCh[tx_obj.member1ID]<-tx_msg_out
                /** Increment the timestamp **/ 
               node.timestamp=node.timestamp+1

               /** Sending commit tx_msg to member1 **/ 
               twoPhaseMsgCh[tx_obj.member2ID]<-tx_msg_out
               /** Increment the timestamp **/ 
               node.timestamp=node.timestamp+1 
               
               /* Set VotesNeeded for COMMIT/ABORT ACK **/
               tx_obj.votesNeeded=2  
               tx_obj.member1Voted = false
               tx_obj.member2Voted = false
               tx_obj.state = AWAITING_REPLY} // End of if(tx_obj.votesNeeded==0)
               break
         
             case COMMIT_ACK:
                
                tx_obj.votesNeeded = tx_obj.votesNeeded -1;
                if(tx_msg_recv.senderID==tx_obj.member1ID) {
                   tx_obj.member1Voted = true; }
                if(tx_msg_recv.senderID==tx_obj.member2ID) {
                   tx_obj.member2Voted = true; }

                /** Broadcast tx only when you have received commit ack from both **/
                if(tx_obj.votesNeeded==0){            
               
                 tx_obj.state = COMMITED

                /** bcast the transaction **/
                bcast_msg.txID = tx_obj.txID
                fmt.Println("Node",node.id,"is broadcasting msg",bcast_msg)
                broadcastTx(bcast_msg) /** A node broadcasts tx to all and itself **/
                          
                /** Atomically increment the global tx count **/
                mutex.Lock()
                global_tx_count+=1
                mutex.Unlock() }
           

             case COMMIT:

               /** Make the Node Die **/ 
               r2:=rand.Intn(100)
               if(r2<30 && global_dead_count<=max_dead_count) { /** Node will die with 30% probability **/
                  node.alive = false
                  fmt.Println("Node",node.id," is Dying")
                  mutex.Lock()
                  global_dead_count+=1
                  mutex.Unlock() 
                  //time.Sleep(time.Second * 2)
                  return 
               }
               
               tx_msg_out.txID = tx_msg_recv.txID  
               tx_msg_out.msgType = COMMIT_ACK
               tx_msg_out.timestamp = node.timestamp 
               tx_msg_out.senderID = node.id        
               
                /** Sending commit tx_msg to member1 **/ 
                twoPhaseMsgCh[tx_obj.coordinatorID]<-tx_msg_out
                /** Increment the timestamp **/ 
                node.timestamp=node.timestamp+1

              case ABORT:
                tx_obj.state = ABORTED 
                             
              default:
               
   
            }//End of switch  
                
      }/** if(tx_present) */
      if(tx_present==false) {
        
          new_tx_obj=new(txObj)
          
          if(tx_msg_recv.msgType==VOTE_REQ){
             
             new_tx_obj.txID =tx_msg_recv.txID  
             new_tx_obj.state = READY
             new_tx_obj.coordinatorID = tx_msg_recv.senderID 
                    
           /** Add transaction to the txMap **/
           
           node.txMap[tx_msg_recv.txID] = new_tx_obj
           
           /** Send 2Phase Message back to the requester **/
            /** Creating Transaction Message **/
           tx_msg_out.txID = tx_msg_recv.txID  
           tx_msg_out.msgType = VOTE_COMMIT
           tx_msg_out.timestamp = node.timestamp 
           tx_msg_out.senderID = node.id        
           
           /** Make the Node Die **/ 
            r3:=rand.Intn(100)
            if(r3<10 && global_dead_count<=max_dead_count){ /** Node will die with 10% probability **/
              
               node.alive = false
               fmt.Println("Node",node.id," is Dying")
               mutex.Lock()
               global_dead_count+=1
               mutex.Unlock() 
               //time.Sleep(time.Second * 2)
               return 
            }
               


           /** Sending tx_msg to requester **/ 
           twoPhaseMsgCh[new_tx_obj.coordinatorID]<-tx_msg_out
 
           /** Increment the timestamp **/ 
           node.timestamp=node.timestamp+1
       
         } /** End of  if(tx_msg.msgType==VOTE_REQ) **/
       }/** End of if(!tx_present) **/ 

        default:
        // fmt.Printf("Node %d No Message Received \n",node.id)
     }     

    /**
     * Receive Ping Message
     */
     select {
       case pingMsg := <-pingCh[node.id]:
        fmt.Printf("received ping message",pingMsg)
        
       default:
        //fmt.Printf("Node %d No Ping Message Received \n",node.id)
      }  

      
    /**
     * Receive Broacast Message
     */
    select {
       case bmsg := <-bcastCh[node.id]:

        fmt.Println("Node",node.id,"received bcast message",bmsg)
        node.txList[node.txCount]=bmsg.txID
       
        fmt.Printf("Node %d Added Transaction: tx_no. %d , tx_id %d in the list \n",
                    node.id,node.txCount,bmsg.txID)
        node.txCount+=1  
              
                      
       default:
         //fmt.Printf("Node %d No Broadcast Message Received \n",node.id)
      }  
      
}


/**
func hashTx(num int) int {
        h := fnv.New32a()
        h.Write([]byte(num))
        return h.Sum32()
}
**/

func nodeRun(node *Node) {

       defer wg.Done()  	
       
       var tx_obj *txObj
       var txID int
       var tx_msg_out txMsg
       var member1ID,member2ID int
       var initiatedTx bool /** Flag to ensure a node initiates only one tx **/
       
       initiatedTx = false    

      // var txMap map[int]txObj /** Map of all the ongoing transactions **/
        /* create a map*/
       node.txMap = make(map[int]*txObj) 
   

       for ;node.alive;
       {
 

         r1:=rand.Intn(nodeCount)
         
         if (r1%2 != 0 && r1 < (nodeCount/2) && initiatedTx==false ) {
         
           txID = ((node.id<<6) | node.timestamp) /** Generating Unique Transaction ID **/
           /** Allocate memory to tx_obj **/
           tx_obj = new(txObj)        
           
           tx_obj.txID = txID
           tx_obj.state = STARTED
           tx_obj.coordinatorID = node.id
           tx_obj.votesNeeded = 2

            /** Genearting MemberIDs to be involved in transaction **/ 
          
           member1ID = (node.id+1+rand.Intn(nodeCount))%nodeCount        
           member2ID = (node.id+2+rand.Intn(nodeCount))%nodeCount
 
           tx_obj.member1ID = member1ID
           tx_obj.member2ID = member2ID

           fmt.Printf("Node with id %d is initiating transaction %d with members %d and %d \n",node.id,txID,
                      tx_obj.member1ID,tx_obj.member2ID)
           
           /** Add transaction to the txMap **/
           node.txMap[txID] = tx_obj


           /** Creating Transaction Message **/
           tx_msg_out.txID = txID  
           //tx_msg_out.seqNum = 
           tx_msg_out.msgType = VOTE_REQ
           tx_msg_out.timestamp = node.timestamp 
           tx_msg_out.senderID = node.id        
           /** Sending tx_msg to member1 **/ 
           twoPhaseMsgCh[tx_obj.member1ID]<-tx_msg_out

            /** Increment the timestamp **/ 
           node.timestamp=node.timestamp+1

            /** Sending tx_msg to member1 **/ 
           tx_msg_out.timestamp = node.timestamp 
           twoPhaseMsgCh[tx_obj.member2ID]<-tx_msg_out

           /** Increment the timestamp **/ 
           node.timestamp=node.timestamp+1

           /** Set the initiatedTx flag **/
           initiatedTx=true
       } //End of if
		

        /** Receive Message **/
        time.Sleep(time.Second * 2)
        recvMsg(node) 
  
        /** 
         * If node txCount becomes equal to global_tx_count 
         * terminate goroutine
         */

        if(node.txCount==max_tx_count){
         break }

  }/* End of For LOOP */

}





func main() {

	var n int
       
        fmt.Println("\n Enter the number of Nodes for Distributed-Ledger-Simulation") 
        fmt.Scan(&nodeCount) 
	//nodeCount = 15
	n = nodeCount
	
        /******************** Initialise the global transaction variables *****************************/
        
        /*Initialise mutex on global_tx_count */ 
        mutex = sync.Mutex{}
        /*Initialise mutex on bcast messages */
        bcast_mutex = sync.Mutex{}
        /*Initialise global_tx_count and global_dead_count */
        global_tx_count=0 
        global_dead_count=0 
        /*Initialise wait group counter*/
        wg.Add(nodeCount)     

        
       
        nodes = make([]Node,n,2*n)
        twoPhaseMsgCh = make([]chan txMsg,n,2*n)
        pingCh = make([]chan pingMsg,n,2*n) 
        bcastCh = make([]chan bcastMsg,n,2*n)        
               
       for i:=0;i<n;i++ {	
	 nodes[i].id=i
	 nodes[i].alive=true
         nodes[i].txList=make([]int,n,2*n) //list.New() 
         nodes[i].txCount=0
         
         twoPhaseMsgCh[i] =make(chan txMsg,2*n)
	 pingCh[i] = make(chan pingMsg,2*n)
         bcastCh[i] = make(chan bcastMsg,2*n)
       }
        
        fmt.Println("\n Enter the maximum count of the tx that can take place") 
        fmt.Scan(&max_tx_count)  
        //max_tx_count = 3

        fmt.Println("\n Enter the maximum count of node that can die") 
        fmt.Scan(&max_dead_count) 
        
        /* Start GoRoutines aka Threads **/
        for i:=0;i<n;i++{
          go nodeRun(&nodes[i])
        }  
	 
        
	

	
       wg.Wait()
      /** Print hash of the linked list for each node **/
      
      var sum int
      //var hashValue int

      //h := sha1.New()

      for i:=0;i<n;i++{
      sum = 0  
     
         fmt.Printf("\nPrinting txs for Node %d : ",i)
         if(nodes[i].alive==false){
         fmt.Printf("Ouch !! Node is dead") 
         }
         
         for e:=range nodes[i].txList {
         if(nodes[i].txList[e]==0){ break} 
         sum = sum + nodes[i].txList[e] 
         fmt.Printf("%d \t",nodes[i].txList[e]) } // End of for loop
         
         //t := strconv.Itoa(sum)
         //h.Write([]byte(t))
         //hash_val :=h.Sum(nil)    
         //fmt.Printf("Txhash for Node %d is %x \n",i,hash_val) 
         if nodes[i].alive {	
           fmt.Printf("\nHash for Node %d is %d \n",i,sum)
       }
      }
         
         fmt.Println("\n done")
}
