/**
 * This code combines USSF Sgp4Prop library with Orekit.
 * See both licenses: SGP4_Open_License.txt and Apache License 2.0
 */

package org.orekit.propagation.analytical.tle;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.InertialProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;

import afspc.astrostds.wrappers.JniDllMain;
import afspc.astrostds.wrappers.JniSgp4Prop;
import afspc.astrostds.wrappers.JniTle;

public class USSFJniTLEPropagator extends TLEPropagator 
{
	/**
	 * Reference to native code structure for TLE
	 */
    protected long satKey = -1;
    
    /**
     * Holds native code error value
     */
    protected int errCode = -1;
    
    protected boolean cleanupEveryTime = false;
    
	protected USSFJniTLEPropagator(TLE initialTLE, AttitudeProvider attitudeProvider, double mass) {
		super(initialTLE, attitudeProvider, mass);
	}

    protected USSFJniTLEPropagator(final TLE initialTLE,
            final AttitudeProvider attitudeProvider,
            final double mass,
            final Frame teme) 
    {
    	super(initialTLE, attitudeProvider, mass,teme);
    }
    
	@Override
	protected void sxpInitialize() {
		// initialize the native code
		JniDllMain.SetElsetKeyMode(JniDllMain.ELSET_KEYMODE_DMA);
		initJni();
	}

	@Override
	protected void sxpPropagate(double t) {
		// we won't use this pattern because computePVCoordinates is private
	}
	
    /** Get the extrapolated position and velocity from an initial TLE.
     * @param date the final date
     * @return the final PVCoordinates
     */
    public PVCoordinates getPVCoordinates(final AbsoluteDate date) {

        // super class makes computePVCoordinates a private method so we can't use this patter
        //sxpPropagate(date.durationFrom(tle.getDate()) / 60.0);

        // Compute PV with previous calculated parameters
        //return computePVCoordinates();
        
    	PVCoordinates pvc = null;
    	try
    	{
    		initJni();
	    	double dt = date.durationFrom(tle.getDate())/60.0d;
	
	        double[] pos = new double[3];   // Position (km) in TEME of Epoch
			double[] vel = new double[3];   // Velocity (km/s) in TEME of Epoch
			double[] llh = new double[3];   // Latitude(deg), Longitude(deg), Height above Geoid (km)
			
			double[] ds50UTC = new double[1];
			
		  	// propagate the initialized TLE to the specified time in minutes since epoch
		  	JniSgp4Prop.Sgp4PropMse(satKey, dt, ds50UTC, pos, vel, llh);
		  	// default units convert to meters
		  	final Vector3D posv = new Vector3D(pos[0]*1000,pos[1]*1000,pos[2]*1000);
		  	final Vector3D velv = new Vector3D(vel[0]*1000,vel[1]*1000,vel[2]*1000);
		  	
		  	pvc = new PVCoordinates(posv,velv);
    	}
    	catch(Exception ex)
    	{
    		Logger.getLogger(USSFJniTLEPropagator.class.getName()).log(Level.WARNING,"Error propagating TLE",ex);
    	}
    	finally
    	{
    		// It may be advantageous to delete native memory every call, but performance may suffer
    		// set this flag to true to cleanup tle in native memory after every call
    		if(cleanupEveryTime)cleanUpJni();
    	}
    	
    	return pvc;
    }
    

    public static TLEPropagator selectExtrapolator(final TLE tle) {
    	Frame teme = FramesFactory.getTEME();
    	
    	USSFJniTLEPropagator prop = new USSFJniTLEPropagator(tle,new InertialProvider(teme),DEFAULT_MASS,teme);
    	
    	return prop;
    }
    
    protected void initJni()
    {
    	if(satKey == -1)
    	{
    		try
    		{
	    		satKey = JniTle.TleAddSatFrLines(tle.getLine1(),tle.getLine2());
	
	    		errCode = JniSgp4Prop.Sgp4InitSat(satKey);
    		}
    		catch(Exception ex)
    		{
    			Logger.getLogger(USSFJniTLEPropagator.class.getName()).log(Level.WARNING,"Error initializing TLE",ex);
    		}
    	}
    	
    	return;
    }
    
    protected void cleanUpJni()
    {
    	if(satKey != -1)
    	{
    		try
    		{
		 	   	// Remove loaded satellites if no longer needed
		 	   	JniTle.TleRemoveSat(satKey);   // remove loaded TLE from memory
		 	   	JniSgp4Prop.Sgp4RemoveSat(satKey);  // remove initialized TLE from memory
    		 	satKey = -1;
    		}
    		catch(Exception ex)
    		{
    			Logger.getLogger(USSFJniTLEPropagator.class.getName()).log(Level.WARNING,"Error cleaning up TLE",ex);
    		}
    		finally
    		{
    			// we really want satKey = -1;
    			satKey = -1;
    		}
    	}
    	
    	return;
    }

    protected void finalize() throws Throwable  
    {
    	cleanUpJni();
    }
}
