#include <cstdlib>
#include <iostream>
#include <fstream>
#include <math.h>
#include <limits.h>
#include <sstream>

using namespace std;


struct EdgeNode
{
    int dest;
    int cost;
    EdgeNode* next;
};

struct EdgeList
{
    struct EdgeNode* head;
};

struct Graph
{
    int V;
    struct EdgeList* A;
};

struct HeapNode
{
    int v;
    int key;
    int parent;
};

struct Heap
{
  int size;
  int* index;
  struct HeapNode** P;
};

struct HeapNode* newHeapNode(int v,int key,int parent)
{
    struct HeapNode* minHeapNode =
           (struct HeapNode*) malloc(sizeof(struct HeapNode));
    minHeapNode->v = v;
    minHeapNode->key = key;
    minHeapNode->parent=parent;
    
    return minHeapNode;
}

struct Heap* createHeap(Graph* graph)
{
    struct Heap* minHeap =
         (struct Heap*) malloc(sizeof(struct Heap));
    minHeap->index = (int*)malloc((graph->V) * sizeof(int));
    minHeap->size = 0;
    minHeap->P=(HeapNode**)malloc((graph->V)*sizeof(HeapNode));
        
    return minHeap;
}


void printHeap(Heap* H)
{    
    for (int i = 0; i < H->size; ++i)
        printf("%d - %d - %d\n", H->P[i]->parent ,H->P[i]->v ,H->P[i]->key);
}

void swapHeapNode(struct HeapNode** a, struct HeapNode** b)
{
    struct HeapNode* t = *a;
    *a = *b;
    *b = t;
}

void Heapify(struct Heap* minHeap, int idx)
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
        HeapNode *smallestNode = minHeap->P[smallest];
        HeapNode *idxNode = minHeap->P[idx];

        minHeap->index[smallestNode->v] = idx;
        minHeap->index[idxNode->v] = smallest;

        swapHeapNode(&minHeap->P[smallest], &minHeap->P[idx]);
 
        Heapify(minHeap, smallest);
    }
}

void HeapifyUp(struct Heap* H , int index)
{
    int c=index,p;
    p=(c-1)/2;
    while(H->P[c]->key<H->P[p]->key)
    {

        if(c!=0)
        {    
        HeapNode* cnode=H->P[c];
        HeapNode* pnode=H->P[p];
        H->index[pnode->v]=c;
        H->index[cnode->v]=p;
        swapHeapNode(&H->P[c],&H->P[p]);  
        c=p;
        p=(c-1)/2;
        }
    }    

}

struct Heap* insertNode(Heap* H,HeapNode* n)
{
    H->P[H->size]=n;
    H->index[H->size]=H->size;
           
    H->size=H->size+1;       
    return H;
}




struct HeapNode* extractMin(struct Heap* minHeap)
{
    if (minHeap->size==0)
        return NULL;

    
    struct HeapNode* root = minHeap->P[0];

    struct HeapNode* lastNode = minHeap->P[minHeap->size - 1];
    minHeap->P[0] = lastNode;
 
    minHeap->index[root->v] = minHeap->size-1;
    minHeap->index[lastNode->v] = 0;

    --minHeap->size;
    
    Heapify(minHeap, 0);
    
 
    return root;
}

Heap* decreaseKey(struct Heap* minHeap, int v, int key)
{
    int i = minHeap->index[v];
    
    minHeap->P[i]->key = key;
    while (i && minHeap->P[i]->key < minHeap->P[(i-1)/2]->key)
    {

        minHeap->index[minHeap->P[i]->v] = (i-1)/2;
        minHeap->index[minHeap->P[(i-1)/2]->v] = i;
               
        swapHeapNode(&minHeap->P[i],  &minHeap->P[(i-1)/2]);

        i=(i-1)/2;
    }

    return minHeap;
}



void printGraph(Graph* graph)
{
    EdgeNode* z;
    for(int i=0;i<graph->V;i++)
    {
        z=graph->A[i].head;
        while(z!=NULL)
        {    
            cout<<i<<","<<(z->dest)<<","<<(z->cost)<<"     ";
            z=z->next;
        }  
    printf("\n");    
    }       
}

struct EdgeNode* newEdgeNode(int dest, int weight)
{
    struct EdgeNode* newNode =
            (struct EdgeNode*) malloc(sizeof(struct EdgeNode));
    newNode->dest = dest;
    newNode->cost = weight;
    newNode->next = NULL;
    return newNode;
}

struct Graph* createGraph(int V)
{
    struct Graph* graph = (struct Graph*) malloc(sizeof(struct Graph));
    graph->V = V;
    
    graph->A = (struct EdgeList*)malloc(V * sizeof(struct EdgeList));
    for (int i = 0; i < V; ++i)
        graph->A[i].head = NULL;
 
    return graph;
}

Graph* addEdge(struct Graph* graph, int src, int dest, int weight)
{
    struct EdgeNode* newNode = newEdgeNode(dest, weight);
    newNode->next = graph->A[src].head;
    graph->A[src].head = newNode;

    newNode = newEdgeNode(src, weight);
    newNode->next = graph->A[dest].head;
    graph->A[dest].head = newNode;
    
    return graph;
}

int PrimsMST(Graph* graph1)
{
   
    Graph* graph2=createGraph(graph1->V);
    Heap* H=createHeap(graph1);

    int total_cost=0;
    H=insertNode(H,newHeapNode(0,0,-10));
    for(int i=1;i<graph1->V;i++)
       H=insertNode(H,newHeapNode(i,INT_MAX,-10));

    int i=0;

    do
    { 
        HeapNode* y=extractMin(H);
        if(y->parent>=0)
             addEdge(graph2,y->parent,y->v,y->key);
        i=y->v;
        total_cost=total_cost+y->key;     
        EdgeNode* z=graph1->A[i].head;
        while(z!=NULL)           
        {               
            int d=z->dest;

            if((H->index[d])<(H->size))
            {  
                if((H->P[H->index[d]]->key)>(z->cost))
                {  
                   H=decreaseKey(H,d,z->cost); 
                   H->P[H->index[d]]->parent=i;
                } 
            }    
        z=z->next;    
        }
        graph1->A[i].head=z;
        
    }
    while(H->size!=0);
 
    return total_cost;
}

Graph* ReadGraphFromFile()
{
    int V=0,E=0,x=0,y=0,z=0;
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
          E++;
      }    
      
    }
    f1.close();

    return graph;
}

int main() 
{

    Graph* graph=ReadGraphFromFile();   
    
    int tc=PrimsMST(graph);
    
    cout<<"The total cost is: "<<tc<<endl;
   
    return 0;
}

