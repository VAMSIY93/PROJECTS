#include <iostream>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <ifaddrs.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <string>
#include <map>
#include <openssl/evp.h>
#include <math.h>
#include <pthread.h>
#include <zlib.h>
#include <vector>
#include <sstream>
#include <iomanip>
#include <utility>
#include "md5.h"


#define DATA_SIZE_KILO 12000   //myhelper.h
#define IP_SIZE 40
#define SERVER_BUSY 'x'
#define SAT_MODE "satm"
#define NORMAL_MODE "norm"


using namespace std;


struct RoutingTable
{
char nodeEntry[9];
char ip[22];
};

struct PastryNode
{
char nodeID[9];
char server_ip_port[22];
struct RoutingTable rTable[8][16];
struct RoutingTable leafSet[4];
struct RoutingTable neighbourSet[4];
map<string,string> myMap;
};


//GLOBAL VARIABLES

char ui_data[DATA_SIZE_KILO];
char server_send_data[DATA_SIZE_KILO], server_recv_data[DATA_SIZE_KILO];
char client_send_data[DATA_SIZE_KILO], client_recv_data[DATA_SIZE_KILO];
char ff_client_send_data[DATA_SIZE_KILO], ff_client_recv_data[DATA_SIZE_KILO];
char dd_client_send_data[DATA_SIZE_KILO], dd_client_recv_data[DATA_SIZE_KILO];

unsigned int server_port = 0;
int serverSock;

bool isCreated = false;
bool hasJoined = false;
int retry_count = 5;
int nop = 3;

unsigned int port = 0;
char *sPort, *destPort,first_ipPort[22];
const char* destIP;
char recv_value[4],mode[4],key_value[20];
char cor_get_value[6];
int last=0;
int fR=0;
int recvd_last=0;
char cmdInExec[10];
bool shutFlag=false;
bool isSending=false;
bool dmpFlag=false;
bool getsat=false;

struct PastryNode Node,recvd;

void helperPut(char * );
void storeInKeyMap(char *);
void printDestKeyMap(char *);
void helperGet(char *);
void printKeyValue(char *);
void getValueForKey(char *,char *);
int prefix(char *, char *);
void helperGet(char *);
void getValueForKey(char *,char *);
void printMyKeys();
void helperDump();
void helperSelf();
bool isKeyPresentInMap(char*);
void updateRecvValue(char*);


long int absolute(long int val)
{
	if(val<0)
		return (val*(-1));
	else 
		return val;
}

char* substring(char *string, int position, int length) 
{
	char *pointer;
	int c;

	pointer = (char*) malloc(length + 1);

	if (pointer == NULL) {
		printf("Inside substring : Unable to allocate memory.\n");
		exit( EXIT_FAILURE);
	}


	for (c = 0; c < length; c++)
		*(pointer + c) = string[position+c];

	*(pointer + c) = '\0';

	return pointer;
}

int hexToDecimal(char c)
{
	if(c>=97&&c<=103)
		return ((int)c-87);
	else 
		return ((int)c-48);
}

char decimalToHex(int i)
{
	if(i>=0&&i<=9)
		return ((char)(i+48));
	else
		return ((char)(i+87));
}

char* decrement(char* c,int i)
{
	if(c[i]=='0')
	{	
		c[i]='f';
		decrement(c,i-1);
	}	
	else if(c[i]=='a')
		c[i]='9';
	else
		c[i]=c[i]-1;

	return c;
}

long int hexStringToInt(char * str)
{
	int x;
	long int sum=0;
	for(int i=0;i<8;i++)
	{
		x=hexToDecimal(str[i]);
		sum+= x* (long int)(pow(16,7-i));
	}

	return sum;
}

char* calc_hex_diff(char* n1,char* n2)
{
	char c1[9],c2[9],temp[9];
	char *res;
	res=(char*) malloc(9*sizeof(char));
	strcpy(c1,n1);
	strcpy(c2,n2);
	if(strcmp(c1,c2)<0)
	{
		strcpy(temp,c1);
		strcpy(c1,c2);
		strcpy(c2,temp);
	}	

	for(int i=7;i>=0;i--)
	{
		if(c1[i]>c2[i])
			res[i]=decimalToHex(hexToDecimal(c1[i])-hexToDecimal(c2[i]));
		else if(c1[i]<c2[i])
		{
			res[i]=decimalToHex(16+hexToDecimal(c1[i])-hexToDecimal(c2[i]));
			strcpy(c1,decrement(c1,i-1));
		}
		else
			res[i]='0';	
	}	
	res[8]='\0';
	return res;
}

char* calc_hex_sum(char* n1,char* n2)
{
	char* res=(char*)malloc(9);
	res[8]='\0';
	long int i = hexStringToInt(n1);
	long int j = hexStringToInt(n2);
	long int sum = i+j;

	for(int k=7;k>=0;k--)
	{
		res[k]= decimalToHex(sum%16);
		sum=sum/16;
	}

	return res;	
}

long int calc_min_hex_difference(char* n1, char* n2)
{
	char c1[9]="ffffffff";
	c1[8]='\0';
	char* temp;
	if(n1[0]<n2[0])
	{
		temp=n1;
		n1=n2;
		n2=temp;
	}	

	char* d1 = calc_hex_diff(n1,n2);
	char* d21 = calc_hex_diff(c1,n1);
	char* d2 = calc_hex_sum(d21,n2);

	if(strcmp(d1,d2)<=0)
		return hexStringToInt(d1);
	else
		return hexStringToInt(d2);

}

int indexOf(char* string, char of) {
	int len = strlen(string);
	for (int i = 0; i < len; i++) {
		if (string[i] == of) {
			return i;
		}
	}
	return len - 1;
}


void helperHelp() {
	cout << "Commands supported: " << endl;

	cout << "help ";
	cout << "==>  Provides a list of command and their usage details"<<endl;

	cout << "port <x>";
	cout << "==>  Sets the port number   !SHOULD ENTER ONLY ONCE."<<endl;
	cout << "\t\t(eg- port 1234)"<<endl;

	cout << "create ";
	cout << "==>  Creates a Pastry"<<endl;

	cout << "join <x:p>";
	cout << "==>  Joins the ring with x address and p port"<<endl;
	cout << "\t\t(eg- join 111.111.111.111:1000)"<<endl;

	cout << "put <key> <value>";
	cout << "==>  Inserts the given <key,value> pair in the Pastry"<<endl;
	cout << "\t\t(eg- put 23 654)"<<endl;

	cout << "get <key>";
	cout << "==>  Returns the value corresponding to the key"<<endl;
	cout << "\t\t(eg- get 23)";

	cout << "lset";
	cout << "==>  Prints the LeafSet of the current node"<<endl;

	cout << "nset";
	cout << "==>  Prints the NieghbourhoodSet of the current node"<<endl;

	cout << "routetable";
	cout << "==>  Prints the route table of the current node"<<endl;

	cout << "dump";
	cout << "==>  Displays all information pertaining to a current node"<<endl;

	cout << "self ";
	cout << "==>  Prints my ip with port"<<endl;

	cout << "clear ";
	cout << "==>  Clears my screen"<<endl;

	cout << "mykeys ";
	cout << "==>  Prints my key values"<<endl;

	cout << "quit ";
	cout << "==>  Quits the node from the Pastry"<<endl;

	cout << "shutdown";
	cout << "==>  Shuts Down the entire Pastry network"<<endl;

	cout << "lset <x:p>";
	cout << "==>  Prints the LeafSet of node with x address and p port"<<endl;

	cout << "nset <x:p>";
	cout << "==>  Prints the NeighbourhoodSet of node with x address and p port"<<endl;

	cout << "routetable <x:p>";
	cout << "==>  Prints the routetable of node with x address and p port"<<endl;

	cout << "dump <x:p>";
	cout << "==>  Prints all the information pertaining to a node with address x and port p"<<endl;

	cout << "store <DAY> <SAT CODE>";
	cout << "==>  Strores the launch code as parts on the network. DAY(1-365)"<<endl;

	cout << "verify <DAY> <SAT CODE>";
	cout << "==>  Veifiy if this SAT CODE is valid for given DAY, if NOT report error"<<endl;
	cout << endl;

	cout << "BONUS COMMANDS EXECUTED"<<endl;
	cout << "----------------------------------------------"<<endl;
	
	cout << "finger";
	cout << "==>  List of addresses of all Nodes in the Pastry"<<endl;

	cout << "dumpall";
	cout << "==>  All Information of all the nodes"<<endl;	

	cout << endl;

}

const char* hashValue(const char* ipPort)
{
	unsigned long hash;
	stringstream ss;
	hash=crc32(0L,(const Bytef*)ipPort,strlen(ipPort));
	ss<<setfill ('0') << setw(8) << hex << hash;
	string result=ss.str();
	return result.c_str();

}

char binaryToHex(char* c1)
{
	int sum=0;
	for(int i=0;i<4;i++)
		sum+= ((int)c1[i]-48)*((int)(pow(2,3-i)));

	return decimalToHex(sum);
}

char* hexToBinary(char c)
{
	char* bin=(char*)malloc(5);
	bin[4]='\0';
	int n=hexToDecimal(c);
	for(int i=3;i>=0;i--)
	{
		bin[i]= (char)((n%2)+48);
		n=n/2;
	}	

	return bin;
}

char* performXor(char* c1, char* c2)
{
	char n1[33],n2[33],res[33];
	char* fres=(char*)malloc(8);	
	n1[32]='\0';
	n2[32]='\0';
	res[32]='\0';
	fres[8]='\0';
	strcpy(n1,"");
	strcpy(n2,"");
	for(int i=0;i<8;i++)
		strcat(n1,hexToBinary(c1[i]));
	for(int i=0;i<8;i++)
		strcat(n2,hexToBinary(c2[i]));

	for(int i=0;i<32;i++)
		if(n1[i]==n2[i])
			res[i]='0';
		else
			res[i]='1';

	for(int i=0;i<8;i++)
		fres[i] = binaryToHex(res+(4*i));

	return fres;	
}

long int getNeighbourDistance(char* XnodeID)
{
	long int d = hexStringToInt(performXor(XnodeID, Node.nodeID));
	return d;
}

void printCurrentLeafSet()
{
	cout<<"The Leaf Set of "<<Node.nodeID<<endl;
	cout<<"-------------------"<<endl;
	cout<<"Smaller than NodeID"<<endl;
	
	for(int i=0;i<2;i++)
		cout<<(i+1)<<"---"<<Node.leafSet[i].nodeEntry<<"---"<<Node.leafSet[i].ip<<endl;	

	cout<<"Larger than NodeID"<<endl;
	for(int i=2;i<4;i++)
		cout<<(i+1)<<"---"<<Node.leafSet[i].nodeEntry<<"---"<<Node.leafSet[i].ip<<endl;
}

void  printCurrentNeighbourSet()
{
	cout<<"The Neighbourhood Set of "<<Node.nodeID<<endl;
	cout<<"--------------------------"<<endl;
	for(int i=0;i<4;i++)
		cout<<(i+1)<<"---"<<Node.neighbourSet[i].nodeEntry<<"---"<<Node.neighbourSet[i].ip<<endl;
}

void printCurrentRoutingTable()
{
	cout<<"The Routing table of "<<Node.nodeID<<endl;
	cout<<"-----------------------"<<endl;
	for(int i=0;i<8;i++)
	{
		cout<<i<<" Match    ";
		for(int j=0;j<16;j++)
		{
			for(int k=0;k<8;k++)
			{	
				if(k==i||k==i+1)
					cout<<"-";	
				cout<<Node.rTable[i][j].nodeEntry[k];

			}	
			cout<<"   ";
		}
		cout<<endl;
	}
}


bool connectToServer(int &sock) {
	struct hostent *host;
	struct sockaddr_in server_addr;

	char hostname[128];
	gethostname(hostname, 128);
	host = gethostbyname(hostname);

	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		perror("Socket");
		exit(1);
	}

	//cout<<"DPORT: "<<destPort<<"   IP: "<<destIP<<"    Data:"<<client_send_data<<endl;
	int dPort = atoi(destPort);
	int q=inet_aton(destIP, &server_addr.sin_addr);
	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(dPort);

	bzero(&(server_addr.sin_zero), 8);

	int retriedCount = 0;
	while (connect(sock, (struct sockaddr *) &server_addr,
			sizeof(struct sockaddr)) == -1) {


		if(strcmp(cmdInExec,"shutdown")==0)
			return false;
		//trying again assuming the server is busy
		retriedCount++;
		cout << "Server busy --- retrying(" << retriedCount << "/"
				<< retry_count << ")" << endl;
		
		sleep(1);
		if (retriedCount == retry_count) {
			cout
					<< "Server is not up or not responding, terminating client...please try again"
					<< endl;
			close(sock);
			return false;
		}
	}
	hasJoined=true;

	return true;
}


