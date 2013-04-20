
package picasso.main;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;
import lejos.nxt.Button;

public class Main 
{
	public static DataInputStream dataIn = null;
	
	public static void main(String [] args)
	{
		System.out.println("Hello World!\n");
		
		byte pin [] = {0, 0, 0, 0};
		Bluetooth.setPin(pin);
		
		Button.ENTER.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) { }
			
			public void buttonPressed(Button b) {
				System.exit(1);
			}
		});
		
		NXTConnection connection = Bluetooth.waitForConnection();
		//if (connection != null)
			Main.dataIn = connection.openDataInputStream();
			
		System.out.println("Online :)\n");
		
		Slave slave = new Slave(2.25f, 5.5f, Motor.A, Motor.B);
		Thread slaveThread = new Thread(slave);
		slaveThread.start();
		
		while (true)
		{
			Curve c = new Curve();
			try {
				//int I = dataIn.readInt(); 
				//System.out.println(Integer.toHexString(I & 0xFF));
				
				//char C = dataIn.readChar();
				//System.out.println(C);
				c.read(dataIn);
			} catch (Exception e) { c = null; }
			if (c != null) 
			{
				slave.add(c);
				//slave.notify(); // mutex
				//slave.mutex.notify(); // mutex
			}
		}
	}	
}
