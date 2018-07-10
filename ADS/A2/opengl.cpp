//g++ sample.cpp -std=c++11 -lglut -lGLU -lGL -o sam

#include <stdlib.h>
#include <stdio.h>
#include <GL/glut.h>

using namespace std;

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

void renderScene(void) 
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
}

/*void renderScene(void)
{
   // Clear Color and Depth Buffers
   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

   // Reset transformations
   glLoadIdentity();
   // Set the camera
   gluLookAt(  0.0f, 0.0f, 10.0f,
         0.0f, 0.0f,  0.0f,
         0.0f, 1.0f,  0.0f);

   glRotatef(angle, 0.0f, 1.0f, 0.0f);

   glBegin(GL_TRIANGLES);
      glVertex3f(-2.0f,-2.0f, 0.0f);
      glVertex3f( 2.0f, 0.0f, 0.0);
      glVertex3f( 0.0f, 2.0f, 0.0);
   glEnd();

   angle+=0.1f;

   glutSwapBuffers();
}*/

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
