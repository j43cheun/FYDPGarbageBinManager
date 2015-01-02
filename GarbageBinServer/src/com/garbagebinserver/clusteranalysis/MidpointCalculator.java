package com.garbagebinserver.clusteranalysis;

import java.util.LinkedHashSet;

import com.garbagebinserver.data.GarbageSpot;

public class MidpointCalculator {
  
  public static <T> Coordinates computeMidpoint( Class<T> coordinatesClass, LinkedHashSet<Coordinates> coordinatesSet ) throws Exception {
    if( coordinatesSet == null ) {
      throw new IllegalArgumentException( "Cannot compute midpoint on null coordinates set!" );
    }
    
    if( CartesianCoordinates.class.isAssignableFrom( coordinatesClass ) ) {
      return MidpointCalculator.computeCartesianMidpoint( coordinatesSet ); 
    }
    else if( GPSCoordinates.class.isAssignableFrom( coordinatesClass ) ) {
      return MidpointCalculator.computeGPSMidpoint( coordinatesSet );
    }
    else {
      throw new Exception( "Unsupported coordinates class!" );
    }
  }
  
  private static Coordinates computeCartesianMidpoint( LinkedHashSet<Coordinates> coordinatesSet ) throws Exception {
    double xTotal = 0;
    double yTotal = 0;
    double zTotal = 0;
    
    for( Coordinates coordinates : coordinatesSet ) {
      if( !( coordinates instanceof CartesianCoordinates ) ) {
        throw new Exception( "All coordinates must be of class CartesianCoordinates when computing cartesian midpoint!" );
      }
      
      CartesianCoordinates cartesianCoordinates = ( CartesianCoordinates )coordinates;
      
      xTotal += cartesianCoordinates.getXCoordinate();
      yTotal += cartesianCoordinates.getYCoordinate();
      zTotal += cartesianCoordinates.getZCoordinate();
    }
    
    double xMidpoint = xTotal / coordinatesSet.size();
    double yMidpoint = yTotal / coordinatesSet.size();
    double zMidpoint = zTotal / coordinatesSet.size();
    
    return new CartesianCoordinates( xMidpoint, yMidpoint, zMidpoint );
  }
  
  private static Coordinates computeGPSMidpoint( LinkedHashSet<Coordinates> coordinatesSet ) throws Exception {
    double xTotal = 0;
    double yTotal = 0;
    double zTotal = 0;
    
    for( Coordinates coordinates : coordinatesSet ) {
      if( !( coordinates instanceof GPSCoordinates ) ) {
        throw new Exception( "All coordinates must be of class GPSCoordinates when computing GPS midpoint!" );
      }
      
      GPSCoordinates gpsCoordinates = ( GPSCoordinates )coordinates;
      CartesianCoordinates cartesianCoordinates = gpsCoordinates.convertToCartesianCoordinates();
      
      xTotal += cartesianCoordinates.getXCoordinate();
      yTotal += cartesianCoordinates.getYCoordinate();
      zTotal += cartesianCoordinates.getZCoordinate();
    }
    
    double xMidpoint = xTotal / coordinatesSet.size();
    double yMidpoint = yTotal / coordinatesSet.size();
    double zMidpoint = zTotal / coordinatesSet.size();
    
    CartesianCoordinates cartesianMidpoint = new CartesianCoordinates( xMidpoint, yMidpoint, zMidpoint );
    
    return cartesianMidpoint.convertToGPSCoordinates();
  }
}