void client()
{
	int sock,bytes_received;

	if(!connectToServer(sock))
	{
		if(strcmp(cmdInExec,"shutdown")==0)
			return;
		client_recv_data[0] = SERVER_BUSY;
		cout<<"Retuning...";
		return;
	}	
	//cout<<"SENDING DATA :"<<client_send_data<<endl;
	send(sock, client_send_data, strlen(client_send_data), 0);

	close(sock);
}

void* reset(void* arg)
{
	sleep(3);
	isSending=false;
}

void recvStateTable(char * str)                    //here str is the received data of server
{

	istringstream ss(str);         //error prone
	string token;
	vector<string> elem;
	while(getline(ss,token,'#'))
		elem.push_back(token);

	strcpy(recvd.nodeID,elem[1].c_str());
	strcpy(recvd.server_ip_port,elem[2].c_str());
	recvd_last=atoi(elem[6].c_str());

		istringstream ssleaf(elem[4]);
		string leaf;
		vector<string> leafelem;
		while(getline(ssleaf,leaf,'!'))
			leafelem.push_back(leaf);
		char* leafhelp;
		for(int i=0;i<4;i++)
		{
			leafhelp=(char*)leafelem[i].c_str();
			strcpy(recvd.leafSet[i].nodeEntry,substring(leafhelp, 0,indexOf(leafhelp,'$') ));
			strcpy(recvd.leafSet[i].ip,substring(leafhelp,indexOf(leafhelp,'$')+1,strlen(leafhelp)-indexOf(leafhelp,'$')-1));
		}


		istringstream ssneigh(elem[5]);
		string neigh;
		vector<string> neighelem;
		while(getline(ssneigh,neigh,'!'))
			neighelem.push_back(neigh);
		char* neighhelp;
		for(int i=0;i<4;i++)
		{
			neighhelp=(char*)neighelem[i].c_str();
			strcpy(recvd.neighbourSet[i].nodeEntry,substring(neighhelp, 0,indexOf(neighhelp,'$') ));
			strcpy(recvd.neighbourSet[i].ip,substring(neighhelp,indexOf(neighhelp,'$')+1,strlen(neighhelp)-indexOf(neighhelp,'$')-1));
				
		}	

	istringstream ssrtable(elem[3]);
	string rtrow;
	vector<string> rtablerow;
	while(getline(ssrtable,rtrow,'@'))
		rtablerow.push_back(rtrow);
	string rtrowele;
	for(int i=0;i<8;i++)
	{
		istringstream ssrtrow(rtablerow[i]);
		vector<string> rowele;
		while(getline(ssrtrow,rtrowele,'!'))
			rowele.push_back(rtrowele);
		char* rtcellhelp;
		for(int j=0;j<16;j++)
		{
			rtcellhelp=(char*)rowele[j].c_str();
			strcpy(recvd.rTable[i][j].nodeEntry,substring(rtcellhelp, 0,indexOf(rtcellhelp,'$') ));
			strcpy(recvd.rTable[i][j].ip,substring(rtcellhelp,indexOf(rtcellhelp,'$')+1,strlen(rtcellhelp)-indexOf(rtcellhelp,'$')-1));	
		}
	}

}

void sendStateTable(char * ipport)
{
	pthread_t cid;
	destIP=substring(ipport,0,indexOf(ipport,':'));
	destPort= substring(ipport, indexOf(ipport, ':')+1, strlen(ipport)-indexOf(ipport, ':')-1);

	strcpy(client_send_data,"");
	if(dmpFlag==false)
		strcat(client_send_data,"stb#");
	else
		strcat(client_send_data,"dmp#");
	strcat(client_send_data,Node.nodeID);
	strcat(client_send_data,"#");
	strcat(client_send_data,Node.server_ip_port);
	strcat(client_send_data,"#");
	dmpFlag=false;
	for(int i=0;i<8;i++)													// delimiting the rtable
	{
		for(int j=0;j<16;j++)
		{
				strcat(client_send_data,Node.rTable[i][j].nodeEntry);
				strcat(client_send_data,"$");								// $ separates nodeID and ipport in each entry
				strcat(client_send_data,Node.rTable[i][j].ip);
				strcat(client_send_data,"!");								// ! separates each entry
		}
		strcat(client_send_data,"@");			//remove if not necessary   @ end of row in rtable
	}
	strcat(client_send_data,"#");
	for(int i=0;i<4;i++)
	{
		strcat(client_send_data,Node.leafSet[i].nodeEntry);
		strcat(client_send_data,"$");										// $ separates node n ip
		strcat(client_send_data,Node.leafSet[i].ip);
		if(i<3)
		{
		strcat(client_send_data,"!");					// ! separated each leafset element
		}
	}
	strcat(client_send_data,"#");
	for(int i=0;i<4;i++)
	{
		strcat(client_send_data,Node.neighbourSet[i].nodeEntry);
		strcat(client_send_data,"$");
		strcat(client_send_data,Node.neighbourSet[i].ip);
		if(i<3)
		{
		strcat(client_send_data,"!");
		}
	}
	strcat(client_send_data,"#");
	stringstream ss;
	ss << last;
	string tlast = ss.str();
	char * slast = (char*)tlast.c_str();
	strcat(client_send_data,slast);
	client();
}

void insertXInNeighbourSet(char* ipPort)
{
	bool isPresent=false;
	int i=0;
	char XnodeID[9];
	strcpy(XnodeID,hashValue(ipPort));
	XnodeID[8]='\0';

	for(i=0;i<4;i++)
		if(strcmp(Node.neighbourSet[i].nodeEntry, XnodeID)==0)
			isPresent=true;

		
	for(i=0;i<4;i++)
	{
		if(isPresent==false)
		if(Node.neighbourSet[i].nodeEntry[0]!='\0' && getNeighbourDistance(XnodeID)<getNeighbourDistance(Node.neighbourSet[i].nodeEntry) )
		{
			for(int j=2;j>=i;j--)
			{
				strcpy(Node.neighbourSet[j+1].nodeEntry,Node.neighbourSet[j].nodeEntry);
				strcpy(Node.neighbourSet[j+1].ip,Node.neighbourSet[j].ip);
			}	
			strcpy(Node.neighbourSet[i].nodeEntry,XnodeID);
			strcpy(Node.neighbourSet[i].ip,ipPort);
			isPresent=true;
		}
		else if(Node.neighbourSet[i].nodeEntry[0]=='\0')
		{
			strcpy(Node.neighbourSet[i].nodeEntry,XnodeID);
			strcpy(Node.neighbourSet[i].ip,ipPort);
			isPresent=true;
		}	
	}

}

void addNodeToPastry(char* fcmd,char* ipPort) //At  A's End
{
	int i,k;
	char nextNodeId[9];
	char dipPort[22];     //1st
	char XnodeID[9];
	strcpy(XnodeID,hashValue(ipPort));
	
	for(i=0;i<8;i++)
		if(XnodeID[i]!=Node.nodeID[i])
			break;

	int j=hexToDecimal(XnodeID[i]);

	if(Node.rTable[i][j].nodeEntry[0]!='\0')
	{	
		strcpy(nextNodeId, Node.rTable[i][j].nodeEntry);
		strcpy(dipPort,Node.rTable[i][j].ip);
	}	
	else	
		last = 1;

	sendStateTable(ipPort);	

	if(last==0)
	{	
	strcpy(client_send_data,"cmd ");	
	destIP=substring(dipPort,0,indexOf(dipPort,':'));
	destPort=substring(dipPort,indexOf(dipPort,':')+1, strlen(dipPort)-indexOf(dipPort,':')-1);
	strcat(client_send_data,fcmd);
	client();
	}

	last=0;

}

void sendKeys(char* ipPort)//At A's End
{
	int i=0,j=0;
	char XnodeID[9];
	strcpy(XnodeID,hashValue(ipPort));
	XnodeID[8]='\0';
	strcpy(client_send_data,"");
	strcat(client_send_data,"key ");
	destIP=substring(ipPort,0,indexOf(ipPort,':'));
	destPort= substring(ipPort, indexOf(ipPort, ':')+1, strlen(ipPort)-indexOf(ipPort, ':')-1);
 

	long int xid = hexStringToInt(XnodeID);
	long int myid = hexStringToInt(Node.nodeID);
	long int hashkey;

	for(map<string,string>::iterator iter=Node.myMap.begin(); iter!=Node.myMap.end(); ++iter)
	{
		for(i=0;i<8;i++)
			if(XnodeID[i]!=iter->first[i])
				break;

		for(j=0;j<8;j++)
			if(Node.nodeID[j]!=iter->first[j])
				break;
	
		if(i>j)
		{
			strcat(client_send_data, (iter->first).c_str());
			strcat(client_send_data, "&");
			strcat(client_send_data, (iter->second).c_str());			
			strcat(client_send_data, "$");
			Node.myMap.erase(iter->first);
		}	
		else if(i==j)
		{	

			hashkey = hexStringToInt((char*)(iter->first).c_str());
			long int xdif=absolute(xid-hashkey);
			long int ndif=absolute(myid-hashkey);
			//long int xdif = calc_min_hex_difference(XnodeID,((char*)(iter->first).c_str()));
			//long int ndif = calc_min_hex_difference(Node.nodeID,((char*)(iter->first).c_str()));
			if(xdif<ndif)
			{
				strcat(client_send_data, (iter->first).c_str());
				strcat(client_send_data, "&");
				strcat(client_send_data, (iter->second).c_str());			
				strcat(client_send_data, "$");
				Node.myMap.erase(iter->first);
			}
		}

	}

	client();	
}

void updateXEntry(char* ipPort)  //At A's end
{
	int i=0;
	char XnodeID[9];
	strcpy(XnodeID,hashValue(ipPort));
	XnodeID[8]='\0';
	for(i=0;i<8;i++)
		if(XnodeID[i]!=Node.nodeID[i])
			break;


	int j=hexToDecimal(XnodeID[i]);
	
	if(Node.rTable[i][j].nodeEntry[0]=='\0')
	{
		strcpy(Node.rTable[i][j].nodeEntry,XnodeID);
		strcpy(Node.rTable[i][j].ip,ipPort);	
	}

	if((strcmp(XnodeID,Node.leafSet[0].nodeEntry)>0) && (strcmp(XnodeID,Node.leafSet[1].nodeEntry)<0))
	{	
		strcpy(Node.leafSet[0].nodeEntry,XnodeID);
		strcpy(Node.leafSet[0].ip, ipPort);
	}	
	else if((strcmp(XnodeID,Node.leafSet[1].nodeEntry)>0) && (strcmp(XnodeID,Node.nodeID))<0)
	{
		strcpy(Node.leafSet[0].nodeEntry,Node.leafSet[1].nodeEntry);
		strcpy(Node.leafSet[0].ip, Node.leafSet[1].ip);
		strcpy(Node.leafSet[1].nodeEntry,XnodeID);
		strcpy(Node.leafSet[1].ip, ipPort);
	}	
	else if((strcmp(XnodeID,Node.nodeID))>0 && ((strcmp(XnodeID,Node.leafSet[2].nodeEntry))<0 || Node.leafSet[2].nodeEntry[0]=='\0' ))
	{
		strcpy(Node.leafSet[3].nodeEntry,Node.leafSet[2].nodeEntry);
		strcpy(Node.leafSet[3].ip, Node.leafSet[2].ip);
		strcpy(Node.leafSet[2].nodeEntry, XnodeID);
		strcpy(Node.leafSet[2].ip, ipPort);
	}
	else if((strcmp(XnodeID,Node.leafSet[2].nodeEntry))>0 && (Node.leafSet[2].nodeEntry[0]!='\0') && ((strcmp(XnodeID,Node.leafSet[3].nodeEntry))<0 || Node.leafSet[3].nodeEntry[0]=='\0'))
	{	
		strcpy(Node.leafSet[3].nodeEntry,XnodeID);
		strcpy(Node.leafSet[3].ip, ipPort);
	}

	insertXInNeighbourSet(ipPort);
	
}

