package picasso.main;

import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class Slave2 implements Runnable {
	private DifferentialPilot pilot = null;
	public double travelSpeed = 2.0;
	public double radius = 1.0;
	public double rad = 100.0;
	private double x, y;
	private double nx, ny;
	private boolean processed = true;
	private double angle = 0;
	public double epsilon = 0.01;
	public Object mutex = new Object();

	public Slave2(double wheelDiameter, double trackWidth, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor);
		pilot.setTravelSpeed(travelSpeed);
		x = y = nx = ny = 0;
	}

	private void Steer(double dTheta) {
		double deg = dTheta * 180.0 / Math.PI;
		pilot.steer((deg < 0) ? -rad : rad, deg);
		angle += dTheta;
	}

	private void Travel(double distance) {
		pilot.travel(distance);
		double dx = distance * Math.sin(angle);
		double dy = distance * Math.cos(angle);
		x += dx;
		y += dy;
	}

	public double DTheta(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;

		double theta = Math.atan2(dy, dx);
		double dTheta = theta - angle;

		return dTheta;
	}

	public void set(double nx, double ny) {
		synchronized (mutex) {
			if (!processed) process();

			this.nx = nx; this.ny = ny;
			System.out.println(x + "," + y + " / " + nx + "," + ny);
			processed = false;
		}
	}

	public void process() {
		synchronized (mutex) {
			if (processed) return;

			if (pilot == null) {
				System.err.println("Error: no pilot");
				return;
			}

			double dTheta = DTheta(x, y, nx, ny);
			double dx = nx - x;
			double dy = ny - y;
			double distance = Math.sqrt(dx * dx + dy * dy);


			Travel(distance);
			Steer(dTheta);

			x = nx; y = ny;
			processed = true;
		}
	}

	public void run() {
		while (true ) {
			try {
				Thread.sleep(100);
			} catch (Exception e) { }
			process();
		}
	}
}
