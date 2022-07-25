/**
 * This code combines USSF Sgp4Prop library with Orekit.
 * See both licenses: SGP4_Open_License.txt and Apache License 2.0
 */
package org.orekit.propagation.analytical.tle;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hipparchus.geometry.euclidean.threed.Vector3D;

import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;

/**
 * This is not configured with Junit to cutdown on dependencies for this package.
 * Getting the OREKITDATA configured and the LD_LIBRARY_PATH is tricky enough.
 *
 */
public class PropagatorTest 
{
	private static final Logger logger = Logger.getLogger(PropagatorTest.class.getName());
	
	public static void main(String args[])
	{
		OrekitDataSetup.initOrekitData();
		
		test1();
		
		test2();
		
		System.exit(0);
		
	}
	
	public static boolean isEqual(PVCoordinates pv1, PVCoordinates pv2, PVCoordinates pv3, double tol) throws Exception
	{
		Vector3D v1 = null;
		Vector3D v2 = null;
		
		// compare positions
		v1 = pv1.getPosition();
		v2 = pv2.getPosition();
		if(diff(v1,v2)>tol)
		{
			throw new Exception("Difference exceeds tolerance: " + v1 + ", " + v2);
		}
		v1 = pv1.getPosition();
		v2 = pv3.getPosition();
		if(diff(v1,v2)>tol)
		{
			throw new Exception("Difference exceeds tolerance: " + v1 + ", " + v2);
		}
		v1 = pv2.getPosition();
		v2 = pv3.getPosition();
		if(diff(v1,v2)>tol)
		{
			throw new Exception("Difference exceeds tolerance: " + v1 + ", " + v2);
		}
		
		
		// compare velocities
		v1 = pv1.getVelocity();
		v2 = pv2.getVelocity();
		if(diff(v1,v2)>tol)
		{
			throw new Exception("Difference exceeds tolerance: " + v1 + ", " + v2);
		}
		v1 = pv1.getVelocity();
		v2 = pv3.getVelocity();
		if(diff(v1,v2)>tol)
		{
			throw new Exception("Difference exceeds tolerance: " + v1 + ", " + v2);
		}
		v1 = pv2.getVelocity();
		v2 = pv3.getVelocity();
		if(diff(v1,v2)>tol)
		{
			throw new Exception("Difference exceeds tolerance: " + v1 + ", " + v2);
		}

		return true;
	}
	
	public static double diff(Vector3D v1, Vector3D v2) throws Exception
	{
		double n1 = v1.getNorm();
		
		if(n1 < 1000)
		{
			throw new Exception("Tiny magnitude not expected: " + v1 +", "+v2);
		}
		
		double diff = v1.subtract(v2).getNorm();
		diff = diff/n1;
		
		return diff;
	}
	
	
	/**
	 * Ensure the original TLEPropagator and the two that use the USSF binaries give the same answer for a regular SGP4 TLE.
	 */
	public static void test1() 
	{
		try
		{
			String line1 = null;
			String line2 = null;

			
			//https://live.ariss.org/tle/
			line1 = "1 25544U 98067A   22164.21264515  .00005702  00000-0  10791-3 0  9996";
			line2 = "2 25544  51.6447 356.2727 0004326 230.6083 279.1425 15.49973985344560";
			
			TLE tle = new TLE(line1,line2);
			
			System.out.println("\n\n\n");
			System.out.println("Testing SGP4 with the following TLE\n");
			System.out.println(line1);
			System.out.println(line2);

			AbsoluteDate ad = null;
			PVCoordinates pv1 = null;
			PVCoordinates pv2 = null;
			PVCoordinates pv3 = null;
			
			
			ad = tle.getDate();

			// when mixing and matching jni and jna, jni really wants to be initialized first
			TLEPropagator prop3 = USSFJniTLEPropagator.selectExtrapolator(tle);

			TLEPropagator prop1 = TLEPropagator.selectExtrapolator(tle);
			TLEPropagator prop2 = USSFJnaTLEPropagator.selectExtrapolator(tle);
			
			for(int i=0; i<100; i++)
			{
				pv1 = prop1.getPVCoordinates(ad);
				System.out.println(pv1);
				pv2 = prop2.getPVCoordinates(ad);
				System.out.println(pv2);
				pv3 = prop3.getPVCoordinates(ad);
				System.out.println(pv3);
				
				isEqual(pv1,pv2,pv3,1e-12);  // this is better than mm agreement in position
				ad = ad.shiftedBy(300);
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.WARNING,"Error running propagators",ex);
		}
	}

	/**
	 * Ensure the TLEPropagator and the two that use the USSF binaries give the same answer for a TLE using SGP4-XP.
	 */
	public static void test2() 
	{
		try
		{
			String line1 = null;
			String line2 = null;
			
			//https://github.com/aholinch/amos2021/tree/main/sgp4xp
			line1 = "1 99999U          21008.00000000 +.00000000 +30976-1 +00000-0 4    00";
			line2 = "2 99999 050.5192 353.2068 1658374 106.0367 124.4650 01.85517713    00";
			
			TLE tle = new TLE(line1,line2);
			
			System.out.println("\n\n\n");
			System.out.println("Testing SGP4-XP with the following TLE\n");
			System.out.println(line1);
			System.out.println(line2);
			
			AbsoluteDate ad = null;
			PVCoordinates pv1 = null;
			PVCoordinates pv2 = null;
			PVCoordinates pv3 = null;
			
			
			ad = tle.getDate();

			// when mixing and matching jni and jna, jni really wants to be initialized first
			TLEPropagator prop3 = USSFJniTLEPropagator.selectExtrapolator(tle);

			TLEPropagator prop1 = TLEPropagator.selectExtrapolator(tle);
			TLEPropagator prop2 = USSFJnaTLEPropagator.selectExtrapolator(tle);
			
			for(int i=0; i<100; i++)
			{
				pv1 = prop1.getPVCoordinates(ad);
				System.out.println(pv1);
				pv2 = prop2.getPVCoordinates(ad);
				System.out.println(pv2);
				pv3 = prop3.getPVCoordinates(ad);
				System.out.println(pv3);
				
				isEqual(pv1,pv2,pv3,1e-15);  // this is better than mm agreement in position
				ad = ad.shiftedBy(300);
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.WARNING,"Error running propagators",ex);
		}
	}

}
