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
Vertex plane1[24], plane2[10], vcs_model1[24], vcs_model2[10];
Vertex view_plane[4], near_plane[4],far_plane[4];
Vertex eye, eye_wcs, eye_vcs, vrp, up, u,v,n;
float width, height, near, far;
int state = 0;

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

float* clipLine(Vertex v1, Vertex v2, Vertex vmin, Vertex vmax)
{
    float tmin=0, tmax=1;
    float *res = new float[6];
    float d1[6],d2[6];

    d1[0]=v1.X()-v2.X();
    d1[1]=v2.X()-v1.X();
    d1[2]=v1.Y()-v2.Y();
    d1[3]=v2.Y()-v1.Y();

    d2[0]=v1.X()-vmin.X();
    d2[1]=vmax.X()-v1.X();
    d2[2]=v1.Y()-vmin.Y();
    d2[3]=vmax.Y()-v1.Y();

    for(int i=0;i<4;i++)
    {
        if(d1[i]==0)
        {
            if(d2[i]<0)
                return NULL;
        }
        else if(d1[i]<0)
            tmin = max(tmin, d2[i]/d1[i]);
        else
            tmax = min(tmax, d2[i]/d1[i]);
    }
    if(tmin>tmax)
        return NULL;

    res[0] = v1.X()+ tmin*(v2.X()-v1.X());
    res[1] = v1.Y()+ tmin*(v2.Y()-v1.Y());
    res[2] = 0;
    res[3] = v1.X()+ tmax*(v2.X()-v1.X());
    res[4] = v1.Y()+ tmax*(v2.Y()-v1.Y());
    res[5] = 0;

    return res;
}

