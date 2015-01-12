package com.garbagebinserver.data;

import java.util.ArrayList;

import com.garbagebinserver.clusteranalysis.KMeansCluster;

public class GarbageClusterData {
  
  private KMeansCluster m_garbageCluster;
  private double        m_avgGarbageVolume;
  
  public GarbageClusterData( final KMeansCluster garbageCluster, 
                             final double        avgGarbageVolume ) {
    
    if( garbageCluster == null ) {
      throw new IllegalArgumentException( "The garbage cluster cannot be null!" );
    }
    else if( avgGarbageVolume < 0 ) {
      throw new IllegalArgumentException( "The average garbage volume cannot be negative!" );
    }
    
    m_garbageCluster = garbageCluster;
    m_avgGarbageVolume = avgGarbageVolume;
  }
  
  public KMeansCluster getGarbageCluster() {
    return m_garbageCluster;
  }
  
  public double getAvgGarbageVolume() {
    return m_avgGarbageVolume;
  }
}