void removeXEntry(char* cmd)//At A's End
{
	istringstream ss(cmd);         //error prone
	string token;
	char x[4][30];
		int i=0,j=0;
	while(getline(ss,token,' '))
	{	
		strcpy(x[i],token.c_str());
		i++;
	}			
	
	char* ipPort = x[2];
	char XnodeID[9];
	strcpy(XnodeID,hashValue(ipPort));
	XnodeID[8]='\0';
	bool isPresent=false;
	i=0;

	for(i=0;i<8;i++)
		if(XnodeID[i]!=Node.nodeID[i])
			break;

	j=hexToDecimal(XnodeID[i]);

	if(Node.rTable[i][j].nodeEntry[0]!='\0' && strcmp(Node.rTable[i][j].nodeEntry,XnodeID)==0 )
	{	
		isPresent=true;
		memset(Node.rTable[i][j].nodeEntry,'\0',9);
		memset(Node.rTable[i][j].ip,'\0',23);
	}	

	for(i=0;i<4;i++)
	{
		if(strcmp(Node.leafSet[i].nodeEntry,XnodeID)==0)
		{
			isPresent=true;
			if(i==0||i==3)
			{
				memset(Node.leafSet[i].nodeEntry,'\0',9);
				memset(Node.leafSet[i].ip,'\0',23);
			}
			else if(i==1)
			{
				strcpy(Node.leafSet[1].nodeEntry,Node.leafSet[0].nodeEntry);
				strcpy(Node.leafSet[1].ip,Node.leafSet[0].ip);
				memset(Node.leafSet[0].nodeEntry,'\0',9);
				memset(Node.leafSet[0].ip,'\0',23);
			}
			else if(i==2)
			{
				strcpy(Node.leafSet[2].nodeEntry,Node.leafSet[3].nodeEntry);
				strcpy(Node.leafSet[2].ip,Node.leafSet[3].ip);
				memset(Node.leafSet[3].nodeEntry,'\0',9);
				memset(Node.leafSet[3].ip,'\0',23);
			}	
		}	
	}		
	if(strcmp(Node.server_ip_port,x[3])!=0)
		updateXEntry(x[3]);

	for(i=0;i<4;i++)
		if(strcmp(Node.neighbourSet[i].nodeEntry,XnodeID)==0)
		{	
			isPresent=true;
			for(j=i;j<3;j++)
			{
				if(Node.neighbourSet[j+1].nodeEntry[0]!='\0')
				{	
					strcpy(Node.neighbourSet[j].nodeEntry, Node.neighbourSet[j+1].nodeEntry);
					strcpy(Node.neighbourSet[j].ip, Node.neighbourSet[j+1].ip);
				}
				else
				{
					memset(Node.neighbourSet[j].nodeEntry,'\0',9);
					memset(Node.neighbourSet[j].ip,'\0',23);					
				}
			}	
			memset(Node.neighbourSet[3].nodeEntry,'\0',9);
			memset(Node.neighbourSet[3].ip,'\0',23);
		}
	if(strcmp(Node.server_ip_port,x[3])!=0)
		insertXInNeighbourSet(x[3]);		
		
	if(isPresent)
	{
		strcpy(client_send_data,cmd);
		for(i=0;i<4;i++)
			if(Node.leafSet[i].nodeEntry[0]!='\0')
			{
				destIP = substring(Node.leafSet[i].ip , 0, indexOf(Node.leafSet[i].ip, ':'));
				destPort = substring(Node.leafSet[i].ip, indexOf(Node.leafSet[i].ip, ':')+1, strlen(Node.leafSet[i].ip)-indexOf(Node.leafSet[i].ip, ':')-1);
				client();
			}	

		for(i=0;i<4;i++)
			if(Node.neighbourSet[i].nodeEntry[0]!='\0')
			{
				destIP = substring(Node.neighbourSet[i].ip , 0, indexOf(Node.neighbourSet[i].ip, ':'));
				destPort = substring(Node.neighbourSet[i].ip, indexOf(Node.neighbourSet[i].ip, ':')+1, strlen(Node.neighbourSet[i].ip)-indexOf(Node.neighbourSet[i].ip, ':')-1);
				client();
			}

		for(i=0;i<8;i++)
			for(j=0;j<16;j++)
				if(Node.rTable[i][j].nodeEntry[0]!='\0' && Node.rTable[i][j].nodeEntry[1]!='\0')
				{
					destIP = substring(Node.rTable[i][j].ip , 0, indexOf(Node.rTable[i][j].ip, ':'));
					destPort = substring(Node.rTable[i][j].ip, indexOf(Node.rTable[i][j].ip, ':')+1, strlen(Node.rTable[i][j].ip)-indexOf(Node.rTable[i][j].ip, ':')-1);
					client();	
				}		
	}	

}

void sendRequestedData(char* cmd) //At A's End
{
	char* rData = substring(cmd , 0, indexOf(cmd, ' '));
	char* ipPort = substring(cmd, indexOf(cmd, ' ')+1, strlen(cmd)-indexOf(cmd, ' ')-1 );
	memset(client_send_data,'\0',500);
	destIP=substring(ipPort,0,indexOf(ipPort,':'));
	destPort= substring(ipPort, indexOf(ipPort, ':')+1, strlen(ipPort)-indexOf(ipPort, ':')-1);

	if(strcmp(rData,"routetable")==0)
	{	
		strcpy(client_send_data,"rtb#");
		strcat(client_send_data, Node.nodeID);
		strcat(client_send_data,"#");

		for(int i=0;i<8;i++)
		{	
			for(int j=0;j<16;j++)
			{	
				strcat(client_send_data,Node.rTable[i][j].nodeEntry);
				strcat(client_send_data, "$");
			}
			strcat(client_send_data, "@");
		}
		client();	
	}
	else if(strcmp(rData,"lset")==0)
	{
		strcpy(client_send_data,"lst#");
		strcat(client_send_data, Node.nodeID);
		strcat(client_send_data,"#");

		for(int i=0;i<4;i++)
		{
			strcat(client_send_data,Node.leafSet[i].nodeEntry);
			strcat(client_send_data,"&");
			strcat(client_send_data,Node.leafSet[i].ip);
			strcat(client_send_data,"$");
		}
		client();	
	}	
	else if(strcmp(rData,"nset")==0)
	{
		strcpy(client_send_data,"nst#");
		strcat(client_send_data, Node.nodeID);
		strcat(client_send_data,"#");

		for(int i=0;i<4;i++)
		{
			strcat(client_send_data,Node.neighbourSet[i].nodeEntry);
			strcat(client_send_data,"&");
			strcat(client_send_data,Node.neighbourSet[i].ip);
			strcat(client_send_data,"$");
		}
		client();
	}
	else if(strcmp(rData,"dump")==0)
	{
		dmpFlag=true;
		sendStateTable(ipPort);
		dmpFlag=false;
	}	
}

void forwardCommand()
{
	for(int i=0;i<4;i++)
		if(Node.leafSet[i].nodeEntry[0]!='\0')	
		{
			destIP = substring(Node.leafSet[i].ip , 0, indexOf(Node.leafSet[i].ip, ':'));
			destPort = substring(Node.leafSet[i].ip, indexOf(Node.leafSet[i].ip, ':')+1, strlen(Node.leafSet[i].ip)-indexOf(Node.leafSet[i].ip, ':')-1);
			client();
		}	

	for(int i=0;i<4;i++)
		if(Node.neighbourSet[i].nodeEntry[0]!='\0')	
		{
			destIP = substring(Node.neighbourSet[i].ip , 0, indexOf(Node.neighbourSet[i].ip, ':'));
			destPort = substring(Node.neighbourSet[i].ip, indexOf(Node.neighbourSet[i].ip, ':')+1, strlen(Node.neighbourSet[i].ip)-indexOf(Node.neighbourSet[i].ip, ':')-1);
			client();
		}	
		

	for(int i=0;i<8;i++)
		for(int j=0;j<16;j++)
			if(Node.rTable[i][j].nodeEntry[1]!='\0')
			{
				destIP = substring(Node.rTable[i][j].ip , 0, indexOf(Node.rTable[i][j].ip, ':'));
				destPort = substring(Node.rTable[i][j].ip, indexOf(Node.rTable[i][j].ip, ':')+1, strlen(Node.rTable[i][j].ip)-indexOf(Node.rTable[i][j].ip, ':')-1);
				client();
			}
}

void sendMyDataAsked(char* cmd)
{
	char* fcmd = substring(cmd, 4, strlen(cmd)-4);
	char* command = substring(fcmd,0,indexOf(fcmd, ' '));
	char* ipPort = substring(fcmd,indexOf(fcmd,' ')+1, strlen(fcmd)-indexOf(fcmd,' ')-1);	
	destIP=substring(ipPort,0,indexOf(ipPort,':'));
	destPort= substring(ipPort, indexOf(ipPort, ':')+1, strlen(ipPort)-indexOf(ipPort, ':')-1);

	if(isSending==false)
	{
		isSending=true;			
		if(strcmp(command,"finger")==0)
		{
			memset(client_send_data,'\0',500);
			strcpy(client_send_data,"fgr ");
			strcat(client_send_data,Node.server_ip_port);
			client();
 		
		}
		else if(strcmp(command,"dumpall")==0)
		{
			memset(client_send_data,'\0',1000);
			dmpFlag=true;
			sendStateTable(ipPort);
			dmpFlag=false;
		}	

	}	
}

void forwardMyDataAsked(char* fcmd)
{
	char* command = substring(fcmd,0,indexOf(fcmd, ' '));
	char* ipPort = substring(fcmd,indexOf(fcmd,' ')+1, strlen(fcmd)-indexOf(fcmd,' ')-1);	

	if(isSending==true)
	{
		memset(client_send_data,'\0',30);
		strcpy(client_send_data,fcmd);
		forwardCommand();
		pthread_t yid;
		pthread_create(&yid,NULL,reset,NULL);
		
	}	


}

void* executeFinger(void* arg)
{
	sendMyDataAsked(server_recv_data);
}

void helperShutDown()
{
	char putDestIP[16],putDestPort[6];
	char msg[40],msg1[50];
	strcpy(client_send_data,"cmd shutdown");
	bool gotEntry=false;		


	if(shutFlag==false)
	{	
	for(int i=0;i<4;i++)
		if(Node.leafSet[i].nodeEntry[0]!='\0')	
		{
			destIP = substring(Node.leafSet[i].ip , 0, indexOf(Node.leafSet[i].ip, ':'));
			destPort = substring(Node.leafSet[i].ip, indexOf(Node.leafSet[i].ip, ':')+1, strlen(Node.leafSet[i].ip)-indexOf(Node.leafSet[i].ip, ':')-1);
			if(gotEntry==false)
			{
				strcpy(putDestIP,destIP);
				strcpy(putDestPort,destPort);
				putDestPort[5]='\0';
				putDestIP[15]='\0';
				gotEntry=true;
			}
			shutFlag=true;
			strcpy(cmdInExec,"shutdown");
			client();
		}

	if(gotEntry==false)
	{
	for(int i=0;i<4;i++)
		if(Node.neighbourSet[i].nodeEntry[0]!='\0')	
		{
			destIP = substring(Node.neighbourSet[i].ip , 0, indexOf(Node.neighbourSet[i].ip, ':'));
			destPort = substring(Node.neighbourSet[i].ip, indexOf(Node.neighbourSet[i].ip, ':')+1, strlen(Node.neighbourSet[i].ip)-indexOf(Node.neighbourSet[i].ip, ':')-1);
			shutFlag=true;
			strcpy(cmdInExec,"shutdown");
			client();
			if(gotEntry==false)
			{
				strcpy(putDestIP,destIP);
				strcpy(putDestPort,destPort);
				gotEntry=true;
			}
		}	
	}

	if(gotEntry==false)
	{
		for(int i=0;i<8;i++)
			for(int j=0;j<16;j++)
				if(Node.rTable[i][j].nodeEntry[1]!='\0')
				{
					destIP = substring(Node.rTable[i][j].ip , 0, indexOf(Node.rTable[i][j].ip, ':'));
					destPort = substring(Node.rTable[i][j].ip, indexOf(Node.rTable[i][j].ip, ':')+1, strlen(Node.rTable[i][j].ip)-indexOf(Node.rTable[i][j].ip, ':')-1);
					shutFlag=true;
					strcpy(cmdInExec,"shutdown");
					client();
					if(gotEntry==false)
					{
						strcpy(putDestIP,destIP);
						strcpy(putDestPort,destPort);
						gotEntry=true;
					}
				}	
	}
	exit(1);
	}
	
}

