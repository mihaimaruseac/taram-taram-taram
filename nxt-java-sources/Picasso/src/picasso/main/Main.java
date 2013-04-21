
package picasso.main;

import java.io.DataInputStream;

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
	
	public static Slave2 slave = new Slave2(1.0f, 3.0f, Motor.A, Motor.B);
	//public static Slave slave = new Slave(1.0f, 3.0f, Motor.A, Motor.B);
	
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
			
			Main.slave.travelSpeed = 10.0 / 
					(double)Math.max(Main.w, Main.h);
			
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
		
		Thread slaveThread = new Thread(Main.slave);
		slaveThread.start();
		
		/*
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
		*/
		
		while (true)
		{
			synchronized (slave.mutex) {
				try{
					int x = Main.dataIn.readInt();
					if (x < 0)
					{
						// todo:
					}
					else 
					{
						int y = Main.dataIn.readInt();
						slave.set((double)x, (double)y);
					}
				} catch (Exception e) { }
			}
		}
	}	
}
