package picasso.main;

import java.io.DataInputStream;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class Main2 {
	public static final boolean kDebug = true;
	public static NXTConnection connection = null;
	public static DataInputStream dataIn = null;
	public static DifferentialPilot pilot = new DifferentialPilot(1.0, 3.0, Motor.A, Motor.B);
	public static int w = 1, h = 1;
	public static double angle = 0;
	public static double x = 0, y = 0;
	public static double rad = 200.0;

	private static void Connect() {
		try{
			Main2.connection.close();
		} catch (Exception e) { }

		System.out.print("connect...");
		Main2.connection = Bluetooth.waitForConnection();
		Main2.connection.setIOMode(NXTConnection.RAW);
		Main2.dataIn = Main2.connection.openDataInputStream();
		System.out.println("done.");

		try {
			w = dataIn.readInt();
			h = dataIn.readInt();
			x = (double)Main2.dataIn.readInt();
			y = (double)Main2.dataIn.readInt();
		} catch (Exception e) { }
	}

	private static void Steer(double dTheta) {
		double deg = dTheta * 180.0 / Math.PI;
		pilot.steer((deg < 0) ? -rad : rad, deg);
		angle += dTheta;
	}

	private static void Travel(double distance) {
		pilot.travel(distance * 0.05);
		double dx = distance * Math.sin(angle);
		double dy = distance * Math.cos(angle);
		x += dx;
		y += dy;
	}

	private static double DTheta(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;

		double theta = Math.atan2(dy, dx);
		return (theta - angle);
	}

	public static void main(String args[]) {
		System.out.println("Hello World!\n");

		pilot.setTravelSpeed(1.0);

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

		boolean penUp = false;

		while (true) {
			try {
				int nx = Main2.dataIn.readInt();
				if (nx < 0)
					if (!penUp) {
						Motor.C.rotate(60, false);
						penUp = true;
					}
				else {
					int ny = Main2.dataIn.readInt();

					double xx = (double)nx;
					double yy = (double)ny;

					double dTheta = DTheta(x, y, xx, yy);

					double dx = xx - x;
					double dy = yy - y;
					double distance = Math.sqrt(dx * dx + dy * dy);

					Steer(dTheta);
					Travel(distance) 
					x = xx; y = yy;

					if (penUp) {
						Motor.C.rotate(-60, false);
						penUp = false;
					}

					if (kDebug) Delay.msDelay(200);
				}

				Thread.sleep(100);
			} catch (Exception e) { } 
		}
	}
}
