#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>
#include <vector>
#include <pthread.h>
#include <unistd.h>
#include <GLUT/glut.h>
#include <string.h>
#include <ctime>
/*network helpful headers*/
#include <SDL2/SDL.h>
#include <SDL2/SDL_mixer.h>
#include <iostream>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <ifaddrs.h>
#include <iomanip>
#include <utility>
//#include "SOIL.h"


//Remember: if we add extra key we need to change storeKeys

#define DATA_SIZE_KILO 12000   //myhelper.h
#define IP_SIZE 40
#define SERVER_BUSY 'x'


#define ROWS 340
#define COLS 600   
using namespace std;

void* server(void*);
void keyOperations(int);
void display(void);
void client(char*,char*);
void sendKeys();
void backToPosition();


float roundUp(float x)
{
    int a=(int)x;
    return (float)a;
}


/*Network Variables*/
bool isCreated = false;
bool hasJoined = false;
int retry_count = 5;
int nop = 10;

bool masterFlag=false;
bool play[6];


const char* destIP;
char *sPort, *destPort,first_ipPort[22];

char ui_data[DATA_SIZE_KILO];
char server_send_data[DATA_SIZE_KILO], server_recv_data[DATA_SIZE_KILO];
char client_send_data[DATA_SIZE_KILO], client_recv_data[DATA_SIZE_KILO];
char ff_client_send_data[DATA_SIZE_KILO], ff_client_recv_data[DATA_SIZE_KILO];
char dd_client_send_data[DATA_SIZE_KILO], dd_client_recv_data[DATA_SIZE_KILO];

unsigned int server_port = 0;
int serverSock;

unsigned int port = 0;

int modeChoice=1;

char masterIpPort[22];


pthread_mutex_t threadLock;
/*Goal Variables*/
int goalA=0,goalB=0;
int passRecvr=-1;
char goalNumA[2];
char goalNumB[2];
char goalDisp[7];

bool keyStates[256];
bool specialKeyStates[256];
bool contact=false,xinc=false,yinc=false;
bool goal=false,movSt=false;
bool hitbound=false,shoot=false,pass=false;
bool playerSwitch=false;
bool gkMove=true;
bool resetgoal=false;       //used as a helper flag for counting goals
bool ballAnti=false;
bool ballClock=false;
bool gameStart=true,getBackBall=false;
bool movPlayers=true;
bool halfTime,resetHalf=true;


/*sound vars*/
int crowdPlay=0;

float gX = 150,gY=125,rMan=10,S;
float gXBall=220,gYBall=150,rBall=5;
float helpX,helpY,diff;
float diagonal = (sqrt(2)*10);
float playerdia = (sqrt(2)*20);
float m11=-16.0/26.0,m12=16.0/26.0;
int t=0;

int keyPower=0;


int frame = 0;
GLuint _textureId[6];

int playNum=0; //tells us which players playNum is in normal mode currently asuming only one

int mins,sec=10,beforeTime=0,backHalfTime,afterTime,backTime=0,Time,gameTime=0,beginSecs;
char dispMin[3],dispSec[3],countDown[10];



class cell
{
public:
    int r = 0, g = 150, b = 0;
};

class Network
{
public:
    char server_ip_port[22];
    int activePlayer;

public:
    Network(char* ip,int active)
    {
        strcpy(server_ip_port,ip);
        activePlayer=active;
    }
};

class Keys
{
public:
    int up;
    int left;
    int down;
    int right;
    int c;
    int v;
    int a;
    int d;
    int y;
    int active;
public:
    Keys(int a1,int b1,int c1,int d1, int e1,int f1,int g1,int h1,int i1,int act)
    {
        up=a1;
        left=b1;
        down=c1;
        right=d1;
        c=e1;
        v=f1;
        a=g1;
        d=h1;
        y=i1;
        active=act;
    }
};

class Player
{
public:
    int playerX,playerY;
    bool hasBall;    // tells which player has the ball
    int speed;
    int strength;
    int colour;

public:
    Player(int posX, int posY,int spd,int str,int col)
    {
        playerX=posX;
        playerY=posY;
        speed=spd;
        strength=str;
        colour=col;
    }
    Player()
    {

    }
    void update(int x,int y)
    {
        playerX=x;
        playerY=y;
    }
    void goalKeeper1Movement();
    void goalKeeper2Movement();
    void botPlayer4Movement();
    void botPlayer3Movement();
    void botPlayer1Movement();
    void botPlayer0Movement();
};

class Ball
{
public:
	float x;
	float y;
	float u;
	float slope;
	float c;
	float d;
	bool isPass;

public:
	Ball()
	{
		x=300;
		y=170;
		d=0.1;
	}
	void moveBall(float S,int j);
    void check_Y();
    bool checkPlayerCollison(float S,float prevX,float prevY,int i,int j);
    bool helpCheckPlayerCollison(float S,float prevX,float prevY,int j);
};


Ball ball;
Player players[6];             

vector<Network> Slave; 

vector<Keys> slaveKeys;


