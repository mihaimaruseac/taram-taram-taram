
package picasso.main;

import java.util.LinkedList;

import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class Slave implements Runnable 
{
	private DifferentialPilot pilot = null;
	
	public double travelSpeed = 2.0;
	
	public double radius = 1.0;
	
	public int step = 3;
	
	private double x = 0, y = 0;
	
	private double angle = 0;
	
	public double epsilon = 0.01;
	
	private LinkedList<Curve> queue = new LinkedList<Curve>();
	
	public Slave(double wheelDiameter, double trackWidth, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) 
	{
		pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor);
		pilot.setTravelSpeed(travelSpeed);
	}

	public void run() 
	{
		while (true)
		{
			try {
				//wait(); // mutex
				//mutex.wait(); // mutex
				Thread.sleep(100);
			} catch (Exception e) { }
			
			while (queue.size() > 0)
			{
				process(queue.remove(0));
			}
		}
	}
	
	public void add(Curve c)
	{
		queue.add(c);
	}
	
	public void process(Curve c)
	{
		if (pilot == null)
		{
			System.err.println("Error: no pilot");
			return;
		}

		double X = (double)c.GetXKth(0, 0);
		double Y = (double)c.GetYKth(0, 0);
		// goto start (X, Y);
		
		for (int i=0; i<c.n-1; i++)
		{
			double x1 = c.GetXKth(i, 0);
			double y1 = c.GetYKth(i, 0);
			
			double x2 = c.GetXKth(i, step);
			double y2 = c.GetYKth(i, step);
			
			double dx = x2 - x1;
			double dy = y2 - y1;
			double theta = Math.atan2(dy, dx);
			
			double dTheta = theta - angle;
			
			if (Math.abs(angle) >= Math.PI) System.out.println("x");
			if (Math.abs(angle) >= Math.PI/2) System.out.println("y");
			//System.out.println(angle + "/" + theta + "/" + dTheta);
			
			if (Math.abs(dTheta) <= epsilon)
				pilot.travel(radius);
			else 
			{
				double rad = 100.0;
				
				double deg = dTheta * 180.0 / Math.PI;
				//pilot.steer(radius, deg);
				pilot.steer(
						(deg < 0) ? -rad : rad, 
						deg);
				angle += dTheta;
			}
			Delay.msDelay(1000);
		}
		
		/*
		pilot.travel(5);
		for (int i=0; i<2; i++)
		{
			pilot.steer(-50, -10, false);				
			pilot.steer(50, 10, false);
			pilot.steer(-50, 10, false);				
			pilot.steer(50, -10, false);
		}
		*/

		//pilot.steer(1.0, 10, false);
		
		//pilot.forward();
		//pilot.travel(10.0);
		
		/*
		pilot.travel(20.0, true);
		
		for (int i=0; i<6; i++)
		{
			pilot.rotate(-10);
			Delay.msDelay(100);
		}
		*/
		
		//pilot.rotate(-60);
		//pilot.travel(5.0, false);
		//Delay.msDelay(1000);
		//pilot.stop();
	}

}
