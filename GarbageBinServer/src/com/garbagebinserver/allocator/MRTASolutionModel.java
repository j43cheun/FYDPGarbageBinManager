package com.garbagebinserver.allocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

import org.apache.commons.math3.distribution.PoissonDistribution;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.data.GarbageBin;
import com.garbagebinserver.data.GarbageClusterData;

public class MRTASolutionModel implements SolutionModel {
  
  private ArrayList<GarbageBin>                      m_garbageBins;
  private LinkedHashMap<Integer, GarbageClusterData> m_garbageClusterDataTable;
  private LinkedHashSet<GarbageClusterData>          m_garbageClusterDataElements;
  private int[]                                      m_garbageBinAssignments;
  private Random                                     m_randomNumberGenerator;
  
  public MRTASolutionModel( final LinkedHashSet<GarbageBin>         garbageBins, 
                            final LinkedHashSet<GarbageClusterData> garbageClusterDataElements ) {
    
    if( garbageBins == null ) {
      throw new IllegalArgumentException( "The set of garbage bins cannot be null!" );
    }
    else if( garbageClusterDataElements == null ) {
      throw new IllegalArgumentException( "The set of garbage cluster elements cannot be null!" );
    }
    else if( garbageBins.size() < garbageClusterDataElements.size() ) {
      throw new IllegalArgumentException( "There must be fewer garbage clusters than garbage bins!" );
    }
    
    final LinkedHashSet<Integer> garbageBinIDs = new LinkedHashSet<Integer>();
    
    for( GarbageBin garbageBin : garbageBins ) {
      if( garbageBinIDs.contains( garbageBin.getGarbageBinID() ) ) {
        throw new IllegalArgumentException( "Cannot have multiple garbage bins with the same ID!" );
      }
      
      garbageBinIDs.add( garbageBin.getGarbageBinID() );
    }
    
    m_garbageBins = new ArrayList<GarbageBin>( garbageBins );
    final LinkedHashSet<Integer> garbageClusterIDs = new LinkedHashSet<Integer>();
    
    for( GarbageClusterData garbageClusterDataElement : garbageClusterDataElements ) {
      if( garbageClusterIDs.contains( garbageClusterDataElement.getGarbageCluster().getClusterID() ) ) {
        throw new IllegalArgumentException( "Cannot have multiple garbage clusters with the same ID!" );
      }
      
      garbageClusterIDs.add( garbageClusterDataElement.getGarbageCluster().getClusterID() );
    }
    
    m_garbageClusterDataElements = new LinkedHashSet<>( garbageClusterDataElements );
    m_garbageClusterDataTable = new LinkedHashMap<Integer, GarbageClusterData>();
    
    for( GarbageClusterData garbageClusterDataElement : garbageClusterDataElements ) {
      m_garbageClusterDataTable.put( garbageClusterDataElement.getGarbageCluster().getClusterID(), garbageClusterDataElement );
    }
    
    final ArrayList<Integer> garbageBinIndexes = new ArrayList<Integer>();
    
    for( int idx = 0; idx < m_garbageBins.size(); ++idx ) {
      garbageBinIndexes.add( idx );
    }
    
    Collections.shuffle( garbageBinIndexes );
    m_garbageBinAssignments = new int[m_garbageBins.size()];
    final ArrayList<Integer> garbageClusterIDArr = new ArrayList<Integer>( garbageClusterIDs );
    
    for( Integer garbageBinIndex : garbageBinIndexes ) {
      if( garbageClusterIDArr.isEmpty() ) {
        m_garbageBinAssignments[garbageBinIndex] = -1;
      }
      else {
        m_garbageBinAssignments[garbageBinIndex] = garbageClusterIDArr.remove( 0 );
      }
    }
    
    m_randomNumberGenerator = new Random();
  }
  