float distBtwnPoints(float x1,float y1,float x2,float y2)
{
    return (sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
}

float distance(Player p,Ball b)
{
    return sqrt(pow((p.playerX-b.x),2)+pow((p.playerY-b.y),2));
}

float playdistance(Player p1,Player p2)
{
    return sqrt(pow((p1.playerX-p2.playerX),2)+pow((p1.playerY-p2.playerY),2));
}

bool checkIfNearest(int active)
{
    float minDis=distance(players[0],ball),index=0;        //check for generalisation
    for(int i=0;i<6;i++)
        if(distance(players[i],ball)<minDis)
        {
            minDis=distance(players[i],ball);
            index=i;
        }       

    if(index==active)
        return true;

    return false;    
}


bool checkOppositionNear(int j)
{
    if(j>2)
    {    
        for(int i=0;i<3;i++)
            if(distance(players[i],ball)<30)                
                return true;
    }            
    else
        for(int i=0;i<3;i++)
            if(distance(players[i+3],ball)<30)
                return true;

    return false;        
}

bool checkOppositionInsideD(int gkNo)
{
    if(gkNo==5)
    {
        for(int i=0;i<2;i++)
            if(players[i].playerX>450 && players[i].playerX<580 && players[i].playerY>(ROWS/2-80) && players[i].playerY<(ROWS/2+80))
                return true;
    }
    else
    {
        for(int i=0;i<2;i++)
            if(players[i+3].playerX>20 && players[i+3].playerX<150 && players[i+3].playerY>(ROWS/2-80) && players[i+3].playerY<(ROWS/2+80))
                return true;   
    }
    return false;
}

bool checkBallInD(int gkNo)
{
    if(gkNo==5)
    {    if(ball.x>500 && ball.x<580 && ball.y>(ROWS/2-80) && ball.y<(ROWS/2+80))
            return true;
    }
    else if(gkNo==2)
    {
        if(ball.x>20 && ball.x<100 && ball.y>(ROWS/2-80) && ball.y<(ROWS/2+80))
            return true;
    }

    return false;
}

bool checkNearGoal(int j)
{
    if(j>2)
    {
        if(players[j].playerX<100 && players[j].playerX>20 && players[j].playerY<310 && players[j].playerY>30)
            return true;
    }
    else
        if(players[j].playerX>500 && players[j].playerX<580 && players[j].playerY<310 && players[j].playerY>30)
            return true;

     return false;   
}

bool checkOtherCollision(float xc,float yc,int pNo)
{
    /*for(int i=0;i<6;i++)
        if(pNo!=i && distBtwnPoints(xc,yc,players[i].playerX,players[i].playerY)<20 && distBtwnPoints(xc,yc,players[i].playerX,players[i].playerY)>15)
            return true;*/

    return false;    
}

void determineWhoHasBall()
{
    for(int i=0;i<6;i++)
        if(distance(players[i],ball)<17)
            players[i].hasBall=true;
        else
            players[i].hasBall=false;
}

bool checkPlayerBtwnPass(int i,int j)
{
    if(players[i].playerX!=players[j].playerX)
    {
    float m1=(players[i].playerY-players[j].playerY)/(players[i].playerX-players[j].playerX);
    float c=players[i].playerY-(m1*players[i].playerX);
    float A=m1*m1+1;
    if(i>2)
        for(int k=0;k<3;k++)
        {    
            float B=2*(m1*(c-players[k].playerY)-players[k].playerX);
            float C=((players[k].playerX*players[k].playerX)-100+(c-players[k].playerY)*(c-players[k].playerY));
            float discr=(B*B)-(4*A*C);
            if(discr>0)
                return true;
        }
    else if(i<=2)
        for(int k=0;k<3;k++)
        {
            float B=2*(m1*(c-players[k+3].playerY)-players[k+3].playerX);
            float C=((players[k+3].playerX*players[k+3].playerX)-100+(c-players[k+3].playerY)*(c-players[k+3].playerY));
            float discr=(B*B)-(4*A*C);
            if(discr>0)
                return true; 
        }        
    }
    return false;
}

void callPassLogic(int spNo,int dpNo,float vel)
{
    ball.u=vel;
    pass=true;
    shoot=false;
    S=1.0;
    t=1;
    if((players[spNo].playerX>ball.x && players[spNo].playerX<players[dpNo].playerX)) 
    {
        players[spNo].playerX=ball.x-15;
        players[spNo].playerY=ball.y;
    }
    else if((players[spNo].playerX<ball.x && players[spNo].playerX>players[dpNo].playerX))
    {
        players[spNo].playerX=ball.x+15;
        players[spNo].playerY=ball.y;
    }
    if((players[spNo].playerY>ball.y && players[spNo].playerY<players[dpNo].playerY)||(players[spNo].playerY<ball.y && players[spNo].playerY>players[dpNo].playerY))
    {
        if(players[spNo].playerY>ball.y)
            players[spNo].playerY=ball.y-15;
        else
            players[spNo].playerY=ball.y+15;    
        players[spNo].playerX=ball.x;
    }    
    float D=distance(players[dpNo],ball);
    float xdif=players[dpNo].playerX-ball.x;
    diff=(xdif/D)*15;
    ball.slope=(float)(players[dpNo].playerY-ball.y)/(players[dpNo].playerX-ball.x);
    ball.c=ball.y-ball.slope*ball.x;
    xinc=(players[dpNo].playerX>ball.x)?true:false;
    yinc=(players[dpNo].playerY>ball.y)?true:false;
    if(ball.x==players[dpNo].playerX)
        movSt=true;
    passRecvr=dpNo;
}

void checkSelfBtwnPass(int spNo,int dpNo)
{
    if(players[spNo].playerX>ball.x && players[spNo].playerX<players[dpNo].playerX)
    {
        players[spNo].playerX=ball.x-15;
        players[spNo].playerY=ball.y;
    }
    else if(players[spNo].playerX<ball.x && players[spNo].playerX>players[dpNo].playerX)
    {
        players[spNo].playerX=ball.x+15;
        players[spNo].playerY=ball.y;
    }

}

void shootBallInDirection(float xs,float ys,float xd,float yd,float vel,int spNo)
{
    ball.u=vel;
    shoot=true;
    S=1.0;
    t=1;
    if((players[spNo].playerX>ball.x && players[spNo].playerX<xd) || (players[spNo].playerX<ball.x && players[spNo].playerX>xd))
    {
        players[spNo].playerX=ball.x+15;
        players[spNo].playerY=ball.y;
    }
    if((players[spNo].playerY>ball.y && players[spNo].playerY<yd)||(players[spNo].playerY<ball.y && players[spNo].playerY>yd))
    {
        if(players[spNo].playerY>ball.y)
            players[spNo].playerY=ball.y-15;
        else
            players[spNo].playerY=ball.y+15;    
        players[spNo].playerX=ball.x;
    }
    float D=distBtwnPoints(xs,ys,xd,yd);
    diff=((xd-xs)/D)*15;    
    ball.slope=(float)(yd-ys)/(xd-xs);
    ball.c=yd-ball.slope*xd;
    xinc=(xd>xs)?true:false;
    yinc=(yd>ys)?true:false;
        if(xd==xs)
            movSt=true;

}

void shootGoal(int i,float u)
{
    if(i>2)
    {               
        if(players[2].playerY<150.0)
            shootBallInDirection(ball.x,ball.y,20.0,225.0,u,i);
        else if(players[2].playerY>190.0)
            shootBallInDirection(ball.x,ball.y,20.0,115.0,u,i);
        else if(players[i].playerY<170)
            shootBallInDirection(ball.x,ball.y,20.0,115.0,u,i);
        else
            shootBallInDirection(ball.x,ball.y,20.0,225.0,u,i);
    }
    else
    {
        if(players[5].playerY<150.0)
            shootBallInDirection(ball.x,ball.y,580.0,225.0,u,i);
        else if(players[2].playerY>190.0)
            shootBallInDirection(ball.x,ball.y,580.0,115.0,u,i);
        else if(players[i].playerY<170)
            shootBallInDirection(ball.x,ball.y,580.0,115.0,u,i);
        else
            shootBallInDirection(ball.x,ball.y,580.0,225.0,u,i);   
    }
}

void moveGoalKeeperToDest(float xdest,float ydest,int gkNo)
{
    float xs=xdest-players[gkNo].playerX;
    float ys=ydest-players[gkNo].playerY;

    if(gkNo==2)
    {    
        if((players[gkNo].playerX+xs)>20 && (players[gkNo].playerX+xs)<100 && xs>-players[gkNo].speed && xs<players[gkNo].speed)
            players[gkNo].playerX=players[gkNo].playerX+xs;
        else if((players[gkNo].playerX+players[gkNo].speed)<100 && xs>=players[gkNo].speed)
            players[gkNo].playerX=players[gkNo].playerX+players[gkNo].speed;
        else if((players[gkNo].playerX-players[gkNo].speed)>20)
            players[gkNo].playerX=players[gkNo].playerX-players[gkNo].speed;
    }
    else if(gkNo==5)
    {
        if((players[gkNo].playerX+xs)<580 && (players[gkNo].playerX+xs)>500 && xs>-players[gkNo].speed && xs<players[gkNo].speed)
            players[gkNo].playerX=players[gkNo].playerX+xs;
        else if((players[gkNo].playerX+players[gkNo].speed)>500 && xs>=players[gkNo].speed)
            players[gkNo].playerX=players[gkNo].playerX+players[gkNo].speed;
        else if((players[gkNo].playerX-players[gkNo].speed)<580)
            players[gkNo].playerX=players[gkNo].playerX-players[gkNo].speed;    
    }    

    if(ys>-players[gkNo].speed && ys<=0 && players[gkNo].playerY+ys>(ROWS/2-80))
        players[gkNo].playerY=players[gkNo].playerY+ys;
    else if(ys>=0 && ys<players[gkNo].speed && players[gkNo].playerY+ys<(ROWS/2+80))
        players[gkNo].playerY=players[gkNo].playerY+ys;
    else if(ys>=players[gkNo].speed && (players[gkNo].playerY+players[gkNo].speed)<(ROWS/2+80))
        players[gkNo].playerY=players[gkNo].playerY+players[gkNo].speed;
    else if(ys<=-players[gkNo].speed && (players[gkNo].playerY-players[gkNo].speed)>(ROWS/2-80))
        players[gkNo].playerY=players[gkNo].playerY-players[gkNo].speed;
}

void movePlayerToPosition(float xd,float yd,int pNo)//Should Not Collide With Anyone
{
    float xmov=xd-players[pNo].playerX;
    float ymov=yd-players[pNo].playerY;
    if(xmov>-players[pNo].speed && xmov<players[pNo].speed && (players[pNo].playerX+xmov)>20 && (players[pNo].playerX+xmov)<580 && checkOtherCollision(players[pNo].playerX+xmov,players[pNo].playerY,pNo)==false)
        players[pNo].playerX=players[pNo].playerX+xmov;
    else if(xmov>=players[pNo].speed && (players[pNo].playerX+players[pNo].speed)<580 && checkOtherCollision(players[pNo].playerX+players[pNo].speed,players[pNo].playerY,pNo)==false)
        players[pNo].playerX=players[pNo].playerX+players[pNo].speed;
    else if(xmov<=-players[pNo].speed && (players[pNo].playerX-players[pNo].speed)>20 && checkOtherCollision(players[pNo].playerX-players[pNo].speed,players[pNo].playerY,pNo)==false)
        players[pNo].playerX=players[pNo].playerX-players[pNo].speed;

    if(ymov>-players[pNo].speed && ymov<players[pNo].speed && (players[pNo].playerY+ymov)>0 && (players[pNo].playerY+ymov)<340 && checkOtherCollision(players[pNo].playerX,players[pNo].playerY+ymov,pNo)==false)
        players[pNo].playerY=players[pNo].playerY+ymov;
    else if(ymov>=players[pNo].speed && (players[pNo].playerY+players[pNo].speed)<340 && checkOtherCollision(players[pNo].playerX,players[pNo].playerY+players[pNo].speed,pNo)==false)
        players[pNo].playerY=players[pNo].playerY+players[pNo].speed;
    else if(ymov<=-players[pNo].speed && (players[pNo].playerY-players[pNo].speed)>0 && checkOtherCollision(players[pNo].playerX,players[pNo].playerY-players[pNo].speed,pNo)==false)
        players[pNo].playerY=players[pNo].playerY-players[pNo].speed;
}

void Player::goalKeeper1Movement()
{
    float desX1=40,desY1=170,des2X=560,desY2=170,xmov1=0,ymov1=0;
    float res1=m11*(ball.x-40)-(ball.y-170);
    float res2=m12*(ball.x-40)-(ball.y-170);

    if(ball.x<100 && checkIfNearest(2)==true && gkMove==true)
    {
        float xs=ball.x-players[2].playerX;
        if(ball.x<120 && xs<players[2].speed && xs>-players[2].speed && players[2].playerX+xs<100 && distance(players[2],ball)>18) 
            players[2].playerX=players[2].playerX+xs;
        else if(ball.x<120 && xs>=players[2].speed && players[2].playerX+players[2].speed<100 && distance(players[2],ball)>18)
            players[2].playerX=players[2].playerX+players[2].speed;
        else if(ball.x<120 && players[2].playerX-players[2].speed>20 && distance(players[2],ball)>18)
            players[2].playerX=players[2].playerX-players[2].speed;

        float ys=ball.y-players[2].playerY;
        if(ys<players[2].speed && ys>-players[2].speed && distance(players[2],ball)>18)
            players[2].playerY=players[2].playerY+ys;             
        else if(ys>=players[2].speed && distance(players[2],ball)>18)
            players[2].playerY=players[2].playerY+players[2].speed;
        else if(distance(players[2],ball)>18)
            players[2].playerY=players[2].playerY-players[2].speed;

        if(distance(players[2],ball)<18)
        {
            if(ball.x<90 && ball.y<(ROWS/2+80) && ball.y>(ROWS/2-80) && checkOppositionInsideD(2))
                players[2].hasBall=true;
            else
            {
                checkSelfBtwnPass(2,0);
                callPassLogic(2,0,14.0);
            }
        }
    }
    else if(gkMove==true)
    {
        if(ball.x<300 && ball.x>20)
        {
            if(checkBallInD(2))
            {
                desX1=ball.x-15;
                desY1=ball.y;
            }
            if(res1>0 && ball.y<170)
            {
                desY1=ball.y+m11*(40-ball.x);
                desX1=40.0;                
            }
            else if(res2<0 && ball.y>170)
            {
                desY1=ball.y+m12*(40-ball.x);
                desX1=40.0;
            }
            else if(ball.x<170 && res1<0 && res2>0)
            {
                desX1=40.0;
                desY1=ball.y;
            }
            else if(ball.y>(ROWS/2+80))
            {
                desX1=40.0;
                desY1=ROWS/2+80;
            }    
            else if(ball.y<(ROWS/2-80))
            {
                desX1=40.0;
                desY1=ROWS/2-80;
            }    
            else
            {
                desY1=170.0;
                desX1=40.0;
            }
            moveGoalKeeperToDest(desX1,desY1,2);    

        }
        else
        {
            desY1=170.0;
            desX1=40.0;
            moveGoalKeeperToDest(desX1,desY1,2);
        }
    } 
}

void Player::goalKeeper2Movement()
{
    float desX2=560,desY2=170;
    float res1=m12*(ball.x-560)-(ball.y-170);
    float res2=m11*(ball.x-560)-(ball.y-170);

    if(ball.x>500 && checkIfNearest(5)==true && gkMove==true)
    {
        float xs=ball.x-players[5].playerX;
        if(ball.x>480 && xs<players[5].speed && xs>-players[5].speed && players[5].playerX+xs<580 && distance(players[5],ball)>18)   
            players[5].playerX=players[5].playerX+xs;
        else if(ball.x>480 && xs>=players[5].speed && players[5].playerX+players[5].speed<580 && distance(players[5],ball)>18)
            players[5].playerX=players[5].playerX+players[5].speed;
        else if(ball.x>480 && players[5].playerX-players[5].speed>500 && distance(players[5],ball)>18)
            players[5].playerX=players[5].playerX-players[5].speed;

        float ys=ball.y-players[5].playerY;
        if(ys<players[5].speed && ys>-players[5].speed && distance(players[5],ball)>18)
            players[5].playerY=players[5].playerY+ys;
        else if(ys>=players[5].speed && distance(players[5],ball)>18)
            players[5].playerY=players[5].playerY+players[5].speed;
        else if(distance(players[5],ball)>18)
            players[5].playerY=players[5].playerY-players[5].speed;

        if(distance(players[5],ball)<18)
        {
            if(ball.x>510 && ball.y<(ROWS/2+80) && ball.y>(ROWS/2-80) && checkOppositionInsideD(5))
                    players[5].hasBall=true;
            else
                callPassLogic(5,3,14.0);
        }
    }
    else if(gkMove==true)
    {
        if(ball.x>300 && ball.x<580)
        {
            if(checkBallInD(5))
            {
                desX2=ball.x+15;
                desY2=ball.y;
            }
            else if(res1>0 && ball.y<170)
            {
                desY2=ball.y+m12*(560-ball.x);
                desX2=560.0;                
            }
            else if(res2<0 && ball.y>170)
            {
                desY2=ball.y+m11*(560-ball.x);
                desX2=560.0;
            }
            else if(ball.x>430 && res1<0 && res2>0)
            {
                desX2=560.0;
                desY2=ball.y;
            }
            else if(ball.y>(ROWS/2+80))
            {
                desX2=560.0;
                desY2=ROWS/2+80;
            }    
            else if(ball.y<(ROWS/2-80))
            {
                desX2=560.0;
                desY2=ROWS/2-80;
            }    
            else
            {
                desY2=170.0;
                desX2=560.0;
            }
            moveGoalKeeperToDest(desX2,desY2,5);    

        }
        else
        {
            desY2=170.0;
            desX2=560.0;
            moveGoalKeeperToDest(desX2,desY2,5);
        }
    }
}

void Player::botPlayer4Movement()
{
    float desX=395.0,desY=205.0;
    if(players[4].hasBall==true)
    {
        if(checkOppositionNear(4))
        {
            if(checkNearGoal(4))
            {
                shootGoal(4,9.0);
                players[4].hasBall=false;
            }
            else
            {
                if(checkPlayerBtwnPass(4,3)==false)
                {
                    callPassLogic(4,3,9.0);
                    players[4].hasBall=false;
                }    
                else
                {   
                    shootBallInDirection(ball.x,ball.y,ball.x-400,170.0,10.0,4);
                    players[4].hasBall=false;
                }
            }    
        }
        else if(checkNearGoal(4))
        {
            shootGoal(4,9.0);
            players[3].hasBall=false;
        }
        else
        {
            if((players[4].playerX-2)>40)
            {
                if(checkOtherCollision(ball.x+15,ball.y,4)==false)
                {
                    players[4].playerX=ball.x+15;
                    players[4].playerY=ball.y;
                }
                if(checkOtherCollision(players[4].playerX-2,players[4].playerY,4)==false)
                {
                    players[4].playerX=players[4].playerX-2;
                    ball.x=ball.x-2;
                }
                if((players[4].playerY+2)<230 && checkOtherCollision(players[4].playerX,players[4].playerY+2,4)==false)
                {
                    players[4].playerY=players[4].playerY+2;
                    ball.y=ball.y+2;
                }    
                else
                {
                    players[4].playerY=players[4].playerY;
                    ball.y=ball.y;
                }
            }    
        }    

    }
    else if(players[2].hasBall==true)
    {
        desX=395.0,desY=205.0;
        movePlayerToPosition(desX,desY,4);
    }
    else if(players[3].hasBall==true)
    {
        if(checkOtherCollision(players[4].playerX-players[4].speed,players[4].playerY,4)==false && (players[4].playerX-players[4].speed)>40)
            players[4].playerX=players[4].playerX-players[4].speed;
        else if(checkOtherCollision(players[4].playerX-players[4].speed,players[4].playerY-players[4].speed,4)==false && (players[4].playerX-players[4].speed)>40 && (players[4].playerY-players[4].speed)>20)
        {
            players[4].playerX=players[4].playerX-players[4].speed;
            players[4].playerY=players[4].playerY-players[4].speed;
        }
        else if(checkOtherCollision(players[4].playerX-players[4].speed,players[4].playerY+players[4].speed,4)==false && (players[4].playerX-players[4].speed)>40 && (players[4].playerY+players[4].speed)<320)
        {
            players[4].playerX=players[4].playerX-players[4].speed;
            players[4].playerY=players[4].playerY+players[4].speed;
        }
    }
    else if(ball.x>410 || checkIfNearest(4)==true)
    {
        desX=ball.x+15;
        desY=ball.y;
        movePlayerToPosition(desX,desY,4);
        if(distance(players[4],ball)<16)
            players[4].hasBall=true;
    }  
    else if((passRecvr==4 && pass==true)==false)
    {
        desX=395.0;
        desY=205.0;
        movePlayerToPosition(desX,desY,4);    
    }
    
}

void Player::botPlayer3Movement()
{
    float desX=345.0,desY=155.0;
    if(players[3].hasBall==true && players[4].hasBall==true)
    {
        ball.x=players[3].playerX-15;
        ball.y=players[3].playerY;
        players[4].hasBall=false;
    }
    if(players[3].hasBall==true)
    {
        if(checkOppositionNear(3))
        {
            if(checkNearGoal(3))
            {                                
                shootGoal(3,9.0);
                players[3].hasBall=false;
            }
            else
            {
                if(checkPlayerBtwnPass(3,4)==false)
                {   
                    checkSelfBtwnPass(3,4);
                    callPassLogic(3,4,9.0);
                    players[3].hasBall=false;                    
                }
                else
                {    
                    shootBallInDirection(ball.x,ball.y,ball.x-400,170.0,10.0,3);
                    players[3].hasBall=false;
                }
            }
        }
        else if(checkNearGoal(3) && distBtwnPoints(players[3].playerX,players[3].playerY,players[2].playerX,players[2].playerY)<40)
        {
            shootGoal(3,9.0);
            players[3].hasBall=false;
        }
        else
        {
            if((players[3].playerX-2)>40)
            {
                if(checkOtherCollision(ball.x+15,ball.y,3)==false)
                {
                    players[3].playerX=ball.x+15;
                    players[3].playerY=ball.y;
                }
                if(checkOtherCollision(players[3].playerX-2,players[3].playerY,3)==false)
                {
                    players[3].playerX=players[3].playerX-2;
                    ball.x=ball.x-2;
                }
                if((players[3].playerY-2)>110 && checkOtherCollision(players[3].playerX,players[3].playerY-2,3)==false)
                {
                    players[3].playerY=players[3].playerY-2;
                    ball.y=ball.y-2;
                }    
                else
                {
                    players[3].playerY=players[3].playerY;
                    ball.y=ball.y;
                }
            }
        }
    }
    else if(players[2].hasBall==true)
    {
        desX=160.0,desY=170.0;
        movePlayerToPosition(desX,desY,3);
    }
    else if(players[4].hasBall==true)
    {
        if(checkOtherCollision(players[3].playerX-players[3].speed,players[3].playerY,3)==false && (players[3].playerX-players[3].speed)>40)
            players[3].playerX=players[3].playerX-players[3].speed;
        else if(checkOtherCollision(players[3].playerX-players[3].speed,players[3].playerY+players[3].speed,3)==false && (players[3].playerX-players[3].speed)>40 && (players[3].playerY+players[3].speed)<320)
        {
            players[3].playerX=players[3].playerX-players[3].speed;
            players[3].playerY=players[3].playerY+players[3].speed;
        }
        else if(checkOtherCollision(players[3].playerX-players[3].speed,players[3].playerY-players[3].speed,3)==false && (players[3].playerX-players[3].speed)>40 && (players[3].playerY-players[3].speed)>20)
        {
            players[3].playerX=players[3].playerX-players[3].speed;
            players[3].playerY=players[3].playerY-players[3].speed;
        }
    }
    else if((passRecvr==3 && pass==true)==false)
    {
        desX=ball.x+15;
        desY=ball.y;
        movePlayerToPosition(desX,desY,3);
        if(distance(players[3],ball)<16)
            players[3].hasBall=true;
    }    

}

void Player::botPlayer1Movement()
{
    float desX=185.0,desY=125.0;
    if(players[1].hasBall==true)
    {
        if(checkOppositionNear(1))
        {
            if(checkNearGoal(1))
            {
                shootGoal(1,12.0);
                players[1].hasBall=false;
            }
            else
            {
                if(checkPlayerBtwnPass(1,0)==false)
                {
                    callPassLogic(1,0,15.0);
                    players[1].hasBall=false;
                }    
                else
                {    
                    shootBallInDirection(ball.x,ball.y,ball.x+400,170.0,12.0,1);
                    players[1].hasBall=false;
                }
            }    
        }
        else if(checkNearGoal(1))
        {
            shootGoal(1,12.0);
            players[1].hasBall=false;
        }
        else
        {
            if((players[1].playerX+2)<560)
            {
                if(checkOtherCollision(ball.x-15,ball.y,1)==false)
                {
                    players[1].playerX=ball.x-15;
                    players[1].playerY=ball.y;
                }
                if(checkOtherCollision(players[1].playerX+2,players[1].playerY,1)==false)
                {
                    players[1].playerX=players[1].playerX+2;
                    ball.x=ball.x+2;
                }
                if((players[1].playerY-2)>110 && checkOtherCollision(players[1].playerX,players[1].playerY-2,1)==false)
                {
                    players[1].playerY=players[1].playerY-2;
                    ball.y=ball.y-2;
                }    
                else
                {
                    players[1].playerY=players[1].playerY;
                    ball.y=ball.y;
                }
            }    
        }    

    }
    else if(players[5].hasBall==true)
    {
        desX=185.0,desY=125.0;
        movePlayerToPosition(desX,desY,1);
    }
    else if(players[0].hasBall==true)
    {
        if(checkOtherCollision(players[1].playerX+players[1].speed,players[1].playerY,1)==false && (players[1].playerX+players[1].speed)<560)
            players[1].playerX=players[1].playerX+players[1].speed;
        else if(checkOtherCollision(players[1].playerX+players[1].speed,players[1].playerY-players[1].speed,1)==false && (players[1].playerX+players[1].speed)<560 && (players[1].playerY-players[1].speed)>20)
        {
            players[1].playerX=players[1].playerX+players[1].speed;
            players[1].playerY=players[1].playerY-players[1].speed;
        }
        else if(checkOtherCollision(players[1].playerX+players[1].speed,players[1].playerY+players[1].speed,1)==false && (players[1].playerX+players[1].speed)<560 && (players[1].playerY+players[1].speed)<320)
        {
            players[1].playerX=players[1].playerX+players[1].speed;
            players[1].playerY=players[1].playerY+players[1].speed;
        }    
    }
    else if(ball.x<190 || checkIfNearest(1)==true)
    {
        desX=ball.x-15;
        desY=ball.y;
        movePlayerToPosition(desX,desY,1);
        if(distance(players[1],ball)<16)
            players[1].hasBall=true;
    }  
    else if((passRecvr==1 && pass==true)==false)
    {
        desX=185.0;
        desY=125.0;
        movePlayerToPosition(desX,desY,1);
        
    }

}

void Player::botPlayer0Movement()
{
    float desX=255.0,desY=185.0;
    if(players[0].hasBall==true && players[1].hasBall==true)
    {
        ball.x=players[0].playerX+15;
        ball.y=players[0].playerY;
        players[1].hasBall=false;
    }
    if(players[0].hasBall==true)
    {
        if(checkOppositionNear(0))
        {
            if(checkNearGoal(0))
            {                                
                shootGoal(0,11.0);
                players[0].hasBall=false;
            }
            else
            {
                if(checkPlayerBtwnPass(0,1)==false)
                {   
                    callPassLogic(0,1,10.0);
                    players[0].hasBall=false;                    
                }
                else
                {    
                    shootBallInDirection(ball.x,ball.y,ball.x+400,170.0,11.0,0);
                    players[0].hasBall=false;
                }
            }
        }
        else if(checkNearGoal(0) && distBtwnPoints(players[0].playerX,players[0].playerY,players[5].playerX,players[5].playerY)<40)
        {
            shootGoal(0,11.0);
            players[0].hasBall=false;
        }
        else
        {
            if((players[0].playerX+2)<560)
            {
                if(checkOtherCollision(ball.x-15,ball.y,0)==false)
                {
                    players[0].playerX=ball.x-15;
                    players[0].playerY=ball.y;
                }
                if(checkOtherCollision(players[0].playerX+2,players[0].playerY,0)==false)
                {
                    players[0].playerX=players[0].playerX+2;
                    ball.x=ball.x+2;
                }
                if((players[0].playerY-2)<230 && checkOtherCollision(players[0].playerX,players[0].playerY+2,0)==false)
                {
                    players[0].playerY=players[0].playerY+2;
                    ball.y=ball.y+2;
                }    
                else
                {
                    players[0].playerY=players[0].playerY;
                    ball.y=ball.y;
                }
            }
        }
    }
    else if(players[5].hasBall==true)
    {
        desX=440.0,desY=170.0;
        movePlayerToPosition(desX,desY,0);
    }
    else if(players[1].hasBall==true)
    {
        if(checkOtherCollision(players[0].playerX+players[0].speed,players[0].playerY,0)==false && (players[0].playerX+players[0].speed)<560)
            players[0].playerX=players[0].playerX+players[0].speed;
        else if(checkOtherCollision(players[0].playerX+players[0].speed,players[0].playerY+players[0].speed,0)==false && (players[0].playerX+players[0].speed)<560 && (players[0].playerY+players[0].speed)<320)
        {
            players[0].playerX=players[0].playerX+players[0].speed;
            players[0].playerY=players[0].playerY+players[0].speed;
        }
        else if(checkOtherCollision(players[0].playerX+players[0].speed,players[0].playerY-players[0].speed,0)==false && (players[0].playerX+players[0].speed)<560 && (players[0].playerY-players[0].speed)>20)
        {
            players[0].playerX=players[0].playerX+players[0].speed;
            players[0].playerY=players[0].playerY-players[0].speed;
        }
    }
    else if((passRecvr==0 && pass==true)==false)
    {
        desX=ball.x-15;
        desY=ball.y;
        movePlayerToPosition(desX,desY,0);
        if(distance(players[0],ball)<16)
            players[0].hasBall=true;
    }    

}

void movePlayers()
{
    determineWhoHasBall();
    players[2].goalKeeper1Movement();
    players[5].goalKeeper2Movement();
    //players[3].botPlayer3Movement();
    //players[4].botPlayer4Movement();
    //players[1].botPlayer1Movement();
    //players[0].botPlayer0Movement();
    if((ball.x>20 && ball.y<580 && ball.y>10 && ball.y<330)==false)
    {
        ball.y=170.0;
        ball.x=300.0;
    }    
}


bool Ball::checkPlayerCollison(float S,float prevX,float prevY,int i,int j)
{
    float x1,x2,y1,y2,xc,yc,xh;
    xh=x;
    xc=players[i].playerX;
    yc=players[i].playerY;
    x1=players[i].playerX-10;
    x2=players[i].playerX+10;
    y1=players[i].playerY-10;
    y2=players[i].playerY+10;
    if(distance(players[i],ball)<15.0 && prevX==x)
    {
        pass=false;
        float Xa=x,Ya,Yb;
        if(x!=xc)
        {    
            if(x>x1 && x<x2)
            {                   
                Ya=yc+sqrt(100-(Xa-xc)*(Xa-xc));
                Yb=yc-sqrt(100-(Xa-xc)*(Xa-xc));
            }
            else if(x<xc)
            {
                Xa=x+5;
                Ya=yc+sqrt(100-(Xa-xc)*(Xa-xc));
                Yb=yc-sqrt(100-(Xa-xc)*(Xa-xc));
            }
            else if(x>xc)
            {
                Xa=x-5;
                Ya=yc+sqrt(100-(Xa-xc)*(Xa-xc));
                Yb=yc-sqrt(100-(Xa-xc)*(Xa-xc));
            }    
            y=(distBtwnPoints(Xa,Ya,prevX,prevY)<distBtwnPoints(Xa,Yb,prevX,prevY))?Ya:Yb;
            if(prevY<yc)
                y=y-5;
            else
                y=y+5;
            float m=(y-yc)/(x-xc);
            slope=(m-(1/m))/2;
            float dis=distBtwnPoints(x,y,prevX,prevY);
            float remDis=S-dis;
            c=y-slope*x;
            float cc=cos(atan(slope));
            if((x>xc && yinc==true)||(x>xc && yinc==false))
                x=x+(remDis)*cos(atan(slope));
            else
                x=x-(remDis)*cos(atan(slope));
            y=slope*x+c;
            if(x>xc)
            {
                xinc=true;
                diff=15*cos(atan(slope));               
            } 
            else
            {
                xinc=false;
                diff=-15*cos(atan(slope));
            }   
            movSt=false;
        }
        else if(x==xc)
        {
            if(prevY<yc)
            {
                y=yc-15;
                x=xc;
                yinc=false;
                float dis=y-prevY;
                float remDis=S-dis;
                y=y-remDis;
            } 
            else if(prevY>yc)
            {
                y=yc+15;
                x=xc;
                yinc=true;
                float dis=prevY-y;
                float remDis=S-dis;
                y=y+remDis;
            }   
        }    

    }    

    if(distance(players[i],ball)<15.0)
    {
        pass=false;
        float A=slope*slope+1;
        float B=2*(slope*(c-yc)-xc);
        float C=((xc*xc)-100+(c-yc)*(c-yc));
        float discr=(B*B)-(4*A*C);
        if(discr<0)
        {
            float c1=c-5*sqrt(1+slope*slope);
            B=2*(slope*(c1-yc)-xc);
            C=((xc*xc)-100+(c1-yc)*(c1-yc));
            discr=(B*B)-(4*A*C);
        }    
        if(discr<0)
        {
            float c1=c+5*sqrt(1+slope*slope);
            B=2*(slope*(c1-yc)-xc);
            C=((xc*xc)-100+(c1-yc)*(c1-yc));
            discr=(B*B)-(4*A*C);
        }    
        float Xa=(-B+sqrt(discr))/(2*A);
        float Xb=(-B-sqrt(discr))/(2*A);
        float Ya=slope*Xa+c;
        float Yb=slope*Xb+c;
        if(xinc)
            Xa=(Xa<Xb)?Xa:Xb;
        else
            Xa=(Xa>Xb)?Xa:Xb; 
        Ya=slope*Xa+c;
        if(xinc)
        {    
            x=Xa-5*cos(atan(slope));
            y=Ya-5*sin(atan(slope));
        }
        else
        {
            x=Xa+5*cos(atan(slope));
            y=Ya+5*sin(atan(slope));
        }    
        if(shoot==false || i%3==2)
        {
            passRecvr=-1;
            players[i].hasBall=true;
        }
        if(shoot==false && (i==0 || i==1))
        {
            if(players[i].playerY<170)
            {
                players[i].playerX=ball.x-11;
                players[i].playerY=ball.y-2;
            }    
            else
            {
                players[i].playerX=ball.x-11;
                players[i].playerY=ball.y+3;
            }
        }
        if(shoot && i%3!=2 && S>2.0)
        {
            float m1=slope;
            float m2=(players[i].playerY-y)/(players[i].playerX-x);
            Xa=x;
            Ya=y;
            slope=((2*m2)-m1+(m1*m2*m2))/(1+m1*m1);
            c=y-slope*x;
            float dis=distBtwnPoints(x,y,prevX,prevY);

            float remDis=S-dis;
            
            float Xi=x+(remDis+5)*cos(atan(slope));
            float Yi=slope*Xi+c;
            float Xj=x-(remDis+5)*cos(atan(slope)); 
            float Yj=slope*Xj+c;
            if(distBtwnPoints(Xi,Yi,players[i].playerX,players[i].playerY)>distBtwnPoints(Xj,Yj,players[i].playerX,players[i].playerY))
            {
                x=Xi;
                y=Yi;
            }   
            else
            {
                x=Xj;
                y=Yj;
            }
            if(xinc)
            {    
                diff=-15*cos(atan(slope));
                xinc=false;
            }    
            else
            {    
                diff=15*cos(atan(slope));
                xinc=true;
            }    
        }
        else
            shoot=false;    
        return true;
    } 

    return false;   
}

bool Ball::helpCheckPlayerCollison(float S,float prevX,float prevY,int j)
{
    bool res=0;
    for(int i=0;i<6;i++)
        res=res|checkPlayerCollison(S,prevX,prevY,i,j);

    return res;
}

void Ball::check_Y()
{
    float x2;
    if(y<15)
    {
        x2=x;
        y=15;
        x=(y-c)/slope;
        slope=-slope;                
        c=15-slope*x;
        x=x2;
        y=slope*x+c;
    }
    else if(y>325)
    {
        x2=x;
        y=325;
        x=(y-c)/slope;
        slope=-slope;
        c=325-slope*x;
        x=x2;
        y=slope*x+c;
    }

}

void Ball::moveBall(float S,int j)
{
    float x2,y2;
    float xc=(float)players[j].playerX;
    float yc=(float)players[j].playerY;
    float prevX=x,prevY=y;
    x2=(S/15)*diff;
    if(x==xc || movSt==true)
    {
        if((y<yc && hitbound==false)||(y>yc && hitbound==true) || yinc==false)
        {
            if(y-S<15)
            {
                y2=15.0-y+S;
                y=15.0;
                if(helpCheckPlayerCollison(S,prevX,prevY,j))
                    return;
                hitbound=true;
                yinc=true;
                y=y2+15;
            }    
            else if(yinc==false)
            {    
                y=y-S;
                if(helpCheckPlayerCollison(S,prevX,prevY,j))
                    return;
            }    
        } 
        if(yinc==true || (y>yc && hitbound==false)||(y<yc && hitbound==true))
        {
            if(y+S>325)
            {
                y2=650.0-(y+S);
                y=325.0;
                if(helpCheckPlayerCollison(S,prevX,prevY,j))
                    return;
                hitbound=true;
                yinc=false;
                y=y2;
            }
            else if(yinc==true)
            {    
                y=y+S;
                if(helpCheckPlayerCollison(S,prevX,prevY,j))
                    return;
            }        
        }     
    }   
    else
    { 
        if((x+x2)<575 && (x+x2)>25)
        {    
            x=x+x2;
            y=slope*x+c;
            if(helpCheckPlayerCollison(S,prevX,prevY,j))
                return;
            if(y<15.0)
            {
                x2=x;
                y=15.0;
                x=(y-c)/slope;
                slope=-slope;                
                c=15.0-slope*x;
                x=x2;
                y=slope*x+c;                
            }
            else if(y>325)
            {
                x2=x;
                y=325;
                x=(y-c)/slope;
                slope=-slope;
                c=325-slope*x;
                x=x2;
                y=slope*x+c;
            }    
        }
        else if(y>(ROWS/2-60) && y<(ROWS/2+60) && (x+x2)<7)
        {   
           goal=true;
            if(goal && resetgoal==false)
                goalB=goalB+1;
            resetgoal=true;
            backTime=clock()/CLOCKS_PER_SEC;
            x2=14-x-x2;
            x=7;
            diff=-diff;
            x=x2;
            slope=-slope;
            c=y-slope*7;
            y=slope*x+c;
            xinc=true;
            movPlayers=false;
        }
        else if(y>(ROWS/2-60) && y<(ROWS/2+60) && (x+x2)>7 && (x+x2)<25)
        {
           goal=true;
            if(goal && resetgoal==false)
                goalB=goalB+1;
            resetgoal=true;
            backTime=clock()/CLOCKS_PER_SEC;
            x=x+x2;
            y=slope*x+c;
            movPlayers=false;
        }    
        else if((x+x2)<25 && (y<(ROWS/2-60) || y>(ROWS/2+60) ))
        {    
            x2=50-x-x2;
            x=25;
            y=slope*x+c;
            diff=-diff;
            goal=false;
            x=x2;
            slope=-slope;
            c=y-slope*25;
            y=slope*x+c;
            check_Y();
            xinc=true;
        }
        else if(y>(ROWS/2-60) && y<(ROWS/2+60) && (x+x2)>593)
        {
            goal=true;
            if(goal && resetgoal==false)
                goalA=goalA+1;
            resetgoal=true;
            backTime=clock()/CLOCKS_PER_SEC;
            x2=1186-(x+x2);
            x=593;
            y=slope*x+c;
            diff=-diff;
            x=x2;
            slope=-slope;
            c=y-slope*593;
            y=slope*x+c;
            xinc=false;
            movPlayers=false;
        }
        else if(y>(ROWS/2-60) && y<(ROWS/2+60) && (x+x2)<593 && (x+x2)>575)
        {
            goal=true;
            if(goal && resetgoal==false)
                goalA=goalA+1;
            resetgoal=true;
            backTime=clock()/CLOCKS_PER_SEC;
            x=x+x2;
            y=slope*x+c;
            movPlayers=false;
        }    
        else if(x+x2>575)
        {
            x2=1150-(x+x2);
            x=575;
            y=slope*x+c;
            diff=-diff;
            goal=false;
            x=x2;
            slope=-slope;
            c=y-slope*575;
            y=slope*x+c;
            check_Y();
            xinc=false;
        }        
    }
}
cell map[ROWS][COLS];

void init(void)
{
    glEnable(GL_BLEND);
    glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glClearColor (0.0, 0.0, 0.0, 0.0);
    glShadeModel (GL_FLAT);
    glutSetCursor(GLUT_CURSOR_FULL_CROSSHAIR);
}

void reshape (int w, int h)
{
   glViewport (0, 0, (GLsizei) w, (GLsizei) h);
   glMatrixMode (GL_PROJECTION);
   glLoadIdentity ();
   glOrtho ( 0.0, COLS, ROWS, 0.0, 1.5, 200.0);
   glMatrixMode (GL_MODELVIEW);
}

 int absolute(long int val)
{
    if(val<0)
        return (val*(-1));
    else 
        return val;
}

void DrawCircle(float cx, float cy, float r, int num_segments)
  {
      glBegin(GL_TRIANGLE_FAN);
      for(int ii = 0; ii < num_segments; ii++)
      {
          float theta = 2.0f * 3.1415926f * float(ii) / float(num_segments);//get the current angle

          float x = r * cosf(theta);//calculate the x component
          float y = r * sinf(theta);//calculate the y component
          glVertex2f(x + cx, y + cy);//output vertex

      }
      glEnd();
  }


//    don't interfere with this function, it renders the map on the display
void generate_map() {
    for( int i = 0; i < ROWS; i++ ) {
        for( int j = 0; j < COLS; j++ ) {
            cell &c = map[i][j];

            if( i % 2 && j % 2 )
                glColor3ub( c.r, c.g+20, c.b );
            else if( j % 2 )
                glColor3ub( c.r, c.g-20, c.b );
            else if( i % 2 )
                glColor3ub( c.r, c.g-40, c.b );
            else
                glColor3ub( c.r, c.g, c.b );

            if( (i == 10 || i == ROWS-11) && (j > 20 && j < COLS-21) )
                glColor3ub( 255, 255, 255 );
            if( (j == 20 || j == COLS-21) && (i >= 10 && i <= ROWS-11) )
                glColor3ub( 255, 255, 255 );
            if( j == COLS/2 && (i >= 10 && i <= ROWS-11) )
                glColor3ub( 255, 255, 255 );
            if( (i == ROWS/2-60 || i == ROWS/2+60) && ( (j >= 2 && j <= 20) || (j < COLS-2 && j > COLS-21) ) )
                glColor3ub( 255, 255, 255 );
            if( (i > ROWS/2-60 && i < ROWS/2+60) && ( j == 2 || j == COLS-3) )
                glColor3ub( 255, 255, 255 );
            if( (i > ROWS/2-80 && i < ROWS/2+80) && ( j == 90 || j == COLS-90) )
                glColor3ub( 255, 255, 255 );
            if( (i == ROWS/2-80 || i == ROWS/2+80) && ( (j >= 20 && j <= 90) || (j < COLS-20 && j >= COLS-90) ) )
                glColor3ub( 255, 255, 255 );


            if( fabs((double)(i-ROWS/2)*(i-ROWS/2) + (double)(j-COLS/2)*(j-COLS/2) - 900.0) <= 35.0  )
                glColor3ub( 255, 255, 255 );

            glBegin(GL_QUADS);
                glVertex3f(   j,   i , 0);
                glVertex3f( j+1,   i , 0);
                glVertex3f( j+1, i+1 , 0);
                glVertex3f(   j, i+1 , 0);
            glEnd();
            
        }
    }
    glColor3ub(255,255,255);
    
}

void inContact()
{
    if((absolute(gXBall-gX)==(rMan+rBall)&& absolute(gYBall-gY)==0) || (absolute(gXBall-gX)==0 && absolute(gYBall-gY)==(rMan+rBall)))
    {
        cout<<"rMan:"<<rMan<<"\t rBall:"<<rBall<<endl;
        cout<<"absolute(gXBall-gX):"<<absolute(gXBall-gX)<<"\t absolute(gYBall-gY)"<<absolute(gYBall-gY)<<endl;
        cout<<"gX:"<<gX<<"\tgY:"<<gY<<endl;
        cout<<"gXBall"<<gXBall<<"\tgYBall:"<<gYBall<<"\n\n"<<endl;
        gXBall=gXBall+(gX-helpX);
        gYBall=gYBall+(gY-helpY);
        contact=true;
    }
}

void keyOperations(int active)
{
    int f,i;
    float gap;
    if((distance(players[playNum],ball)<=diagonal))
        f=2;
    else if(playNum==1 || playNum==4)
        f=2;
    else
        f=3;   
    if(specialKeyStates[GLUT_KEY_RIGHT]==true)
    {
        helpX=players[active].playerX;
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[active],players[i]);
            if(i!=active)
            {
                if(players[active].playerY==players[i].playerY && gap<=20 && (players[active].playerX<players[i].playerX))
                    break;
                else if(gap>0 && gap<20 && (players[active].playerX<players[i].playerX)  && !(gap>=15))
                    break;
                else if(i==5)
                {
                    players[active].playerX=players[active].playerX+f;
                    if(specialKeyStates[GLUT_KEY_UP]==true)
                        players[active].playerY=players[active].playerY-f;
                    else if(specialKeyStates[GLUT_KEY_DOWN]==true)
                        players[active].playerY=players[active].playerY+f;
                }
            }
        }
    }
    else if(specialKeyStates[GLUT_KEY_LEFT]==true)
    {
        helpX=players[active].playerX;
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[i],players[active]);
            if(i!=active)
            {
                if(players[active].playerY==players[i].playerY && gap<=20 && (players[active].playerX>players[i].playerX))
                    break;
                else if(gap>0 && gap<20 && (players[active].playerX>players[i].playerX)  && !(gap>=15))
                    break;
                else if(i==5)
                {
                    players[active].playerX=players[active].playerX-f;
                    if(specialKeyStates[GLUT_KEY_UP]==true)
                        players[active].playerY=players[active].playerY-f;
                    else if(specialKeyStates[GLUT_KEY_DOWN]==true)
                        players[active].playerY=players[active].playerY+f;
                }
            }
        }
    }
    else if(specialKeyStates[GLUT_KEY_UP]==true)
    {
        helpY=players[active].playerY;
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[active],players[i]);
            if(i!=active)
            {
                if(players[active].playerX==players[i].playerX && gap<=20 && (players[active].playerY>players[i].playerY))
                    break;
                else if(gap>0 && gap<20 && (players[active].playerY>players[i].playerY)  && !(gap>=15))
                    break;
                else if(i==5)
                    players[active].playerY=players[active].playerY-f;
            }
        }        
    }
    else if(specialKeyStates[GLUT_KEY_DOWN]==true)
    {
        helpY=players[active].playerY;
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[active],players[i]);
            if(i!=active)
            {
                if(players[active].playerX == players[i].playerX && gap<=20 && (players[active].playerY<players[i].playerY))
                    break;
                else if(gap>0 && gap<20 && (players[active].playerY<players[i].playerY)  && !(gap>=15))
                    break;
                else if(i==5)
                    players[active].playerY=players[active].playerY+f;
            }
        }
        
    }
    else if(keyStates['c']==true)
    {
        float xc=players[playNum].playerX,yc=players[playNum].playerY;
        if((ball.x-xc)<22 && (ball.x-xc)>-22)
        {    
            if(playNum==0)
                callPassLogic(0,1,10.0);
            else if(playNum==1)
                callPassLogic(1,0,10.0);
            else if(playNum==3)
                callPassLogic(3,4,10.0);
            else if(playNum==4)
                callPassLogic(4,3,10.0);
        }
    }
    if(keyStates['v']==true)
    {
        float xc=players[active].playerX,yc=players[active].playerY;
        if(distance(players[active],ball)<18)
        {   
            if(keyPower>40)
                keyPower=40;
            if(active%3==0) 
                ball.u=11.0+(keyPower/10);
            else if(active%3==1)
                ball.u=12.0+(keyPower/10);
            shoot=true;
            S=1.0;
            t=1;
            diff=(float)(ball.x-xc);
            ball.slope=(float)(yc-ball.y)/(xc-ball.x);
            ball.c=yc-ball.slope*xc;
            xinc=(ball.x>xc)?true:false;
            yinc=(ball.y>yc)?true:false;
            if(ball.x==xc)
                movSt=true;
        }
    
    }
    else if(keyStates['h']==true)
        playerSwitch=true;
    else if(keyStates['a']==true)
        ballAnti=true;
    else if(keyStates['d']==true)
        ballClock=true;
    else if(keyStates['y']==true && (ball.x<0 || ball.x>=610 || ball.y<0 || ball.y>=350) )
        getBackBall=true;
}

