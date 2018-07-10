
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <math.h>
#include <limits.h>
#include <sstream>

using namespace std;

struct EdgeNode
{
    int source;
    int destination;
    int cost;
};

struct fibnode
{
    struct fibnode *parent;
    int key;
    int degree;
    bool mark;
    bool vmark;
    bool inHeap;
    int label;
    int parent_vertex;
    int dest_tree;
    struct EdgeNode* opedge;
    struct fibnode *left;
    struct fibnode *right;
    struct fibnode *child;

};

struct FibonacciHeap
{
    int size;
    struct fibnode* head;
    struct fibnode* min;
};

struct TreeEdge
{
    int sTNo;
    int dTNo;
    struct EdgeNode* edge;
    struct TreeEdge* next;
};

struct TreeNode
{
    struct TreeEdge* head1;
    struct fibnode* head2;
};
 
struct Graph
{
    int V;
    struct TreeNode* A;
};




FibonacciHeap* makeFibonacciHeap()
{
    struct FibonacciHeap* H=(FibonacciHeap*)malloc(sizeof(FibonacciHeap));
    H->size=0;
    H->head=NULL;
    H->min=NULL;
    
    return H;
}

fibnode* createNode(int value)
{
    fibnode* n1=(fibnode*)malloc(sizeof(fibnode));
    n1->key=value;
    n1->parent=NULL;
    n1->child=NULL;
    n1->left=NULL;
    n1->right=NULL;
    n1->degree=0;
    n1->mark=false;
    n1->vmark=false;
    n1->inHeap=false;
    n1->label=-10;
    n1->parent_vertex=-10;
    n1->dest_tree=10;
    n1->opedge=NULL;
    return n1;

}

FibonacciHeap* insertElement(FibonacciHeap* H,int value)
{
    struct fibnode* n1=createNode(value);

    if(H->head==NULL)
    {
        H->head=n1;
        H->min=n1;
        H->head->left=H->head;
        H->head->right=H->head;
    }
    else
    {        
        n1->right=H->head->right;
        n1->left=H->head;
        H->head->right->left=n1;
        H->head->right=n1;       
    }
    if(H->min->key>n1->key)
        H->min=n1;
    H->size=H->size+1;
    
    return H;
}

void printHeap(FibonacciHeap* H)
{
    if(H->head!=NULL)
    {    
    
    struct fibnode* temp=H->head->right;
    cout<<H->head->parent_vertex<<","<<(H->head->key)<<","<<H->head->dest_tree;
    while(H->head!=temp)
    {

        cout<<"--->"<<temp->parent_vertex<<","<<(temp->key)<<","<<temp->dest_tree;
        temp=temp->right;
    }
    cout<<""<<endl;
    }
    else
        cout<<"Empty Heap"<<endl;
}

FibonacciHeap* heapUnion(FibonacciHeap* H1,FibonacciHeap* H2)
{
    
    struct fibnode* temp1=(fibnode*)(malloc(sizeof(fibnode)));
    struct fibnode* temp2=(fibnode*)(malloc(sizeof(fibnode)));
    temp1=H1->head->right;
    temp2=H2->head->left;
    H1->head->right=H2->head;
    H2->head->left=H1->head;
    temp1->left=temp2;   
    temp2->right=temp1;

    if((H1->min->key)>(H2->min->key))
        H1->min=H2->min;
    
    H1->size=H1->size + H2->size;        

    return H1;
}

FibonacciHeap* heapLink(FibonacciHeap* H,fibnode* l,fibnode* u)
{
    if(H->head==l)
        H->head=u;
    if(H->min==l)
        H->min=u;
    
    l->left->right=l->right;
    l->right->left=l->left;
    if(u->child!=NULL)
    {
        l->right=u->child->right;
        l->left=u->child;
        u->child->right->left=l;
        u->child->right=l;
    }
    else
    {    
       u->child=l;
       l->right=l;
       l->left=l;
    }
    l->parent=u;
    u->degree=u->degree+1;
    l->mark=false;   
    
    return H;
}

FibonacciHeap* addNodeToHeap(FibonacciHeap* H,fibnode* n)
{
    if(H->size==0)
    {
        H->head=n;
        H->min=n;
        H->head->right=H->head;
        H->head->left=H->head;
    } 
    else
    {    
    n->right=H->head->right;
    n->left=H->head;
    H->head->right->left=n;
    H->head->right=n;
    }
    if((H->min->key)>(n->key))
        H->min=n;
    
    return H;
}

