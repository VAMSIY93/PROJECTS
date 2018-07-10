//g++ Des.cpp -std=c++11 -lglut -lGLU -lGL -o des

#include <iostream>
#include <string.h>
#include <cstdlib>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <unistd.h>
#include <GL/glut.h>

using namespace std;

int count=0;
int max_count=0;
bool stampade=false,h=false;
int press_treshold=25;
int choice,n1,n2;

struct Node
{
long int key;
int x;
int y;
int speed;
int pressure;
int destD;
int eot;
bool umf;
};

struct Heap
{
  int size;
  struct Node** P;
};

struct Node* node;
struct Heap *H1,*H2;
struct Node*** A;

struct Heap* createHeap(int n)
{
    struct Heap* minHeap =
         (struct Heap*) malloc(sizeof(struct Heap));
    minHeap->size = 0;
    minHeap->P=(Node**)malloc((20*n)*sizeof(Node));
        
    return minHeap;
}

void swapHeapNode(struct Node** a, struct Node** b)
{
    struct Node* t = *a;
    *a = *b;
    *b = t;
}

void HeapifyDown(struct Heap* minHeap, int idx)
{
    int smallest,left,right;
    smallest=idx;
    left= 2*idx+1;
    right=2*idx+2;
 
    if (left<minHeap->size && (minHeap->P[left]->key < minHeap->P[smallest]->key))
      smallest = left;
 
    if (right<minHeap->size && (minHeap->P[right]->key < minHeap->P[smallest]->key))
      smallest = right;
 
    if (smallest != idx)
    {
        Node *smallestNode = minHeap->P[smallest];
        Node *idxNode = minHeap->P[idx];

        swapHeapNode(&minHeap->P[smallest], &minHeap->P[idx]);
 
        HeapifyDown(minHeap, smallest);
    }
}

void HeapifyUp(struct Heap* H, int index)
{
    int c=index,p;
    p=(c-1)/2;
    while(H->P[c]->key<H->P[p]->key)
    {
        if(c!=0)
        {    
        Node* cnode=H->P[c];
        Node* pnode=H->P[p];

        swapHeapNode(&H->P[c],&H->P[p]);  
        c=p;
        p=(c-1)/2;
        }
    }    

}

struct Heap* insertNode(Heap* H,Node* n)
{
    H->P[H->size]=n;
    HeapifyUp(H,H->size);          
    H->size=H->size+1;       
    return H;
}

struct Node* extractMin(struct Heap* minHeap)
{
    if (minHeap->size==0)
        return NULL;

    
    struct Node* root = minHeap->P[0];

    struct Node* lastNode = minHeap->P[minHeap->size - 1];
    minHeap->P[0] = lastNode;

    --minHeap->size;

    HeapifyDown(minHeap, 0);
    return root;
}

void drawCircle(float x1,float y1)
{
  	double radius=0.04;
  	float x2=0.0,y2=0.0;
  	float angle=0.0f;
  		
  	glVertex2f(x1,y1);
  	for (angle=1.0f;angle<361.0f;angle+=0.2)
    {
    	x2 = x1+sin(angle)*radius;
      	y2 = y1+cos(angle)*radius;
      	glVertex2f(x2,y2);
    }
}