void sendKeys(Keys k)
{
    char key[2];
    strcpy(client_send_data,"cmd ");
    strcat(client_send_data,"key ");
    sprintf(key,"%d",k.up);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.left);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.down);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.right);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.c);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.v);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.a);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.d);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",k.y);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    sprintf(key,"%d",playNum);
    strcat(client_send_data,key);
    strcat(client_send_data," ");
    client((char*)destIP,(char*)destPort);
}


void keyOperationsSlave()
{
    Keys keyPress=Keys(0,0,0,0,0,0,0,0,0,0);
    bool flag=false;
    if(specialKeyStates[GLUT_KEY_RIGHT]==true)
    {
        flag=true;
        keyPress.right=1;
        if(specialKeyStates[GLUT_KEY_UP]==true)
            keyPress.up=1;
        else if(specialKeyStates[GLUT_KEY_DOWN]==true)
            keyPress.down=1;
    }
    else if(specialKeyStates[GLUT_KEY_LEFT]==true)
    {
        flag=true;
        keyPress.left=1;
        if(specialKeyStates[GLUT_KEY_UP]==true)
            keyPress.up=1;
        else if(specialKeyStates[GLUT_KEY_DOWN]==true)
            keyPress.down=1;
    }
    else if(specialKeyStates[GLUT_KEY_UP]==true)
    {
        flag=true;
        keyPress.up=1;
        if(specialKeyStates[GLUT_KEY_RIGHT]==true)
            keyPress.right=1;
        else if(specialKeyStates[GLUT_KEY_LEFT]==true)
            keyPress.left=1;
    }
    else if(specialKeyStates[GLUT_KEY_DOWN]==true)
    {
        flag=true;
        keyPress.down=1;
        if(specialKeyStates[GLUT_KEY_RIGHT]==true)
            keyPress.right=1;
        else if(specialKeyStates[GLUT_KEY_LEFT]==true)
            keyPress.left=1;
    }
    else if(keyStates['c']==true)
    {
        flag=true;
        keyPress.c=1;
    }
    else if(keyStates['v']==true)
    {
        flag=true;
        keyPress.v=1;
    }
    else if(keyStates['h']==true)
    {                   //switching is possible only when atleast one is in bot mode
        flag=true;
        if(playNum==1)        //bot players availdable  
            playNum=0;
        else if(playNum==0)
            playNum=1;
        else if(playNum==4)
            playNum=3;
        else if(playNum==3)
            playNum=4;
        keyStates['h']=false;
    }
    else if(keyStates['a']==true)
    {
        flag=true;
        keyPress.a=1;
    }
    else if(keyStates['d']==true)
    {
        flag=true;
        keyPress.d=1;
    }
    else if(keyStates['y']==true)
    {
        flag=true;
        keyPress.y=1;
    }
    if(flag==true)
        sendKeys(keyPress);
}


