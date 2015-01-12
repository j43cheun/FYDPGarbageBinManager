package com.garbagebinserver.data;

import java.sql.Date;

public class GarbageSpotHistory {
  
  // TODO The mass property might need to be removed. There is no way to measure it.
  
  private int m_garbageSpotID;
  private double m_mass;
  private double m_volume;
  private Date m_date;
  
  public GarbageSpotHistory( final int garbageSpotID, final double mass, final double volume, final Date date ) {
    m_garbageSpotID = garbageSpotID;
    m_mass = mass;
    m_volume = volume;
    m_date = date;
  }
  
  public int getGarbageSpotID() {
    return m_garbageSpotID;
  }
  
  public double getMass() {
    return m_mass;
  }
  
  public double getVolume() {
    return m_volume;
  }
  
  public Date getDate() {
    return m_date;
  }
}
