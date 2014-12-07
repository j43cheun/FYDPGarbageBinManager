package com.garbagebinserver.data;

import com.garbagebinserver.clusteranalysis.GPSCoordinates;

public class GarbageSpot extends GPSCoordinates {
  
  private int m_garbageSpotID;
  private String m_name;
  private String m_description;

  public GarbageSpot(final int garbageSpotID, final String name, final double latitude, final double longitude, final String description) {
    super(latitude, longitude);
    
    m_garbageSpotID = garbageSpotID;
    m_name = name;
    m_description = description;
  }
  
  public int getGarbageSpotID() {
    return m_garbageSpotID;
  }
  
  public String getName() {
    return m_name;
  }
  
  public String getDescription() {
    return m_description;
  }
}
