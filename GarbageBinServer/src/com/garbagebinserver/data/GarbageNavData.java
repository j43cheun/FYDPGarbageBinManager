package com.garbagebinserver.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.KMeansCluster;

public class GarbageNavData {
  
  private static GarbageNavData m_instance = null;
  
  private int m_nextGarbageSpotID;
  private int m_nextGarbageClusterID;
  private LinkedHashMap<Integer, GarbageSpot> m_garbageSpotTable;
  private LinkedHashMap<Integer, KMeansCluster> m_kmeansClusterTable;
  private LinkedHashSet<Integer> m_availableGarbageSpotsByID;
  
  public GarbageNavData() {
    m_nextGarbageSpotID = 1;
    m_nextGarbageClusterID = 1;
    m_garbageSpotTable = new LinkedHashMap<Integer, GarbageSpot>();
    m_kmeansClusterTable = new LinkedHashMap<Integer, KMeansCluster>();
    m_availableGarbageSpotsByID = new LinkedHashSet<Integer>();
  }
  
  public static GarbageNavData getInstance() {
    if( m_instance == null ) {
      m_instance = new GarbageNavData();
    }
    
    return m_instance;
  }
  
  public int addGarbageSpot( final String name, final double latitude, final double longitude, final String description ) {
    // TODO
    // <latitude, longitude, name> tuple needs to be unique! Verify through SQL.
    // Adding a garbage spot will automatically add it to SQL database.
    // Return -1 if unable to add garbage spot!
    // Will need Evgeny's help with this!
    // Also need to check boundaries!
    
    int garbageSpotID = m_nextGarbageSpotID++;
    GarbageSpot garbageSpot = new GarbageSpot( garbageSpotID, name, latitude, longitude, description );
    m_garbageSpotTable.put( garbageSpotID, garbageSpot );
    m_availableGarbageSpotsByID.add( garbageSpotID );
    return garbageSpotID;
  }
  
  public LinkedHashSet<Coordinates> getAllGarbageSpots() {
    return new LinkedHashSet<Coordinates>( m_garbageSpotTable.values() );
  }
  
  public LinkedHashSet<Coordinates> getAvailableGarbageSpots() {
    final LinkedHashSet<Coordinates> availableGarbageSpots = new LinkedHashSet<Coordinates>();
    
    for( Integer availableGarbageSpotID : m_availableGarbageSpotsByID ) {
      // Note: All garbage spot IDs MUST have a corresponding garbage spot!
      availableGarbageSpots.add( m_garbageSpotTable.get( availableGarbageSpotID ) );
    }
    
    return availableGarbageSpots;
  }
  
  public LinkedHashSet<Integer> getAvailableGarbageSpotsByID() {
    return m_availableGarbageSpotsByID;
  }
  
  public LinkedHashSet<KMeansCluster> getGarbageClusters() {
    return new LinkedHashSet<KMeansCluster>( m_kmeansClusterTable.values() );
  }
  
  public void addGarbageClusters( final LinkedHashSet<KMeansCluster> garbageClusters ) {
    for( KMeansCluster kmeansCluster : garbageClusters ) {
      m_kmeansClusterTable.put( kmeansCluster.getClusterID() , kmeansCluster );
    }
  }
  
  public void setGarbageClusters( final LinkedHashSet<KMeansCluster> garbageClusters ) {
    m_kmeansClusterTable.clear();
    
    for( KMeansCluster kmeansCluster : garbageClusters ) {
      m_kmeansClusterTable.put( kmeansCluster.getClusterID() , kmeansCluster );
    }
  }
}