void helperFinger()
{
	helperSelf();
	memset(client_send_data,'\0',15);
	strcpy(client_send_data,"cmd finger ");
	strcat(client_send_data,Node.server_ip_port);
				
	forwardCommand();
}


void helperDumpAll()
{
	helperDump();
	memset(client_send_data,'\0',15);
	strcpy(client_send_data,"cmd dumpall ");
	strcat(client_send_data,Node.server_ip_port);

	forwardCommand();
}

void runCommand(char* cmd)
{
	char* fcmd = substring(cmd, 4, strlen(cmd)-4);
	char* command = substring(fcmd,0,indexOf(fcmd, ' '));
	char* Data = substring(fcmd,indexOf(fcmd,' ')+1, strlen(fcmd)-indexOf(fcmd,' ')-1);
	if(strcmp(command,"join")==0)
		addNodeToPastry(fcmd,Data);
	else if( strcmp(command,"updateMe") == 0 )
		updateXEntry(Data);
	else if(strcmp(command,"put")==0)
		helperPut(fcmd);
	else if(strcmp(command,"get")==0)
		helperGet(fcmd);
	else if( strcmp(command,"sendMyKeys") == 0)
		sendKeys(Data);
	else if(strcmp(command,"quit")==0)
		removeXEntry(cmd);
	else if( strcmp(command, "send") == 0)
		sendRequestedData(Data);
	else if(strcmp(fcmd, "shutdown")==0)
		helperShutDown();
	else if(strcmp(command,"finger")==0)
	{	
		pthread_t zid;
		pthread_create(&zid,NULL,executeFinger,NULL);
	}	
	else if(strcmp(command,"dumpall")==0)
	{	
		pthread_t zid;
		pthread_create(&zid,NULL,executeFinger,NULL);
	}	
	else if(strcmp(command,"gotIt")==0)
		forwardMyDataAsked(Data);
	else if(strcmp(command,"forward")==0 && strcmp(Data,"finger")==0)
		helperFinger();
	else if(strcmp(command,"forward")==0 && strcmp(Data,"dumpall")==0)
		helperDumpAll();
}

void updateMyMap(char* cmd)  //At X's End
{
	char* keyvalues = substring(cmd, 4, strlen(cmd)-4);
	istringstream ss(keyvalues);
	string token1;
	char token[80];
	while(getline(ss,token1,'$'))
	{
		strcpy(token,token1.c_str());
		char* key=substring(token,0, indexOf(token,'&'));
		char* value=substring(token, indexOf(token,'&')+1, strlen(token)-indexOf(token,'&')-1 );
		Node.myMap.insert(pair<char*,char*>(key,value));
	}	
}

void updateStateTables(char* cmd)   //At X's End
{
	recvStateTable(cmd);

	char* stb = substring(cmd, 4, strlen(cmd)-4); 
	int k=0,j=0;

	if(strcmp(first_ipPort,recvd.server_ip_port)==0)
		for(int i=0;i<4;i++)
		{	
			strcpy(Node.neighbourSet[i].nodeEntry,recvd.neighbourSet[i].nodeEntry);
			strcpy(Node.neighbourSet[i].ip, recvd.neighbourSet[i].ip);
		}	

	insertXInNeighbourSet(recvd.server_ip_port);	

	if(recvd_last==1)
		if(strcmp(recvd.nodeID,Node.nodeID)>0)
		{
			strcpy(Node.leafSet[2].nodeEntry,recvd.nodeID);
			strcpy(Node.leafSet[2].ip, recvd.server_ip_port);
		}	
		else
		{
			strcpy(Node.leafSet[1].nodeEntry,recvd.nodeID);
			strcpy(Node.leafSet[1].ip, recvd.server_ip_port);
		}	

	
	if(recvd_last==1)
		for(int i=0;i<4;i++)
		{	
			if(strcmp(Node.nodeID, recvd.leafSet[i].nodeEntry) >0)
			{
				if(Node.leafSet[1].nodeEntry[0]=='\0' && recvd.leafSet[i].nodeEntry[0]!='\0')
				{
					strcpy(Node.leafSet[1].nodeEntry,recvd.leafSet[i].nodeEntry);
					strcpy(Node.leafSet[1].ip, recvd.leafSet[i].ip);	
				}
				else if(Node.leafSet[1].nodeEntry[0]!='\0' && recvd.leafSet[i].nodeEntry[0]!='\0' && strcmp(recvd.leafSet[i].nodeEntry,Node.leafSet[1].nodeEntry)<0 )
				{
					strcpy(Node.leafSet[0].nodeEntry,recvd.leafSet[i].nodeEntry);
					strcpy(Node.leafSet[0].ip, recvd.leafSet[i].ip);	
				}	
				else if(Node.leafSet[1].nodeEntry[0]!='\0' && recvd.leafSet[i].nodeEntry[0]!='\0' && strcmp(recvd.leafSet[i].nodeEntry,Node.leafSet[1].nodeEntry)>0 )
				{
					strcpy(Node.leafSet[0].nodeEntry,Node.leafSet[1].nodeEntry);
					strcpy(Node.leafSet[0].ip, Node.leafSet[1].ip);
					strcpy(Node.leafSet[1].nodeEntry,recvd.leafSet[i].nodeEntry);
					strcpy(Node.leafSet[1].ip, recvd.leafSet[i].ip);
				}	
			}	
			else if(strcmp(Node.nodeID, recvd.leafSet[i].nodeEntry) <0)
			{
				if(Node.leafSet[2].nodeEntry[0]=='\0' && recvd.leafSet[i].nodeEntry[0]!='\0')
				{
					strcpy(Node.leafSet[2].nodeEntry,recvd.leafSet[i].nodeEntry);
					strcpy(Node.leafSet[2].ip, recvd.leafSet[i].ip);
				}	
				else if(Node.leafSet[2].nodeEntry[0]!='\0' && Node.leafSet[3].nodeEntry[0]=='\0' && recvd.leafSet[i].nodeEntry[0]!='\0')
				{					
					if(strcmp(Node.leafSet[2].nodeEntry,recvd.leafSet[i].nodeEntry)<0)
					{	
						strcpy(Node.leafSet[3].nodeEntry,recvd.leafSet[i].nodeEntry);
						strcpy(Node.leafSet[3].ip, recvd.leafSet[i].ip);
					}
					else if(strcmp(Node.leafSet[2].nodeEntry,recvd.leafSet[i].nodeEntry)>0)
					{
						strcpy(Node.leafSet[3].nodeEntry,Node.leafSet[2].nodeEntry);
						strcpy(Node.leafSet[3].ip, Node.leafSet[2].ip);
						strcpy(Node.leafSet[2].nodeEntry,recvd.leafSet[i].nodeEntry);
						strcpy(Node.leafSet[2].ip, recvd.leafSet[i].ip);
					}	
				}	
			}	
		}
			

	for(j=0;j<8;j++)
		if(recvd.nodeID[j]!=Node.nodeID[j])
			break;	

	for(k=fR;k<=j;k++)
		for(int i=0;i<16;i++)
		{	
			if(k==j&&i==hexToDecimal(recvd.nodeID[j]))			
			{
				strcpy(Node.rTable[k][i].nodeEntry, recvd.nodeID);
				strcpy(Node.rTable[k][i].ip, recvd.server_ip_port);
			}
			else if(k==j&&i==hexToDecimal(Node.nodeID[j]))
			{	
				Node.rTable[k][i].nodeEntry[0] = Node.nodeID[j];
				strcpy(Node.rTable[k][i].ip, Node.server_ip_port);
			}	
			else 
			{			
				strcpy(Node.rTable[k][i].nodeEntry, recvd.rTable[k][i].nodeEntry);	
				strcpy(Node.rTable[k][i].ip, recvd.rTable[k][i].ip);
			}
	
		}
	fR=k;	

	if(recvd_last==1)
	{	
		for(int i=0;i<4;i++)
			if(Node.leafSet[i].nodeEntry[0]!='\0' )
			{
				char msg[40]="cmd updateMe ";
				strcat(msg, Node.server_ip_port);	
				destIP = substring(Node.leafSet[i].ip , 0, indexOf(Node.leafSet[i].ip, ':'));
				destPort = substring(Node.leafSet[i].ip, indexOf(Node.leafSet[i].ip, ':')+1, strlen(Node.leafSet[i].ip)-indexOf(Node.leafSet[i].ip, ':')-1);
				strcpy(client_send_data, msg);
				client();

			}	

		for(int i=0;i<4;i++)
			if(Node.neighbourSet[i].nodeEntry[0]!='\0')
			{
				char msg[40]="cmd updateMe ";
				strcat(msg, Node.server_ip_port);	
				destIP = substring(Node.neighbourSet[i].ip , 0, indexOf(Node.neighbourSet[i].ip, ':'));
				destPort = substring(Node.neighbourSet[i].ip, indexOf(Node.neighbourSet[i].ip, ':')+1, strlen(Node.neighbourSet[i].ip)-indexOf(Node.neighbourSet[i].ip, ':')-1);
				strcpy(client_send_data, msg);
				client();

			}	

		for(int i=0;i<8;i++)
			for(j=0;j<16;j++)
				if(Node.rTable[i][j].nodeEntry[0]!='\0' && Node.rTable[i][j].nodeEntry[1]!='\0' )
				{
					char msg[40]="cmd updateMe ";
					strcat(msg, Node.server_ip_port);	
					destIP = substring(Node.rTable[i][j].ip , 0, indexOf(Node.rTable[i][j].ip, ':'));
					destPort = substring(Node.rTable[i][j].ip, indexOf(Node.rTable[i][j].ip, ':')+1, strlen(Node.rTable[i][j].ip)-indexOf(Node.rTable[i][j].ip, ':')-1);
					strcpy(client_send_data, msg);
					client();
				}	
	}

	if(recvd_last==1)
	{
		for(int i=0;i<4;i++)
			if(Node.leafSet[i].nodeEntry[0]!='\0' )
			{
				char msg[40]="cmd sendMyKeys ";
				strcat(msg, Node.server_ip_port);	
				destIP = substring(Node.leafSet[i].ip , 0, indexOf(Node.leafSet[i].ip, ':'));
				destPort = substring(Node.leafSet[i].ip, indexOf(Node.leafSet[i].ip, ':')+1, strlen(Node.leafSet[i].ip)-indexOf(Node.leafSet[i].ip, ':')-1);
				strcpy(client_send_data, msg);
				client();
			}	
	}	
	recvd_last=0;			
}

int getClosetLeafIndex()
{
	int i=0,j=0;
	for(i=0;i<8;i++)
		if(Node.nodeID[i]!=Node.leafSet[1].nodeEntry[i])
			break;

	for(j=0;j<8;j++)
		if(Node.nodeID[j]!=Node.leafSet[2].nodeEntry[j])
			break;

	if(i>j)
		return 1;
	else if(i<j)
		return 2;
	else if(i==j && Node.leafSet[1].nodeEntry[0]!='\0' && Node.leafSet[2].nodeEntry[0]=='\0')
		return 1;
	else if(i==j && Node.leafSet[1].nodeEntry[0]=='\0' && Node.leafSet[1].nodeEntry[0]!='\0')
		return 2;
	else if(i==j && Node.leafSet[1].nodeEntry[0]=='\0' && Node.leafSet[2].nodeEntry[0]=='\0')
		return -1;
	else if(i==j && Node.leafSet[1].nodeEntry[0]!='\0' && Node.leafSet[2].nodeEntry[0]!='\0')
	{
		long int nid = hexStringToInt(Node.nodeID);
		long int li = hexStringToInt(Node.leafSet[1].nodeEntry);
		long int lj = hexStringToInt(Node.leafSet[2].nodeEntry);
		if(absolute(nid-li)<=absolute(lj-nid))
			return 1;
		else
			return 2;
	}			
}

