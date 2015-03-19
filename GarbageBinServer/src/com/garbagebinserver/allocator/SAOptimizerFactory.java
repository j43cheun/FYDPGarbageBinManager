package com.garbagebinserver.allocator;

import java.util.ArrayList;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.clusteranalysis.KMeansCluster;
import com.garbagebinserver.data.GarbageBin;
import com.garbagebinserver.data.GarbageClusterData;
import com.garbagebinserver.data.GarbageSpot;
import com.garbagebinserver.data.ServiceStation;

public class SAOptimizerFactory {
  public static SolutionModel solveAndOptimize( final ArrayList<GarbageBin> garbageBins, 
                                                final ArrayList<GarbageClusterData> garbageClusterDataElements, 
                                                final ArrayList<ServiceStation> serviceStations, 
                                                final double coolingFactor, 
                                                final double initialTemp, 
                                                final double finalTemp,
                                                final double iterPerTemp, 
                                                final double maxIter ) {
    
    SolutionModel initialSolution = new MRTASolutionModel( garbageBins, garbageClusterDataElements, serviceStations );
    SAOptimizer saOptimizer = new SAOptimizer( coolingFactor, initialTemp, finalTemp, iterPerTemp, maxIter );
    SolutionModel optimizedSolution = saOptimizer.optimizeSolution( initialSolution );
    
    return optimizedSolution;
  }
}