void renderScene(void) 
{
   	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
   	int u=(n2%2==0)?(n2/2):(1+n2/2);
   	int l=(n2/2);
   	
   	glColor3f(1,1,1);

  	glBegin(GL_QUADS);
    	glVertex2f( 1.0,1.0);
   		glVertex2f( -1.0,1.0);
    	glVertex2f(-1.0,-1.0);
   		glVertex2f(1.0,-1.0);
  	glEnd();

  	glColor3f(1,1,0);
  	glBegin(GL_QUADS);
  		glVertex2f(l*-0.1,1.0);
  		glVertex2f(u*0.1,1.0);
  		glVertex2f(u*0.1,0.0);
  		glVertex2f(l*-0.1,0.0);
  	glEnd();

  	glBegin(GL_QUADS);
  		glVertex2f(-1.0,0.0);
  		glVertex2f(1.0,0.0);
  		glVertex2f(1.0,n1*-0.1);
  		glVertex2f(-1.0,n1*-0.1);
  	glEnd();

  	glBegin(GL_LINES);
    	for(float i=-1.0;i<=1.0;i=i+0.1) 
    	{
      		if (i==0.0) { glColor3f(0,0,0); } else { glColor3f(0,0,0); }
      		glVertex2f(i,-1.0);
      		glVertex2f(i,1.0);
      		if (i==0.0) { glColor3f(0,0,0); } else { glColor3f(0,0,0); }
      		glVertex2f(-1.0,i);
      		glVertex2f(1.0,i);
    	}
  	glEnd();

  	
  	float x1=0.0,y1=0.0;
  	for(int i=0;i<20;i++)
    {      
        y1=(9-i)*0.1+0.05;
             
        for(int j=0;j<20;j++)
        {
            x1=(j-10)*0.1+0.05;
            
            if(A[i][j]!=NULL)
            { 
                glBegin(GL_TRIANGLE_FAN);
                	if(A[i][j]->speed==1)
                		glColor3f(0.4f,0.4f,0.4f);
                	else if(A[i][j]->speed==2)
                		glColor3f(0,1,0);
                	else if(A[i][j]->speed==3)
                		glColor3f(0,0,1);
               		drawCircle(x1,y1);
                glEnd();
            }

            
        }  
    }	
    float sx=0.0,sy=0.0;
    if(stampade==true)
 	{
 		glColor3f(1,0,0);
 		glLineWidth(5);
 		sx=0.1*(node->y-10),sy=0.1*(10-node->x);

 		glBegin(GL_LINES);
 			glVertex2f(sx,sy);
 			glVertex2f(sx+0.1,sy-0.1);
 			glVertex2f(sx,sy-0.1);
 			glVertex2f(sx+0.1,sy);
 		glEnd();
 	}

 	glutSwapBuffers();

}

Node*** insertNewElements(Node*** A,int n,int count,int n1,int n2)
{	
	for(int i=0;i<n1+n2;i++)
	{	
		int l=rand()%3;
		int m=rand()%n;
		int a=0;
		if(m<n-1)
		{	
		struct Node* node=(struct Node*)malloc(sizeof(struct Node));
		node->eot=count+l+1;
		node->speed=l+1;
		node->pressure=0;
		node->umf=false;
		if(i<n1 && A[10+i][0]==NULL)
		{	
			node->destD=20;
			node->key=(node->eot*1000)+node->destD;
			node->x=10+i;
			node->y=0;
			A[10+i][0]=node;
			H1=insertNode(H1,node);
		}
		else if(i>=n1)
		{			
			if(n1>0)
				a=i-n1;
			else
				a=i;
			int b=n2/2;
			
			if(A[0][10-b+a]==NULL)
			{
				node->y=10-b+a;
				node->x=0;
				node->destD=10;
				node->key=(node->eot*1000)+node->destD;
				A[0][10-b+a]=node;
				H2=insertNode(H2,node);
			}
		}
		}
	}
	return A;
}

