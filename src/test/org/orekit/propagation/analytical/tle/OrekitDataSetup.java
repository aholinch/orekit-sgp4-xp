/**
 * This code combines USSF Sgp4Prop library with Orekit.
 * See both licenses: SGP4_Open_License.txt and Apache License 2.0
 */
package org.orekit.propagation.analytical.tle;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

public class OrekitDataSetup 
{
	private static final Logger logger = Logger.getLogger(OrekitDataSetup.class.getName());
	
	public static void initOrekitData()
	{
		try
		{
			final File home       = new File(System.getProperty("user.home"));
			File orekitData = getOrekitDataDir();
	    	
	        if (!orekitData.exists()) {
	            System.err.format(Locale.US, "Failed to find %s folder%n",
	                              orekitData.getAbsolutePath());
	            System.err.format(Locale.US, "You need to download %s from %s, unzip it in %s and rename it 'orekit-data' for this tutorial to work%n",
	                              "orekit-data-master.zip", "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
	                              home.getAbsolutePath());
	        }
	        final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
	        manager.addProvider(new DirectoryCrawler(orekitData));
		}
		catch(Exception ex)
		{
			logger.log(Level.WARNING,"Error setting up Orekit Data",ex);
		}
	}
	
    public static File getOrekitDataDir()
    {
		File f = searchExistingDir("orekit-data","orekit-data-master","orekit_data","orekit.data");
		if(f != null)
		{
			return f;
		}
    	return null;
    }
    
    /**
     * Looks in env param, java define param, running dir, and then home for directory.
     * 
     * @param dirEnd
     * @param env
     * @param param
     * @return
     */
    public static File searchExistingDir(String name1, String name2, String env, String param)
    {
    	File f = null;
    	String path = null;
    	// check env first
    	if(env != null)
    	{
    		path = System.getenv(env);
    		if(path != null)
    		{
    			f = new File(path);
    		}
    	}
    	
    	if(f==null || !f.exists())
    	{
    		// check define param
    		if(param != null)
    		{
    			path = System.getProperty(param);
    			if(path != null)
    			{
    				f = new File(path);
    			}
    		}
    	}
    	
    	if(f == null || !f.exists())
    	{
    		// check home directory
    		final File home = new File(System.getProperty("user.home"));
    		f = new File(home,name1);
    		if(!f.exists() && name2 != null)
    		{
    			f = new File(home,name2);
    		}
    	}
    	
    	if(f == null || !f.exists())
    	{
    		// check current directory
    		f = new File(name1);
    		if(!f.exists() && name2 != null)
    		{
    			f = new File(name2);
    		}
    	}
    	
    	if(f == null || !f.exists())
    	{
    		logger.warning("Unable to find dir for " + String.valueOf(name1) + " after checking multiple places");
    	}
    	
    	return f;
    }
	
}
