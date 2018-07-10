//g++ Pipeline.cpp -std=c++11 -lglut -lGLU -lGL -o pipe

#include <GL/glut.h>
#include <iostream>
#include <fstream>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include "Vertex.h"

using namespace std;

Vertex model1[24], model2[10], viewport[4];
Vertex plane1[24], plane2[10];
Vertex view_plane[4], near_plane[4],far_plane[4];
Vertex eye, eye_wcs, eye_vcs, vrp, up, u,v,n;
float width, height, near, far;
int num=1;

void extractVertices(char* str, int m, int n)
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
                if(k==0)
	                model1[m].setX(atof(ch));
	            else if(k==1)
	            	model1[m].setY(atof(ch));
	            else
	            	model1[m].setZ(atof(ch));
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
                if(k==0)
	                model2[m].setX(atof(ch));
	            else if(k==1)
	            	model2[m].setY(atof(ch));
	            else
	            	model2[m].setZ(atof(ch));
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

Vertex setVertex(char* str)
{
	Vertex v;
	char ch[100];
	bool flag=true;
	int i=0, j=0, k=0;
	do
    {
        if(str[i]==' ' || str[i]=='\0')
        {
            ch[j]='\0';
            if(k==0)
	            v.setX(atof(ch));
	        else if(k==1)
	        	v.setY(atof(ch));
	        else
	           	v.setZ(atof(ch));
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

    return v;
}

void readData()
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
                extractVertices(str,j,i);
            }
        }
    }
    myfile.close();

    myfile.open("parameters.txt");
    if(!myfile.eof())
    {
    	myfile.getline(str,100);
    	int nop = atoi(str);
    	for(int i=0;i<nop;i++)
    	{
    		myfile.getline(str,100);
    		if(strcmp(str,"eye")==0)
    		{
    			myfile.getline(str,100);
    			eye = setVertex(str);
    		}
    		else if(strcmp(str,"vrp")==0)
    		{
    			myfile.getline(str,100);
    			vrp = setVertex(str);
    		}
    		else if(strcmp(str,"up")==0)
    		{
    			myfile.getline(str,100);
    			up = setVertex(str);
    		}
            else if(strcmp(str,"width")==0)
            {
                myfile.getline(str,100);
                width = atof(str);
            }
            else if(strcmp(str,"height")==0)
            {
                myfile.getline(str,100);
                height = atof(str);
            }
            else if(strcmp(str,"near")==0)
            {
                myfile.getline(str,100);
                near = atof(str);
            }
            else if(strcmp(str,"far")==0)
            {
                myfile.getline(str,100);
                far = atof(str);
            }
            else if(strcmp(str,"case")==0)
            {
                myfile.getline(str,100);
                num = atoi(str);
            }
    	}
    }
}

void printVertex(Vertex v)
{
    cout<< v.X()<< "  "<< v.Y()<< "  "<< v.Z()<< endl; 
}

Vertex scalarMultiply(Vertex v, float sclr)
{
    Vertex res;
	res.setX(sclr * v.X());
	res.setY(sclr * v.Y());
	res.setZ(sclr * v.Z());

	return res;
}

float dotProduct(Vertex v1, Vertex v2)
{
	return (v1.X()*v2.X() + v1.Y()*v2.Y() + v1.Z()*v2.Z());
}

float magnetude(Vertex v)
{
    return sqrt(v.X()*v.X() + v.Y()*v.Y() + v.Z()*v.Z());
}

Vertex crossProduct(Vertex v1, Vertex v2)
{
	Vertex res;
	res.setX(v1.Y()*v2.Z()-v2.Y()*v1.Z());
	res.setY(v1.Z()*v2.X()-v1.X()*v2.Z());
	res.setZ(v1.X()*v2.Y()-v1.Y()*v2.X());

	return res;
}

Vertex normalize(Vertex v)
{
    float norm = magnetude(v);
    Vertex res;
    res.setVertices(v.X()/norm, v.Y()/norm, v.Z()/norm);

    return res;
}

Vertex norm(Vertex v)
{
    float max;
    if(v.X()>v.Y() && v.X()>v.Z())
        max = v.X();
    else if(v.Y()>v.X() && v.Y()>v.Z())
        max = v.Y();
    else
        max = v.Z(); 

    Vertex res;
    res.setVertices(v.X()/max, v.Y()/max, v.Z()/max);

    return res;
}

Vertex addVertices(Vertex v1, Vertex v2)
{
    Vertex res;
    res.setX(v1.X()+v2.X());
    res.setY(v1.Y()+v2.Y());
    res.setZ(v1.Z()+v2.Z());

    return res;
}