void helperQuit()
{
	char putDestIP[16],putDestPort[6];
	char msg[40],msg1[50];
	strcpy(msg,"cmd quit ");
	strcat(msg,Node.server_ip_port);
	strcpy(client_send_data,msg);
	strcat(client_send_data, " ");
	int x = getClosetLeafIndex();
	if(x>0)
		strcat(client_send_data, Node.leafSet[x].ip);
	bool gotEntry=false;

	for(int i=0;i<4;i++)
		if(Node.leafSet[i].nodeEntry[0]!='\0')	
		{
			destIP = substring(Node.leafSet[i].ip , 0, indexOf(Node.leafSet[i].ip, ':'));
			destPort = substring(Node.leafSet[i].ip, indexOf(Node.leafSet[i].ip, ':')+1, strlen(Node.leafSet[i].ip)-indexOf(Node.leafSet[i].ip, ':')-1);
			if(gotEntry==false)
			{
				strcpy(putDestIP,destIP);
				strcpy(putDestPort,destPort);
				putDestPort[5]='\0';
				putDestIP[15]='\0';
				gotEntry=true;
			}
			client();
		}	

	if(gotEntry==false)
	{
	for(int i=0;i<4;i++)
		if(Node.neighbourSet[i].nodeEntry[0]!='\0')	
		{
			destIP = substring(Node.neighbourSet[i].ip , 0, indexOf(Node.neighbourSet[i].ip, ':'));
			destPort = substring(Node.neighbourSet[i].ip, indexOf(Node.neighbourSet[i].ip, ':')+1, strlen(Node.neighbourSet[i].ip)-indexOf(Node.neighbourSet[i].ip, ':')-1);
			client();
			if(gotEntry==false)
			{
				strcpy(putDestIP,destIP);
				strcpy(putDestPort,destPort);
				gotEntry=true;
			}
		}	
	}	

	if(gotEntry==false)
	{
		for(int i=0;i<8;i++)
			for(int j=0;j<16;j++)
				if(Node.rTable[i][j].nodeEntry[1]!='\0')
				{
					destIP = substring(Node.rTable[i][j].ip , 0, indexOf(Node.rTable[i][j].ip, ':'));
					destPort = substring(Node.rTable[i][j].ip, indexOf(Node.rTable[i][j].ip, ':')+1, strlen(Node.rTable[i][j].ip)-indexOf(Node.rTable[i][j].ip, ':')-1);
					client();
					if(gotEntry==false)
					{
						strcpy(putDestIP,destIP);
						strcpy(putDestPort,destPort);
						gotEntry=true;
					}
				}	
	}	

	if(gotEntry==true)
	{	
		destIP=putDestIP;
		destPort=putDestPort;

		strcpy(msg,"cmd put ");
		strcpy(msg1," ");
		strcat(msg1,destIP);
		strcat(msg1,":");
		strcat(msg1,destPort);
		for(map<string,string>::iterator iter=Node.myMap.begin(); iter!=Node.myMap.end(); ++iter)
		{
			memset(client_send_data,'\0',10000);
			strcpy(client_send_data,msg);				
			strcat(client_send_data,(iter->first).c_str());
			strcat(client_send_data," ");
			strcat(client_send_data,(iter->second).c_str());
			strcat(client_send_data, msg1);
			client();
			Node.myMap.erase(iter->first);
		}

		gotEntry=false;	
	}

	if(gotEntry==false)
	{
		cout<<"Exiting from Pastry."<<endl;
		exit(1);
	}	
}


void receiveRouteTable(char* cmd)
{
	char* recvd_nodeID = substring(cmd, 4, 8);
	char* rtableData = substring(cmd, 13, strlen(cmd)-13);
	char rtable[8][16][9];
	int i=0,j=0;
	istringstream ss(rtableData);
	string token;

	while(getline(ss,token,'@'))
	{
		string rtableEntry="";
		istringstream iss(token);
		j=0;
		while(getline(iss, rtableEntry, '$') && j<16)
		{
			memset(rtable[i][j],'\0', 8);
			strcpy(rtable[i][j],rtableEntry.c_str());
			j++;
		}	
		i++;
	}

	cout<<"The Routing table of "<<recvd_nodeID<<endl;
	cout<<"-----------------------"<<endl;
	for(i=0;i<8;i++)
	{
		cout<<i<<" Match    ";
		for(j=0;j<16;j++)
		{
			for(int k=0;k<8;k++)
			{	
				if(k==i||k==i+1)
					cout<<"-";	
				cout<<rtable[i][j][k];

			}	
			cout<<"   ";
		}
		cout<<endl;
	}

	cout << "\n------------------------------" << endl;
	cout << ">>>: ";
}

void receieveLeafSet(char* cmd)
{
	char* recvd_nodeID = substring(cmd, 4, 8);
	char* lsetData = substring(cmd, 13, strlen(cmd)-13);
	char lset[4][9];
	char lsetip[4][22];

	istringstream ss(lsetData);
	string token;
	int i=0;
	while(getline(ss,token,'$'))
	{
		char* tkn = (char*)token.c_str();
		char* id = substring(tkn, 0, indexOf(tkn, '&'));
		char* ip = substring(tkn, indexOf(tkn, '&')+1, strlen(tkn)-indexOf(tkn, '&')-1);
		strcpy(lset[i],id);
		strcpy(lsetip[i],ip);
		i++;
	}	

	cout<<"The Leaf Set of "<<recvd_nodeID<<endl;
	cout<<"-------------------"<<endl;
	cout<<"Smaller than NodeID"<<endl;
	
	for(i=0;i<2;i++)
		cout<<(i+1)<<"---"<<lset[i]<<"---"<<lsetip[i]<<endl;	

	cout<<"Larger than NodeID"<<endl;
	for(i=2;i<4;i++)
		cout<<(i+1)<<"---"<<lset[i]<<"---"<<lsetip[i]<<endl;

	cout << "\n------------------------------" << endl;
	cout << ">>>: ";

}

void recieveNeighbourSet(char* cmd)
{
	char* recvd_nodeID = substring(cmd, 4, 8);
	char* nsetData = substring(cmd, 13, strlen(cmd)-13);
	char nset[4][9];
	char nsetip[4][9];

	istringstream ss(nsetData);
	string token;
	int i=0;
	while(getline(ss,token,'$'))
	{
		char* tkn = (char*)token.c_str();
		char* id = substring(tkn, 0, indexOf(tkn, '&'));
		char* ip = substring(tkn, indexOf(tkn, '&')+1, strlen(tkn)-indexOf(tkn, '&')-1);
		strcpy(nset[i],id);
		strcpy(nsetip[i],ip);
		i++;
	}

	cout<<"The Neighbourhood Set of "<<recvd_nodeID<<endl;
	cout<<"-------------------"<<endl;
	for(i=0;i<4;i++)
		cout<<(i+1)<<"---"<<nset[i]<<"---"<<nsetip[i]<<endl;

	cout << "\n------------------------------" << endl;
	cout << ">>>: ";
}

void printFingerData(char* ipPort)
{
	char recvd_nodeID[9];
	strcpy(recvd_nodeID,hashValue(ipPort));
	recvd_nodeID[8]='\0';

	cout<<endl;
	cout<<"NodeID:"<<recvd_nodeID<<"     IPPORT:"<<ipPort<<endl;	

	destIP=substring(ipPort,0,indexOf(ipPort,':'));
	destPort= substring(ipPort, indexOf(ipPort, ':')+1, strlen(ipPort)-indexOf(ipPort, ':')-1);
	memset(client_send_data,'\0',50);
	strcpy(client_send_data,"cmd gotIt finger ");
	strcat(client_send_data,Node.server_ip_port);

	client();

	cout << "\n------------------------------" << endl;
	cout << ">>>: ";
}

void printDumpData(char* Data)
{
	int i=0,j=0;
	recvStateTable(Data);
	cout<<endl;
	cout<<"NodeID: "<<recvd.nodeID<<endl;
	cout<<"IP: "<<recvd.server_ip_port<<endl;	
	cout<<endl;
	cout<<"The Leaf Set of "<<recvd.nodeID<<endl;
	cout<<"-------------------"<<endl;
	cout<<"Smaller than NodeID"<<endl;
	
	for(i=0;i<2;i++)
		cout<<(i+1)<<"---"<<recvd.leafSet[i].nodeEntry<<"---"<<recvd.leafSet[i].ip<<endl;	

	cout<<"Larger than NodeID"<<endl;
	for(i=2;i<4;i++)
		cout<<(i+1)<<"---"<<recvd.leafSet[i].nodeEntry<<"---"<<recvd.leafSet[i].ip<<endl;
	cout<<endl;

	cout<<"The Routing table of "<<recvd.nodeID<<endl;
	cout<<"-----------------------"<<endl;
	for(i=0;i<8;i++)
	{
		cout<<i<<" Match    ";
		for(j=0;j<16;j++)
		{
			for(int k=0;k<8;k++)
			{	
				if(k==i||k==i+1)
					cout<<"-";	
				cout<<recvd.rTable[i][j].nodeEntry[k];
			}	
			cout<<"   ";
		}
		cout<<endl;
	}
	cout<<endl;

	cout<<"The Neighbourhood Set of "<<recvd.nodeID<<endl;
	cout<<"-------------------"<<endl;
	for(i=0;i<4;i++)
		cout<<(i+1)<<"---"<<recvd.neighbourSet[i].nodeEntry<<"---"<<recvd.neighbourSet[i].ip<<endl;

	if(strcmp(cmdInExec,"dump")!=0)
	{	
		destIP=substring(recvd.server_ip_port,0,indexOf(recvd.server_ip_port,':'));
		destPort= substring(recvd.server_ip_port, indexOf(recvd.server_ip_port, ':')+1, strlen(recvd.server_ip_port)-indexOf(recvd.server_ip_port, ':')-1);
		memset(client_send_data,'\0',50);
		strcpy(client_send_data,"cmd gotIt dumpall");
		strcat(client_send_data,Node.server_ip_port);
		client();
	}
	cout << "\n------------------------------" << endl;
	cout << ">>>: ";

}

void * server(void * arg)
{
	int sock, connected, trueint = 1;

	struct sockaddr_in server_addr, client_addr;
	unsigned int sin_size;

	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		perror("Socket");
		exit(1);
	}

	serverSock = sock;

	if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &trueint, sizeof(int)) == -1) {
		perror("Setsockopt");
		exit(1);
	}

	server_addr.sin_family = AF_INET;
	server_port=port;
	if (server_port != 0) //Let the server choose the port itself if not supplied externally
		server_addr.sin_port = htons(server_port);
	
	server_addr.sin_addr.s_addr = INADDR_ANY;

	bzero(&(server_addr.sin_zero), 8);

	if (bind(sock, (struct sockaddr *) ((&server_addr)),
			sizeof(struct sockaddr)) == -1) {
		perror("Unable to bind. Check if you have entered a valid port number.");
		exit(1);
	}
	if (listen(sock, 5) == -1) { 		//manage no of clients to be cnctd
		perror("Listen");
		exit(1);
	}

	fflush(stdout);
	while (1) 
	{
		int bytes_received;
		sin_size = sizeof(struct sockaddr_in);
		connected
				= accept(sock, (struct sockaddr*) ((&client_addr)), &sin_size);

		bytes_received = recv(connected, server_recv_data, DATA_SIZE_KILO, 0);
		server_recv_data[bytes_received] = '\0';

		char* type = substring(server_recv_data, 0, 3);  


		char* Data = substring(server_recv_data,4,strlen(server_recv_data));


		if(strcmp(type,"cmd")==0)
			runCommand(server_recv_data);
		else if(strcmp(type,"stb")==0)
			updateStateTables(server_recv_data);
		else if(strcmp(type,"fil")==0)					//fil is for filling the map
			storeInKeyMap(Data);
		else if(strcmp(type,"rec")==0)
			printDestKeyMap(Data);
		else if(strcmp(type,"rcv")==0)
			updateRecvValue(Data);				//related to sat mode success n unsuccess
		else if(strcmp(type,"gmv")==0)
			printKeyValue(Data);
		else if(strcmp(type,"nxt")==0)
		{
			string s1,s2,s3;
			istringstream ss(Data);
			ss>>s1>>s2>>s3;
			char keyHash[9];
			strcpy(keyHash,(char*)s2.c_str());
			char* ipport=(char*)s3.c_str();
			keyHash[8]='\0';
			getValueForKey(keyHash,ipport);
		}
		else if(strcmp(type,"key")==0 )
			updateMyMap(server_recv_data);
		else if(strcmp(type,"rtb")==0)
			receiveRouteTable(server_recv_data);
		else if(strcmp(type,"lst")==0)
			receieveLeafSet(server_recv_data);
		else if(strcmp(type,"nst")==0)
			recieveNeighbourSet(server_recv_data);
		else if(strcmp(type,"fgr")==0)
			printFingerData(Data);
		else if(strcmp(type,"dmp")==0)
			printDumpData(server_recv_data);
		
		fflush(stdout);
		close(connected);
		isCreated=true;	
		hasJoined=true;		
	}
	close(sock);

	return NULL;
}

