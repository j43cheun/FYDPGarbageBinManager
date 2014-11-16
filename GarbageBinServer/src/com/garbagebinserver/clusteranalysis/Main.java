package com.garbagebinserver.clusteranalysis;

import java.util.LinkedHashSet;

public class Main {
  public static void main(String[] args) {
    LinkedHashSet<Coordinates> coordinatesSet = new LinkedHashSet<Coordinates>();
    GPSCoordinates a = new GPSCoordinates( 43.877336, -79.371222 );
    GPSCoordinates b = new GPSCoordinates( 43.877800, -79.370567 );
    GPSCoordinates c = new GPSCoordinates( 43.877676, -79.372048 );
    GPSCoordinates d = new GPSCoordinates( 43.879401, -79.367370 );
    GPSCoordinates e = new GPSCoordinates( 43.878821, -79.366834 );
    GPSCoordinates f = new GPSCoordinates( 43.879377, -79.366308 );
    coordinatesSet.add( a );
    coordinatesSet.add( b );
    coordinatesSet.add( c );
    coordinatesSet.add( d );
    coordinatesSet.add( e );
    coordinatesSet.add( f );
    KMeansAnalyzer kmeansAnalyzer = KMeansAnalyzerFactory.findClusters(1, 2, coordinatesSet);
    LinkedHashSet<KMeansCluster> clusters = kmeansAnalyzer.getClusters();
    
    for( KMeansCluster cluster : clusters ) {
      GPSCoordinates centroid = (GPSCoordinates)cluster.getCentroid();
      System.out.println("lat: " + centroid.getLatitude() + " long: " + centroid.getLongitude());
    }
  }
}
