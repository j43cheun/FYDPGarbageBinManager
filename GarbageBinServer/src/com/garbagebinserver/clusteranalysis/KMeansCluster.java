package com.garbagebinserver.clusteranalysis;

import java.util.LinkedHashSet;

public class KMeansCluster implements Cluster {

  protected int m_clusterID;
  protected Coordinates m_centroid;
  protected LinkedHashSet<Coordinates> m_clusterPoints;
  
  public KMeansCluster( Coordinates initialCentroid ) throws IllegalArgumentException {
    if( initialCentroid == null ) {
      throw new IllegalArgumentException( "Cannot set centroid to null!" );
    }
    
    m_clusterID = -1;
    m_centroid = initialCentroid;
    m_clusterPoints = new LinkedHashSet<Coordinates>();
  }
  
  public Coordinates getCentroid() {
    return m_centroid;
  }
  
  public void setCentroid( Coordinates centroid ) throws IllegalArgumentException {
    if( centroid == null ) {
      throw new IllegalArgumentException( "Cannot set centroid to null!" );
    }
    
    m_centroid = centroid;
  }
  
  public void updateCentroid() {
    Coordinates previousCentroid = m_centroid;
    
    try {
      m_centroid = MidpointCalculator.computeMidpoint( m_centroid.getClass(), m_clusterPoints );
    }
    catch( Exception e ) {
      e.printStackTrace();
      m_centroid = previousCentroid;
    }
  }
  
  public double getDistanceToCentroid( Coordinates clusterPoint ) throws IllegalArgumentException{
    if( clusterPoint == null ) {
      throw new IllegalArgumentException( "Cannot get distance from null coordinates to centroid!" );
    }
    
    double distanceToCentroid;
    
    try {
      distanceToCentroid = clusterPoint.getDistance( m_centroid );
    }
    catch( Exception e ) {
      e.printStackTrace();
      distanceToCentroid = -1;
    }
    
    return distanceToCentroid;
  }
  
  public LinkedHashSet<Coordinates> getClusterPoints() {
    return m_clusterPoints;
  }
  
  public void addClusterPoint( Coordinates clusterPoint ) throws IllegalArgumentException {
    if( clusterPoint == null ) {
      throw new IllegalArgumentException( "Cannot add null coordinates to cluster!" );
    }
    
    m_clusterPoints.add( clusterPoint );
  }
  
  public void removeClusterPoint( Coordinates clusterPoint ) {
    if( clusterPoint == null ) {
      throw new IllegalArgumentException( "Cannot remove null coordinates from cluster!" );
    }
    
    m_clusterPoints.remove( clusterPoint );
  }
  
  public int getClusterID() {
    return m_clusterID;
  }
  
  public void setClusterID( int generalID ) {
    m_clusterID = generalID;
  }
}