void helperDump()
{
	cout<<"NodeID: "<<Node.nodeID<<endl;
	cout<<"IP: "<<Node.server_ip_port<<endl;
	printCurrentLeafSet();
	printCurrentRoutingTable();
	printCurrentNeighbourSet();
	//printMyKeys();

}

char* getSelfIpPort()
{
	char hostname[40];
	struct hostent *host;
	if(gethostname(hostname,40)==0)
		host=gethostbyname(hostname);
	char* selfIP = inet_ntoa(*(struct in_addr *)* host->h_addr_list);
	char* ipPort = strcat(selfIP,":");
	ipPort = strcat(ipPort,sPort);

	return ipPort;
}

void fillNodeEntries()
{	 
	char* ipPort = getSelfIpPort();
	strcpy(Node.server_ip_port,ipPort);

	char* nodeId=(char *)hashValue(ipPort);
	strcpy(Node.nodeID,nodeId);

	for(int i=0;i<8;i++)
	{	
		Node.rTable[i][hexToDecimal(*(nodeId+i))].nodeEntry[0]=*(nodeId+i);
		strcpy(Node.rTable[i][hexToDecimal(*(nodeId+i))].ip,Node.server_ip_port);
	}	
}	

void helperCreate()
{
	if(isCreated)
	{		
		cout<<"A network already created.....Give some other port this time."<<endl;
		return;
	}	
	else
	{
		pthread_t sid,cid;
		pthread_create(&sid,NULL,server,NULL);
		fillNodeEntries();
		cout<<"Pastry is Up !!!   Keep Distributing !!"<<endl;
	}	
}

void helperJoin(char* cmdData)
{
	char* cmd = substring(cmdData, 0, indexOf(cmdData, ' '));
	char* Data = substring(cmdData, indexOf(cmdData, ' ')+1, strlen(cmdData)-indexOf(cmdData, ' ')-1);
	strcpy(first_ipPort,Data);
	first_ipPort[strlen(Data)-1]='\0';
	destIP = substring(Data, 0, indexOf(Data, ':'));
	destPort = substring(Data, indexOf(Data, ':')+1, strlen(Data)-indexOf(Data, ':')-1);
	if(isCreated || hasJoined)
	{
		cout<<"This Node already in some other network....Give some other port this time."<<endl;
		return;
	}
	else
	{
		pthread_t sid;
		pthread_create(&sid,NULL,server,NULL);
		fillNodeEntries();
		char msg_type[10] ;
		strcpy(msg_type,"cmd ");
		char* ipPort = getSelfIpPort();
		strcat(msg_type,"join ");
		strcpy(client_send_data,msg_type);
		strcat(client_send_data, ipPort);
		client();

	}	
}

void updateRecvValue(char* recv)
{
	strcpy(recv_value,"");
	strcpy(recv_value,recv);
	recv_value[3]='\0';
}

void printDestKeyMap(char* str)
{
	cout<<"The key value is stored at :" << str<<endl;
}

int prefix(char* keyHash,char* nodeid)
{
	int i;
	for(i=0;i<8;i++)
	{
		if(keyHash[i]!=nodeid[i])
			break;
	}
	return i;
}

void storeInKeyMap(char* str)
{
	string s1,s2,s3,s4,s5;
	istringstream ss(str);
	ss>>s1>>s2>>s3>>s4>>s5;
	char keyHash[9] ;
	strcpy(keyHash,(char*)s2.c_str());
	char* value = (char*)s3.c_str();
	char* ipport=(char*)s4.c_str();
	keyHash[8]='\0';
	char ov[3];
	strcpy(ov,(char*)s5.c_str());
	ov[2]='\0';
	if(strcmp(mode,SAT_MODE)==0)
	{
		if(strcmp(ov,"ud")==0)
		{
			if(isKeyPresentInMap(keyHash))	
			{
				destIP=substring(ipport,0,indexOf(ipport,':'));
				destPort=substring(ipport,indexOf(ipport,':')+1,strlen(ipport)-indexOf(ipport,':')-1);
				strcpy(client_send_data,"");
				strcpy(client_send_data,"rcv ");
				strcat(client_send_data,"uns");
				client();
				return;
			}
			else
			{
				destIP=substring(ipport,0,indexOf(ipport,':'));
				destPort=substring(ipport,indexOf(ipport,':')+1,strlen(ipport)-indexOf(ipport,':')-1);
				strcpy(client_send_data,"");
				strcpy(client_send_data,"rcv ");
				strcat(client_send_data,"suc");
				client();
			}
		}
	}

	Node.myMap[keyHash]=value;
	destIP=substring(ipport,0,indexOf(ipport,':'));
	destPort=substring(ipport,indexOf(ipport,':')+1,strlen(ipport)-indexOf(ipport,':')-1);
	strcpy(client_send_data,"");
	strcpy(client_send_data,"rec ");
	strcat(client_send_data,Node.nodeID);
	client();
	
}



void helperPut(char* str)
{
	string s1,s2,s3,s4;
	istringstream ss(str);
	ss>>s1>>s2>>s3>>s4;
	char keyHash[9] ;
	strcpy(keyHash, (char*)s2.c_str());
	char* ipport=(char*)s4.c_str();

	keyHash[8]='\0';
	if(strcmp(keyHash,Node.nodeID)==0)
	{
		storeInKeyMap(str);
	}
	else
	{
		int left=0;
		int right=3;
		while(left<2)
		{
			if(Node.leafSet[left].nodeEntry[0]!='\0')				// check nodeEntry != '\0';
				break;
			else
				left++;
		}
		while(right>1)
		{
			if(Node.leafSet[right].nodeEntry[0]!='\0')
				break;
			else
				right--;
		}
		if((left<right)&&((strcmp(keyHash,Node.leafSet[left].nodeEntry)>0) && (strcmp(keyHash,Node.leafSet[right].nodeEntry)<0)))
			{
				long int min=hexStringToInt((char*)"ffffffff");
				long int x;
				RoutingTable temp_node;
				x=absolute(hexStringToInt(keyHash)-hexStringToInt(Node.nodeID));
				if(x<min)
				{
					strcpy(temp_node.nodeEntry,Node.nodeID);
					strcpy(temp_node.ip,Node.server_ip_port);
					min=x;
				}
				
				for(int i=left;i<=right;i++)
				{
					x=absolute((hexStringToInt(keyHash))-(hexStringToInt(Node.leafSet[i].nodeEntry)));
					if(x<min)
					{
						strcpy(temp_node.nodeEntry,Node.leafSet[i].nodeEntry);
						strcpy(temp_node.ip,Node.leafSet[i].ip);
						min=x;
					}
				}
				char msgtype[30];
				strcpy(msgtype,"fil ");
				strcpy(client_send_data,"");
				strcpy(client_send_data,msgtype);
				strcat(client_send_data,str);
				char *destIpPort=temp_node.ip;
				destIP=substring(destIpPort,0,indexOf(destIpPort,':'));
				destPort=substring(destIpPort,indexOf(destIpPort,':')+1,strlen(destIpPort)-indexOf(destIpPort,':')-1);
				client();	
			}	
			else
			{
				int l=prefix(keyHash,Node.nodeID);
				int j=hexToDecimal(keyHash[l]);
				if(Node.rTable[l][j].nodeEntry[0]!='\0')
				{
					char *destIpPort = Node.rTable[l][j].ip;
					destIP=substring(destIpPort,0,indexOf(destIpPort,':'));
					destPort=substring(destIpPort,indexOf(destIpPort,':')+1,strlen(destIpPort)-indexOf(destIpPort,':')-1);
					char msgtype[30];
					strcpy(msgtype,"cmd ");
					strcpy(client_send_data,"");
					strcpy(client_send_data,msgtype);
					strcat(client_send_data,str);
					client();
				}
				else
				{
					long int min=hexStringToInt((char*)"ffffffff");
					long int x,leafkey,nodekey,rcellkey,nkey;				//nkey is used if neigh set is implemented
					nodekey=absolute((hexStringToInt(Node.nodeID))-(hexStringToInt(keyHash)));
					RoutingTable temp_node;
					for(int i=left;i<=right;i++)
					{
						leafkey=absolute((hexStringToInt(Node.leafSet[i].nodeEntry))-(hexStringToInt(keyHash)));
						if((prefix(Node.leafSet[i].nodeEntry,keyHash)>=l) && (leafkey < nodekey) )
						{
							x=absolute((hexStringToInt(keyHash))-(hexStringToInt(Node.leafSet[i].nodeEntry)));
							if(x<min)
							{
								strcpy(temp_node.nodeEntry,Node.leafSet[i].nodeEntry);
								strcpy(temp_node.ip,Node.leafSet[i].ip);
								min=x;
							}
						}

					}	
					for(int i=0;i<8;i++)	
					{
						for(int j=0;j<16;j++)
						{
							if((Node.rTable[i][j].nodeEntry[0]!='\0') && (Node.rTable[i][j].nodeEntry[1]!='\0'))
							{
								rcellkey=absolute((hexStringToInt(Node.rTable[i][j].nodeEntry))-(hexStringToInt(keyHash)));
								if((prefix(Node.rTable[i][j].nodeEntry,keyHash)>=l) && (rcellkey < nodekey))
								{	
									x=absolute((hexStringToInt(keyHash))-(hexStringToInt(Node.rTable[i][j].nodeEntry)));
									if(x<min)
									{
										//cout<<"rtable min in "<<min<<endl;
										strcpy(temp_node.nodeEntry,Node.rTable[i][j].nodeEntry);
										strcpy(temp_node.ip,Node.rTable[i][j].ip);
										min=x;
										//cout<<"rtable min out "<<min<<endl;
										
									}
								}
							}
						}
					}
					for(int i=0;i<4;i++)
					{
						nkey=absolute((hexStringToInt(Node.neighbourSet[i].nodeEntry))-(hexStringToInt(keyHash)));
						if((prefix(Node.neighbourSet[i].nodeEntry,keyHash)>=l)&&(nkey < nodekey))
						{
							x=nkey;
							if(x<min)
							{
								strcpy(temp_node.nodeEntry,Node.neighbourSet[i].nodeEntry);
								strcpy(temp_node.ip,Node.neighbourSet[i].ip);
								min=x;
							}
						}
					}
					if(min==hexStringToInt((char*)"ffffffff"))
					{
						storeInKeyMap(str);
						return;
					}

					char *destIpPort = temp_node.ip;
					destIP=substring(destIpPort,0,indexOf(destIpPort,':'));
					destPort=substring(destIpPort,indexOf(destIpPort,':')+1,strlen(destIpPort)-indexOf(destIpPort,':')-1);
					char msgtype[30];
					strcpy(msgtype,"cmd ");
					strcpy(client_send_data,"");
					strcpy(client_send_data,msgtype);
					strcat(client_send_data,str);
					client();
				}
			}
	}
}


bool isKeyPresentInMap(char *key) 
{
	for(map<string,string>::iterator iter=(Node.myMap).begin();iter!=(Node.myMap).end();++iter)
	{
		
		if(strcmp(key,(((*iter).first).c_str()))==0)
		{
			return true;
		}
	}
	return false;
					
}


void printKeyValue(char* str)
{
	char* value;
	value=substring(str,0,indexOf(str,' ')+1);
	if(strcmp(mode,SAT_MODE)==0 && getsat)
	{
		if(strcmp(value,"D_N_F")==0)
		{
			strcpy(cor_get_value,value);		// for verify in satellite mode
			cor_get_value[5]='\0';
		}
		else
		{
			strcpy(cor_get_value,"D_C_F");
			cor_get_value[5]='\0';
			strcpy(key_value,value);
		}
		return;
	}
	if(strcmp(value,(char*)"D_N_F")==0)
	{
		cout<<"Data is not found"<<endl;
	}
	else
	{
		cout<<"Value:  "<<value<<endl;
	}

	
}

void getValueForKey(char* key,char* ipport)
{
	destIP=substring(ipport,0,indexOf(ipport,':'));
	destPort=substring(ipport,indexOf(ipport,':')+1,strlen(ipport)-indexOf(ipport,':')-1);
	char* value;	
	char keyHash[9];
	strcpy(keyHash,key);
	keyHash[8]='\0';		
	if(!(isKeyPresentInMap(keyHash)))
	{
		value=(char*)"D_N_F";
	}
	else
	{

		map<string,string>::iterator iter = Node.myMap.find(keyHash);
		value=(char*)((*iter).second).c_str(); 
	}
	strcpy(client_send_data,"");
	strcpy(client_send_data,"gmv ");
	strcat(client_send_data,value);
	client();
}

