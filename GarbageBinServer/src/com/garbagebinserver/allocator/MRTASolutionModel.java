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
import com.garbagebinserver.data.ServiceStation;

public class MRTASolutionModel implements SolutionModel
{
  private ArrayList<GarbageBin>                             m_garbageBins;
  private ArrayList<GarbageClusterData>                     m_garbageClusterDataElements;
  private LinkedHashMap<GarbageClusterData, ServiceStation> m_nearestServiceStationTable;
  private int[]                                             m_garbageBinAllocation;
  private Random                                            m_randomNumberGenerator;
  
  public MRTASolutionModel( final ArrayList<GarbageBin> garbageBins,
                            final ArrayList<GarbageClusterData> garbageClusterDataElements,
                            final ArrayList<ServiceStation> serviceStations )
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
    m_nearestServiceStationTable = new LinkedHashMap<GarbageClusterData, ServiceStation>();
    
    for( GarbageClusterData garbageClusterDataElement : m_garbageClusterDataElements )
    {
      ServiceStation closestServiceStation = null;
      double closestDistance = -1;
      
      for( ServiceStation serviceStation : serviceStations )
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
    
    // Generate initial solution for garbage bin allocation.
    final ArrayList<Integer> garbageClusterIndices = new ArrayList<Integer>();
    
    for( int idx = 0; idx < m_garbageClusterDataElements.size(); idx++ )
    {
      garbageClusterIndices.add( idx );
    }
    
    Collections.shuffle( garbageClusterIndices );
    m_garbageBinAllocation = new int[m_garbageBins.size()];
    
    for( int idx = 0; idx < m_garbageBins.size(); idx++ )
    {
      if( garbageClusterIndices.isEmpty() )
      {
        m_garbageBinAllocation[idx] = -1;
      }
      else
      {
        m_garbageBinAllocation[idx] = garbageClusterIndices.remove( 0 );
      }
    }
  }
  
  private MRTASolutionModel( final ArrayList<GarbageBin> garbageBins,
                             final ArrayList<GarbageClusterData> garbageClusterDataElements,
                             final LinkedHashMap<GarbageClusterData, ServiceStation> nearestServiceStationTable,
                             final int[] garbageBinAllocation )
  {
    // Initialize random number generator.
    m_randomNumberGenerator = new Random( System.currentTimeMillis() );
    
    // Load garbage bins.
    m_garbageBins = garbageBins;
    
    // Load garbage cluster data elements.
    m_garbageClusterDataElements = garbageClusterDataElements;
    
    // Load nearest service station table.
    m_nearestServiceStationTable = nearestServiceStationTable;
    
    // Load garbage bin allocation.
    m_garbageBinAllocation = garbageBinAllocation;
  }
  
  @Override
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
        
        // This block is necessary because the Poisson Distribution only accepts positive arguments!
        double avgGarbageVolume;
        if( garbageClusterDataElement.getAvgGarbageVolume() <= 0 ) {
          avgGarbageVolume = 0.000001;
        }
        else {
          avgGarbageVolume = garbageClusterDataElement.getAvgGarbageVolume();
        }
        
        final PoissonDistribution poissonDistribution = new PoissonDistribution( avgGarbageVolume );
        final double underloadProb = poissonDistribution.cumulativeProbability( ( int ) Math.floor( remainingVolume  ) );
        