/*Reformed Dribble for slave*/
void slaveDribble(Keys k)
{
    float gap;
    int playNum=k.active;
    Player active=players[k.active];
    if(active.playerX==ball.x)
    {
        if((ball.y-active.playerY)>=0 && (ball.y-active.playerY)<=10 && k.down==1)      //you can also add >0 and <=10
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (ball.y>=325))
                    {
                        players[playNum].playerY=players[playNum].playerY-2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.y=ball.y+2;
                    }
                }
            }
        }
        else if((active.playerY-ball.y)>=0 && (active.playerY-ball.y)<=10 && k.up==1)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (ball.y<=15))
                    {
                        players[playNum].playerY=players[playNum].playerY+2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.y=ball.y-2;
                    }
                }
            }
        }
    }
    else if(active.playerY==ball.y)
    {
        if((ball.x-active.playerX)>=0 && (ball.x-active.playerX)<=10 && k.right==1)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x>=593) || ball.x>=575)))
                    {
                        players[playNum].playerX=players[playNum].playerX-2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.x=ball.x+2;
                    }
                }
            }
            
        }
        else if((active.playerX-ball.x)>=0 && (active.playerX-ball.x)<=10 && k.left==1)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x<=7) || ball.x<=25)))
                    {
                        players[playNum].playerX=players[playNum].playerX+2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.x=ball.x-2;
                    }
                }
            }
        }
    }
    else
    {
        if((distance(active,ball)<=diagonal))
        {
            if( (ball.y<active.playerY) )
            {
                if(k.up==1)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.y==players[i].playerY) || ball.y<players[i].playerY || gap>=12)) || (ball.y<=15))
                            {
                                players[playNum].playerY=players[playNum].playerY+2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.y=ball.y-2;
                            }
                        }
                    }
                }
                if(ball.x<active.playerX && k.left==1)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX) || gap>=12 || ball.x<players[i].playerX))  || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x<=7) || ball.x<=25)))
                            {
                                players[playNum].playerX=players[playNum].playerX+2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x-2;
                            }
                        }
                    }
                }
                else if(ball.x>active.playerX && k.right==1)
                { 
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX) || gap>=12 || ball.x>players[i].playerX)) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x>=593) || ball.x>=575)))
                            {
                                players[playNum].playerX=players[playNum].playerX-2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x+2;
                            }
                        }
                    }
                }
            }
            else if(ball.y>active.playerY)
            {
                if(k.down==1)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.y==players[i].playerY) || gap>=12 || ball.y>players[i].playerY)) || (ball.y>=325))
                            {
                                players[playNum].playerY=players[playNum].playerY-2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.y=ball.y+2;
                            }
                        }
                    }
                }
                if(ball.x<active.playerX && k.left==1)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX)||gap>=12 || ball.x<players[i].playerX))  || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x<=7) || ball.x<=25)))
                            {
                                players[playNum].playerX=players[playNum].playerX+2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x-2;
                            }
                        }
                    }
                }
                else if(ball.x>active.playerX && k.right==1)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX) || ball.x>players[i].playerX || gap>=12 )) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x>=593) || ball.x>=575)))
                            {
                                players[playNum].playerX=players[playNum].playerX-2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x+2;
                            }
                        }
                    }
                }
            }
        }
    }
}



