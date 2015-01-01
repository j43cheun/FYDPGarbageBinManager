package com.garbagebinserver.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GarbageNavData {
  
  private static GarbageNavData m_instance = null;
  
  private int m_nextGarbageSpotID;
  private int m_nextGarbageClusterID;
  private LinkedHashMap<Integer, GarbageSpot> m_garbageSpotTable;
  private LinkedHashSet<GarbageSpot> m_availableGarbageSpots;
  
  public GarbageNavData() {
    m_nextGarbageSpotID = 1;
    m_nextGarbageClusterID = 1;
    m_garbageSpotTable = new LinkedHashMap<Integer, GarbageSpot>();
    m_availableGarbageSpots = new LinkedHashSet<GarbageSpot>();
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
    m_availableGarbageSpots.add( garbageSpot );
    return garbageSpotID;
  }
  
  public LinkedHashSet<GarbageSpot> getGarbageSpots() {
    return new LinkedHashSet<GarbageSpot>( m_garbageSpotTable.values() );
  }
}
