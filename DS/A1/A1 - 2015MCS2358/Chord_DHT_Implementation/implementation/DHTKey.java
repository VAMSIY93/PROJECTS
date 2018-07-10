package implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.sun.istack.internal.logging.Logger;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;

public class DHTKey implements Comparable {

	String identifier;

	byte[] key;

	public DHTKey(byte[] key) {
		this.key = key;
	}

	final static Logger log = Logger.getLogger(DHTKey.class); 
	
	public DHTKey(String identifier) {
		this.identifier = identifier;
		log.info("DHTKey input identifier ="+identifier);
		this.key = HashFunc.hash(identifier);
		log.info("Hash Key ="+Arrays.toString(this.key));
	}

	public boolean isBetween(DHTKey fromKey, DHTKey toKey) {
		if (fromKey.compareTo(toKey) < 0) {
			if (this.compareTo(fromKey) > 0 && this.compareTo(toKey) < 0) {
				return true;
			}
		} else if (fromKey.compareTo(toKey) > 0) {
			if (this.compareTo(toKey) < 0 || this.compareTo(fromKey) > 0) {
				return true;
			}
		}
		return false;
	}

	

	

	public DHTKey createStartKey(int index) {
		//log.info("\n Inside Func createStartKey");
		//log.info("\n Key Length = "+key.length);
		
		byte[] newKey = new byte[key.length];
		System.arraycopy(key, 0, newKey, 0, key.length);

		int pos = key.length-1 - index/8;
		int value = key[pos] & 0xff;
		value += (1 << (index%8));
		newKey[pos] = (byte)value;

		// handling carry
		int carry = (value>>8)&0xff;
		int i = pos - 1;
		while (carry > 0 && i>=0) {
			int v = key[i] & 0xff;
			v += carry;
			newKey[i] = (byte)v;
			carry = (v>>8)&0xff;
			i--;
		}
		//System.out.println("start key ="+newKey.toString());
		return new DHTKey(newKey);
		
	}
	
	public int compareTo(Object obj) {
		DHTKey targetKey = (DHTKey) obj;
		for (int i = 0; i < key.length; i++) {
			int loperand = (this.key[i] & 0xff);
			int roperand = (targetKey.getKey()[i] & 0xff);
			if (loperand != roperand) {
				int result = loperand - roperand;
				return result;
			}
		}
		return 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (key.length > 4) {
			for (int i = 0; i < key.length; i++) {
				sb.append(Integer.toString(((int) key[i]) & 0xff) + ".");
			}
			return sb.substring(0, sb.length() - 1).toString();
		} else {
			long n = 0;
			for (int i = key.length - 1, j = 0; i >= 0; i--, j++) {
				n |= ((key[i] << (8 * j)) & (0xffL << (8 * j)));
			}
			sb.append(Long.toString(n));
			return sb.toString();
		}
	}
	
	/**
	 * Calculate the angle in identifier ring.
	 * 
	 * @return the degree of angel in radian (0 - 1)
	 */
	public double getAngle() {
		double maxValue = Math.pow(2, HashFunc.KEY_LENGTH);
		double keyValue = getDoubleValue();
		double degree = ((keyValue / maxValue)*365);
		double radian =  degree /180 * Math.PI;
		return radian; 
		
	}

	public double getDoubleValue() {
		double result = 0;
		for (int i=key.length-1; i>=0; i--) {
			double value = (key[i] & 0xff);
			for (int j=i; j<key.length-1; j++) {
				value *= Math.pow(2, 8);
			}
			result += value;
		}
		return result;
	}
	
	

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
