import java.io.*;
import java.util.*;

class OoS
{
	String st[];
	OoS()
	{
		st=new String[3];
	}
}

class Test
{
	public static void main(String args[])
	{
		Vector v=new Vector();
		OoS o1=new OoS();
		o1.st[0]="hai";
		v.add(o1);
		Object o2=v.get(0);
		System.out.println(((OoS)o2).st[0]);
	}

}