Vertex normalizeVolume(Vertex v1)
{
    float wl=-width/2, wr=width/2, wb=-height/2, wt=height/2;
    float vl=0, vr=1, vb=0, vt=1;
    float F=near, B=far;

    float su = (vl-vr)/(wl-wr);
    float sv = (vt-vb)/(wt-wb);
    float sn = (eye.Z()-B)*(eye.Z()-F)/(eye.Z()*eye.Z()*(B-F));

    float ru = (vr*wl - vl*wr)/(wl-wr);
    float rv = (vb*wt - vt*wb)/(wt-wb);
    float rn = F*(eye.Z()-B)/(eye.Z()*(F-B));

    Vertex res;
    res.setX(v1.X()*su+ru);
    res.setY(v1.Y()*sv+rv);
    res.setZ(v1.Z()*sn+rn);
    
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
    switch(state)
    {
        case 0:
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                gluLookAt(0.4, 0.4, 0.6, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
                drawModels();
                glFlush();
                break;
            }

        case 1:
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();

                n = computeNormal(vrp);
                v = computeUpVector(up, n);
                u = crossProduct(n, v);

                viewport[0].setVertices(-width/2, height/2, 0);
                viewport[1].setVertices(width/2, height/2, 0);
                viewport[2].setVertices(width/2, -height/2, 0);
                viewport[3].setVertices(-width/2, -height/2, 0);

                glOrtho(-4, 4, -4, 4, -3, 3);

                glBegin(GL_LINE_LOOP);
                    glColor3f(0.0, 0.0, 0.0);
                    for(int i=0;i<4;i++)
                        glVertex3d(viewport[i].X(), viewport[i].Y(), 1);
                glEnd();

                for(int i=0;i<24;i++)
                {
                    Vertex temp = WCStoVCS(model1[i]);
                    float x_val = perspective(temp.X(), temp.Z());
                    float y_val = perspective(temp.Y(), temp.Z());
                    plane1[i].setVertices(x_val, y_val,1);
                }

                glBegin(GL_LINE_LOOP);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<21;i=i+4)
                    {
                        glVertex3d(plane1[i].X(), plane1[i].Y(), plane1[i].Z()); 
                        glVertex3d(plane1[i+1].X(), plane1[i+1].Y(), plane1[i+1].Z());
                        glVertex3d(plane1[i+2].X(), plane1[i+2].Y(), plane1[i+2].Z());
                        glVertex3d(plane1[i+3].X(), plane1[i+3].Y(), plane1[i+3].Z());
                    }
                glEnd();

                for(int i=0;i<10;i++)
                {
                    Vertex temp = WCStoVCS(model2[i]);
                    float x_val = perspective(temp.X(), temp.Z());
                    float y_val = perspective(temp.Y(), temp.Z());
                    plane2[i].setVertices(x_val, y_val,1);
                }

                glBegin(GL_LINE_LOOP);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<7;i=i+2)
                    {
                        glVertex3d(plane2[i].X(), plane2[i].Y(), plane2[i].Z()); 
                        glVertex3d(plane2[i+1].X(), plane2[i+1].Y(), plane2[i+1].Z());
                        glVertex3d(plane2[i+2].X(), plane2[i+2].Y(), plane2[i+2].Z());
                    }
                glEnd();
                glFlush();
                break;
            }

        case 2:
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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

                glOrtho(-4, 4, -4, 4, -3, 3);
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
                    vcs_model1[i] = VCSToViewPlane(temp);
                    plane1[i] = VCStoWCS(vcs_model1[i]);
                }

                glBegin(GL_LINE_LOOP);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<21;i=i+4)
                    {
                        glVertex3d(plane1[i].X(), plane1[i].Y(), plane1[i].Z()); 
                        glVertex3d(plane1[i+1].X(), plane1[i+1].Y(), plane1[i+1].Z());
                        glVertex3d(plane1[i+2].X(), plane1[i+2].Y(), plane1[i+2].Z());
                        glVertex3d(plane1[i+3].X(), plane1[i+3].Y(), plane1[i+3].Z());
                    }
                glEnd();

                for(int i=0;i<10;i++)
                {
                    Vertex temp = WCStoVCS(model2[i]);
                    vcs_model2[i] = VCSToViewPlane(temp);
                    plane2[i] = VCStoWCS(vcs_model2[i]);
                }

                glBegin(GL_LINE_LOOP);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<7;i=i+2)
                    {
                        glVertex3d(plane2[i].X(), plane2[i].Y(), plane2[i].Z()); 
                        glVertex3d(plane2[i+1].X(), plane2[i+1].Y(), plane2[i+1].Z());
                        glVertex3d(plane2[i+2].X(), plane2[i+2].Y(), plane2[i+2].Z());
                    }
                glEnd();
                glFlush();
                break;
            }

        case 3:
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();

                n = computeNormal(vrp);
                v = computeUpVector(up, n);
                u = crossProduct(n, v);

                viewport[0].setVertices(-width/2, height/2, 0);
                viewport[1].setVertices(width/2, height/2, 0);
                viewport[2].setVertices(width/2, -height/2, 0);
                viewport[3].setVertices(-width/2, -height/2, 0);

                for(int i=0;i<4;i++)
                {   view_plane[i] = normalizeVolume(viewport[i]);
                    view_plane[i] = VCStoWCS(view_plane[i]);
                }

                glOrtho(-4, 4, -4, 4, -3, 3);
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
                    near_plane[i] = normalizeVolume(near_plane[i]);     
                    near_plane[i] = VCStoWCS(near_plane[i]);

                    far_plane[i] = scalarMultiply(viewport[i], far_ratio);
                    far_plane[i].setZ(far);
                    far_plane[i] = normalizeVolume(far_plane[i]);
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
                    temp = VCSToViewPlane(temp);
                    temp = normalizeVolume(temp);
                    plane1[i] = VCStoWCS(temp);
                }

                glBegin(GL_LINE_LOOP);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<21;i=i+4)
                    {
                        glVertex3d(plane1[i].X(), plane1[i].Y(), plane1[i].Z()); 
                        glVertex3d(plane1[i+1].X(), plane1[i+1].Y(), plane1[i+1].Z());
                        glVertex3d(plane1[i+2].X(), plane1[i+2].Y(), plane1[i+2].Z());
                        glVertex3d(plane1[i+3].X(), plane1[i+3].Y(), plane1[i+3].Z());
                    }
                glEnd();

                for(int i=0;i<10;i++)
                {
                    Vertex temp = WCStoVCS(model2[i]);
                    temp = VCSToViewPlane(temp);
                    temp = normalizeVolume(temp);
                    plane2[i] = VCStoWCS(temp);
                }

                glBegin(GL_LINE_LOOP);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<7;i=i+2)
                    {
                        glVertex3d(plane2[i].X(), plane2[i].Y(), plane2[i].Z()); 
                        glVertex3d(plane2[i+1].X(), plane2[i+1].Y(), plane2[i+1].Z());
                        glVertex3d(plane2[i+2].X(), plane2[i+2].Y(), plane2[i+2].Z());
                    }
                glEnd();
                glFlush();
                break;
            }

        case 4:
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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

                glOrtho(-4, 4, -4, 4, -3, 3);
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
                    vcs_model1[i] = VCSToViewPlane(temp);
                    plane1[i] = VCStoWCS(vcs_model1[i]);
                }

                Vertex temp1,temp2;
                float **clip_vert;
                clip_vert = new float*[4];
                for(int i=0;i<4;i++)
                    clip_vert[i] = new float[6];

                glBegin(GL_LINES);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<21;i=i+4)
                    {
                        clip_vert[0] = clipLine(vcs_model1[i], vcs_model1[i+1], viewport[3], viewport[1]);
                        if(clip_vert[0]!=NULL)
                        {
                            temp1.setVertices(clip_vert[0][0],clip_vert[0][1],clip_vert[0][2]);
                            temp2.setVertices(clip_vert[0][3],clip_vert[0][4],clip_vert[0][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }

                        clip_vert[1] = clipLine(vcs_model1[i+1], vcs_model1[i+2], viewport[3], viewport[1]);
                        if(clip_vert[1]!=NULL)
                        {    
                            temp1.setVertices(clip_vert[1][0],clip_vert[1][1],clip_vert[1][2]);
                            temp2.setVertices(clip_vert[1][3],clip_vert[1][4],clip_vert[1][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }

                        clip_vert[2] = clipLine(vcs_model1[i+2], vcs_model1[i+3], viewport[3], viewport[1]);
                        if(clip_vert[2]!=NULL)
                        {    
                            temp1.setVertices(clip_vert[2][0],clip_vert[2][1],clip_vert[2][2]);
                            temp2.setVertices(clip_vert[2][3],clip_vert[2][4],clip_vert[2][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }

                        clip_vert[3] = clipLine(vcs_model1[i+3], vcs_model1[i], viewport[3], viewport[1]);
                        if(clip_vert[3]!=NULL)
                        {    
                            temp1.setVertices(clip_vert[3][0],clip_vert[3][1],clip_vert[3][2]);
                            temp2.setVertices(clip_vert[3][3],clip_vert[3][4],clip_vert[3][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }    
                    }
                glEnd();

                for(int i=0;i<10;i++)
                {
                    Vertex temp = WCStoVCS(model2[i]);
                    vcs_model2[i] = VCSToViewPlane(temp);
                    plane2[i] = VCStoWCS(vcs_model2[i]);
                }

                glBegin(GL_LINES);
                    glColor3f(1.0, 0.0, 0.0);
                    for(int i=0;i<7;i=i+2)
                    {
                        clip_vert[0] = clipLine(vcs_model2[i], vcs_model2[i+1], viewport[3], viewport[1]);
                        if(clip_vert[0]!=NULL)
                        {
                            temp1.setVertices(clip_vert[0][0],clip_vert[0][1],clip_vert[0][2]);
                            temp2.setVertices(clip_vert[0][3],clip_vert[0][4],clip_vert[0][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }

                        clip_vert[1] = clipLine(vcs_model2[i+1], vcs_model2[i+2], viewport[3], viewport[1]);
                        if(clip_vert[1]!=NULL)
                        {    
                            temp1.setVertices(clip_vert[1][0],clip_vert[1][1],clip_vert[1][2]);
                            temp2.setVertices(clip_vert[1][3],clip_vert[1][4],clip_vert[1][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }

                        clip_vert[2] = clipLine(vcs_model2[i+2], vcs_model2[i], viewport[3], viewport[1]);
                        if(clip_vert[2]!=NULL)
                        {    
                            temp1.setVertices(clip_vert[2][0],clip_vert[2][1],clip_vert[2][2]);
                            temp2.setVertices(clip_vert[2][3],clip_vert[2][4],clip_vert[2][5]);
                            temp1 = VCStoWCS(temp1);
                            temp2 = VCStoWCS(temp2);
                            glVertex3d(temp1.X(),temp1.Y(),temp1.Z());
                            glVertex3d(temp2.X(),temp2.Y(),temp2.Z());
                        }
                    }
                glEnd();
                glFlush();
                break;
            }

        default:
            break;    
    }
}

void changeViews(unsigned char key, int x, int y) 
{
    switch (key) 
    {
        case '0':
            state = 0;
            break;
        case '1':
            state = 1;
            break;
        case '2':
            state = 2;
            break;
        case '3':
            state = 3;
            break;
        case '4':
            state = 4;
            break;
        default:
            state = 1;
            break;
    }
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

void update(int value) 
{
    glutPostRedisplay();
    glutTimerFunc(25, update, 0);
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
    glutKeyboardFunc(changeViews);
    glutTimerFunc(25, update, 0);
    glutMainLoop();

    return 0;
}