FibonacciHeap* insertNewNodeToHeap(FibonacciHeap* H,fibnode* n)
{
    if(H->size==0)
    {
        H->head=n;
        H->min=n;
        H->head->right=H->head;
        H->head->left=H->head;
    } 
    else
    {    
    n->right=H->head->right;
    n->left=H->head;
    H->head->right->left=n;
    H->head->right=n;
    }

    if((H->min->key)>(n->key))
    {    
        H->min=n;
    }     
    
    H->size=H->size+1;
   
    return H;
}

FibonacciHeap* consolidate(FibonacciHeap* H)
{
    int n=(int)((log2((double)(H->size)))/(log2(1.6))),i=0,d=0;
    n=n+5;
    fibnode* x=(fibnode*)(malloc(sizeof(fibnode)));
    fibnode* y=(fibnode*)(malloc(sizeof(fibnode)));
    fibnode* temp;
    fibnode* A[n];
    x=H->min;

    for(i=0;i<n;i++)
        A[i]=NULL;
    
    fibnode *current, *last;
    bool isRootListCompleted = false;
    current = H->min;
    last = H->min->left;
    while(!isRootListCompleted)
    {    
        x=current;
        x->parent=NULL;
        d=x->degree;
        
        if(current == last)
            isRootListCompleted = true;
        else
            current = current->right;
       
        while(A[d]!=NULL)
        {

            y=A[d];
            if((x->key)>(y->key))
            {
              temp=x;
              x=y;
              y=temp;
            }
            H=heapLink(H,y,x);
            A[d]=NULL;
            d=d+1;        
        }
        A[d]=x;              

    }
    H->min=NULL;

    for(i=0;i<n;i++)
    {
        if(A[i]!=NULL)
        {
            if(H->min==NULL)
            {
                H->min=A[i];
                H->head=A[i];
                H->head->left=H->head;
                H->head->right=H->head;
            }
            else
                H=addNodeToHeap(H,A[i]);               
        }    
    
    }
    return H;
}


fibnode* extractMin(FibonacciHeap* H)
{
    struct fibnode* x;
    struct fibnode* t;
    struct fibnode* temp;
    temp=H->min;
    if(temp!=NULL)
    {
        if(temp->child!=NULL)
        {
            
            x=H->min->child;
            t=H->min->child->right;
            while(x!=t)
            {
                t->parent=NULL;
                t->mark=false;
                x->right=t->right;
                t->right->left=x;
                t->right=H->head->right;
                t->left=H->head;
                H->head->right->left=t;
                H->head->right=t;
                t=t->right;
                                                  
            }    
            x->right=H->head->right;
            x->left=H->head;
            H->head->right->left=x;
            H->head->right=x;
            x->mark=false;
            H->min->child=NULL;
        }    
        
            if(H->size>1)
            {
                if(H->head==H->min)
                    H->head=H->head->right;
                H->min->right->left=H->min->left;
                H->min->left->right=H->min->right;                                   
            }
            temp->right=temp;
            temp->left=temp;
            if(H->size==1)
            {
                H->min=NULL;
                H->head=NULL;
                H->size=0;
                return temp;
            }   
            temp=H->min;
            H->min=H->min->right;
         
            H=consolidate(H);
            H->size=H->size-1;            
            
        
    }
    else
       temp=H->min;
    
    return temp;
}

FibonacciHeap* Cut(FibonacciHeap* H,fibnode* n,fibnode* p)
{
    if((p->child==n)&&(p->child->right!=p->child))
    {
        p->child=p->child->right;
        n->right->left=n->left;
        n->left->right=n->right;
        
    }    
    else if((p->child==n)&&(p->child->right==p->child))
        p->child=NULL;
    else if(p->child!=n)
    {
       n->right->left=n->left;
       n->left->right=n->right;       
    }
    p->degree=p->degree-1;
    addNodeToHeap(H,n);
    n->parent=NULL;
    n->mark=false;
 
    return H;
}

FibonacciHeap* Cascading_Cut(FibonacciHeap* H,fibnode* p)
{
    fibnode* gp=(fibnode*)(malloc(sizeof(fibnode)));
    gp=p->parent;
    if(gp!=NULL)
    {
        if(p->mark==false)
            p->mark=true;
        else
        {
            Cut(H,p,gp);
            Cascading_Cut(H,gp);
        }   
    }
    
    return H;
}

