package com.garbagebinserver.clusteranalysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

public class KMeansAnalyzer implements ClusterAnalyzer {

  private int m_nextClusterID;
  private int m_numClusters;
  private int m_maxIterations;
  private double m_cost;
  private LinkedHashSet<KMeansCluster> m_clusters;
  private LinkedHashSet<Coordinates> m_coordinatesSet;
  private LinkedHashMap<Coordinates, KMeansCluster> m_coordinatesToClusterMap;
  
  public KMeansAnalyzer( int numClusters, int maxIterations, LinkedHashSet<Coordinates> coordinatesSet, int nextClusterID ) {
    if( numClusters <= 0 ) {
      throw new IllegalArgumentException( "Cannot perform K-Means cluster analysis on zero or fewer clusters!" );
    }
    
    if( maxIterations <= 0 ) {
      throw new IllegalArgumentException( "Cannot perform K-Means cluster analysis on zero or fewer iterations!" );
    }
    
    if( coordinatesSet == null ) {
      throw new IllegalArgumentException( "Cannot perform K-Means cluster analysis on a null coordinate set!" );
    }
    
    if( coordinatesSet.size() < numClusters ) {
      throw new IllegalArgumentException( "Cannot perform K-Means cluster analysis on a coordinate set with fewer coordinates than clusters!" );
    }
    
    m_numClusters = numClusters;
    m_maxIterations = maxIterations;
    m_coordinatesSet = coordinatesSet;
    m_nextClusterID = nextClusterID;
    m_cost = -1;
  }
  
  public LinkedHashSet<KMeansCluster> getClusters() {
    return m_clusters;
  }
  
  public double getCost() {
    return m_cost;
  }
  
  public void init() throws Exception {
    m_clusters = new LinkedHashSet<KMeansCluster>();
    m_coordinatesToClusterMap = new LinkedHashMap<Coordinates, KMeansCluster>();
    
    ArrayList<Coordinates> coordinatesList = new ArrayList<Coordinates>( m_coordinatesSet );
    Random random = new Random(System.currentTimeMillis());
    
    Coordinates clusterCentroid = coordinatesList.remove( random.nextInt( coordinatesList.size() ) );
    m_clusters.add( new KMeansCluster( clusterCentroid, m_nextClusterID++ ) );
    
    for( int i = 1; i < m_numClusters; ++i ) {
      double currentTotalMinDistanceSquared = 0;
      double[] rouletteWheel = new double[coordinatesList.size()];
      
      for( int j = 0; j < coordinatesList.size(); ++j ) {
        double minDistanceSquared = -1;
        
        for( KMeansCluster cluster : m_clusters ) {
          double distanceToCentroid = coordinatesList.get( j ).getDistance( cluster.getCentroid() );
          double distanceToCentroidSquared = distanceToCentroid * distanceToCentroid;
          
          if( minDistanceSquared == -1 || distanceToCentroidSquared < minDistanceSquared ) {
            minDistanceSquared = distanceToCentroidSquared;
          }
        }
        
        currentTotalMinDistanceSquared += minDistanceSquared;
        rouletteWheel[j] = currentTotalMinDistanceSquared;
      }
      
      double randomDraw = random.nextDouble() * currentTotalMinDistanceSquared;
      int imin = 0;
      int imax = coordinatesList.size() - 1;
      Coordinates nextClusterCentroid = null;
      
      while( imax >= imin ) {
        int imid = imin + ( imax - imin ) / 2;
        double pinwheelPrevious = ( imid > 0 ) ? rouletteWheel[imid - 1] : 0;
        
        if( randomDraw > pinwheelPrevious && randomDraw <= rouletteWheel[imid] ) {
          nextClusterCentroid = coordinatesList.remove( imid );
          m_clusters.add( new KMeansCluster( nextClusterCentroid, m_nextClusterID++ ) );
          break;
        }
        else if( randomDraw > rouletteWheel[imid] ) {
          imin = imid + 1;
        }
        else if( randomDraw < pinwheelPrevious ) {
          imax = imid - 1;
        }
        else {
          throw new Exception( "Unreachable case for binary search!" );
        }
      }
    }
  }
  
  @Override
  public void run() throws Exception {
    if( m_clusters == null || m_coordinatesToClusterMap == null ) {
      throw new Exception( "Must initialize K-Means analyzer before attempting to run K-Means cluster analysis!" );
    }
    
    double cost = 0;
    
    for( int idx = 0; idx < m_maxIterations; ++idx ) {
      boolean assignmentsNoChange = true;
      
      for( Coordinates coordinates : m_coordinatesSet ) {
        double minDistanceSquared = -1;
        KMeansCluster closestCluster = null;
        
        for( KMeansCluster cluster : m_clusters ) {
          double distanceToCentroid = cluster.getDistanceToCentroid( coordinates );
          double distanceToCentroidSquared = distanceToCentroid * distanceToCentroid;
          
          if( minDistanceSquared == -1 || minDistanceSquared > distanceToCentroidSquared ) {
            minDistanceSquared = distanceToCentroidSquared;
            closestCluster = cluster;
          }
        }
        
        KMeansCluster previousCluster = m_coordinatesToClusterMap.get( coordinates );
        
        if( previousCluster == null ) {
          m_coordinatesToClusterMap.put( coordinates, closestCluster );
          closestCluster.addClusterPoint( coordinates );
          assignmentsNoChange = false;
        }
        else if( previousCluster != closestCluster ) {
          previousCluster.removeClusterPoint( coordinates );
          m_coordinatesToClusterMap.put( coordinates, closestCluster );
          closestCluster.addClusterPoint( coordinates );
          assignmentsNoChange = false;
        }
        
        cost += minDistanceSquared;
      }
      
      m_cost = cost;
      
      if( assignmentsNoChange ) {
        break;
      }
      
      for( KMeansCluster cluster : m_clusters ) {
        cluster.updateCentroid();
      }
    }
    
    for( KMeansCluster kmeansCluster : m_clusters ) {
      for( Coordinates coordinate : kmeansCluster.getClusterPoints() ) {
        coordinate.setClusterID( kmeansCluster.getClusterID() );
      }
    }
  }
}
