package com.garbagebinserver.clusteranalysis;

public class CartesianCoordinates implements Coordinates {

  private int m_clusterID;
  private double m_x;
  private double m_y;
  private double m_z;
  
  public CartesianCoordinates( double x, double y, double z ) {
    m_clusterID = -1;
    m_x = x;
    m_y = y;
    m_z = z;
  }
  
  public double getXCoordinate() {
    return m_x;
  }
  
  public void setXCoordinate( double x ) {
    m_x = x;
  }
  
  public double getYCoordinate() {
    return m_y;
  }
  
  public void setYCoordinate( double y ) {
    m_y = y;
  }
  
  public double getZCoordinate() {
    return m_z;
  }
  
  public void setZCoordinate( double z ) {
    m_z = z;
  }
  
  public double getDistance( Coordinates coordinates ) {
    if( !( coordinates instanceof CartesianCoordinates ) ) {
      throw new IllegalArgumentException( "Cannot compute GPS distance between coordinates of different classes!" );
    }
    
    CartesianCoordinates cartesianCoordinates = ( CartesianCoordinates )coordinates;
    double deltaX = cartesianCoordinates.getXCoordinate() - m_x;
    double deltaY = cartesianCoordinates.getYCoordinate() - m_y;
    double deltaZ = cartesianCoordinates.getZCoordinate() - m_z;
    double cartesianDistance = Math.sqrt( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) + Math.pow( deltaZ, 2 ) );
    
    return cartesianDistance;
  }
  
  public int getClusterID() {
    return m_clusterID;
  }
  
  public void setClusterID( int clusterID ) {
    m_clusterID = clusterID;
  }
  
  public GPSCoordinates convertToGPSCoordinates() {
    double tenToPowerOfNegativeNine = Math.pow( 10, -9 );
    
    // Special case for which converted GPS coordinates point to geographical center of Earth.
    if ( Math.abs( m_x ) < tenToPowerOfNegativeNine && Math.abs( m_y ) < tenToPowerOfNegativeNine && Math.abs( m_z ) < tenToPowerOfNegativeNine ) {
      return new GPSCoordinates( 34.513299000000000000, -94.162880700000020000 );
    }
    
    double longitudeRadians = Math.atan2( m_y, m_x );
    double hyp = Math.sqrt( ( m_x * m_x ) + ( m_y * m_y ) );
    double latitudeRadians = Math.atan2( m_z, hyp );
    
    double latitudeDegrees = latitudeRadians * 180 / Math.PI;
    double longitudeDegrees = longitudeRadians * 180 / Math.PI;
    
    return new GPSCoordinates( latitudeDegrees, longitudeDegrees );
  }
}