FibonacciHeap* decreaseKey(FibonacciHeap* H,fibnode* n,int k)
{   
    struct fibnode* p;
    if(k<(n->key))
    {
    n->key=k;
    p=n->parent;
    if((p!=NULL)&&(n->key<p->key))
    {    
        Cut(H,n,p);
        Cascading_Cut(H,p);
    }    
    }
    if((n->key)<(H->min->key))
    {    
        H->min=n;
    }
    return H;
}

fibnode* deleteHeapNode(FibonacciHeap* H,fibnode* n)
{
    fibnode* dNode=(fibnode*)(malloc(sizeof(fibnode)));
    H=decreaseKey(H,n,-5);
    dNode=extractMin(H);

    return dNode;
}


struct Graph* createGraph(int V)
{
    struct Graph* graph = (struct Graph*) malloc(sizeof(struct Graph));
    graph->V = V;
    graph->A = (struct TreeNode*) malloc(V * sizeof(struct TreeNode));
    
    
    for (int i = 0; i < V; i++)
    {
        graph->A[i].head1 = NULL;
        graph->A[i].head2 = NULL;
        graph->A[i].head2 = (fibnode*)(malloc(sizeof(fibnode)));
        graph->A[i].head2->inHeap=false;
        graph->A[i].head2->label=i;
        graph->A[i].head2->parent_vertex=-20;
        graph->A[i].head2->vmark=false;
    }
    return graph;
}

TreeEdge* newTreeEdge(int src,int dest, int weight)
{
    struct TreeEdge* newNode =
            (struct TreeEdge*) malloc(sizeof(struct TreeEdge));
    
    newNode->edge = (EdgeNode*)(malloc(sizeof(EdgeNode)));
    
    newNode->sTNo = src;
    newNode->edge->source = src;
    newNode->dTNo = dest;
    newNode->edge->destination = dest;
    newNode->edge->cost = weight;
    newNode->next = NULL;
    return newNode;
}

Graph* addEdge(struct Graph* graph, int src, int dest, int weight)
{
    struct TreeEdge* newNode = newTreeEdge(src ,dest, weight);
    newNode->next = graph->A[src].head1;
    graph->A[src].head1 = newNode;
 
    newNode = newTreeEdge(dest, src, weight);
    newNode->next = graph->A[dest].head1;
    graph->A[dest].head1 = newNode;
    
    return graph;
}

void printGraph(Graph* graph)
{
    TreeEdge* z;
    for(int i=0;i<graph->V;i++)
    {
        z=graph->A[i].head1;
        while(z!=NULL)
        {    
            cout<<(z->sTNo)<<","<<(z->dTNo)<<","<<z->edge->cost<<","<<graph->A[i].head2->label<<"     ";
            z=z->next;
        }  
    printf("\n");    
    }       
}


