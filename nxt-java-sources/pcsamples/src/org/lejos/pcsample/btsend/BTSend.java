package org.lejos.pcsample.btsend;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.util.Delay;

/**
 * This is a PC sample. It connects to the NXT, and then
 * sends an integer and waits for a reply, 100 times.
 * 
 * Compile this program with javac (not nxjc), and run it 
 * with java.
 * 
 * You need pccomm.jar and bluecove.jar on the CLASSPATH. 
 * On Linux, you will also need bluecove-gpl.jar on the CLASSPATH.
 * 
 * Run the program by:
 * 
 *   java BTSend 
 * 
 * Your NXT should be running a sample such as BTReceive or
 * SignalTest. Run the NXT program first until it is
 * waiting for a connection, and then run the PC program. 
 * 
 * @author Lawrie Griffiths
 *
 */
public class BTSend {	
	public static void main(String[] args) {
		
		NXTConnector conn = new NXTConnector();
	
		conn.addLogListener(new NXTCommLogListener(){

			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: "+message);
				
			}

			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				 throwable.printStackTrace();	
			}
		} 
		);
		
		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://");
	
		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}
		
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		Random rand = new Random();
				
		try{
			int n = 9;
			
			int [] x = new int [n];
			int [] y = new int [n];
			
			x[0] = 0; y[0] = 0;
			x[1] = 1; y[1] = 0;
			x[2] = 2; y[2] = 1;
			x[3] = 3; y[3] = 2;
			x[4] = 4; y[4] = 4;
			x[5] = 4; y[5] = 6;
			x[6] = 1; y[6] = 7;
			x[7] = -2; y[7] = 6;
			x[8] = -3; y[8] = 3;
			
			Curve c = new Curve(n, x, y);
			c.write(dos);		
			System.out.println(c.ToString());
			dos.flush();
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		//Delay.msDelay(5000);
		
		try {
			dos.close();
			conn.close();
		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
	}
}