  public MRTASolutionModel( final LinkedHashSet<GarbageBin>         garbageBins, 
                            final LinkedHashSet<GarbageClusterData> garbageClusterDataElements, 
                            final int[]                             garbageBinAssignments ) {
    
    if( garbageBins == null ) {
      throw new IllegalArgumentException( "The set of garbage bins cannot be null!" );
    }
    else if( garbageClusterDataElements == null ) {
      throw new IllegalArgumentException( "The set of garbage cluster elements cannot be null!" );
    }
    else if( garbageBins.size() < garbageClusterDataElements.size() ) {
      throw new IllegalArgumentException( "There cannot be fewer garbage bins than garbage clusters!" );
    }
    else if( garbageBinAssignments.length < garbageBins.size() ) {
      throw new IllegalArgumentException( "The number of garbage bin assignments must be equal to the number of garbage bins!" );
    }
    
    final LinkedHashSet<Integer> garbageBinIDs = new LinkedHashSet<Integer>();
    
    for( GarbageBin garbageBin : garbageBins ) {
      if( garbageBinIDs.contains( garbageBin.getGarbageBinID() ) ) {
        throw new IllegalArgumentException( "Cannot have multiple garbage bins with the same ID!" );
      }
      
      garbageBinIDs.add( garbageBin.getGarbageBinID() );
    }
    
    m_garbageBins = new ArrayList<GarbageBin>( garbageBins );
    final LinkedHashSet<Integer> garbageClusterIDs = new LinkedHashSet<Integer>();
    
    for( GarbageClusterData garbageClusterDataElement : garbageClusterDataElements ) {
      if( garbageClusterIDs.contains( garbageClusterDataElement.getGarbageCluster().getClusterID() ) ) {
        throw new IllegalArgumentException( "Cannot have multiple garbage clusters with the same ID!" );
      }
      
      garbageClusterIDs.add( garbageClusterDataElement.getGarbageCluster().getClusterID() );
    }
    
    m_garbageClusterDataElements = new LinkedHashSet<>( garbageClusterDataElements );
    m_garbageClusterDataTable = new LinkedHashMap<Integer, GarbageClusterData>();
    
    for( GarbageClusterData garbageClusterDataElement : garbageClusterDataElements ) {
      m_garbageClusterDataTable.put( garbageClusterDataElement.getGarbageCluster().getClusterID(), garbageClusterDataElement );
    }
    
    final LinkedHashSet<Integer> assignedClusterIDs = new LinkedHashSet<Integer>();
    
    for( int idx = 0; idx < m_garbageBinAssignments.length; ++idx ) {
      if( !garbageClusterIDs.contains( m_garbageBinAssignments[idx] ) && garbageBinAssignments[idx] != -1 ) {
        throw new IllegalArgumentException( "Garbage bins cannot be assigned to a non-existent garbage cluster ID!" );
      }
      else if( assignedClusterIDs.contains( m_garbageBinAssignments[idx] ) ) {
        throw new IllegalArgumentException( "Cannot assign multiple garbage bins to the same cluster ID!" );
      }
      else if( m_garbageBinAssignments[idx] == -1 ) {
        continue;
      }
      
      assignedClusterIDs.add( m_garbageBinAssignments[idx] );
    }
    
    m_garbageBinAssignments = garbageBinAssignments;
    m_randomNumberGenerator = new Random( System.currentTimeMillis() );
  }
  
  @Override
  public double computeCost() {
    double distancePenalty = 0;
    double overloadPenalty = 0;
    double lowPowerPenalty = 0;
    
    for( int idx = 0; idx < m_garbageBinAssignments.length; ++idx ) {
      final GarbageBin garbageBin = m_garbageBins.get( idx );
      final GarbageClusterData garbageClusterData = m_garbageClusterDataTable.get( m_garbageBinAssignments[idx] );
      
      final Coordinates garbageBinLatLng = garbageBin.getCurrentGPSCoordinate();
      final Coordinates garbageClusterLatLng = garbageClusterData.getGarbageCluster().getCentroid();
      final Coordinates serviceStationLatLng = garbageBin.getServiceStationGPSCoordinate();
      
      final double currentToClusterDistance = garbageClusterLatLng.getDistance( garbageBinLatLng );
      final double clusterToServiceStationDistance = serviceStationLatLng.getDistance( garbageClusterLatLng );
      distancePenalty += currentToClusterDistance + clusterToServiceStationDistance;
      
      final double remainingVolume = garbageBin.getMaxGarbageVolume() - garbageBin.getCurrentGarbageVolume();
      final double underloadProbability = new PoissonDistribution( garbageClusterData.getAvgGarbageVolume() ).cumulativeProbability( ( int )Math.floor( remainingVolume ) );
      overloadPenalty += 100* ( 1 - underloadProbability );
      
      lowPowerPenalty += 100 - garbageBin.getPercentRemainingPower();
    }
    
    final double cost = Math.sqrt( Math.pow( distancePenalty, 2 ) + Math.pow( overloadPenalty, 2 ) + Math.pow( lowPowerPenalty, 2 ) );
    
    return cost;
  }

  @Override
  public SolutionModel generateNeighboringSolution() {
    SolutionModel neighboringSolution;
    
    switch( m_randomNumberGenerator.nextInt( 4 ) ) {
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
        break;
    }
    
    return neighboringSolution;
  }
  