void dribble(Player active)
{
    float gap;
    if(active.playerX==ball.x)
    {
        if((ball.y-active.playerY)>=0 && (ball.y-active.playerY)<=10 && specialKeyStates[GLUT_KEY_DOWN])      //you can also add >0 and <=10
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (ball.y>=325))
                    {
                        players[playNum].playerY=players[playNum].playerY-2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.y=ball.y+2;
                    }
                }
            }
        }
        else if((active.playerY-ball.y)>=0 && (active.playerY-ball.y)<=10 && specialKeyStates[GLUT_KEY_UP])
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (ball.y<=15))
                    {
                        players[playNum].playerY=players[playNum].playerY+2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.y=ball.y-2;
                    }
                }
            }
        }
    }
    else if(active.playerY==ball.y)
    {
        if((ball.x-active.playerX)>=0 && (ball.x-active.playerX)<=10 && specialKeyStates[GLUT_KEY_RIGHT])
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x>=593) || ball.x>=575)))
                    {
                        players[playNum].playerX=players[playNum].playerX-2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.x=ball.x+2;
                    }
                }
            }
            
        }
        else if((active.playerX-ball.x)>=0 && (active.playerX-ball.x)<=10 && specialKeyStates[GLUT_KEY_LEFT])
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=playNum)
                {
                    if((gap>0 && gap<=15) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x<=7) || ball.x<=25)))
                    {
                        players[playNum].playerX=players[playNum].playerX+2;
                        break;
                    }
                    else if(i==5)
                    {
                        ball.x=ball.x-2;
                    }
                }
            }
        }
    }
    else
    {
        if((distance(active,ball)<=diagonal))
        {
            if( (ball.y<active.playerY) )
            {
                if(specialKeyStates[GLUT_KEY_UP]==true)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.y==players[i].playerY) || ball.y<players[i].playerY || gap>=12)) || (ball.y<=15))
                            {
                                players[playNum].playerY=players[playNum].playerY+2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.y=ball.y-2;
                            }
                        }
                    }
                }
                if(ball.x<active.playerX && specialKeyStates[GLUT_KEY_LEFT]==true)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX) || gap>=12 || ball.x<players[i].playerX))  || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x<=7) || ball.x<=25)))
                            {
                                players[playNum].playerX=players[playNum].playerX+2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x-2;
                            }
                        }
                    }
                }
                else if(ball.x>active.playerX && specialKeyStates[GLUT_KEY_RIGHT]==true)
                { 
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX) || gap>=12 || ball.x>players[i].playerX)) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x>=593) || ball.x>=575)))
                            {
                                players[playNum].playerX=players[playNum].playerX-2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x+2;
                            }
                        }
                    }
                }
            }
            else if(ball.y>active.playerY)
            {
                if(specialKeyStates[GLUT_KEY_DOWN]==true)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.y==players[i].playerY) || gap>=12 || ball.y>players[i].playerY)) || (ball.y>=325))
                            {
                                players[playNum].playerY=players[playNum].playerY-2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.y=ball.y+2;
                            }
                        }
                    }
                }
                if(ball.x<active.playerX && specialKeyStates[GLUT_KEY_LEFT]==true)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX)||gap>=12 || ball.x<players[i].playerX))  || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x<=7) || ball.x<=25)))
                            {
                                players[playNum].playerX=players[playNum].playerX+2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x-2;
                            }
                        }
                    }
                }
                else if(ball.x>active.playerX && specialKeyStates[GLUT_KEY_RIGHT]==true)
                {
                    for(int i=0;i<6;i++)
                    {
                        gap=distance(players[i],ball);
                        if(i!=playNum)
                        {
                            if((gap>0 && gap<=15 && !((ball.x==players[i].playerX) || ball.x>players[i].playerX || gap>=12 )) || (((ball.y>=(ROWS/2)-60+5 && ball.y<=(ROWS/2)+60-5 && ball.x>=593) || ball.x>=575)))
                            {
                                players[playNum].playerX=players[playNum].playerX-2;
                                break;
                            }
                            else if(i==5)
                            {
                                ball.x=ball.x+2;
                            }
                        }
                    }
                }
            }
        }
    }
}

