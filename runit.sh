#!/bin/sh

orekit_data=$HOME/orekit-data
export orekit_data
LD_LIBRARY_PATH=$HOME/Sgp4Prop/Lib/Linux64
export LD_LIBRARY_PATH

LIBDIR=lib
JARS=${LIBDIR}/hipparchus/hipparchus-core-2.0.jar:${LIBDIR}/hipparchus/hipparchus-filtering-2.0.jar:${LIBDIR}/hipparchus/hipparchus-optim-2.0.jar:${LIBDIR}/hipparchus/hipparchus-stat-2.0.jar:${LIBDIR}/hipparchus/hipparchus-geometry-2.0.jar:${LIBDIR}/hipparchus/hipparchus-fitting-2.0.jar:${LIBDIR}/hipparchus/hipparchus-ode-2.0.jar:${LIBDIR}/orekit/orekit-11.2.jar:${LIBDIR}/ussfsgp4/afspc.jar:${LIBDIR}/ussfsgp4/jna-5.7.0.jar:${LIBDIR}/ussfsgp4/jna-platform-5.7.0.jar

JAVA="java -classpath bin:$JARS"
$JAVA org.orekit.propagation.analytical.tle.PropagatorTest