  private MRTASolutionModel swap() {
    final ArrayList<Integer> neighboringGarbageBinAssignmentsArr = new ArrayList<Integer>();
    int swapIndexA = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    int swapIndexB = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    
    Collections.swap( neighboringGarbageBinAssignmentsArr, swapIndexA, swapIndexB );
    int[] neighboringGarbageBinAssignments = new int[neighboringGarbageBinAssignmentsArr.size()];
    
    for( int idx = 0; idx < neighboringGarbageBinAssignmentsArr.size(); ++idx ) {
      neighboringGarbageBinAssignments[idx] = neighboringGarbageBinAssignmentsArr.get( idx );
    }
    
    return new MRTASolutionModel( new LinkedHashSet<GarbageBin>( m_garbageBins ), m_garbageClusterDataElements, neighboringGarbageBinAssignments );
  }
  
  private MRTASolutionModel deleteAndInsert() {
    final ArrayList<Integer> neighboringGarbageBinAssignmentsArr = new ArrayList<Integer>();
    int deleteIndex = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    int insertIndex = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    
    Integer deletedGarbageClusterID = neighboringGarbageBinAssignmentsArr.remove( deleteIndex );
    neighboringGarbageBinAssignmentsArr.add( insertIndex, deletedGarbageClusterID );
    int[] neighboringGarbageBinAssignments = new int[neighboringGarbageBinAssignmentsArr.size()];
    
    for( int idx = 0; idx < neighboringGarbageBinAssignmentsArr.size(); ++idx ) {
      neighboringGarbageBinAssignments[idx] = neighboringGarbageBinAssignmentsArr.get( idx );
    }
    
    return new MRTASolutionModel( new LinkedHashSet<GarbageBin>( m_garbageBins ), m_garbageClusterDataElements, neighboringGarbageBinAssignments );
  }
  
  private MRTASolutionModel invert() {
    final ArrayList<Integer> neighboringGarbageBinAssignmentsArr = new ArrayList<Integer>();
    int invertIndexA = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    int invertIndexB = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    int startIndex = Math.min( invertIndexA, invertIndexB );
    int endIndex = Math.max( invertIndexA, invertIndexB );
    
    ArrayList<Integer> invertList = new ArrayList<Integer>( neighboringGarbageBinAssignmentsArr.subList( startIndex, endIndex + 1 ) );
    neighboringGarbageBinAssignmentsArr.subList( startIndex, endIndex + 1 ).clear();
    Collections.reverse( invertList );
    neighboringGarbageBinAssignmentsArr.addAll( startIndex, invertList );
    int[] neighboringGarbageBinAssignments = new int[neighboringGarbageBinAssignmentsArr.size()];
    
    for( int idx = 0; idx < neighboringGarbageBinAssignmentsArr.size(); ++idx ) {
      neighboringGarbageBinAssignments[idx] = neighboringGarbageBinAssignmentsArr.get( idx );
    }
    
    return new MRTASolutionModel( new LinkedHashSet<GarbageBin>( m_garbageBins ), m_garbageClusterDataElements, neighboringGarbageBinAssignments );
  }
  
  private MRTASolutionModel scramble() {
    final ArrayList<Integer> neighboringGarbageBinAssignmentsArr = new ArrayList<Integer>();
    int scrambleIndexA = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    int scrambleIndexB = m_randomNumberGenerator.nextInt( neighboringGarbageBinAssignmentsArr.size() );
    int startIndex = Math.min( scrambleIndexA, scrambleIndexB );
    int endIndex = Math.max( scrambleIndexA, scrambleIndexB );
    
    ArrayList<Integer> scrambleList = new ArrayList<Integer>( neighboringGarbageBinAssignmentsArr.subList( startIndex, endIndex + 1 ) );
    neighboringGarbageBinAssignmentsArr.subList( startIndex, endIndex + 1 ).clear();
    Collections.shuffle( scrambleList );
    neighboringGarbageBinAssignmentsArr.addAll( startIndex, scrambleList );
    int[] neighboringGarbageBinAssignments = new int[neighboringGarbageBinAssignmentsArr.size()];
    
    for( int idx = 0; idx < neighboringGarbageBinAssignmentsArr.size(); ++idx ) {
      neighboringGarbageBinAssignments[idx] = neighboringGarbageBinAssignmentsArr.get( idx );
    }
    
    return new MRTASolutionModel( new LinkedHashSet<GarbageBin>( m_garbageBins ), m_garbageClusterDataElements, neighboringGarbageBinAssignments );
  }
}
