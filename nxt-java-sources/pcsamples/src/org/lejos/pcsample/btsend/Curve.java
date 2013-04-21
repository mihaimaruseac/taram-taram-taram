package org.lejos.pcsample.btsend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Curve
{
	public int n;
	public int [] x;
	public int [] y;

	public Curve() { }

	public Curve(int n, int [] x, int [] y) {
		this.n = n;
		this.x = new int [n];
		this.y = new int [n];
		for (int i=0; i<n; i++) {
			this.x[i] = x[i];
			this.y[i] = y[i];
		}
	}

	public int GetXKth(int i, int k) {
		return ((i + k) < n) ? x[i+k] : x[n-1];
	}

	public int GetYKth(int i, int k) {
		return ((i + k) < n) ? y[i+k] : y[n-1];
	}

	public void read(DataInputStream in) throws IOException {
		this.n = in.readInt();
		this.x = new int [n];
		this.y = new int [n];
		for (int i=0; i<n; i++) {
			this.x[i] = in.readInt();
			this.y[i] = in.readInt();
		}
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeInt(n);
		for (int i=0; i<n; i++) {
			out.writeInt(x[i]);
			out.writeInt(y[i]);
		}
	}

	public String ToString() {
		String s = n + ":";
		for (int i=0; i<n; i++)
			s += "(" + x[i] + "," + y[i] + ") ";
		return s + "\n";
	}
}