void simulateMovement()
{

	count=1;	
	int u=(n2%2==0)?(9+n2/2):(10+n2/2);

	while(max_count>=count)
	{
		renderScene();
		//sleep(1);

		while(H1->size>0 && (H1->P[0]->key)/1000==count)
		{
			node=extractMin(H1);

			if(node->destD==1)
			{	
				A[node->x][node->y]=NULL;
			}	
			else if(A[node->x][node->y+1]==NULL)
			{				
				A[node->x][node->y+1]=node;
				A[node->x][node->y]=NULL;
				node->y=node->y+1;				
				node->pressure=0;
				node->umf=false;
				node->eot=node->eot+node->speed;
				node->destD=node->destD-1;
				node->key=(node->eot*1000)+node->destD;
				H1=insertNode(H1,node);
			}
			else if(A[node->x+1][node->y+1]==NULL && (node->x+1)<(10+n1) && (A[node->x+1][node->y]==NULL || (A[node->x+1][node->y]!=NULL && (A[node->x+1][node->y]->eot)>(A[node->x][node->y]->eot)) ))
			{
				A[node->x+1][node->y+1]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x+1;
				node->y=node->y+1;
				node->pressure=0;
				node->umf=false;
				node->destD=node->destD-1;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H1=insertNode(H1,node);	

			}
			else if(A[node->x-1][node->y+1]==NULL && node->x>10 && (A[node->x-1][node->y]==NULL || (A[node->x-1][node->y]!=NULL && (A[node->x-1][node->y]->eot)>(A[node->x][node->y]->eot)) ))
			{
				A[node->x-1][node->y+1]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x-1;
				node->y=node->y+1;
				node->pressure=0;
				node->umf=false;
				node->destD=node->destD-1;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H1=insertNode(H1,node);
			}	
			else if(A[node->x+1][node->y]==NULL && (node->x+1)<(10+n1) && ((node->y>0 && (A[node->x+1][node->y-1]==NULL || (A[node->x+1][node->y-1]!=NULL && (A[node->x+1][node->y-1]->eot)>(A[node->x][node->y]->eot)))) || node->y==0) )
			{
				A[node->x+1][node->y]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x+1;
				node->pressure=0;
				node->umf=false;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H1=insertNode(H1,node);
			}
			else if(A[node->x-1][node->y]==NULL && node->x>10 && ((node->y>0 && (A[node->x-1][node->y-1]==NULL || (A[node->x-1][node->y-1]!=NULL && (A[node->x-1][node->y-1]->eot)>(A[node->x][node->y]->eot)))) || node->y==0) )
			{
				A[node->x-1][node->y]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x-1;
				node->pressure=0;
				node->umf=false;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H1=insertNode(H1,node);
			}
			else
			{
				struct Node *nd1,*nd2;
				nd2=A[node->x][node->y];
				nd1=A[node->x][node->y+1];
				while(nd1!=NULL)
				{
					nd1->pressure=(nd1->pressure)/4+nd2->pressure+1;
					if(nd1->pressure>press_treshold)
					{	
						stampade=true;
						node=nd1;
					
						renderScene();
						sleep(10);
						exit(0);
					}
				
					nd2=nd1;
					nd1=A[nd1->x][nd1->y+1];
					
					if(nd2->umf==false)
						break;
				}
				node->eot=node->eot+1;
				node->key=(node->eot*1000)+node->destD;
				H1=insertNode(H1,node);
			}				
		}

		while(H2->size>0 && (H2->P[0]->key)/1000==count)
		{
			node=extractMin(H2);
			if(node->x==9)
			{
				if(A[node->x+1][node->y]==NULL)
				{					
					A[node->x+1][node->y]=node;
					A[node->x][node->y]=NULL;
					node->x=node->x+1;
					node->pressure=0;
					node->umf=false;
					node->destD=20-node->y;
					node->eot=node->eot+node->speed;
					node->key=(node->eot*1000)+node->destD;
					H1=insertNode(H1,node);
				}
				else if(A[node->x+1][node->y+1]==NULL)
				{					
					A[node->x+1][node->y+1]=node;
					A[node->x][node->y]=NULL;
					node->x=node->x+1;
					node->y=node->y+1;
					node->pressure=0;
					node->umf=0;
					node->destD=20-node->y;
					node->eot=node->eot+node->speed;
					node->key=(node->eot*1000)+node->destD;
					H1=insertNode(H1,node);
				}
				else
				{
					node->eot=node->eot+1;
					node->key=(node->eot*1000)+node->destD;
					H2=insertNode(H2,node);	
				}	

			}
			else if(A[node->x+1][node->y]==NULL)
			{
				A[node->x+1][node->y]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x+1;
				node->pressure=0;
				node->umf=false;
				node->destD=node->destD-1;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H2=insertNode(H2,node);
			}
			else if(A[node->x+1][node->y+1]==NULL && (node->y+1)<=u && (A[node->x][node->y+1]==NULL || (A[node->x][node->y+1]!=NULL && (A[node->x][node->y+1]->eot)>(A[node->x][node->y]->eot)) ))
			{
				A[node->x+1][node->y+1]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x+1;
				node->y=node->y+1;
				node->pressure=0;
				node->umf=false;
				node->destD=node->destD-1;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H2=insertNode(H2,node);
			}
			else if(A[node->x+1][node->y-1]==NULL && (node->y-1)>=(10-n2/2) && (A[node->x][node->y-1]==NULL || (A[node->x][node->y-1]!=NULL && (A[node->x][node->y-1]->eot)>(A[node->x][node->y]->eot)) ))
			{
				A[node->x+1][node->y-1]=node;
				A[node->x][node->y]=NULL;
				node->x=node->x+1;
				node->y=node->y-1;
				node->pressure=0;
				node->umf=false;
				node->destD=node->destD-1;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H2=insertNode(H2,node);
			}	
			else if(A[node->x][node->y+1]==NULL && (node->y+1)<=u && ((node->x>0 && (A[node->x-1][node->y+1]==NULL || (A[node->x-1][node->y+1]!=NULL && (A[node->x-1][node->y+1]->eot)>(A[node->x][node->y]->eot)))) || node->x==0) )
			{
				A[node->x][node->y+1]=node;
				A[node->x][node->y]=NULL;
				node->y=node->y+1;
				node->pressure=0;
				node->umf=false;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H2=insertNode(H2,node);
			}		
			else if(A[node->x][node->y-1]==NULL && (node->y-1)>=(10-n2/2) && ((node->x>0 && (A[node->x-1][node->y-1]==NULL || (A[node->x-1][node->y-1]!=NULL && (A[node->x-1][node->y-1]->eot)>(A[node->x][node->y]->eot)))) || node->x==0) )
			{
				A[node->x][node->y-1]=node;
				A[node->x][node->y]=NULL;
				node->y=node->y-1;
				node->pressure=0;
				node->umf=false;
				node->eot=node->eot+node->speed;
				node->key=(node->eot*1000)+node->destD;
				H2=insertNode(H2,node);
			}
			else
			{
				struct Node *nd1,*nd2;
				nd2=A[node->x][node->y];
				nd1=A[node->x+1][node->y];
				while(nd1!=NULL && nd1->x<=10)
				{
					nd1->pressure=(nd1->pressure)/4+nd2->pressure+1;
					if(nd1->pressure>press_treshold)
					{	
						stampade=true;
						node=nd1;
						
						renderScene();
						sleep(10);
						exit(0);
					}
				
					nd2=nd1;
					nd1=A[nd1->x+1][nd1->y];
					
					if(nd2->umf==false)
						break;
				}
				node->eot=node->eot+1;
				node->key=(node->eot*1000)+node->destD;
				H2=insertNode(H2,node);
			}	

		}	

	if(count%3==0)
		A=insertNewElements(A,choice,count,n1,n2);

	count++;		
	}	

	exit(0);
}





