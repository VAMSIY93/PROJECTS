

#include <iostream>
#include <stdio.h>

using namespace std;

class Vertex
{
private:
	float x,y,z;

public:
	void setVertices(float x_val, float y_val, float z_val)
	{
		x=x_val;
		y=y_val;
		z=z_val;
	}
	
	void setX(float x_val)
	{
		x=x_val;
	}

	void setY(float y_val)
	{
		y=y_val;
	}

	void setZ(float z_val)
	{
		z=z_val;
	}

	float X()
	{
		return x;
	}

	float Y()
	{
		return y;
	}

	float Z()
	{
		return z;
	}
};
