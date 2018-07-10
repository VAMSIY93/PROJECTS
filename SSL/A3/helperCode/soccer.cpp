#include <GL/glut.h>
#include <iostream>
#include <stdio.h>
#include <math.h>
#include <list>
#include <vector>
#include <pthread.h>
#include <unistd.h>
#define ROWS 270
#define COLS 480
using namespace std;

class cell
{
public:
    int r = 0, g = 150, b = 0;
};

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

            if( (i == 5 || i == ROWS-6) && (j > 20 && j < COLS-21) )
                glColor3ub( 255, 255, 255 );
            if( (j == 20 || j == COLS-21) && (i >= 5 && i <= ROWS-6) )
                glColor3ub( 255, 255, 255 );
            if( j == COLS/2 && (i >= 5 && i <= ROWS-6) )
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
                glVertex3f(   j,   i, 0);
                glVertex3f( j+1,   i, 0);
                glVertex3f( j+1, i+1, 0);
                glVertex3f(   j, i+1, 0);
            glEnd();
        }
    }
}

void display(void)
{
    glClear (GL_COLOR_BUFFER_BIT);
    glLoadIdentity ();
    gluLookAt (0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    generate_map();

    glutSwapBuffers();
    glutPostRedisplay();
}




int main(int argc, char** argv)
{

    glutInit(&argc, argv);
    glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB);
    glutInitWindowPosition (0, 0);   
    glutCreateWindow (argv[0]);
    glutFullScreen();
    init (); 

    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutMainLoop();
    return 0;
}
