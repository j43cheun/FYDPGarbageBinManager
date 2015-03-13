package com.garbagebinserver.allocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.PoissonDistribution;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.data.GarbageBin;
import com.garbagebinserver.data.GarbageClusterData;

public class SimpleAuctionAllocator
{
  private ArrayList<GarbageBin>                             m_garbageBins;
  private ArrayList<GarbageClusterData>                     m_garbageClusterDataElements;
  private LinkedHashMap<GarbageClusterData, GPSCoordinates> m_nearestServiceStationTable;
  private int[]                                             m_garbageBinAllocation;
  private Random                                            m_randomNumberGenerator;
  
  public SimpleAuctionAllocator( final ArrayList<GarbageBin> garbageBins,
                                 final ArrayList<GarbageClusterData> garbageClusterDataElements,
                                 final ArrayList<GPSCoordinates> serviceStations )
  {
    if( garbageBins == null ) {
      throw new IllegalArgumentException( "The set of garbage bins cannot be null!" );
    }
    else if( garbageClusterDataElements == null ) {
      throw new IllegalArgumentException( "The set of garbage cluster elements cannot be null!" );
    }
    else if( serviceStations == null ) {
      throw new IllegalArgumentException( "The set of service stations cannot be null!" );
    }
    else if( garbageBins.isEmpty() ) {
      throw new IllegalArgumentException( "The set of garbage bins cannot be empty!" );
    }
    else if( garbageClusterDataElements.isEmpty() ) {
      throw new IllegalArgumentException( "The set of garbage cluster elements cannot be empty!" );
    }
    else if( serviceStations.isEmpty() ) {
      throw new IllegalArgumentException( "The set of service stations cannot be empty!" );
    }
    else if( garbageBins.size() < garbageClusterDataElements.size() ) {
      throw new IllegalArgumentException( "There must be fewer garbage clusters than garbage bins!" );
    }
    
    // Initialize random number generator.
    m_randomNumberGenerator = new Random( System.currentTimeMillis() );
    
    // Load garbage bins.
    m_garbageBins = garbageBins;
    
    // Load garbage cluster data elements.
    m_garbageClusterDataElements = garbageClusterDataElements;
    
    // Map each garbage cluster data element to the nearest service station.
    m_nearestServiceStationTable = new LinkedHashMap<GarbageClusterData, GPSCoordinates>();
    
    for( GarbageClusterData garbageClusterDataElement : m_garbageClusterDataElements )
    {
      GPSCoordinates closestServiceStation = null;
      double closestDistance = -1;
      
      for( GPSCoordinates serviceStation : serviceStations )
      {
        double distance = garbageClusterDataElement.getGarbageCluster().getCentroid().getDistance( serviceStation );
        
        if( closestServiceStation == null || closestDistance == -1 || distance < closestDistance )
        {
          closestServiceStation = serviceStation;
          closestDistance = distance;
        }
      }
      
      m_nearestServiceStationTable.put( garbageClusterDataElement, closestServiceStation );
    }
    
    // Initialize garbage bin allocation.
    m_garbageBinAllocation = new int[m_garbageBins.size()];
  }
  
  public void allocate()
  {
    ArrayList<Integer> clusterIndices = new ArrayList<Integer>();
    ArrayList<Integer> garbageBinIndices = new ArrayList<Integer>();
    
    for( int idx = 0; idx < m_garbageClusterDataElements.size(); idx++ )
    {
      clusterIndices.add( idx );
    }
    
    for( int idx = 0; idx < m_garbageBins.size(); idx++ )
    {
      garbageBinIndices.add( idx );
    }
    
    Collections.shuffle( clusterIndices );
    Collections.shuffle( garbageBinIndices );
    
    while( !garbageBinIndices.isEmpty() )
    {
      int lowestBidder = -1;
      double lowestBid = -1;
      
      if( clusterIndices.isEmpty() )
      {
        m_garbageBinAllocation[lowestBidder] = -1;
        garbageBinIndices.remove( lowestBidder );
      }
      else
      {
        int currentClusterIndex = clusterIndices.remove( 0 );
        GarbageClusterData garbageClusterDataElement = m_garbageClusterDataElements.get( currentClusterIndex );
        
        for( int idx = 0; idx < garbageBinIndices.size(); idx++ )
        {
          GarbageBin garbageBin = m_garbageBins.get( garbageBinIndices.get( idx ) );
          
          // Compute distance.
          final double binToClusterDistance = garbageBin.getCurrentGPSCoordinate().getDistance( garbageClusterDataElement.getGarbageCluster().getCentroid() );
          final double clusterToServiceStationDistance = garbageClusterDataElement.getGarbageCluster().getCentroid().getDistance( m_nearestServiceStationTable.get( garbageClusterDataElement ) );
          final double distance = binToClusterDistance + clusterToServiceStationDistance;
          
          // Compute overload.
          final double remainingVolume = garbageBin.getMaxGarbageVolume() - garbageBin.getCurrentGarbageVolume();
          final PoissonDistribution poissonDistribution = new PoissonDistribution( garbageClusterDataElement.getAvgGarbageVolume() );
          final double underloadProb = poissonDistribution.cumulativeProbability( ( int ) Math.floor( remainingVolume  ) );
          final double overload = 100 * ( 1 - underloadProb );
          
          // Compute current bid.
          final double currentBid = Math.sqrt( ( distance * distance ) + ( overload * overload ) );
          
          if( lowestBidder == -1 || lowestBid == -1 || currentBid < lowestBid )
          {
            lowestBidder = idx;
            lowestBid = currentBid;
          }
        }
        
        m_garbageBinAllocation[lowestBidder] = currentClusterIndex;
        garbageBinIndices.remove( lowestBidder );
      }
    }
  }
  
  public double computeCost()
  {
    double totalDistance = 0;
    double totalOverload = 0;
    
    for( int idx = 0; idx < m_garbageBinAllocation.length; idx++ )
    {
      if( m_garbageBinAllocation[idx] != -1 )
      {
        // Compute distance cost.
        final GarbageBin garbageBin = m_garbageBins.get( idx );
        final GarbageClusterData garbageClusterDataElement = m_garbageClusterDataElements.get( m_garbageBinAllocation[idx] );
        
        final GPSCoordinates nearestServiceStation = m_nearestServiceStationTable.get( garbageClusterDataElement );
        final Coordinates garbageBinLocation = garbageBin.getCurrentGPSCoordinate();
        final Coordinates garbageClusterLocation = garbageClusterDataElement.getGarbageCluster().getCentroid();
        
        final double binToClusterDistance = garbageBinLocation.getDistance( garbageClusterLocation );
        final double clusterToServiceStationDistance = garbageClusterLocation.getDistance( nearestServiceStation );
        
        totalDistance += binToClusterDistance + clusterToServiceStationDistance;
        
        // Compute overload cost.
        final double remainingVolume = garbageBin.getMaxGarbageVolume() - garbageBin.getCurrentGarbageVolume();
        final PoissonDistribution poissonDistribution = new PoissonDistribution( garbageClusterDataElement.getAvgGarbageVolume() );
        final double underloadProb = poissonDistribution.cumulativeProbability( ( int ) Math.floor( remainingVolume  ) );
        
        totalOverload += 100 * ( 1 - underloadProb );
      }
    }
    
    final double cost = Math.sqrt( ( totalDistance * totalDistance ) + ( totalOverload * totalOverload ) );
    
    return cost;
  }
}
