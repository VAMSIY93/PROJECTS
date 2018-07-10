#include<iostream>
#include<cstdio>
#include<map>
#include<queue>
#include<vector>
#include<utility>
#include<algorithm>
#include<string>
#include<cstring>
#include<ctime>

using namespace std;

int main() 
{
    
    int V,E,t,i,cost;
    bool check=true;
    cout<<"Enter the number of vertices: ";
    cin>>V;
    cout<<"Enter the number of edges: ";
    cin>>E;
    freopen("inputGraph.txt","w",stdout);
    
    vector<vector <int> > EList;
    for(i=0;i<V;i++)
    {
        printf("%d\n",i);
        vector<int> L;
        EList.push_back(L);
    }
    printf("#\n");

    for(i=1;i<V;i++)
    {           
        int d=rand()%i;       
        int cost=rand()%(3*V);
        printf("%d %d %d\n",i,d,cost);
        EList[d].push_back(i);
    }
    E=E-V-1;
    while(E)
    {
     int s=rand()%V;
     int d=rand()%V;
     if(s>d)
     {//swapping the source and destination
        t=s;
        s=d;
        d=t;
     }   
     if(s==d)
         E++;
     else
     {
         check=true;
         cost=rand()%(3*V);
         for(int i=0;i<EList[s].size();i++)
             if(EList[s][i]==d)
                 check=false;
         for(int i=0;i<EList[d].size();i++)
             if(EList[d][i]==s)
                 check=false;
         if(check==0)
         {
             E++;
         }   
         else
         {
             printf("%d %d %d\n",s,d,cost);
             EList[s].push_back(d);
         }
     
     }
     E--;
     }            
    
    return 0;
}