Vertex subtractVertices(Vertex v1, Vertex v2)
{
    Vertex res;
    res.setX(v1.X()-v2.X());
    res.setY(v1.Y()-v2.Y());
    res.setZ(v1.Z()-v2.Z());

    return res;   
}

Vertex computeNormal(Vertex r)
{
	Vertex n;
	float psi = acos(r.Z()/sqrt(r.X()*r.X() + r.Y()*r.Y() + r.Z()*r.Z()));
	float theta = acos(r.X()/sqrt(r.X()*r.X() + r.Y()*r.Y()));
	n.setZ(cos(psi));
	n.setY(sin(psi)*sin(theta));
	n.setX(sin(psi)*cos(theta));

	return n;
}

Vertex computeUpVector(Vertex v1, Vertex v2)
{
    Vertex res = subtractVertices(up, scalarMultiply(v2, dotProduct(v1,v2)));
    res = normalize(res);

    return res;
}

Vertex WCStoVCS(Vertex vert)
{
    Vertex temp = subtractVertices(vert, vrp);
    Vertex res;
    res.setVertices(dotProduct(temp,u), dotProduct(temp,v), dotProduct(temp,n));

    return res;
}

Vertex VCStoWCS(Vertex vert)
{
    float t1 = vert.X() * u.X() + vert.Y() * v.X() + vert.Z() * n.X() + vrp.X();
    float t2 = vert.X() * u.Y() + vert.Y() * v.Y() + vert.Z() * n.Y() + vrp.Y();
    float t3 = vert.X() * u.Z() + vert.Y() * v.Z() + vert.Z() * (n.Z()) + vrp.Z();

    Vertex res;
    res.setVertices(t1, t2, t3);

    return res;
}

Vertex transformToWCS(Vertex vert)
{
    float t1 = vert.X() * u.X() + vert.Y() * v.X() - vert.Z() * n.X() + vrp.X();
    float t2 = vert.X() * u.Y() + vert.Y() * v.Y() - vert.Z() * n.Y() + vrp.Y();
    float t3 = vert.X() * u.Z() + vert.Y() * v.Z() - vert.Z() * n.Z() + vrp.Z();

    Vertex res;
    res.setVertices(t1, t2, t3);

    return res;   
}

Vertex findIntersectionPoint(Vertex v1, Vertex v2)
{
    float t = (v2.Z()-eye_vcs.Z())/v1.Z();
    Vertex res = addVertices(eye_vcs, scalarMultiply(subtractVertices(v1, eye_vcs), t));

    return res;
}

void init(void)
{
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH);
    glClearColor(1.0, 1.0, 1.0, 1.0);
}

Vertex VCSToViewPlane(Vertex vert)
{
    float u_val = eye.Z()*vert.X()/(eye.Z()-vert.Z());
    float v_val = eye.Z()*vert.Y()/(eye.Z()-vert.Z());

    Vertex res;
    res.setVertices(u_val, v_val, 0);

    return res;
}

float perspective(float val, float z)
{
    float res = val/(1-z/eye.Z());

    return res;
}

void drawModels()
{
	for(int i=0;i<21;i=i+4)
	{
		glBegin(GL_LINE_LOOP);
    		glColor3f(0.0f, 0.0f, 0.0f);
     	 	glVertex3d(model1[i].X(), model1[i].Y(), model1[i].Z());
    		glVertex3d(model1[i+1].X(), model1[i+1].Y(), model1[i+1].Z());
    		glVertex3d(model1[i+2].X(), model1[i+2].Y(), model1[i+2].Z());
    		glVertex3d(model1[i+3].X(), model1[i+3].Y(), model1[i+3].Z());
		glEnd();
	}

	for(int i=0;i<7;i=i+2)
	{
		glBegin(GL_LINE_LOOP);
    		glColor3f(0.0f, 0.0f, 0.0f);
     	 	glVertex3d(model2[i].X(), model2[i].Y(), model2[i].Z());
    		glVertex3d(model2[i+1].X(), model2[i+1].Y(), model2[i+1].Z());
    		glVertex3d(model2[i+2].X(), model2[i+2].Y(), model2[i+2].Z());
    	glEnd();
	}
}

void display()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    //glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    n = computeNormal(vrp);
    v = computeUpVector(up, n);
    u = crossProduct(n, v);

    viewport[0].setVertices(-width/2, height/2, 0);
    viewport[1].setVertices(width/2, height/2, 0);
    viewport[2].setVertices(width/2, -height/2, 0);
    viewport[3].setVertices(-width/2, -height/2, 0);

    for(int i=0;i<4;i++)
        view_plane[i] = VCStoWCS(viewport[i]);

    
    //gluLookAt(0.0, 0.0, -3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    glOrtho(-4, 4, -4, 4, -3, 3);
    //glMatrixMode(GL_MODELVIEW);
    //glLoadIdentity();

