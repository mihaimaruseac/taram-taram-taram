
package picasso.main;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class Main 
{
	public static final boolean kDebug = true;
	
	public static NXTConnection connection = null;
	
	public static DataInputStream dataIn = null;
	
	public static int w = 1, h = 1;
	
	private static void Connect()
	{
		try{
			Main.connection.close();
		} catch (Exception e) { }
		System.out.print("connect...");
		Main.connection = Bluetooth.waitForConnection();
		Main.connection.setIOMode(NXTConnection.RAW);
		Main.dataIn = Main.connection.openDataInputStream();
		System.out.println("done.");
		
		try {
			Main.w = Main.dataIn.readInt();
			Main.h = Main.dataIn.readInt();
		} catch (Exception e) { }
	}
	
	public static void main(String [] args)
	{
		System.out.println("Hello World!\n");
		
		byte pin [] = {0, 0, 0, 0};
		Bluetooth.setPin(pin);
		
		Button.RIGHT.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) { }
			
			public void buttonPressed(Button b) { Connect(); }
		});
		
		Button.LEFT.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) { }
			
			public void buttonPressed(Button b) {
				System.exit(1);
			}
		});
		
		Connect();
		
		Slave slave = new Slave(1.0f, 3.0f, Motor.A, Motor.B);
		Thread slaveThread = new Thread(slave);
		slaveThread.start();
		
		while (true)
		{
			Curve c = new Curve();
			try {
				//int I = dataIn.readInt(); 
				//System.out.println(I + " ");
				
				c.read(dataIn);
			} catch (Exception e) { c = null; }
			if (c != null) 
			{
				slave.add(c);
			}
		}
	}	
}