void printKeys(Keys k)
{
    cout<<"\nPrint Keys are : "<<k.up<<"   "<<k.left<<"   "<<k.down<<"  "<<k.right<<"  "<<k.c<<"   "<<k.v<<"   "<<k.a<<"   "<<k.d<<"   "<<k.y<<"   "<<k.active<<endl;
}


void keyApply(Keys k)
{
    int f,i;
    int index=k.active;
    bool redisplay=false;
    float gap;
    printKeys(k);
    if((distance(players[index],ball)<=diagonal))
        f=2;
    else if(index==1 || index==4)
        f=2;
    else
        f=3;   
    if(k.right==1)
    {
        redisplay=true;      //used for debugging
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[index],players[i]);
            if(i!=index)
            {
                if(players[index].playerY==players[i].playerY && gap<=20 && (players[index].playerX<players[i].playerX))
                {
                    break;
                }
                else if(gap>0 && gap<20 && (players[index].playerX<players[i].playerX) && !(gap>=15))
                {
                    break;
                }
                else if(i==5)
                {
                    players[index].playerX=players[index].playerX+f;
                    if(k.up==1)
                        players[index].playerY=players[index].playerY-f;
                    else if(k.down==1)
                        players[index].playerY=players[index].playerY+f;
                }
            }
        }
    }
    else if(k.left==1)
    {
        redisplay=true;      //used for debugging
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[i],players[index]);
            if(i!=index)
            {
                if(players[index].playerY==players[i].playerY && gap<=20 && (players[index].playerX>players[i].playerX))
                    break;
                else if(gap>0 && gap<20 && (players[index].playerX>players[i].playerX) && !(gap>=15))
                    break;
                else if(i==5)
                {
                    players[index].playerX=players[index].playerX-f;
                     if(k.up==1)
                        players[index].playerY=players[index].playerY-f;
                    else if(k.down==1)
                        players[index].playerY=players[index].playerY+f;
                }
            }
        }
    }
    else if(k.up==1)
    {
        redisplay=true;      //used for debugging
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[index],players[i]);
            if(i!=index)
            {
                if(players[index].playerX==players[i].playerX && gap<=20 && (players[index].playerY>players[i].playerY))
                    break;
                else if(gap>0 && gap<20 && (players[index].playerY>players[i].playerY) && !(gap>=15) )
                    break;
                else if(i==5)
                {
                    players[index].playerY=players[index].playerY-f;
                    if(k.left==1)
                        players[index].playerX=players[index].playerX-f;
                    else if(k.right==1)
                        players[index].playerX=players[index].playerX+f;
                }
            }
        }  
    }
    else if(k.down==1)
    {
        redisplay=true;      //used for debugging
        for(i=0;i<6;i++)
        {
            gap=playdistance(players[index],players[i]);
            if(i!=index)
            {
                if(players[index].playerX == players[i].playerX && gap<=20 && (players[index].playerY<players[i].playerY))
                    break;
                else if(gap>0 && gap<20 && (players[index].playerY<players[i].playerY) && !(gap>=15))
                    break;
                else if(i==5)
                {
                    players[index].playerY=players[index].playerY+f;
                    if(k.left==1)
                        players[index].playerX=players[index].playerX-f;
                    else if(k.right==1)
                        players[index].playerX=players[index].playerX+f;
                }
            }
        }
    }
    else if(k.c==1)
    {
        redisplay=true;      //used for debugging
        float xc=players[index].playerX,yc=players[index].playerY;
        if((ball.x-xc)<22 && (ball.x-xc)>-22)
        {    
            ball.u=7.0;
            pass=true;
            S=1.0;
            t=1;
            diff=(float)(ball.x-xc);
            ball.slope=(float)(yc-ball.y)/(xc-ball.x);
            ball.c=yc-ball.slope*xc;
        }
    }
    else if(k.v==1)
    {
        redisplay=true;      //used for debugging
        float xc=players[index].playerX,yc=players[index].playerY;
        if((ball.x-xc)<22 && (ball.x-xc)>-22)
        {    
            ball.u=10.0;
            shoot=true;
            S=1.0;
            t=1;
            diff=(float)(ball.x-xc);
            ball.slope=(float)(yc-ball.y)/(xc-ball.x);
            ball.c=yc-ball.slope*xc;
        }
    }
}


/*Networking Starts Here*/

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

int indexOf(char* string, char of) {
    int len = strlen(string);
    for (int i = 0; i < len; i++) {
        if (string[i] == of) {
            return i;
        }
    }
    return len - 1;
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


bool connectToServer(int &sock,char* destI,char* destP) {
    struct hostent *host;
    struct sockaddr_in server_addr;

    char hostname[128];
    gethostname(hostname, 128);
    host = gethostbyname(hostname);

    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket");
        exit(1);
    }
    int dPort = atoi(destP);
    int q=inet_aton(destI, &server_addr.sin_addr);
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(dPort);

    bzero(&(server_addr.sin_zero), 8);

    int retriedCount = 0;
    while (connect(sock, (struct sockaddr *) &server_addr,
            sizeof(struct sockaddr)) == -1) 
    {
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


void client(char* destI,char* destP)
{
    int sock,bytes_received;

    if(!connectToServer(sock,destI,destP))
    {
        client_recv_data[0] = SERVER_BUSY;
        cout<<"Retuning...";
        return;
    }   
    send(sock, client_send_data, strlen(client_send_data), 0);

    close(sock);
}

int countSpace(char* string)
{
    int count=0;
    int len = strlen(string);
    for (int i = 0; i < len; i++) {
        if (string[i] == ' ') {
            count++;
        }
    }
    return count;
}

void addPlayerToNetwork(char* data)
{
    int i;
    Network networkBot=Network(data,10);
    Slave.push_back(networkBot);
    strcpy(client_send_data,(char*)"cmd ");
    strcat(client_send_data,(char*)"play ");
    for(i=0;i<Slave.size();i++)
    {
        if(Slave[i].activePlayer==0)
            break;
    }
    if(i==Slave.size())
        strcat(client_send_data,(char*)"0.TEAM-A_Agile ");
    for(i=0;i<Slave.size();i++)
    {
        if(Slave[i].activePlayer==1)
            break;
    }
    if(i==Slave.size())
        strcat(client_send_data,(char*)"1.TEAM-A_Strong ");
    for(i=0;i<Slave.size();i++)
    {
        if(Slave[i].activePlayer==3)
            break;
    }
    if(i==Slave.size())
        strcat(client_send_data,(char*)"3.TEAM-B_Agile ");
    for(i=0;i<Slave.size();i++)
    {
        if(Slave[i].activePlayer==4)
            break;
    }
    if(i==Slave.size())
        strcat(client_send_data,(char*)"4.TEAM-B_Strong ");
    destIP=substring(data,0,indexOf(data,':'));
    destPort=substring(data,indexOf(data,':')+1,strlen(data)-indexOf(data,':')-1);
    client((char*)destIP,(char*)destPort);
}

void initialPlayerSetup()
{
    cout<<"\n------Select from the available Players------"<<endl;
    cout<<"0.TEAM-A_Agile"<<endl;
    cout<<"1.TEAM-A_Strong"<<endl;
    cout<<"3.TEAM-B_Agile"<<endl;
    cout<<"4.TEAM-B_Strong\n"<<endl;
    cin>>playNum;
}

void availablePlayers(char* data)
{
    char helperdata[100],printdata[20],helperPlay[2];
    strcpy(helperdata,data);
    int bound=countSpace(data);
    cout<<"\n------Select from the available players------"<<endl;
    for(int i=0;i<bound;i++)
    {
        strcpy(printdata,substring(helperdata,0,indexOf(helperdata,' ')));
        strcpy(helperdata,substring(helperdata,indexOf(helperdata,' ')+1,strlen(helperdata)-indexOf(helperdata,' ')-1));
        cout<<printdata<<endl;
    }
    cout<<"Enter your choice:"<<endl;
    cin>>playNum;
    destIP=substring(masterIpPort,0,indexOf(masterIpPort,':'));
    destPort=substring(masterIpPort,indexOf(masterIpPort,':')+1,strlen(masterIpPort)-indexOf(masterIpPort,':')-1);
    strcpy(client_send_data,"cmd ");
    strcat(client_send_data,"sel ");
    strcat(client_send_data,getSelfIpPort());
    strcat(client_send_data," ");
    sprintf(helperPlay,"%d",playNum);
    strcat(client_send_data,helperPlay);
    client((char*)destIP,(char*)destPort);
}

void selectedPlayer(char* data)
{
    int ind;
    char* ip_port=substring(data,0,indexOf(data,' '));
    char* play = substring(data,indexOf(data,' ')+1,strlen(data)-indexOf(data,' ')-1);
    for(int i=0;i<Slave.size();i++)
    {
        if(strcmp(Slave[i].server_ip_port,ip_port)==0)
            {
                ind=i;
                break;
            }
    }
    Slave[ind].activePlayer=atoi(play);
}

void storeKeys(char* data)
{
    Keys k=Keys(0,0,0,0,0,0,0,0,0,0);
    char* key[10];         //here if we add extra key we need to change 6 as 7
    cout<<"\n\nStored Keys:" <<data<<endl;
    cout<<"Keys : ";
    for(int i=0;i<10;i++)
    {
        key[i]=substring(data,0,indexOf(data,' '));
        printf(" %s",key[i]);
        data=substring(data,indexOf(data,' ')+1,strlen(data)-indexOf(data,' ')-1);
    }
    printf("\n");
    k.up=atoi(key[0]);
    k.left=atoi(key[1]);
    k.down=atoi(key[2]);
    k.right=atoi(key[3]);
    k.c=atoi(key[4]);
    k.v=atoi(key[5]);
    k.a=atoi(key[6]);
    k.d=atoi(key[7]);
    k.y=atoi(key[8]);
    k.active=atoi(key[9]);
    printf("KEYS : %d %d %d %d %d %d %d %d %d %d ",k.up,k.left,k.down,k.right,k.c,k.v,k.a,k.d,k.y,k.active);
    pthread_mutex_lock(&threadLock);
    slaveKeys.push_back(k);
    pthread_mutex_unlock(&threadLock);
}





void updateCoordinates(char* data)
{
    int index;
    char helperUpdate[80];
    char helperPoint[7];
    char helpX[3],helpY[3];
    strcpy(helperUpdate,data);
    for(int i=0;i<6;i++)                                                // Loop updates all the coordinates
    {
        index=indexOf(helperUpdate,' ');
        strcpy(helperPoint,substring(helperUpdate,0,index));
        strcpy(helpX,substring(helperPoint,0,indexOf(helperPoint,',')));
        strcpy(helpY,substring(helperPoint,indexOf(helperPoint,',')+1,strlen(helperPoint)-indexOf(helperPoint,',')-1));
        players[i].playerX=atoi(helpX);
        players[i].playerY=atoi(helpY);
        strcpy(helperUpdate,substring(helperUpdate,index+1,strlen(helperUpdate)-index-1));
    }
    index=indexOf(helperUpdate,' ');
    strcpy(helperPoint,substring(helperUpdate,0,index));                                            //updating BallX
    strcpy(helpX,substring(helperPoint,0,indexOf(helperPoint,',')));                                //updating BallY
    strcpy(helpY,substring(helperPoint,indexOf(helperPoint,',')+1,strlen(helperPoint)-indexOf(helperPoint,',')-1));
    ball.x=atoi(helpX);
    ball.y=atoi(helpY);
    strcpy(helperUpdate,substring(helperUpdate,index+1,strlen(helperUpdate)-index-1));
    index=indexOf(helperUpdate,' ');
    strcpy(helperPoint,substring(helperUpdate,0,index));                                             //updating goalA
    strcpy(helpX,substring(helperPoint,0,indexOf(helperPoint,',')));                                //updating goalB
    strcpy(helpY,substring(helperPoint,indexOf(helperPoint,',')+1,strlen(helperPoint)-indexOf(helperPoint,',')-1));
    if(atoi(helpX)>goalA || atoi(helpY)>goalB)
    {
        goal=true;
        goalA=atoi(helpX);
        goalB=atoi(helpY);
    }
    strcpy(helperUpdate,substring(helperUpdate,index+1,strlen(helperUpdate)-index-1));
    index=indexOf(helperUpdate,' ');
    strcpy(helperPoint,substring(helperUpdate,0,index));                                             //updating goalA
    strcpy(helpX,substring(helperPoint,0,indexOf(helperPoint,',')));
    strcpy(helpY,substring(helperPoint,indexOf(helperPoint,',')+1,strlen(helperPoint)-indexOf(helperPoint,',')-1));
    if(atoi(helpX)>mins || (atoi(helpX)==mins && atoi(helpY)>sec))
        goal=false; 
    mins=atoi(helpX);
    sec=atoi(helpY);
    strcpy(helperPoint,substring(helperUpdate,index+1,strlen(helperUpdate)-index-1));
    strcpy(helpX,substring(helperPoint,0,indexOf(helperPoint,',')));
    strcpy(helpY,substring(helperPoint,indexOf(helperPoint,',')+1,strlen(helperPoint)-indexOf(helperPoint,',')-1));
    if(atoi(helpX)==0)
        gameStart=true;
    else
        gameStart=false;
    beginSecs=atoi(helpY);
}

void sendCoordinates()
{
    int gStart;
    char helpX[3],helpY[3],ipport[22],destI[16],destP[4];
    strcpy(client_send_data,"cmd ");
    strcat(client_send_data,"cod ");
    for(int i=0;i<6;i++)                    // 6 as we have only 6 players  all coordinates are sent in this loop itself
    {
        sprintf(helpX,"%d",players[i].playerX);
        sprintf(helpY,"%d",players[i].playerY);
        strcat(client_send_data,helpX);
        strcat(client_send_data,",");
        strcat(client_send_data,helpY);
        strcat(client_send_data," ");
    }
    sprintf(helpX,"%d",(int)ball.x);
    sprintf(helpY,"%d",(int)ball.y);
    strcat(client_send_data,helpX);         //BallX is sent
    strcat(client_send_data,",");
    strcat(client_send_data,helpY);         //BallY is sent
    strcat(client_send_data," ");
    sprintf(helpX,"%d",goalA);
    sprintf(helpY,"%d",goalB);
    strcat(client_send_data,helpX);         //goalA is sent
    strcat(client_send_data,",");
    strcat(client_send_data,helpY);         //goalB is sent
    strcat(client_send_data," ");
    sprintf(helpX,"%d",mins);
    sprintf(helpY,"%d",sec);
    strcat(client_send_data,helpX);            //time is sent
    strcat(client_send_data,",");
    strcat(client_send_data,helpY);
    strcat(client_send_data," ");
    if(gameStart==true)
        gStart=0;
    else 
        gStart=1;
    sprintf(helpX,"%d",gStart);                 //begin screen and secs
    sprintf(helpY,"%d",beginSecs);
    strcat(client_send_data,helpX);
    strcat(client_send_data,",");
    strcat(client_send_data,helpY);
    strcat(client_send_data," ");
    for(int i=0;i<Slave.size();i++)
    {
        strcpy(ipport,Slave[i].server_ip_port);             //when more comes u need to chane as loop condition
        strcpy(destI,substring(ipport,0,indexOf(ipport,':')));
        strcpy(destP,substring(ipport,indexOf(ipport,':')+1,strlen(ipport)-indexOf(ipport,':')-1));
        client((char*)destI,(char*)destP);
    }
}

void helperSelf()
{
    cout<<endl;
    cout<<"IPPORT:"<<getSelfIpPort()<<"       ACTIVE PLAYNUM:"<<playNum<<endl;
}

void fillNetworkBot()
{
    initialPlayerSetup();
    Network bot=Network(getSelfIpPort(),playNum);
    Slave.push_back(bot);
    helperSelf();
    
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
        fillNetworkBot();
    }   
}

void helperPort(char* cmd)
{
    sPort = cmd;
    port = atoi(sPort);
}

void helperJoin(char* cmdData)
{
    char* Data = substring(cmdData, 0, strlen(cmdData));
    strcpy(first_ipPort,Data);
    first_ipPort[strlen(Data)-1]='\0';
    destIP = substring(Data, 0, indexOf(Data, ':'));
    destPort = substring(Data, indexOf(Data, ':')+1, strlen(Data)-indexOf(Data, ':'));
    if(isCreated || hasJoined)
    {
        cout<<"This Node already in some other network....Give some other port this time."<<endl;
        return;
    }
    else
    {
        pthread_t sid;
        pthread_create(&sid,NULL,server,NULL);
        char msg_type[10] ;
        strcpy(msg_type,"cmd ");
        char* ipPort = getSelfIpPort();
        strcat(msg_type,"join ");
        strcpy(client_send_data,msg_type);
        strcat(client_send_data, ipPort);
        client((char*)destIP,(char*)destPort);

    }   
}

void runCommand(char* cmd)
{
    char* fcmd=substring(cmd,4,strlen(cmd)-4);
    char* command=substring(fcmd,0,indexOf(fcmd,' '));
    char* Data = substring(fcmd,indexOf(fcmd,' ')+1,strlen(fcmd)-indexOf(fcmd,' '));
    if(strcmp(command,"join")==0)
        addPlayerToNetwork(Data);
    if(strcmp(command,"play")==0)
        availablePlayers(Data);
    if(strcmp(command,"sel")==0)
        selectedPlayer(Data);
    if(strcmp(command,"key")==0)
        storeKeys(Data);
    if(strcmp(command,"cod")==0)
        updateCoordinates(Data);
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
    if (::bind(sock, (struct sockaddr *) ((&server_addr)),
            sizeof(struct sockaddr)) == -1) {
        perror("Unable to bind. Check if you have entered a valid port number.");
        exit(1);
    }
    if (listen(sock, 5) == -1) {        //manage no of clients to be cnctd
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

        if(strcmp(type,"cmd")==0)
            runCommand(server_recv_data);

        fflush(stdout);
        close(connected);
        isCreated=true; 
        hasJoined=true; 

    }
    close(sock);

    return NULL;
}

void drawBitmapText(char *string,float x,float y)
{ 
    char *c; 
    glRasterPos2f(x, y); 
    int l=0; 
    for (c=string; (l<strlen(string)); c++,l++) 
        { 
            glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, *c);
        } 
}