void helperGet(char *str)
{
	string s1,s2,s3;
	istringstream ss(str);
	ss>>s1>>s2>>s3;
	char keyHash[9]; ;
	strcpy(keyHash,(char*)s2.c_str());
	keyHash[8]='\0';
	char* ipport= (char*)s3.c_str();
	if(strcmp(keyHash,Node.nodeID)==0)
	{
		getValueForKey(keyHash,ipport);
	}
	else
	{
		int left=0;
		int right=3;
		while(left<2)
		{
			if(Node.leafSet[left].nodeEntry[0]!='\0')				// check nodeEntry != '\0';
				break;
			else
				left++;
		}
		while(right>1)
		{
			if(Node.leafSet[right].nodeEntry[0]!='\0')
				break;
			else
				right--;
		}

		if((left<right)&&((strcmp(keyHash,Node.leafSet[left].nodeEntry)>0) && (strcmp(keyHash,Node.leafSet[right].nodeEntry)<0)))
			{
				long int min=hexStringToInt((char*)"ffffffff");
				long int x;
				RoutingTable temp_node;
				x=absolute((hexStringToInt(keyHash)-hexStringToInt(Node.nodeID)));
				if(x<min)
				{
					strcpy(temp_node.nodeEntry,Node.nodeID);
					strcpy(temp_node.ip,Node.server_ip_port);
					min = x;
				}
				for(int i=left;i<=right;i++)
				{
					x=absolute((hexStringToInt(keyHash))-(hexStringToInt(Node.leafSet[i].nodeEntry)));
					if(x<min)
					{
						strcpy(temp_node.nodeEntry,Node.leafSet[i].nodeEntry);
						strcpy(temp_node.ip,Node.leafSet[i].ip);
						min=x;
					}
				}
				
				char msgtype[30];
				strcpy(msgtype,"nxt ");
				strcpy(client_send_data,"");
				strcpy(client_send_data,msgtype);
				strcat(client_send_data,str);
				char *destIpPort=temp_node.ip;
				destIP=substring(destIpPort,0,indexOf(destIpPort,':'));
				destPort=substring(destIpPort,indexOf(destIpPort,':')+1,strlen(destIpPort)-indexOf(destIpPort,':')-1);
				client();	
			}
		else
			{
				int l=prefix(keyHash,Node.nodeID);
				int j=hexToDecimal(keyHash[l]);
				if(Node.rTable[l][j].nodeEntry[0]!='\0')
				{
					char *destIpPort = Node.rTable[l][j].ip;
					
					destIP=substring(destIpPort,0,indexOf(destIpPort,':'));
					destPort=substring(destIpPort,indexOf(destIpPort,':')+1,strlen(destIpPort)-indexOf(destIpPort,':')-1);
					char msgtype[30];
					strcpy(msgtype,"cmd ");
					strcpy(client_send_data,"");
					strcpy(client_send_data,msgtype);
					strcat(client_send_data,str);
					client();
				}
				else
				{
					long int min=hexStringToInt((char*)"ffffffff");
					long int x,leafkey,nodekey,rcellkey,nkey;				//nkey is used if neigh set is implemented
					nodekey=absolute((hexStringToInt(Node.nodeID))-(hexStringToInt(keyHash)));
					RoutingTable temp_node;
					for(int i=left;i<=right;i++)
					{
						leafkey=absolute((hexStringToInt(Node.leafSet[i].nodeEntry))-(hexStringToInt(keyHash)));
						if((prefix(Node.leafSet[i].nodeEntry,keyHash)>=l) && (leafkey < nodekey) )
						{
							x=absolute((hexStringToInt(keyHash))-(hexStringToInt(Node.leafSet[i].nodeEntry)));
							if(x<min)
							{
								strcpy(temp_node.nodeEntry,Node.leafSet[i].nodeEntry);
								strcpy(temp_node.ip,Node.leafSet[i].ip);
								min=x;
							}
						}

					}	
					for(int i=0;i<8;i++)	
					{
						for(int j=0;j<16;j++)
						{
							if((Node.rTable[i][j].nodeEntry[0]!='\0') && (Node.rTable[i][j].nodeEntry[1]!='\0'))
							{
								rcellkey=absolute((hexStringToInt(Node.rTable[i][j].nodeEntry))-(hexStringToInt(keyHash)));
								if((prefix(Node.rTable[i][j].nodeEntry,keyHash)>=l) && (rcellkey < nodekey))
								{	
									x=absolute((hexStringToInt(keyHash))-(hexStringToInt(Node.rTable[i][j].nodeEntry)));
									if(x<min)
									{
										strcpy(temp_node.nodeEntry,Node.rTable[i][j].nodeEntry);
										strcpy(temp_node.ip,Node.rTable[i][j].ip);
										min=x;
									}
								}
							}
						}
					}
					for(int i=0;i<4;i++)
					{
						nkey=absolute((hexStringToInt(Node.neighbourSet[i].nodeEntry))-(hexStringToInt(keyHash)));
						if((prefix(Node.neighbourSet[i].nodeEntry,keyHash)>=l)&&(nkey < nodekey))
						{
							x=nkey;
							if(x<min)
							{
								strcpy(temp_node.nodeEntry,Node.neighbourSet[i].nodeEntry);
								strcpy(temp_node.ip,Node.neighbourSet[i].ip);
								min=x;
							}
						}
					}
					if(min==hexStringToInt((char*)"ffffffff"))
					{
						getValueForKey(keyHash,ipport);
						return;
					}	

					char *destIpPort = temp_node.ip;
					destIP=substring(destIpPort,0,indexOf(destIpPort,':'));
					destPort=substring(destIpPort,indexOf(destIpPort,':')+1,strlen(destIpPort)-indexOf(destIpPort,':')-1);
					char msgtype[30];
					strcpy(msgtype,"cmd ");
					strcpy(client_send_data,"");
					strcpy(client_send_data,msgtype);
					strcat(client_send_data,str);
					client();
				}
			}
			
	}

}


void helperputsat(char* str)
{
	string s1,s2,s3;
	istringstream ss(str);
	ss>>s1>>s2>>s3;     //s4 would be added if n is taken dynamically
	char* day=(char*)s2.c_str();
	char* satcode= (char*)s3.c_str();
	char md_satcode[32],ow[3];
	strcpy(md_satcode,"");
	strcpy(md_satcode,md5(satcode).c_str());
	vector<int> nparts;
	vector<int> key;
	vector<string> helpkey;
	vector<string> keyHash;
	vector<string> put;
	int q,r;
	r=32%nop;  // normally n
	q=32/nop;
	for(int i=0;i<(nop-1);i++)
	{
		nparts.push_back(q);
	}
	nparts.push_back(q+r);
	vector<string> valuecode;
	int dayval=atoi(day);
	for(int i=0;i<nop;i++)
	{
		key.push_back(dayval+(1000*(i+1)));
	}
	int sum=0;
	for(int i=0;i<nop;i++)
	{
		valuecode.push_back(substring(md_satcode,sum,nparts[i]));
		sum+=nparts[i];
	}
	for(int i=0;i<nop;i++)
	{
		stringstream sskey;
		string strkey;
		sskey<<key[i];
		strkey=sskey.str();
		helpkey.push_back(strkey);
	}
	char helperkey[9];
	for(int i=0;i<nop;i++)
	{
		strcpy(helperkey,"");
		strcpy(helperkey,hashValue(helpkey[i].c_str()));
		helperkey[8]='\0';
		keyHash.push_back(helperkey);
	}
	strcpy(ow,"");
	strcpy(ow,"ud");
	ow[2]='\0';
	char helpput[100];
	for(int i=0;i<nop;i++)
	{
		strcpy(helpput,"");
		strcat(helpput,"put ");
		strcat(helpput,keyHash[i].c_str());
		strcat(helpput," ");
		strcat(helpput,valuecode[i].c_str());
		strcat(helpput," ");
		strcat(helpput,Node.server_ip_port);
		strcat(helpput," ");
		strcat(helpput,ow);
		put.push_back(helpput);
	}
	helperPut((char*)put[0].c_str());
	while(1)
	{
		if(recv_value[0]!='\0')
		{
			if(strcmp(recv_value,"uns")==0)
			{
				char input;
				cout<<"The Code is Already stored. Do You like to continue[Y/N]"<<endl;
				cin>>input;
				if(input=='Y'||input=='y')
				{
					char helpputin[100];
					for(int j=0;j<nop;j++)
					{
						strcpy(helpputin,"");
						strcpy(helpputin,put[j].c_str());
						helpputin[strlen(helpputin)-2]='y';
						helpputin[strlen(helpputin)-1]='e';
						put[j]=helpputin;
					}
					for(int i=0;i<nop;i++)
					{
						helperPut((char*)put[i].c_str());
					}
				}
				else if(input=='N'||input=='n')
					cout<<"Thank You!! For ur feedback..."<<endl;
				else
					cout<<"Enter Correct decision Yes / No";
			}
			else
			{
				for(int i=1;i<nop;i++)
				{
					helperPut((char*)put[i].c_str());
				}
				sleep(1.2);
			}
			memset(recv_value,'\0',strlen(recv_value));
			break;
		}
	}
	memset(recv_value,'\0',strlen(recv_value));	
	memset(ow,'\0',strlen(ow));
	nparts.clear();
	key.clear();
	helpkey.clear();
	keyHash.clear();
	valuecode.clear();
	put.clear();
}


void helperVerifySat(char* str)
{
	getsat=true;
	string s1,s2,s3;
	istringstream ss(str);
	ss>>s1>>s2>>s3;     //s4 would be added if n is taken dynamically
	char* day=(char*)s2.c_str();
	char* satcode= (char*)s3.c_str();
	char md_satcode[33];
	strcpy(md_satcode,"");
	strcpy(md_satcode,md5(satcode).c_str());
	md_satcode[32]='\0';
	char helpkey1[10],helpkey2[10],helpkey3[10];   //error prone
	vector<int> key;
	vector<string> helpkey;
	vector<string> keyHash;
	vector<string> get;
	int dayval=atoi(day);
	for(int i=0;i<nop;i++)
	{
		key.push_back(dayval+(1000*(i+1)));
	}
	for(int i=0;i<nop;i++)
	{
		stringstream sskey;
		string strkey;
		sskey<<key[i];
		strkey=sskey.str();
		helpkey.push_back(strkey);
	}
	char helperkey[9];
	for(int i=0;i<nop;i++)
	{
		strcpy(helperkey,"");
		strcpy(helperkey,hashValue(helpkey[i].c_str()));
		helperkey[8]='\0';
		keyHash.push_back(helperkey);
	}
	char helpget[100];
	for(int i=0;i<nop;i++)
	{
		strcpy(helpget,"");
		strcat(helpget,"get ");
		strcat(helpget,keyHash[i].c_str());
		strcat(helpget," ");
		strcat(helpget,Node.server_ip_port);
		get.push_back(helpget);
	}

	helperGet((char*)get[0].c_str());
	int i=1;
	vector<string> value;
	while(1)
	{
		if(i>=nop && cor_get_value[0]!='\0' && ((strcmp(cor_get_value,"D_N_F")!=0)||(strcmp(cor_get_value,"D_N_F")==0) ))
		{
			if(strcmp(cor_get_value,"D_N_F")==0)
			{
				cout<<"ERROR FAIL!!!!!....Satellite Code is INVALID for given Day "<<day<<endl;
				value.clear();
				break;
			}
			char temp_md5[32];
			value.push_back(key_value);
			strcpy(temp_md5,"");
			for(int j=0;j<nop;j++)
			{
				strcat(temp_md5,(char*)value[j].c_str());
			}
			if(strcmp(temp_md5,md5(satcode).c_str())==0)
				cout<<"The Satellite Code is Valid For Given Day "<<day<<endl;
			else
				cout<<"ERROR!!!!!....Satellite Code is INVALID for given Day "<<day<<endl;
			memset(cor_get_value,'\0',strlen(cor_get_value));
			value.clear();
			break;
		}

		if(cor_get_value[0]!='\0' && i<nop)
		{
			if(strcmp(cor_get_value,"D_N_F")==0)
			{
				cout<<"ERROR!!!!!....Satellite Code is INVALID for given Day "<<day<<endl;
			 	value.clear();
				break;
			}
			else
			{
				memset(cor_get_value,'\0',strlen(cor_get_value));
				value.push_back(key_value);
				helperGet((char*)get[i].c_str());
				i++;
	
			}
		}
	}
	keyHash.clear();
	helpkey.clear();
	get.clear();
	key.clear();
	getsat=false;
}



