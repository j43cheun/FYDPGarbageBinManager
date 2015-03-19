package com.garbagebinserver.data;

import com.garbagebinserver.clusteranalysis.GPSCoordinates;

public class ServiceStation extends GPSCoordinates {
  
  private int m_serviceStationID;
  private String m_name;
  private String m_description;
  
  public ServiceStation( final int serviceStationID, 
                         final String name, 
                         final double latitude, 
                         final double longitude, 
                         final String description ) {
    super( latitude, longitude );
    m_serviceStationID = serviceStationID;
    m_name = name;
    m_description = description;
  }
  
  public int getServiceStationID() {
    return m_serviceStationID;
  }
  
  public String getName() {
    return m_name;
  }
  
  public String getDescription() {
    return m_description;
  }
}