void drawBitmapGoal(char *string,float x,float y)
{ 
    char *c; 
    glRasterPos2f(x, y); 
    int l=0; 
    for (c=string; (l<strlen(string)); c++,l++) 
        { 
            glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_24, *c);
        } 
}

void drawBitmapNum(char *string,float x,float y)
{ 
    char *c; 
    glRasterPos2f(x, y); 
    int l=0; 
    for (c=string; (l<strlen(string)); c++,l++) 
        { 
            glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_24, *c);
        } 
}

void displayGoal()
{
    sprintf(goalNumA,"%d",goalA);
    sprintf(goalNumB,"%d",goalB);
    strcpy(goalDisp,goalNumA);
    strcat(goalDisp," : ");
    strcat(goalDisp,goalNumB);
    glColor3f(1.0f,1.0f,1.0f);
    drawBitmapText((char*)goalDisp,270.0,7.0);
    if(goal)
    {
        glColor3f(0.0,0.0,0.0);
        drawBitmapGoal((char*)"GOALLL !!!",280,80);
    }
}

void displayGameStarts()
{
    char help[3];
    int secs;
    glColor3f(0.0,0.0,0.0);
    drawBitmapGoal((char*)"GAME BEGINS",265,80);
    drawBitmapGoal((char*)"IN ",295,95);
    if(masterFlag==true )
    {
        beginSecs=(gameTime-clock()/CLOCKS_PER_SEC);
    }   
    sprintf(help,"%d",beginSecs);
    drawBitmapGoal((char*)help,295,110);
    if(beginSecs==0)
    {
        backToPosition();
        gameStart=false;
    }
}

void displayTime()
{
    int d;
    if(masterFlag==true  && halfTime==true)
    {
        d=(clock()/CLOCKS_PER_SEC-beforeTime);
        mins=d/60;
        if(d<60)
            sec=d;
        else
            sec=d%60;
    }   
    else if(masterFlag==true && halfTime==false)
    {
        cout<<"Heyyy"<<endl;
        d=(afterTime -(clock()/CLOCKS_PER_SEC));
        mins=d/60;
        sec=d%60;
    }
    if(halfTime==true && resetHalf==false && masterFlag==true)
    {
        //cout<<"Hello"<<endl;
        mins=Time;
        sec=0;
    }
    sprintf(dispMin,"%d",mins);
    sprintf(dispSec,"%d",sec);
    strcpy(countDown,dispMin);
    strcat(countDown,":");
    strcat(countDown,dispSec);
    if((mins>=Time-2 && halfTime==true)||(mins<=2 && halfTime==false))
        glColor3f(1.0f,0.0f,0.0f);
    else
        glColor3f(1.0,1.0,1.0);
    drawBitmapText((char*)countDown,310.0,7.0);
    if(mins==0 && sec==0 && halfTime==false)
    {
        if(goal && masterFlag==true)
        {
            sec=2;
            return;
        }
        glColor3f(0.0,0.0,0.0);
        if(goalA==goalB)
            drawBitmapGoal((char*)"MATCH DRAWN!!",260,80);
        else if(goalA>goalB)
            drawBitmapGoal((char*)"TEAM-A WINS!!!",260,80);
        else
            drawBitmapGoal((char*)"TEAM-B WINS!!!",260,80);
    }
    if(mins==Time && sec==0 && halfTime==true)
    {
        if(halfTime==true && resetHalf==true)
        {
            resetHalf=false;
            movPlayers=false;
            backHalfTime=clock()/CLOCKS_PER_SEC;
        }
        glColor3f(0.0,0.0,0.0);
        drawBitmapGoal((char*)"HALF-TIME!!!",260,100);
    }
}

void backToPosition()
{       
    players[0].update(255,185);
    players[1].update(185,125);
    players[2].update(40,170);
    players[3].update(345,155);
    players[4].update(395,205);
    players[5].update(560,170);
    ball.x=300;
    ball.y=170;
}

void* playCrowd(void *)
{
    if((clock()/CLOCKS_PER_SEC)-crowdPlay<=5)
        system("canberra-gtk-play -f applause.wav -l 0");
}

