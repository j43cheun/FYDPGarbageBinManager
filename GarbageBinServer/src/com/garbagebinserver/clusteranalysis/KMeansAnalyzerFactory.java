package com.garbagebinserver.clusteranalysis;

import java.util.LinkedHashSet;

public class KMeansAnalyzerFactory {
  public static KMeansAnalyzer findClusters( int numClusters, int maxIterations, LinkedHashSet<Coordinates> coordinatesSet ) {
    KMeansAnalyzer kmeansAnalyzer = new KMeansAnalyzer( numClusters, maxIterations, coordinatesSet );
    
    try {
      kmeansAnalyzer.init();
      kmeansAnalyzer.run();
    }
    catch ( Exception e ) {
      System.out.println("landing in here!");
      e.printStackTrace();
      kmeansAnalyzer = null;
    }
    
    return kmeansAnalyzer;
  }
}
