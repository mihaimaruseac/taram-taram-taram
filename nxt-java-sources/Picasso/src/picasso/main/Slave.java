
package picasso.main;

import java.util.LinkedList;

import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class Slave implements Runnable 
{
	public static final boolean kDebug = Main.kDebug;
	
	private DifferentialPilot pilot = null;
	
	public double travelSpeed = 2.0;
	
	public double radius = 1.0;
	
	public double rad = 100.0;
	
	public int step = 3;
	
	private double x = 0, y = 0;
	
	private double angle = 0;
	
	public double epsilon = 0.01;
	
	private LinkedList<Curve> queue = new LinkedList<Curve>();
	
	public Slave(double wheelDiameter, double trackWidth, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) 
	{
		pilot = new DifferentialPilot(wheelDiameter, 
									trackWidth, leftMotor, rightMotor);
		pilot.setTravelSpeed(travelSpeed);
	}

	public void run() 
	{
		while (true)
		{
			try {
				Thread.sleep(100);
			} catch (Exception e) { }
			
			while (queue.size() > 0)
				process(queue.remove(0));
		}
	}
	
	public void add(Curve c)
	{
		queue.add(c);
	}
	
	private void Steer(double dTheta)
	{
		double deg = dTheta * 180.0 / Math.PI;
		pilot.steer((deg < 0) ? -rad : rad, 
				deg);
		angle += dTheta;
	}
	
	private void Travel(double distance)
	{
		pilot.travel(distance);
		double dx = distance * Math.sin(angle);
		double dy = distance * Math.cos(angle);
		x += dx;
		y += dy;
	}
	
	public double DTheta(double x1, double y1, double x2, double y2)
	{
		double dx = x2 - x1;
		double dy = y2 - y1;
		
		double theta = Math.atan2(dy, dx);
		double dTheta = theta - angle;
		
		return dTheta;
	}
	
	public void process(Curve c)
	{
		if (pilot == null)
		{
			System.err.println("Error: no pilot");
			return;
		}
		
		if (c.n <= 2) 
		{
			System.err.println("Error: not enough points");
			return;
		}
		
		double dTheta = DTheta(x, y, 
				(double)c.GetXKth(0, 0), (double)c.GetYKth(0, 0));
		
		double dx = (double)c.GetXKth(0, 0) - x;
		double dy = (double)c.GetYKth(0, 0) - y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		
		Steer(dTheta);
		Travel(distance);
		
		for (int i=0; i<c.n-1; i++)
		{
			dTheta = DTheta(c.GetXKth(i, 0), c.GetYKth(i, 0),
					c.GetXKth(i, step), c.GetYKth(i, step));
			
			if (Math.abs(dTheta) <= epsilon) Travel(radius);
			else Steer(dTheta);
			
if (kDebug) Delay.msDelay(1000);
		}
	}

}