void helperGetAllSat(char* str)
{
	getsat=true;
	string s1,s2;
	istringstream ss(str);
	ss>>s1>>s2;
	char* day=(char*)s2.c_str();
	char* startDay=substring(day,0,indexOf(day,'-'));
	char* endDay=substring(day,indexOf(day,'-')+1,strlen(day)-indexOf(day,'-')-1);
	int startDayVal=atoi(startDay);
	int endDayVal=atoi(endDay);
	int no_of_days= endDayVal-startDayVal+1;
	vector<int> key;
	vector<string> helpkey;
	vector<string> keyHash;
	vector<string> get;
	if(endDayVal<startDayVal)
	{
		cout<<"Enter the least to largest days."<<endl;
		return;
	}
	for(int i=1;i<=no_of_days;i++)
	{
		
		for(int i=0;i<nop;i++)
		{
			key.push_back(startDayVal+(1000*(i+1)));
		}
		for(int i=0;i<nop;i++)
		{
			stringstream sskey;
			string strkey;
			sskey<<key[i];
			strkey=sskey.str();
			helpkey.push_back(strkey);
		}
		char helperkey[9];
		for(int i=0;i<nop;i++)
		{
			strcpy(helperkey,"");
			strcpy(helperkey,hashValue(helpkey[i].c_str()));
			helperkey[8]='\0';
			keyHash.push_back(helperkey);
		}
		char helpget[100];
		for(int i=0;i<nop;i++)
		{
			strcpy(helpget,"");
			strcat(helpget,"get ");
			strcat(helpget,keyHash[i].c_str());
			strcat(helpget," ");
			strcat(helpget,Node.server_ip_port);
			get.push_back(helpget);
		}
		helperGet((char*)get[0].c_str());
		int j=1;
		vector<string> value;
		while(1)
		{
			if(j>=nop && cor_get_value[0]!='\0' && ((strcmp(cor_get_value,"D_N_F")!=0)||(strcmp(cor_get_value,"D_N_F")==0) ))
			{
				if(strcmp(cor_get_value,"D_N_F")==0)
				{
					cout<<"The Code for Day fail "<<startDayVal<<" is :MISSING "<<endl;
					break;
				}
				char temp_md5[33];
				value.push_back(key_value);
				strcpy(temp_md5,"");
				for(int k=0;k<nop;k++)
				{
					strcat(temp_md5,value[k].c_str());
				}
				temp_md5[32]='\0';
				cout<<"The Code for Day "<<startDayVal<<"is :"<<temp_md5<<endl;
				break;
			}
			if(cor_get_value[0]!='\0' && j<nop)
			{
				if(strcmp(cor_get_value,"D_N_F")==0)
				{
					cout<<"The Code for Day "<<startDayVal<<" is :MISSING "<<endl;
					break;
				}
				else
				{
					memset(cor_get_value,'\0',strlen(cor_get_value));
					value.push_back(key_value);
					helperGet((char*)get[j].c_str());
					j++;
				}
			}
		}
		startDayVal++;
		memset(cor_get_value,'\0',strlen(cor_get_value));
		value.clear();
		keyHash.clear();
		helpkey.clear();
		get.clear();
		key.clear();

	}
	getsat=false;
}


void helpHelperFinger()
{
	strcpy(client_send_data,"cmd forward finger");
	destIP=substring(Node.server_ip_port,0,indexOf(Node.server_ip_port,':'));
	destPort=substring(Node.server_ip_port,indexOf(Node.server_ip_port,':')+1, strlen(Node.server_ip_port)-indexOf(Node.server_ip_port,':')-1);
	client();
}


void helpHelperDumpAll()
{
	strcpy(client_send_data,"cmd forward dumpall");
	destIP=substring(Node.server_ip_port,0,indexOf(Node.server_ip_port,':'));
	destPort=substring(Node.server_ip_port,indexOf(Node.server_ip_port,':')+1, strlen(Node.server_ip_port)-indexOf(Node.server_ip_port,':')-1);
	client();
}

void helperPrintRouteTable(char* cmd)
{

	char* command = substring(cmd, 0, indexOf(cmd, ' '));
	char* address = substring(cmd, indexOf(cmd, ' ')+1, strlen(cmd)-indexOf(cmd, ' ')-1 );
	if(address[0]=='\0')
		printCurrentRoutingTable();
	else
	{
		strcpy(client_send_data,"cmd send routetable ");
		strcat(client_send_data,Node.server_ip_port);
		destIP=substring(address,0,indexOf(address,':'));
		destPort=substring(address,indexOf(address,':')+1, strlen(address)-indexOf(address,':')-1);
		client();
	}	
	
}

void helperPrintLeafSet(char* cmd)
{
	char* address = substring(cmd, indexOf(cmd, ' ')+1, strlen(cmd)-indexOf(cmd, ' ')-1 );
	if(address[0]=='\0')
		printCurrentLeafSet();
	else
	{
		strcpy(client_send_data,"cmd send lset ");
		strcat(client_send_data,Node.server_ip_port);
		destIP=substring(address,0,indexOf(address,':'));
		destPort=substring(address,indexOf(address,':')+1, strlen(address)-indexOf(address,':')-1);
		client();
	}	
}

void helperPrintNeighbourSet(char* cmd)
{
	char* address = substring(cmd, indexOf(cmd, ' ')+1, strlen(cmd)-indexOf(cmd, ' ')-1 );
	if(address[0]=='\0')
		printCurrentNeighbourSet();
	else
	{
		strcpy(client_send_data,"cmd send nset ");
		strcat(client_send_data,Node.server_ip_port);
		destIP=substring(address,0,indexOf(address,':'));
		destPort=substring(address,indexOf(address,':')+1, strlen(address)-indexOf(address,':')-1);
		client();
	}	
}

void helperPrintDump(char* cmd)
{
	char* address = substring(cmd, indexOf(cmd, ' ')+1, strlen(cmd)-indexOf(cmd, ' ')-1 );
	if(address[0]=='\0')
		helperDump();
	else
	{
		strcpy(client_send_data,"cmd send dump ");
		strcat(client_send_data,Node.server_ip_port);
		destIP=substring(address,0,indexOf(address,':'));
		destPort=substring(address,indexOf(address,':')+1, strlen(address)-indexOf(address,':')-1);
		strcpy(cmdInExec,"dump");
		client();
	}	
}

void helperRouteTable(char* Data)
{
	if(Data[0]=='\0')
	{
		printCurrentRoutingTable();
	}
	else
	{
		destIP=substring(Data,0,indexOf(Data,':'));
		destPort=substring(Data,indexOf(Data,':')+1,strlen(Data)-indexOf(Data,':')-1);
		strcpy(client_send_data,"");
		strcpy(client_send_data,"rtq ");
		strcat(client_send_data,Node.server_ip_port);
		client();
	}
}

void helperSelf()
{
	cout<<endl;
	cout<<"NodeID:"<<Node.nodeID<<"     IPPORT:"<<Node.server_ip_port<<endl;
}

void helperPort(char* cmd)
{
	sPort = substring(cmd, indexOf(cmd, ' ')+1, strlen(cmd)-indexOf(cmd, ' ')-2);
	port = atoi(sPort);
}

void printMyKeys()
{
	cout<<"The key values stored in node "<<Node.nodeID<<" are: "<<endl;
	for(map<string,string>::iterator iter=Node.myMap.begin(); iter!=Node.myMap.end(); ++iter)
		cout<<"Key: "<<iter->first<<"     Value:"<<iter->second<<endl;

}

void executeCommand()
{


	while (1) {
		cout << "\n------------------------------" << endl;

		cout << ">>>: ";
		fgets(ui_data, sizeof(ui_data), stdin);

		cout << "<<<: " << ui_data;

		char* cmdType = substring(ui_data, 0, indexOf(ui_data, ' '));
		char* Data   = substring(ui_data,indexOf(ui_data,' ')+1,strlen(ui_data)-indexOf(ui_data,' ')-1);
		//strcpy(mode,SAT_MODE);


		if(strcmp(cmdType, "help") == 0)
			helperHelp();

		else if (strcmp(cmdType, "store")==0)
		{
			if(strcmp(mode,SAT_MODE)==0)
				//if you implement fr more than 3 parts take input here and accordingly concat the value to ui_data
				helperputsat(ui_data);
			else 
				cout<<"UnAuthorized Operation... \n You need to be in Satellite Mode to perform this operation..."<<endl;
		}

		else if (strcmp(cmdType, "verify")==0)
		{
			if(strcmp(mode,SAT_MODE)==0)
				helperVerifySat(ui_data);
			else
				cout<<"UnAuthorized Operation... \n You need to be in Satellite Mode to perform this operation..."<<endl;
		}

		else if (strcmp(cmdType, "getall")==0)
		{
			if(strcmp(mode,SAT_MODE)==0)
				helperGetAllSat(ui_data);
			else
				cout<<"UnAuthorized Operation... \n You need to be in Satellite Mode to perform this operation..."<<endl;
		}

		else if (strcmp(cmdType, "dump") == 0)
			helperPrintDump(ui_data);

		else if(strcmp(cmdType, "lset") == 0)
			helperPrintLeafSet(ui_data);

		else if (strcmp(cmdType, "nset") == 0)
			helperPrintNeighbourSet(ui_data);

		else if (strcmp(cmdType, "create") == 0)
			helperCreate();

		else if (strcmp(cmdType, "join") == 0)
			helperJoin(ui_data);

		else if (strcmp(cmdType, "put")==0){
			char* key=substring(Data,0,indexOf(Data,' '));
			char* value=substring(Data , indexOf(Data,' '),strlen(Data)-indexOf(Data,' ')-1);
			char keyHash[9];
			strcpy(keyHash,hashValue(key));
			keyHash[8]='\0';
			strcpy(ui_data,"");
			strcpy(ui_data,cmdType);
			strcat(ui_data," ");
			strcat(ui_data,keyHash);
			strcat(ui_data," ");
			strcat(ui_data,value);
			strcat(ui_data," " );
			strcat(ui_data,Node.server_ip_port);
			helperPut(ui_data);
		}

		else if (strcmp(cmdType, "get")==0){
			char* key=substring(Data,0,indexOf(Data,' '));
			char keyHash[9];
			strcpy(keyHash,hashValue(key));
			keyHash[8]='\0';
			strcpy(ui_data,"");
			strcpy(ui_data,cmdType);
			strcat(ui_data," ");
			strcat(ui_data,keyHash);
			strcat(ui_data," ");
			strcat(ui_data,Node.server_ip_port);
			helperGet(ui_data);
		}

		else if (strcmp(cmdType, "routetable") == 0)
			helperPrintRouteTable(ui_data);

		else if (strcmp(cmdType, "port") == 0)
			helperPort(ui_data);

		else if(strcmp(cmdType, "mykeys") == 0)
			printMyKeys();

		else if(strcmp(cmdType, "self") == 0)
			helperSelf();

		else if(strcmp(cmdType, "quit") == 0)
			helperQuit();

		else if(strcmp(cmdType, "shutdown")==0)
			helperShutDown();

		else if(strcmp(cmdType, "finger")==0)
			helpHelperFinger();

		else if(strcmp(cmdType, "dumpall")==0)
			helpHelperDumpAll();

		else
			cout << "Please Enter a Valid command or Type help for list of Commands"<<endl;


		fflush(stdout);
	}
}


int main(int argc, char* argv[])
{
	if(argc>=2)
	{
		if(argc==2 && strcmp((char*)argv[1]," --sat"))
		{
			cout<<"\nEntering Satellite Mode!!!"<<endl;
			sleep(1.5);
			cout<<"\nProcessing..."<<endl;
			sleep(1.5);
			cout<<"\nSatellite Mode ACTIVATED....."<<endl;
			strcpy(mode,SAT_MODE);
		}
		else
		{
			cout<<"You Have entered Unnecessary Arguments"<<endl;
			exit(1);
		}
	}
	else
	{
		strcpy(mode,NORMAL_MODE);
	}
	char* str;
	//cout<<endl;
	cout<<"Enter port <portnumber> to listen on a port:  "<<endl;

	executeCommand();

	return 0;
}