/*    glBegin(GL_LINES);
        glColor3f(1.0, 0.0, 1.0);
        glVertex3d(0.0, 0.0, 0.0);
        glVertex3d(u.X(),u.Y(),u.Z());

        glColor3f(1.0, 0.0, 1.0);
        glVertex3d(0.0, 0.0, 0.0);
        glVertex3d(v.X(),v.Y(),v.Z());

        glColor3f(1.0, 0.0, 1.0);
        glVertex3d(0.0, 0.0, 0.0);
        glVertex3d(n.X(),n.Y(),n.Z());
    glEnd();*/

    drawModels();

    eye_wcs = VCStoWCS(eye);
    glBegin(GL_TRIANGLES);
        glColor3f(1.0, 0.0, 0.0);
        glVertex3f(eye_wcs.X()-0.02, eye_wcs.Y(), eye_wcs.Z());
        glVertex3f(eye_wcs.X()-0.04, eye_wcs.Y()-0.04, eye_wcs.Z());
        glVertex3f(eye_wcs.X(), eye_wcs.Y()-0.04, eye_wcs.Z());
    glEnd();

    glBegin(GL_LINE_LOOP);
        glColor3f(0.0,1.0,0.0);
        for(int i=0;i<4;i++)
            glVertex3d(view_plane[i].X(), view_plane[i].Y(), view_plane[i].Z());
    glEnd();

    float near_ratio = (eye.Z()-near)/eye.Z();
    float far_ratio = (eye.Z()-far)/eye.Z();
    for(int i=0;i<4;i++)
    {
        near_plane[i] = scalarMultiply(viewport[i], near_ratio);
        near_plane[i].setZ(near);     
        near_plane[i] = VCStoWCS(near_plane[i]);

        far_plane[i] = scalarMultiply(viewport[i], far_ratio);
        far_plane[i].setZ(far);
        far_plane[i] = VCStoWCS(far_plane[i]);
    }

    glBegin(GL_LINE_LOOP);
        glColor3f(0.0,0.0,0.0);
        for(int i=0;i<4;i++)
            glVertex3d(near_plane[i].X(), near_plane[i].Y(), near_plane[i].Z());
    glEnd();

    glBegin(GL_LINE_LOOP);
    glColor3f(0.0, 0.0, 0.0);
        for(int i=0;i<4;i++)
            glVertex3d(far_plane[i].X(), far_plane[i].Y(), far_plane[i].Z());
    glEnd();


    glBegin(GL_LINES);
    glColor3f(0.0, 0.0, 0.0);
    for(int i=0;i<4;i++)
    {
        glVertex3d(eye_wcs.X(), eye_wcs.Y(), eye_wcs.Z());
        glVertex3d(far_plane[i].X(), far_plane[i].Y(), far_plane[i].Z());
    }
    glEnd();

    for(int i=0;i<24;i++)
    {
        Vertex temp = WCStoVCS(model1[i]);
        Vertex temp2 = VCSToViewPlane(temp);
        float x_val = perspective(temp.X(), temp.Z());
        float y_val = perspective(temp.Y(), temp.Z());
        temp.setVertices(x_val, y_val,0);
        printVertex(temp);
        printVertex(temp2);
        plane1[i] = VCStoWCS(temp);
    }

    glBegin(GL_LINE_LOOP);
        glColor3f(1.0, 0.0, 0.0);
        for(int i=0;i<24;i=i+4)
        {
            glVertex3d(plane1[i].X(), plane1[i].Y(), plane1[i].Z()); 
            glVertex3d(plane1[i+1].X(), plane1[i+1].Y(), plane1[i+1].Z());
            glVertex3d(plane1[i+2].X(), plane1[i+2].Y(), plane1[i+2].Z());
            glVertex3d(plane1[i+3].X(), plane1[i+3].Y(), plane1[i+3].Z());
        }
    glEnd();

    glFlush();
}

void reshape(int w, int h)
{
    glViewport(0, 0, (GLsizei) w, (GLsizei) h);
    if(h==0)
        h=1;
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glFrustum(-1.0, 1.0, -1.0, 1.0, -20, 20.0);
}

int main(int argc, char** argv)
{
    readData();

    glutInit(&argc, argv);
    glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB);
    glutInitWindowPosition (100, 100);
    glutInitWindowSize(500, 500);   
    glutCreateWindow (argv[0]);
    
    init ();
    //glutReshapeFunc(reshape);
    glutDisplayFunc(display);
    glutMainLoop();

    return 0;
}