void display(void)
{
    int d;
    glClear (GL_COLOR_BUFFER_BIT);
    glLoadIdentity ();
    gluLookAt (0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    generate_map();
    if(gameStart==false)
    {
        glColor3ub(255,255,255);
        DrawCircle(ball.x,ball.y,5,20);
        glColor3ub(0,180,255);
        DrawCircle(players[0].playerX,players[0].playerY,10,20);
        glColor3f(1.0,0.0,0.0);
        drawBitmapNum((char*)"A0",(float)players[0].playerX-6,(float)players[0].playerY+3);
        glColor3ub(0,180,255);
        DrawCircle(players[1].playerX,players[1].playerY,10,20);
        glColor3f(1.0,0.0,0.0);
        drawBitmapNum((char*)"A1",(float)players[1].playerX-6,(float)players[1].playerY+3);
        glColor3ub(0,180,255);
        DrawCircle(players[2].playerX,players[2].playerY,10,20);
        glColor3f(1.0,0.0,0.0);
        drawBitmapNum((char*)"AG",(float)players[2].playerX-6,(float)players[2].playerY+3);
        glColor3ub(225,225,0);
        DrawCircle(players[3].playerX,players[3].playerY,10,20);
        glColor3f(1.0,0.0,0.0);
        drawBitmapNum((char*)"B3",(float)players[3].playerX-6,(float)players[3].playerY+3);
        glColor3ub(225,225,0);
        DrawCircle(players[4].playerX,players[4].playerY,10,20);
        glColor3f(1.0,0.0,0.0);
        drawBitmapNum((char*)"B4",(float)players[4].playerX-6,(float)players[4].playerY+3);
        glColor3ub(225,225,0);
        DrawCircle(players[5].playerX,players[5].playerY,10,20);
        glColor3f(1.0,0.0,0.0);
        drawBitmapNum((char*)"BG",(float)players[5].playerX-6,(float)players[5].playerY+3);
        displayGoal();
        displayTime();
        if(goal)
        {
            if((clock()/CLOCKS_PER_SEC-backTime)==5 )
            {
                resetgoal=false;
                backToPosition();
                beforeTime=beforeTime+5;
                afterTime=afterTime+5;
                resetgoal=false;
                goal=false;
            }
        }
        if(resetHalf==false)
        {
            if((clock()/CLOCKS_PER_SEC-backHalfTime)==5)
            {
                resetHalf=true;
                afterTime=afterTime+5;
                movPlayers=true;
                backToPosition();
                halfTime=false;
        
            }
        }

        glutSwapBuffers();
        if(mins==0 && sec==0  && halfTime==false)
        {
            sendCoordinates();
            sleep(10);
        }
    }
    else if(gameStart==true)
    {
        displayGameStarts();
    }
}


void ballShift(int active)
{
    int tr=15;          //add of radii
    int shift=roundUp(15/(sqrt(2)));
    float gap;
    if(distance(players[active],ball)<=18)
    {
        if(absolute(ball.y-players[active].playerY)>=0 && absolute(ball.y-players[active].playerY)<=3)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=active)
                {
                    if((gap>=0 && gap<=14) || ((ball.y>=325 || ball.y<=15) || (ball.x>575 || ball.x<35)))
                        break;
                    else if(i==5 )
                    {
                        if(ball.x>players[active].playerX)
                        {
                            ball.x=ball.x-(tr-shift);
                            ball.y=ball.y+shift;
                        }
                        else if(ball.x<players[active].playerX)
                        {
                            ball.x=ball.x+(tr-shift);
                            ball.y=ball.y-(shift);
                        }
                    }
                }
            } 
        }
        else if(absolute(ball.x-players[active].playerX)>=0 && absolute(ball.x-players[active].playerX)<=3)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=active)
                {
                    if((gap>=0 && gap<=14) || ((ball.y>=325 || ball.y<=15 ) || (ball.x>575 || ball.x<25 )))
                        break;
                    else if(i==5)
                    {
                        if(ball.y>players[active].playerY)
                        {
                            ball.x=ball.x-shift;
                            ball.y=ball.y-(tr-shift);
                        }
                        else if(ball.y<players[active].playerY)
                        {
                            ball.x=ball.x+shift;
                            ball.y=ball.y+(tr-shift);
                        }
                    }
                }
            }
        }
        else
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=active)
                {
                    if((gap>=0 && gap<=14) || ((ball.y>=325 || ball.y<=15 ) || (ball.x>575 || ball.x<25 )))
                        break;
                    else if(i==5)
                    {
                        if(ball.x<players[active].playerX)
                        {
                            if(ball.y<players[active].playerY)
                            {
                                ball.x=players[active].playerX;
                                ball.y=players[active].playerY-tr;
                            }
                            else
                            {
                                ball.x=players[active].playerX-tr;
                                ball.y=players[active].playerY;
                            }
                        }
                        else
                        {
                            if(ball.y<players[active].playerY)
                            {
                                ball.x=players[active].playerX+tr;
                                ball.y=players[active].playerY;
                            }
                            else
                            {
                                ball.x=players[active].playerX;
                                ball.y=players[active].playerY+tr;
                            }
                        }
                    }
                }
            }
        }
       
    }
}


void ballShiftAnti(int active)
{
    int tr=15;          //add of radii
    int shift=roundUp(15/(sqrt(2)));
    float gap;
    if(distance(players[active],ball)<=18)
    {
        if(absolute(ball.y-players[active].playerY)>=0 && absolute(ball.y-players[active].playerY)<=3)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=active)
                {
                    if((gap>=0 && gap<=14) || ((ball.y>=325 || ball.y<=15) || (ball.x>575 || ball.x<35)))
                        break;
                    else if(i==5 )
                    {
                        if(ball.x>players[active].playerX)
                        {
                            ball.x=ball.x-(tr-shift);
                            ball.y=ball.y-shift;
                        }
                        else if(ball.x<players[active].playerX)
                        {
                            ball.x=ball.x+(tr-shift);
                            ball.y=ball.y+(shift);
                        }
                    }
                }
            } 
        }
        else if(absolute(ball.x-players[active].playerX)>=0 && absolute(ball.x-players[active].playerX)<=3)
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=active)
                {
                    if((gap>=0 && gap<=14) || ((ball.y>=325 || ball.y<=15 ) || (ball.x>575 || ball.x<25 )))
                        break;
                    else if(i==5)
                    {
                        if(ball.y>players[active].playerY)
                        {
                            ball.x=ball.x+shift;
                            ball.y=ball.y-(tr-shift);
                        }
                        else if(ball.y<players[active].playerY)
                        {
                            ball.x=ball.x-shift;
                            ball.y=ball.y+(tr-shift);
                        }
                    }
                }
            }
        }
        else
        {
            for(int i=0;i<6;i++)
            {
                gap=distance(players[i],ball);
                if(i!=active)
                {
                    if((gap>=0 && gap<=14) || ((ball.y>=325 || ball.y<=15 ) || (ball.x>575 || ball.x<25 )))
                        break;
                    else if(i==5)
                    {
                        if(ball.x<players[active].playerX)
                        {
                            if(ball.y<players[active].playerY)
                            {
                                ball.x=players[active].playerX-tr;
                                ball.y=players[active].playerY;
                            }
                            else
                            {
                                ball.x=players[active].playerX;
                                ball.y=players[active].playerY+tr;
                            }
                        }
                        else
                        {
                            if(ball.y<players[active].playerY)
                            {
                                ball.x=players[active].playerX;
                                ball.y=players[active].playerY-tr;
                            }
                            else
                            {
                                ball.x=players[active].playerX+tr;
                                ball.y=players[active].playerY;
                            }
                        }
                    }
                }
            }
        }
   
    }
}




void ballRoll(int active)
{
    if(S>0.1 && (shoot==true || pass==true) && keyStates['v']==false)
        {     
            float S1=ball.u*t-0.1*t*t;
            float S2=ball.u*(t-1)-0.1*(t-1)*(t-1);
            S=S1-S2;
            ball.moveBall(S,active);
            keyPower=0;
            t++;
        }

        if(S<0.1)
        {
            t=0;    
            shoot=false;
            pass=false;
            ball.x=roundUp(ball.x);
            ball.y=roundUp(ball.y);
            hitbound=false;
        }
}

void processDisplay()  
{
    if(masterFlag==false && gameStart==false)
        keyOperationsSlave();
    else if(gameStart==false)
    {
        keyOperations(playNum);
        dribble(players[playNum]);
        ballRoll(playNum);
        play[playNum]=true;
        if(ballAnti==true)
        {
            ballShiftAnti(playNum);
            ballAnti=false;
        }
        if(ballClock==true)
        {
            ballShift(playNum);
            ballClock=false;
        }
        if(playerSwitch==true)
        {
            keyStates['h']=false;
            playerSwitch=false;
            play[playNum]=false;
            if(playNum==0)
                playNum=1;
            else if(playNum==1)
                playNum=0;
            else if(playNum==3)
                playNum=4;
            else if(playNum==4)
                playNum=3;
            play[playNum]=true;
        }
        if(getBackBall==true)
        {
            keyStates['y']=false;
            getBackBall=false;
            ball.x=COLS/2;
            ball.y=ROWS/2;
        }
        if(modeChoice==2)          
        {
            for(int i=0;i<Slave.size();i++)
            {
                play[Slave[i].activePlayer]=true;
            }
            pthread_mutex_lock(&threadLock);
            for(int i=0;i<slaveKeys.size();i++)
            {
                Keys help=slaveKeys[0];
                printKeys(help);
                slaveKeys.pop_back();
                keyApply(help);
                slaveDribble(help);
                ballRoll(help.active);
                if(help.a==1)
                    ballShiftAnti(help.active);
                if(help.d==1)
                    ballShift(help.active);
                if(help.y==1 && (ball.x<0 || ball.x>=610 || ball.y<0 || ball.y>=350))
                {
                    ball.x=COLS/2;
                    ball.y=ROWS/2;
                }
            }
            pthread_mutex_unlock(&threadLock);
        }

        determineWhoHasBall();
        for(int i=0;i<6;i++)
        {
            if(play[i]==false && movPlayers)
            {
                if(i==0)
                    players[0].botPlayer0Movement();
                else if(i==1)
                    players[1].botPlayer1Movement();
                else if(i==2)
                    players[2].goalKeeper1Movement();
                else if(i==3)
                    players[3].botPlayer3Movement();
                else if(i==4)
                    players[4].botPlayer4Movement();
                else if(i==5)
                    players[5].goalKeeper2Movement();
            }
        }
        if(goal==false)
            movPlayers=true;
        if((ball.x>20 && ball.y<580 && ball.y>10 && ball.y<330)==false)
        {
            ball.y=170.0;
            ball.x=300.0;
        }    
        for(int i=0;i<6;i++)
            if(players[i].playerX<0 && players[i].playerX>600 && players[i].playerY<0 && players[i].playerY>340)
            {
                backToPosition();
            }
        if(modeChoice==2)
            sendCoordinates();
        for(int i=0;i<6;i++)
            play[i]=false;
    }
    else if(modeChoice==2 && gameStart==true  )
        sendCoordinates();

    glutPostRedisplay();
}


void specialKeyPress(int key,int x,int y)
{
    specialKeyStates[key]=true;
}

void specialKeyUp(int key, int x,int y)
{
    specialKeyStates[key]=false;
}

void keyPress(unsigned char key,int x,int y )
{
    if(key=='v')
    {
        int xc=players[playNum].playerX,yc=players[playNum].playerY;
        if((ball.x-xc)<22 && (ball.x-xc)>-22)
            keyPower++;
    }
    keyStates[key]=true;
}

void keyUp(unsigned char key,int x,int y)
{
    keyStates[key]=false;
}

void initialize()
{
    players[0].speed=3;
    players[1].speed=2;
    players[2].speed=5;
    players[3].speed=3;
    players[4].speed=2;
    if(modeChoice==2)
        players[5].speed=5;
    else
        players[5].speed=1;
}

int main(int argc, char** argv)
{
    float dist;
    int choice;
    int networkChoice;
    char join_data[25];
    char port_data[10];
    cout<<"\n\nWelcome to Street FootBall"<<endl;
    cout<<"~~~~~~~~~~~~~~~~~~~~~~~~~~~"<<endl;
    cout<<"\nSelect a Mode "<<endl;
    cout<<"\n 1.Single Player Mode"<<endl;
    cout<<"\n 2.MultiPlayer Mode"<<endl;
    cout<<"\n Enter Your Choice: ";
    cin>>modeChoice;
    if(modeChoice==2)
    {
        cout<<"\nEnter only the Port to listen(<num>): ";
        cin>>port_data;
        helperPort(port_data);
        halfTime=true;
        cout<<"\n Select an Option "<<endl;
        cout<<"\n 1.Create a Network"<<endl;     
        cout<<"\n 2.Join already Created Network: ";
        cin>>networkChoice;
        if(networkChoice==1)            
        {
            cout<<"\nCreating Server..."<<endl;
            helperCreate();
            cout<<"Enter the Duration of HalfTime in Mins..."<<endl;
            cin>>Time;
            afterTime=2*60*Time;
            cout<<"\n\nWait for all intereseted players to connect...."<<endl;
            cout<<"Press any key to continue after all have joined..!!\n"<<endl;
            cin>>choice;
            masterFlag=true;
            gameTime=20;
            beforeTime=beforeTime+20;
            afterTime=afterTime+20;
            gameStart=true;
        }
        else
        {               
            cout<<"\nEnter <ip:port> of Network to be joined: ";
            cin>>join_data;
            strcpy(masterIpPort,join_data);
            helperJoin(join_data);
            masterFlag=false;
            sleep(2);
            cout<<"\nPress any key to continue after all have joined..!!\n"<<endl;
            cin>>choice;
            //prevPlayNum=playNum;
        }
    }
    else
    {
        cout<<"Enter the Half Time Duration in Mins..."<<endl;
        cin>>Time;
        afterTime=2*60*Time;
        halfTime=true;
        initialPlayerSetup();
        gameTime=5;
        beforeTime=beforeTime+5;
        afterTime=afterTime+5;
        gameStart=true;
        masterFlag=true;
    }   
    backToPosition();
    initialize();
    glutInit(&argc, argv);
    glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB);
    glutInitWindowPosition (0, 0);   
    glutCreateWindow (argv[0]);
    glutFullScreen();
    init (); 
    
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutKeyboardFunc(keyPress);
    glutKeyboardUpFunc(keyUp);
    glutSpecialFunc(specialKeyPress);
    glutSpecialUpFunc(specialKeyUp);
    glutIdleFunc(processDisplay);
    glutMainLoop();
    return 0;
}