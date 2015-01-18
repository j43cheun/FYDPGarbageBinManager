package com.garbagebinserver.data;

import com.garbagebinserver.clusteranalysis.GPSCoordinates;

public class GarbageBin {
  
  private int            m_garbageBinID;
  private double         m_currentGarbageVolume;
  private double         m_maxGarbageVolume;
  private double         m_percentRemainingPower;
  private GPSCoordinates m_currentGPSCoordinate;
  private GPSCoordinates m_serviceStationGPSCoordinate;
  private String         m_garbageBinIPAddress;
  
  public GarbageBin( final int            garbageBinID, 
                     final double         maxGarbageVolume, 
                     final GPSCoordinates currentGPSCoordinate, 
                     final GPSCoordinates serviceStationGPSCoordinate, 
                     final String         garbageBinIPAddress ) {
    
    if( maxGarbageVolume <= 0 ) {
      throw new IllegalArgumentException( "The max garbage volume must be greater than 0!" );
    }
    else if( currentGPSCoordinate == null ) {
      throw new IllegalArgumentException( "The current GPS coordinate cannot be null!" );
    }
    else if( serviceStationGPSCoordinate == null ) {
      throw new IllegalArgumentException( "The service station GPS coordinate cannot be null!" );
    }
    else if( garbageBinIPAddress == null ) {
      throw new IllegalArgumentException( "The garbage bin IP address cannot be null!" );
    }
    
    m_garbageBinID = garbageBinID;
    m_currentGarbageVolume = 0;
    m_maxGarbageVolume = maxGarbageVolume;
    m_percentRemainingPower = 100;
    m_currentGPSCoordinate = currentGPSCoordinate;
    m_serviceStationGPSCoordinate = serviceStationGPSCoordinate;
    m_garbageBinIPAddress = garbageBinIPAddress;
  }
  
  public int getGarbageBinID() {
    return m_garbageBinID;
  }
  
  public double getCurrentGarbageVolume() {
    return m_currentGarbageVolume;
  }
  
  public void setCurrentGarbageVolume( final double currentGarbageVolume ) {
    if( currentGarbageVolume < 0 ) {
      throw new IllegalArgumentException( "The current garbage volume cannot be negative!" );
    }
    
    m_currentGarbageVolume = currentGarbageVolume;
  }
  
  public double getMaxGarbageVolume() {
    return m_maxGarbageVolume;
  }
  
  public double getPercentRemainingPower() {
    return m_percentRemainingPower;
  }
  
  public void setPercentPowerRemaining( final double percentRemainingPower ) {
    if( percentRemainingPower < 0 ) {
      throw new IllegalArgumentException( "The percent remaining power cannot be negative!" );
    }
  }
  
  public GPSCoordinates getCurrentGPSCoordinate() {
    return m_currentGPSCoordinate;
  }
  
  public GPSCoordinates getServiceStationGPSCoordinate() {
    return m_serviceStationGPSCoordinate;
  }
  
  public void setCurrentGPSCoordinate( final GPSCoordinates currentGPSCoordinate ) {
    if( currentGPSCoordinate == null ) {
      throw new IllegalArgumentException( "The current GPS coordinate cannot be null!" );
    }
    
    m_currentGPSCoordinate = currentGPSCoordinate;
  }
  
  public String getGarbageBinIPAddress() {
    return m_garbageBinIPAddress;
  }
}