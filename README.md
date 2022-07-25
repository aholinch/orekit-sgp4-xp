# orekit-sgp4-xp
This project integrates USSF binaries for SGP4 and SGP4-XP propagators into Orekit.  The new SGP4-XP propagator is very, very accurate for high orbits.  See my recent conference [paper](https://amostech.com/TechnicalPapers/2021/Astrodynamics/Holincheck.pdf) for more information. 

The binaries are available from the [space-track.org](https://space-track.org) website.  After you login, go to Help, select SGP4, and you should see a zip file you can download.  If you click it, read and agree to the license, the zip file will download.

This repository was built and tested using v8.3 of the Spg4Prop zip file.  The file downloaded from space-track.org contains documentation, sample code, wrappers, and binaries.

To call binaries from Java, you can use JNA or JNI.  I've done a sample implementation of each following the examples provided by USSF.

This repository is setup as an eclipse project that has all of the dependencies you need to compile the `TLEPropagator` instances for Orekit.  We have one class that uses JNA, `USSFJnaTLEPropagator`, and one that uses JNI, `USSFJnaTLEPropagator`.

# Running the Code
To run the code in this repository you need the following
1. Orekit Data directory
2. The binaries from Sgp4Prop.zip
3. The JNI binaries if you want to use JNI.
4. The SGP4_Open_License.txt file.

## Orekit Data
You need to download the data file from [here](https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip).  Unzip the file, and remember the full path to the directory.  If you unzip the file and call it orekit-data and have it located in your home directory, Orekit should find it at runtime.  If not, specify and environment variable for orekit_data, or pass a startup parameter to java named orekit.data.

## Binaries from Sgp4Prop
On linux, the shared object files must be available in a directory specified by the LD_LIBRARY_PATH directory.  From the directory where you unzipped the Sgp4Prop zip file, you can copy the appropriate directory (32-bit or 64-bit) or just specify the path.  The relative path is Sgp4Prop/Lib/Linux64.

On Windows, the directory with the DLLs needs to be specified in the PATH environment variable.  Sgp4Prop/Lib/Win64.

## JNI Binaries
If you want to use the JNI version, copy the compiled binaries to the other library folder you just setup.  The binaries for linux are located Sgp4Prop/SampleCode/Java/jni/c_jni_export/lib/Linux64.

The JNI Binaries for Windows would need to be copied from Sgp4Prop/SampleCode/Java/jni/c_jni_export/lib/Win64.

**Note:** the JNI binaries in v8.3 for linux seem to have been compiled with a debug statement left uncommented that prints a message to standard out every time a .dll or .so is loaded into memory.
 
## License File
At runtime, the USSF code checks for the presence of the SGP4_Open_License.txt file.  With version 8.3, it seems to be happy with the file being in the same directory as the binaries.  However, you may need to create a copy of the file in the directory where you are running the code.

# Running the test from the command line
If you don't build the project in Eclipse, you can use the command line scripts to compile and run the test.  On linux run `buildit.sh` to compile.  To run the tests, edit `runit.sh` to set the `orekit_data` environment variable and the `LD_LIBRARY_PATH` environment variable.  Then execute `runit.sh`.

For windows, make sure that you have a `javac` command on your path that is 1.8 or later.  Run `buildit.bat` to compile it.  Edit `runit.bat` to set `orekit_data` and add the Win64 library directory to your `PATH` environment variable.  Then execute `runit.bat`. 


# Integrating with Orekit
Each implementation (JNI or JNA) has a static method for `selectExtrapolator` that takes a `TLE` as an argument and returns a `TLEPropagator`.  You can all this method directly if you want to easily control when the USSF binaries are called.  If you want to seamlessly get the USSF version whenever an SGP4-XP tle is passed to the base `TLEPropagator` class, then feel free to replace the one in your version of Orekit with the one here.  Because the USSF binaries are currently the only way to use the new SGP4-XP propagator, pass all TLEs with the ephemeris type of 4 to the new classes.  Again use either JNI or JNA.  Theres no need to use both.  You can copy these few lines of code into the existing `selectExtrapolator` method in `TLEPropagator` right above where it decides between `SGP4` and `SDP4`.  These binaries can replace both of those propagators as well, though the implements in Orekit are definitely accurate.

        /******************************************************************************************/
        /*  Insert prefered JNA or JNI implementation here, use for just SGP4-XP or all flavors   */
        /******************************************************************************************/
        if(tle.getEphemerisType() == 4)
        {
        	// The value '4' signifies SGP4-XP.  In older libraries it meant SGP8, but no one uses that.
        	return new USSFJnaTLEPropagator(tle,attitudeProvider,mass,teme);
        	//return new USSFJniTLEPropagator(tle,attitudeProvider,mass,teme);
        }
