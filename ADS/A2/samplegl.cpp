//g++ sample.cpp -std=c++11 -lglut -lGLU -lGL -o sam

#include <stdlib.h>
#include <stdio.h>
#include <GL/glut.h>
#include <math.h>

using namespace std;

int PI=3.1416;
float angle = 0.0f;
void changeSize(int w, int h) 
{

   // Prevent a divide by zero, when window is too short
   // (you cant make a window of zero width).
   if (h == 0)
      h = 1;

   float ratio =  w * 1.0 / h;

   // Use the Projection Matrix
   glMatrixMode(GL_PROJECTION);

   // Reset Matrix
   glLoadIdentity();

   // Set the viewport to be the entire window
   glViewport(0, 0, w, h);

   // Set the correct perspective.
   gluPerspective(45,ratio,1,100);

   // Get Back to the Modelview
   glMatrixMode(GL_MODELVIEW);
}

/*void renderScene(void) 
{
   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

   glColor3f(1,1,1);
   glScalef(0.25f,0.25f,0.25f);
  glBegin(GL_QUADS);
    glVertex2f( 20,20);
    glVertex2f( -20,20);
    glVertex2f(-20,-20);
    glVertex2f(20,-20);
  glEnd();

  glBegin(GL_LINES);
    for(int i=-20;i<=20;i++) 
    {
      if (i==0) { glColor3f(0,0,0); } else { glColor3f(0,0,0); }
      glVertex2f(i,-20);
      glVertex2f(i,20);
      if (i==0) { glColor3f(0,0,0); } else { glColor3f(0,0,0); }
      glVertex2f(-20,i);
      glVertex2f(20,i);
    }
  glEnd();

  glutSwapBuffers();
}*/

/*void renderScene(void)
{
   // Clear Color and Depth Buffers
   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    float x,y;
    float radius = 0.5f;
    glColor3f(1,1,1);

    glBegin(GL_LINES);     
    x = (float)radius * cos(359 * PI/180.0f);
    y = (float)radius * sin(359 * PI/180.0f);
    for(int j = 0; j < 360; j++)
    {
        glColor3f(1,0,0);
        glVertex2f(x,y);
        x = (float)radius * cos(j * PI/180.0f);
        y = (float)radius * sin(j * PI/180.0f);
        glVertex2f(x,y);
    }
    glEnd();

   glutSwapBuffers();
}*/

void drawCircle(float x1,float y1)
{
  double radius=0.05;
  float x2=0.0,y2=0.0;
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
    float x1,y1,x2,y2;
    float angle;
    glColor3f(1,1,1);
    x1 = 0.0,y1=0.0;

    for(int i=-10;i<10;i++)
    {  
      y1=i*0.1;
      
      for(int j=-10;j<10;j++)
      {
        x1=j*0.1; 
        glBegin(GL_TRIANGLE_FAN);
        if(y1<=0)
          glColor3f(0,1,0);
        else
          glColor3f(0,0,1);
        glVertex2f(x1,y1);
         
        drawCircle(x1,y1);
        glEnd();
      }  
    }
    glutSwapBuffers();
}

int main(int argc, char **argv) {

   // init GLUT and create window
   glutInit(&argc, argv);
   glutInitDisplayMode(GLUT_DEPTH | GLUT_DOUBLE | GLUT_RGBA);
   glutInitWindowPosition(100,100);
   glutInitWindowSize(3200,3200);
   glutCreateWindow("Lighthouse3D - GLUT Tutorial");
   //glutFullScreen();
   // register callbacks
   glutDisplayFunc(renderScene);
   //glutReshapeFunc(changeSize);

   // enter GLUT event processing loop
   glutMainLoop();

   return 0;
}