package test;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import implementation.Chord;
import implementation.ChordException;
import implementation.DHTKey;
import implementation.Message;
import implementation.Node;
import implementation.Message.msgEnum;

public class Console {
	// begin to take user input
	public static int getRandomArbitrary() {
		  return (int) (Math.random() * (4000) + 5000);
		}
/**	
	public void fileEnum(int value) {
	    this.value = value;
	  }
	public static enum fileEnum 
	{ addNode(1), 
		deleteNode(2), 
		addFile(3),
		lookupFile(4),
		printNodeList(5), 
		printSortedNodeList(6),
		printNodeFingerTable(7),
		exit(8);
		private int value;	
	} **/
	
	public static Message console_mssg = new Message();
	public static void consoleScreen(PrintStream out,PrintStream out1,PrintStream out2,String host) throws MalformedURLException, InterruptedException
	{
		int port;        
		DHTKey fileKey,nodeKey;
		String fileName;
		String nodeID;
		Node node = null;
		URL url;
		double average=0;
		
				
		Scanner userinput = new Scanner(System.in);
			   
					System.out.println("\n Welcome to the Chord Java Application");    
					
					System.out.println("\n Enter the number of nodes for "+
						                 "creating a stabilized overlay Network\n");
					Main1.NUM_OF_NODES = userinput.nextInt();
					
					long startTime = System.currentTimeMillis();
					
					Main1.createOverlayNetwork(out, host);
										
					long endTime = System.currentTimeMillis();
					int interval = (int) (endTime - startTime);
					int elapsedTime = interval / 1000;
					out.printf("Time Elapsed in Creating Stabilized Network of %d nodes : %d.%d",Main1.NUM_OF_NODES,elapsedTime,
								interval % 1000); 
			
					//Main1.startNodeThreads(out1);
					/**
					URL url = new URL("http","192.168.1.1",9000+"");
					try {
						Main1.mainNode = Chord.createNode(url.toString());
					} catch (ChordException e) {
						e.printStackTrace();
						System.exit(0);
					}
					
					Main1.NUM_OF_NODES++; **/
					
					while(true) {
					System.out.println("\n Select one of the following menu options : "); 
					System.out.println("\n 1. To add a node to the system "+
					                   "\n 2. To delete a node from the system"+
							           "\n 3. To add a file to the system"+
					                   "\n 4. To lookup a file "+
							           "\n 5. To list all the nodes in arrival order"+
							           "\n 6. To list all the nodes in Chronological order"+
							           "\n 7. To list the finger table of all nodes"+
							           "\n 8. To list the finger table of a given node"+
							           "\n 9. To compute average successor lookup cost"+
							           "\n 10. To exit the chord application");
					int number = userinput.nextInt();
						switch(number)
						{
						   case 1:
							   System.out.println("\n Enter the number of new nodes you wish to create");
							   number = userinput.nextInt();
							   
							 while(number>0)
							 { 
						   	   port = getRandomArbitrary(); 
							   url = new URL("http", host, port, "");
								try {
									node = (Node)Chord.createNode(url.toString());
								} catch (ChordException e) {
									e.printStackTrace();
									System.exit(0);
								}
								//node.startThread(out);
								Main1.NUM_OF_NODES++;
							 	System.out.println(node+" Created Successfully");
							 	number--;
							 	}
							   
							   break;
							   
						   case 2:
							   
							    System.out.println("\n Please select one of the following option to delete node");
							    while(true){
							    System.out.println("\n 1. To delete a node using its NodeID"+
					                              "\n 2. To delete a node using its ThreadID");
							    
							    number = userinput.nextInt();
							    
							    if(number==1){
							    	
							    	System.out.println("\n Enter the nodeID");
							    	
							    	nodeID = userinput.next();
							    	nodeKey = new DHTKey(nodeID);
							    	 node = Chord.sortedNodeMap.get(nodeKey); 
							    	 if (node== null) {
							    	 System.out.println("\n No such node exits for given NodeID");}
							    	 else{
							    		 System.out.println("found "+node);
								 		  node.deleteNode(); 
								 		  node = null; 
								 		  System.out.println("Successfully deleted node");}
							    		
							        	 
							    	
							    	break;
							    }
							    else if(number==2){
							    	System.out.println("\n Enter thread ID of the node");	    	
							    	int tid = userinput.nextInt();
		    
							    	break;
							    }
							    else{
								   System.out.println("\n Invalid Choice, please enter the choice again");				                             
							    }
							    }// End of while(true)
							   break;
						   case 3:
							   System.out.println("\n Please enter the name of the file to be added");
							   fileName = userinput.nextLine();
							   fileKey = new DHTKey(fileName);
							   
		                       /** Without IPC **/
							   node = Chord.getNode(0).findSuccessor(fileKey);
		                       if(node!=null)
		                    	   node.addFile(fileName, fileKey);
		                       else
		                       System.out.println("\n Couldnt find the successor for the file");   
		                    	
		                        /** With IPC **/ 
		                    	console_mssg.msg_enum = msgEnum.FIND_SUCCESSOR;
		               		    console_mssg.destNode = node; 
		               		    console_mssg.srcNode = Main1.mainNode;
		               		    console_mssg.sender = Main1.mainNode;
		               		    console_mssg.data = (DHTKey) fileKey;	   
		                    	                     	   
		                     
							   break ;
							   
						   case 4:
							   System.out.println("\n Please enter the name of the file to be looked up");
							   fileName = userinput.nextLine();
							   fileKey = new DHTKey(fileName);
							   
							   node = Chord.getNode(0).findSuccessor(fileKey);
		                       if(node!=null) {
		                    	 System.out.println("Found file at "+node); }
             	  
		                       else
		                       System.out.println("\n Couldnt find the successor for the file");   
		                    	
							   break ;
							   
						   case 5:
								System.out.println("\n Printing Nodes in the arrival order :\n"); 
								out.println("\n Printing Nodes in the arrival order :\n");
							   for (int i = 0; i < Main1.NUM_OF_NODES; i++) {
									node = Chord.getNode(i);
									System.out.println(node);
									out.println(node);
									
							   }
							   break;
						        
						case 6:
							System.out.println("\n Printing Nodes in the sorted order :\n"); 
							out.println("\n Printing Nodes in the sorted order :\n"); 
							for (int i = 0; i < Main1.NUM_OF_NODES; i++) {
									node = Chord.getSortedNode(i);
									System.out.println(node);
									out.println(node);
									
							   }		
							 break;
						
						case 7 :	
							System.out.println("\n Printing Finger Table of the Chord Ring Network :\n"); 
							out.println("\n rinting Finger Table of the Chord Ring Network :\n"); 
							for (int i = 0; i < Main1.NUM_OF_NODES; i++) {
									node = Chord.getSortedNode(i);
									node.printFingerTable(out);
									node.printFingerTable(System.out);	
							   }		
							 break;
							
						case 8 :
							System.out.println("\n Enter the nodeID :");
							nodeID = userinput.nextLine();
							nodeKey = new DHTKey(nodeID);
							node = Chord.sortedNodeMap.get(node.getNodeKey());
							if(node!=null)
							node.printFingerTable(System.out);	
							else
								System.out.println("\n Node not found"); 
							break;
													
						case 9:
							average = Main1.avgLookupCount(out2);
							System.out.println("Avergae Lookup Count for "+Main1.NUM_OF_NODES+" = "+number); 
							out2.println("\n Avergae Lookup Count for "+Main1.NUM_OF_NODES+" = "+number); 
						    break;
						case 10:
							System.out.println("\n Bye Bye See you Again"); 
							System.exit(1);
							
							break;
							
						default :
							System.out.println("\n Invalid Input, Please enter a number "
									+ "between 1 to 9"); 
								
							
						}
			    }
	}
}