int main(int argc, char **argv)
{
	char ch;
	cout<<"Enter main path number of queues (1-9): ";
	cin>>n1;
	cout<<"Enter 2nd path number of queues (1-18): ";
	cin>>n2;
	cout<<"Enter time to monitor in minutes: ";
	cin>>max_count;
	max_count=max_count*60;
	cout<<"Enter type of distribution to monitor: "<<endl;
	cout<<"a)Sparse		b)Medium		c)Dense"<<endl;
	cout<<"Enter ur choice: ";
	cin>>ch;

	srand(time(NULL));
	H1=createHeap(n1);
	H2=createHeap(n2);

	A = (Node***)malloc(20*sizeof(Node**));
	for(int i=0;i<20;i++)
		A[i]=(Node**)malloc(20*sizeof(Node*));

	for(int i=0;i<20;i++)
		for(int j=0;j<20;j++)
			A[i][j]=NULL;

	if(ch=='a')
		choice=2;
	else if(ch=='b')
		choice=4;
	else if(ch=='c')
		choice=8;	
	count=0;

	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DEPTH | GLUT_DOUBLE | GLUT_RGBA);
	glutInitWindowPosition(0,0);
	glutInitWindowSize(3200,3200);
	glutCreateWindow("DISCRETE EVENT SIMULATION");

	glutDisplayFunc(renderScene);

	if(max_count>count)
	{
		A=insertNewElements(A,choice,count,n1,n2);
		glutIdleFunc(simulateMovement);
	}

	glutMainLoop();
	return 0;
}


