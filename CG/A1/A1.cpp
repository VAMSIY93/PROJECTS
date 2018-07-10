//g++ A1.cpp -std=c++11 -lglut -lGLU -lGL -o A1

#include <GL/glut.h>
#include <iostream>
#include <stdio.h>
#include <math.h>
#include <fstream>
#include <unistd.h>

#define ROWS 270
#define COLS 480

float vertices1[24][3];
float vertices2[10][3];

using namespace std;

void init(void)
{
	glClearColor(0.0, 0.0, 0.0, 0.0);
	glShadeModel(GL_FLAT);
}

void reshape(int w, int h)
{
	glViewport(0, 0, (GLsizei) w, (GLsizei) h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glFrustum(-1.0, 1.0, -1.0, 1.0, 1.5, 20.0);
	glMatrixMode(GL_MODELVIEW);
}

void display() 
{
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
    glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer (background)

    glLoadIdentity();
    gluLookAt(0.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    for(int i=0;i<21;i=i+4)
    {
    glBegin(GL_LINE_LOOP);              // Each set of 4 vertices form a quad
      glColor3f(1.0f, 1.0f, 1.0f);
      for(int j=0;j<4;j++)
        glVertex3d(vertices1[i+j][0],vertices1[i+j][1],vertices1[i+j][2]);
    glEnd();
    } 

    for(int i=0;i<7;i=i+2)
    {
    glBegin(GL_LINE_LOOP);              // Each set of 4 vertices form a quad
      glColor3f(1.0f, 1.0f, 1.0f);
    for(int j=0;j<3;j++)
      glVertex3d(vertices2[i+j][0], vertices2[i+j][1], vertices2[i+j][2]);
    glEnd();
    }

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    glFrustum(-2.0, 2.0, -2.0, 2.0, 0.4, 2.0);

    glMatrixMode(GL_MODELVIEW);

   glFlush();  // Render now
}

void setVertices(char* str, int m, int n)
{
    int i=0,j=0,k=0;
    char ch[10];
    bool flag=true;
    if(n==0)
    {
        do
        {
            if(str[i]==' ' || str[i]=='\0')
            {
                ch[j]='\0';
                vertices1[m][k]=atof(ch);
                j=0;
                k++;
                if(str[i]=='\0')
                    flag=false;
            }
            else
            {
                ch[j]=str[i];
                j++;
            }
            i++;
        }
        while(flag);
    }
    else
    {
        do
        {
            if(str[i]==' ' || str[i]=='\0')
            {
                ch[j]='\0';
                vertices2[m][k]=atof(ch);
                j=0;
                k++;
                if(str[i]=='\0')
                    flag=false;
            }
            else
            {
                ch[j]=str[i];
                j++;
            }
            i++;
        }
        while(flag);
    }
}

int main(int argc, char** argv)
{
    char str[100];    
    ifstream myfile;
    myfile.open("models_new.txt");
    if(!myfile.eof())
    {
        myfile.getline (str,100);
        int nom = atoi(str);
        for(int i=0;i<nom;i++)
        {
            myfile.getline(str,100);
            int nov = atoi(str);
            for(int j=0;j<nov;j++)
            {
                myfile.getline(str,100);
                setVertices(str,j,i);
            }
        }
    }

    glutInit(&argc, argv);
    glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB);
    glutInitWindowPosition (100, 100);
    glutInitWindowSize(500, 500);   
    glutCreateWindow (argv[0]);
    init (); 

    glutReshapeFunc(reshape);
    glutDisplayFunc(display);
    
    glutMainLoop();
    return 0;
}