int TarjanMST(Graph* graph1,Graph* graph2,int size,int* total_cost)
{
    int i=0,j=0,lcount=0;
    bool leaveHeap,flag2;
    TreeEdge* z;
    int c=0;
    graph1->A[i].head2->label=0;    
    for(j=0;j<graph1->V;j++)
    {  
      flag2=false;  
      i=j; 
      leaveHeap=false;
      if(graph1->A[j].head2->vmark==false)
      {
      struct FibonacciHeap* H=(FibonacciHeap*)(malloc(sizeof(FibonacciHeap)));  
      graph1->A[j].head2->label=lcount;

      do 
      {   z=graph1->A[i].head1;       
          while(graph1->A[i].head1!=NULL&&leaveHeap==false)
          {
             if (((graph1->A[graph1->A[i].head1->dTNo].head2->vmark==true)&&(graph1->A[graph1->A[i].head1->sTNo].head2->label)==(graph1->A[graph1->A[i].head1->dTNo].head2->label))==false)
              {
             
              if((graph1->A[graph1->A[i].head1->dTNo].head2->inHeap==false)&&((H->size)<=size))
              {
                  graph1->A[graph1->A[i].head1->dTNo].head2->key=graph1->A[i].head1->edge->cost;
                  graph1->A[graph1->A[i].head1->dTNo].head2->inHeap=true;
                  graph1->A[graph1->A[i].head1->dTNo].head2->parent_vertex=graph1->A[i].head1->sTNo;
                  graph1->A[graph1->A[i].head1->dTNo].head2->opedge=graph1->A[i].head1->edge;
                  graph1->A[graph1->A[i].head1->dTNo].head2->dest_tree=graph1->A[i].head1->dTNo;
                  H=insertNewNodeToHeap(H,(graph1->A[graph1->A[i].head1->dTNo].head2));
                  
              }  
              else if((graph1->A[graph1->A[i].head1->dTNo].head2->inHeap==true)&&((H->size)<=size))
              {                      
                 if(((graph1->A[graph1->A[i].head1->sTNo].head2->label)==lcount) && (flag2==1) && ((graph1->A[graph1->A[graph1->A[i].head1->dTNo].head2->parent_vertex].head2->label)==lcount))             
                 {
                     if((graph1->A[graph1->A[i].head1->dTNo].head2->key)>(graph1->A[i].head1->edge->cost))
                     {
                         graph1->A[graph1->A[i].head1->dTNo].head2->key=graph1->A[i].head1->edge->cost;
                         graph1->A[graph1->A[i].head1->dTNo].head2->parent_vertex=graph1->A[i].head1->sTNo;
                         graph1->A[graph1->A[i].head1->dTNo].head2->opedge=graph1->A[i].head1->edge;
                         H=decreaseKey(H,graph1->A[graph1->A[i].head1->dTNo].head2,graph1->A[i].head1->edge->cost);
                         
                     }    
                 
                 }
                 else if((graph1->A[graph1->A[i].head1->sTNo].head2->label)==lcount&&flag2==false)
                 {
                  graph1->A[graph1->A[i].head1->dTNo].head2->key=graph1->A[i].head1->edge->cost;
                  graph1->A[graph1->A[i].head1->dTNo].head2->inHeap=true;
                  graph1->A[graph1->A[i].head1->dTNo].head2->parent_vertex=graph1->A[i].head1->sTNo;
                  graph1->A[graph1->A[i].head1->dTNo].head2->opedge=graph1->A[i].head1->edge;
                  graph1->A[graph1->A[i].head1->dTNo].head2->dest_tree=graph1->A[i].head1->dTNo;
                  H=insertNewNodeToHeap(H,(graph1->A[graph1->A[i].head1->dTNo].head2));
                                  
                 }   
                                  
                 else if((graph1->A[graph1->A[i].head1->dTNo].head2->label)!=lcount)
                 {
                  graph1->A[graph1->A[i].head1->dTNo].head2->key=graph1->A[i].head1->edge->cost;
                  graph1->A[graph1->A[i].head1->dTNo].head2->inHeap=true;
                  graph1->A[graph1->A[i].head1->dTNo].head2->parent_vertex=i;
                  graph1->A[graph1->A[i].head1->dTNo].head2->opedge=graph1->A[i].head1->edge;
                  graph1->A[graph1->A[i].head1->dTNo].head2->dest_tree=graph1->A[i].head1->dTNo;
                  H=insertNewNodeToHeap(H,graph1->A[graph1->A[i].head1->dTNo].head2);
                 
                 }    
              
              }
              else if(H->size>size||H->size==0)
              {
                  graph1->A[i].head2->label=lcount;
                  leaveHeap=true;
              } 
              }
              graph1->A[i].head1=graph1->A[i].head1->next;
             
          }    
          graph1->A[i].head2->vmark=true;           
          graph1->A[i].head1=z;   
          
          if(leaveHeap==false)
          {     

              fibnode* z=(fibnode*)(malloc(sizeof(fibnode)));

              z=extractMin(H); 
              if(z->vmark==false)
              {
                  
                  z->vmark=true;
                  z->label=lcount;
                  z->parent_vertex=-20;
                  i=z->dest_tree;
                  z->inHeap=false;
                  flag2=true;
                  graph1->A[z->dest_tree].head2->label=lcount;
                  *total_cost=*total_cost+z->opedge->cost;
                  addEdge(graph2,z->opedge->source,z->opedge->destination,z->opedge->cost);          
              }    
              else
              {                  
                  leaveHeap=true;
                  graph1->A[i].head2->label=lcount;
              }      
          }   

      }
      while(H->size>0&&H->size<=size&&leaveHeap==false); 
      
      lcount++;
      }      
    } 
    return lcount;
}