        totalOverload += 100 * ( 1 - underloadProb );
      }
    }
    
    final double cost = Math.sqrt( ( totalDistance * totalDistance ) + ( totalOverload * totalOverload ) );
    
    return cost;
  }
  
  @Override
  public SolutionModel generateNeighboringSolution()
  {
    SolutionModel neighboringSolution;
    
    switch( m_randomNumberGenerator.nextInt( 4 ) )
    {
      case 0:
        neighboringSolution = swap();
        break;
      case 1:
        neighboringSolution = deleteAndInsert();
        break;
      case 2:
        neighboringSolution = invert();
        break;
      default:
        neighboringSolution = scramble();
    }
    
    return neighboringSolution;
  }
  
  private MRTASolutionModel swap()
  {
    int[] neighborGarbageBinAllocation = new int[m_garbageBinAllocation.length];
    
    // Copy existing garbage bin allocation to neighbor.
    for( int idx = 0; idx < neighborGarbageBinAllocation.length; idx++ )
    {
      neighborGarbageBinAllocation[idx] = m_garbageBinAllocation[idx];
    }
    
    if( m_garbageBinAllocation.length > 1 ) {
      // Generate two indices to swap.
      int swapIndexA = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      int swapIndexB = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      
      while( swapIndexB == swapIndexA )
      {
        swapIndexB = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      }
      
      // Swap at two indices.
      int temp = neighborGarbageBinAllocation[swapIndexA];
      neighborGarbageBinAllocation[swapIndexA] = neighborGarbageBinAllocation[swapIndexB];
      neighborGarbageBinAllocation[swapIndexB] = temp;
    }
    
    return new MRTASolutionModel( m_garbageBins, m_garbageClusterDataElements, m_nearestServiceStationTable, neighborGarbageBinAllocation );
  }
  
  private MRTASolutionModel deleteAndInsert()
  {
    int[] neighborGarbageBinAllocation = new int[m_garbageBinAllocation.length];
    
    // Copy existing garbage bin allocation to neighbor.
    for( int idx = 0; idx < neighborGarbageBinAllocation.length; idx++ )
    {
      neighborGarbageBinAllocation[idx] = m_garbageBinAllocation[idx];
    }
    
    if( m_garbageBinAllocation.length > 1 ) {
      // Generate two indices to delete and insert.
      int deleteIndex = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      int insertIndex = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      
      while( insertIndex == deleteIndex )
      {
        insertIndex = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      }
      
      // Delete and insert at two indices.
      int deletedClusterIdx = neighborGarbageBinAllocation[deleteIndex];
      
      if( deleteIndex < insertIndex )
      {
        for( int idx = deleteIndex; idx < insertIndex; idx++ )
        {
          neighborGarbageBinAllocation[idx] = neighborGarbageBinAllocation[idx + 1];
        }
      }
      else if( deleteIndex > insertIndex )
      {
        for( int idx = deleteIndex; idx > insertIndex; idx-- )
        {
          neighborGarbageBinAllocation[idx] = neighborGarbageBinAllocation[idx - 1];
        }
      }
      
      neighborGarbageBinAllocation[insertIndex] = deletedClusterIdx;
    }
    
    return new MRTASolutionModel( m_garbageBins, m_garbageClusterDataElements, m_nearestServiceStationTable, neighborGarbageBinAllocation );
  }
  
  private MRTASolutionModel invert()
  {
    int[] neighborGarbageBinAllocation = new int[m_garbageBinAllocation.length];
    
    // Copy existing garbage bin allocation to neighbor.
    for( int idx = 0; idx < neighborGarbageBinAllocation.length; idx++ )
    {
      neighborGarbageBinAllocation[idx] = m_garbageBinAllocation[idx];
    }
    
    if( m_garbageBinAllocation.length > 1 ) {
      // Generate two indices to invert.
      int invertIndexA = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      int invertIndexB = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      
      while( invertIndexB == invertIndexA )
      {
        invertIndexB = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      }
      
      // Invert at two indices.
      int rightIdx;
      int leftIdx;
      
      if( invertIndexA < invertIndexB )
      {
        rightIdx = invertIndexA;
        leftIdx = invertIndexB;
      }
      else
      {
        rightIdx = invertIndexB;
        leftIdx = invertIndexA;
      }
      
      while( rightIdx < leftIdx )
      {
        int temp = neighborGarbageBinAllocation[rightIdx];
        neighborGarbageBinAllocation[rightIdx] = neighborGarbageBinAllocation[leftIdx];
        neighborGarbageBinAllocation[leftIdx] = temp;
        rightIdx++;
        leftIdx--;
      }
    }
    
    return new MRTASolutionModel( m_garbageBins, m_garbageClusterDataElements, m_nearestServiceStationTable, neighborGarbageBinAllocation );
  }
  
  private MRTASolutionModel scramble()
  {
    int[] neighborGarbageBinAllocation = new int[m_garbageBinAllocation.length];
    
    // Copy existing garbage bin allocation to neighbor.
    for( int idx = 0; idx < neighborGarbageBinAllocation.length; idx++ )
    {
      neighborGarbageBinAllocation[idx] = m_garbageBinAllocation[idx];
    }
    
    if( m_garbageBinAllocation.length > 1 ) {
      // Generate two indices to scramble.
      int scrambleIndexA = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      int scrambleIndexB = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      
      while( scrambleIndexB == scrambleIndexA )
      {
        scrambleIndexB = m_randomNumberGenerator.nextInt( neighborGarbageBinAllocation.length );
      }
      
      // Scramble at two indices.
      int lowerIndex;
      int upperIndex;
      
      if( scrambleIndexA < scrambleIndexB )
      {
        lowerIndex = scrambleIndexA;
        upperIndex = scrambleIndexB;
      }
      else
      {
        lowerIndex = scrambleIndexB;
        upperIndex = scrambleIndexA;
      }
      
      for( int idx = lowerIndex; idx <= upperIndex; idx++ )
      {
        int swapIndex = m_randomNumberGenerator.nextInt( upperIndex - lowerIndex + 1 ) + lowerIndex;
        int temp = neighborGarbageBinAllocation[idx];
        neighborGarbageBinAllocation[idx] = neighborGarbageBinAllocation[swapIndex];
        neighborGarbageBinAllocation[swapIndex] = temp;
      }
    }
    
    return new MRTASolutionModel( m_garbageBins, m_garbageClusterDataElements, m_nearestServiceStationTable, neighborGarbageBinAllocation );
  }
  
  public LinkedHashMap<Integer, Integer> getMappedAllocation() {
    LinkedHashMap<Integer, Integer> mappedAllocation = new LinkedHashMap<Integer, Integer>();
    
    for( int i = 0; i < m_garbageBinAllocation.length; ++i ) {
      int garbageBinID = m_garbageBins.get( i ).getGarbageBinID();
      
      if( m_garbageBinAllocation[i] != -1 ) {
        int clusterID = m_garbageClusterDataElements.get( m_garbageBinAllocation[i] ).getGarbageCluster().getClusterID();
        mappedAllocation.put( garbageBinID,  clusterID );
      }
    }
    
    return mappedAllocation;
  }
}
