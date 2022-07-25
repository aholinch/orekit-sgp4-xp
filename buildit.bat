mkdir bin

set LIBDIR=lib
set JARS=%LIBDIR%\hipparchus\hipparchus-core-2.0.jar;%LIBDIR%\hipparchus\hipparchus-filtering-2.0.jar;%LIBDIR%\hipparchus\hipparchus-optim-2.0.jar;%LIBDIR%\hipparchus\hipparchus-stat-2.0.jar;%LIBDIR%\hipparchus\hipparchus-geometry-2.0.jar;%LIBDIR%\hipparchus\hipparchus-fitting-2.0.jar;%LIBDIR%\hipparchus\hipparchus-ode-2.0.jar;%LIBDIR%\orekit\orekit-11.2.jar;%LIBDIR%\ussfsgp4\afspc.jar;%LIBDIR%\ussfsgp4\jna-5.7.0.jar;%LIBDIR%\ussfsgp4\jna-platform-5.7.0.jar

set JAVAC=javac -classpath bin;%JARS% -d bin
%JAVAC% src\main\org\orekit\propagation\analytical\tle\USSFJniTLEPropagator.java
%JAVAC% src\main\org\orekit\propagation\analytical\tle\USSFJnaTLEPropagator.java
%JAVAC% src\main\org\orekit\propagation\analytical\tle\TLEPropagator.java
%JAVAC% src\test\org\orekit\propagation\analytical\tle\OrekitDataSetup.java
%JAVAC% src\test\org\orekit\propagation\analytical\tle\PropagatorTest.java