Graph* RadixSort(Graph* graph1,int labels)
{
    int i=1;

    struct Graph* graph2=createGraph(labels);
    struct Graph* graph3=createGraph(labels);

    //Radix Sort pass1 based on destinations.
    for(i=0;i<graph1->V;i++)
    {  
        int sl=graph1->A[i].head2->label;
        while(graph1->A[i].head1!=NULL)
        {
            int dl=graph1->A[graph1->A[i].head1->dTNo].head2->label;
            if(dl!=sl)
            {
                TreeEdge* temp=graph1->A[i].head1->next; 
                graph1->A[i].head1->next=graph2->A[dl].head1;
                graph2->A[dl].head1=graph1->A[i].head1;
                graph1->A[i].head1=temp;                
                graph2->A[dl].head1->dTNo=dl;
                graph2->A[dl].head1->sTNo=sl;
            }                                                          
            else
                graph1->A[i].head1=graph1->A[i].head1->next; 
        }                            
    } 
    
    //Radix Sort pass2 based on Sources 
    for(i=0;i<graph2->V;i++)
    {
        int dNo=graph2->A[i].head1->dTNo;
        while((graph2->A[i].head1)!=NULL)
        {
            int sNo=graph2->A[i].head1->sTNo;
            if(graph3->A[sNo].head1==NULL)
            {
                graph3->A[sNo].head1=graph2->A[i].head1;
                graph2->A[i].head1=graph2->A[i].head1->next; 
                graph3->A[sNo].head1->next=NULL;
            }
            else if((graph3->A[sNo].head1->sTNo)==(graph2->A[i].head1->sTNo)&&(graph3->A[sNo].head1->dTNo)==(graph2->A[i].head1->dTNo))
            {
                if((graph3->A[sNo].head1->edge->cost)>(graph2->A[i].head1->edge->cost))
                {   
                    TreeEdge* temp=graph2->A[i].head1->next;
                    graph2->A[i].head1->next=graph3->A[sNo].head1->next;
                    graph3->A[sNo].head1=graph2->A[i].head1;
                    graph2->A[i].head1=temp;
                }            
                else
                    graph2->A[i].head1=graph2->A[i].head1->next;
               
            }
            else
            {                
                TreeEdge* temp=graph2->A[i].head1->next;
                graph2->A[i].head1->next=graph3->A[sNo].head1;
                graph3->A[sNo].head1=graph2->A[i].head1;
                graph2->A[i].head1=temp; 
            
            }    
        }  
    }
    
    return graph3;
}


Graph* FredmanMST(Graph* graph1,int E)
{
    Graph* graph2=createGraph(graph1->V);
    int total_cost=0;
    int labels=graph1->V,size=(int)ceil(1.0*E/(graph1->V));

    while(labels>1)
    {
    size=(int)(ceil(pow(2.0,(double)size)));        
    labels=TarjanMST(graph1,graph2,size,&total_cost);
    if(labels>1)
         graph1=RadixSort(graph1,labels);
    }
    printf("\nThe Total Min Cost is :%d\n",total_cost);
    
    return graph2;
}

Graph* ReadGraphFromFile(int* E)
{
    int V=0,x=0,y=0,z=0;
    bool flag=false;
    string s;
    fstream f1;
    f1.open("inputGraph.txt",ios::in);
    while(getline(f1,s))
    {
      if(s[0]=='#')
          break;
      V++;
    }
    f1.close();

    Graph* graph=createGraph(V);

    f1.open("inputGraph.txt",ios::in);
    while(getline(f1,s))
    {
      istringstream  var(s); 
      if(s[0]=='#'&& flag==false)
          flag=true;
      else if(flag==true && s[0]!='#')
      {
          string ss;
          var >> ss;
          x=atoi(ss.c_str());
          
          var >> ss;
          y=atoi(ss.c_str());
          
          var >> ss;
          z=atoi(ss.c_str());         
          graph=addEdge(graph,x,y,z);
          *E=*E+1;
      }    
      
    }
    f1.close();
   
    return graph;
}

int main() 
{
    int E=0;
    Graph* graph=ReadGraphFromFile(&E);; 

    graph=FredmanMST(graph,E); 

    cout<<"Happy Ending......";

    return 0;
}

