/**
 * This code combines USSF Sgp4Prop library with Orekit.
 * See both licenses: SGP4_Open_License.txt and Apache License 2.0
 */
package org.orekit.propagation.analytical.tle;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;

import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
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
		
		main1();
	}
	

	public static void main1() 
	{
		try
		{

			
			String line1 = null;
			String line2 = null;
			
			line1 = "1 99999U          21008.00000000 +.00000000 +30976-1 +00000-0 4    00";
			line2 = "2 99999 050.5192 353.2068 1658374 106.0367 124.4650 01.85517713    00";
			//line1 = "1 25984U 99065E   22001.16913692  .00000296  00000-0  15764-3 0  9992";
			//line2 = "2 25984  45.0387 286.1141 0002151  61.6197 298.4867 14.33180775153001";
			
			TLE tle = new TLE(line1,line2);
			
			Frame teme = FramesFactory.getTEME();
			Frame itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
	        
			BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
					Constants.WGS84_EARTH_FLATTENING,
					itrf);
			
			GeodeticPoint loc = null;
			Vector3D pos = null;
			AbsoluteDate ad = null;
			PVCoordinates pv = null;
			/*
			for(int i=0; i<size; i++)
			{
				pos = toPV(cart).getPosition();
				ad = toAD(cart.getEpoch());
				loc = earth.transform(pos, frame, ad);
			}
			*/
			
			ad = tle.getDate();

			// when mixing and matching jni and jna, jni really wants to be initialized first
			TLEPropagator prop3 = USSFJniTLEPropagator.selectExtrapolator(tle);

			TLEPropagator prop1 = TLEPropagator.selectExtrapolator(tle);
			TLEPropagator prop2 = USSFJnaTLEPropagator.selectExtrapolator(tle);
			for(int i=0; i<100; i++)
			{
				pv = prop1.getPVCoordinates(ad);
				System.out.println(pv);
				pv = prop2.getPVCoordinates(ad);
				System.out.println(pv);
				pv = prop3.getPVCoordinates(ad);
				System.out.println(pv);
				pos = pv.getPosition();
				loc = earth.transform(pos, teme, ad);
				System.out.println(loc);
				ad = ad.shiftedBy(300);
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.WARNING,"Error running propagators",ex);
		}
	}

}
