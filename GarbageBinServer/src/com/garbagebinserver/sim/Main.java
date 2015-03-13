package com.garbagebinserver.sim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.garbagebinserver.allocator.MRTASolutionModel;
import com.garbagebinserver.allocator.SAOptimizer;
import com.garbagebinserver.allocator.SimpleAuctionAllocator;
import com.garbagebinserver.allocator.SolutionModel;
import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzer;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzerFactory;
import com.garbagebinserver.clusteranalysis.KMeansCluster;
import com.garbagebinserver.data.GarbageBin;
import com.garbagebinserver.data.GarbageClusterData;
import com.garbagebinserver.data.GarbageSpot;

public class Main {

  public static void main(String[] args) {
    final String inCsvFile = "C:/Users/Justin/Dropbox/FYDP/ece498b/prototype_data/allocation_sim/sim_1.csv";
    
    BufferedReader bufferedReader = null;
    String line = "";
    String csvSplitBy = ",";
    
    int numGarbageBins = 100;
    int garbageBinID = 1;
    int garbageSpotID = 1;
    
    ArrayList<GPSCoordinates> serviceStations = new ArrayList<GPSCoordinates>();
    LinkedHashSet<GarbageBin> garbageBins = new LinkedHashSet<GarbageBin>();
    LinkedHashSet<Coordinates> garbageSpots = new LinkedHashSet<Coordinates>();
    
    // TODO: Temporarily represent this as a double. In the future, we should use Garbage Spot History.
    //       That hasn't completely been integrated yet.
    LinkedHashMap<GarbageSpot, Double> garbageSpotHistoryTable = new LinkedHashMap<GarbageSpot, Double>();
    
    try
    {
      bufferedReader = new BufferedReader( new FileReader( inCsvFile ) );
      
      while( ( line = bufferedReader.readLine() ) != null )
      {
        String[] attribute = line.split( csvSplitBy );
        
        if( attribute[0].equals( "q" ) )
        {
          double latitude = Double.parseDouble( attribute[1] );
          double longitude = Double.parseDouble( attribute[2] );
          
          GPSCoordinates serviceStation = new GPSCoordinates( latitude, longitude );
          
          serviceStations.add( serviceStation );
        }
        else if( attribute[0].equals( "b" ) )
        {
          double latitude = Double.parseDouble( attribute[1] );
          double longitude = Double.parseDouble( attribute[2] );
          double maxGarbageVolume = Double.parseDouble( attribute[3] );
          double garbageVolume = Double.parseDouble( attribute[4] );
          
          GPSCoordinates location = new GPSCoordinates( latitude, longitude );
          GarbageBin garbageBin = new GarbageBin( garbageBinID++, maxGarbageVolume, location, "" );
          
          garbageBins.add( garbageBin );
        }
        else if( attribute[0].equals( "g" ) )
        {
          double latitude = Double.parseDouble( attribute[1] );
          double longitude = Double.parseDouble( attribute[2] );
          double avgGarbageVolumeProduced = Double.parseDouble( attribute[3] );
          
          GarbageSpot garbageSpot = new GarbageSpot( garbageSpotID++, "", latitude, longitude, "" );
          
          garbageSpotHistoryTable.put( garbageSpot, avgGarbageVolumeProduced );
          garbageSpots.add( garbageSpot );
        }
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      if( bufferedReader != null )
      {
        try
        {
          bufferedReader.close();
        }
        catch( IOException e )
        {
          e.printStackTrace();
        }
      }
    }
    
    // Garbage Cluster Simulation starts here! //
    long startTime = System.currentTimeMillis();
    KMeansAnalyzer kmeansAnalyzer = KMeansAnalyzerFactory.findClusters(numGarbageBins, 100000, garbageSpots, 1);
    long stopTime = System.currentTimeMillis();
    System.out.println("KMEANS COST: " + kmeansAnalyzer.getCost() + " ELAPSED TIME MS: " + ( stopTime - startTime ) );
    
    // Garbage Bin Allocation Simulation starts here! //
    ArrayList<GarbageBin> garbageBinArray = new ArrayList<GarbageBin>( garbageBins );
    ArrayList<GarbageClusterData> garbageClusterDataElements = new ArrayList<GarbageClusterData>();
    
    // Populate garbage cluster data elements!
    for( KMeansCluster garbageCluster : kmeansAnalyzer.getClusters() )
    {
      double totalAvgGarbageVolumeProduced = 0;
      
      for( Coordinates coordinates : garbageCluster.getClusterPoints() )
      {
        GarbageSpot garbageSpot = ( GarbageSpot ) coordinates;
        totalAvgGarbageVolumeProduced += garbageSpotHistoryTable.get( garbageSpot );
      }
      
      GarbageClusterData garbageClusterDataElement = new GarbageClusterData( garbageCluster, totalAvgGarbageVolumeProduced );
      garbageClusterDataElements.add( garbageClusterDataElement );
    }
    
    // Allocate with Simple Auction Allocator.
    startTime = System.currentTimeMillis();
    SimpleAuctionAllocator simpleAuctionAllocator = new SimpleAuctionAllocator( garbageBinArray, garbageClusterDataElements, serviceStations );
    simpleAuctionAllocator.allocate();
    stopTime = System.currentTimeMillis();
    
    System.out.println( "SIMPLE AUCTION COST: " + simpleAuctionAllocator.computeCost() + " ELAPSED TIME MS: " + ( stopTime - startTime ) );
    
    // Allocate with MRTA Allocator.
    
    
    final double coolingFactor = 0.9;
    final double initialTemp = 100;
    final double finalTemp = 1;
    final int iterPerTemp = 10;
    final int maxIter = 1000;
    startTime = System.currentTimeMillis();
    MRTASolutionModel initialSolution = new MRTASolutionModel( garbageBinArray, garbageClusterDataElements, serviceStations );
    SAOptimizer saOptimizer = new SAOptimizer( coolingFactor, initialTemp, finalTemp, iterPerTemp, maxIter );
    SolutionModel optimizedSolution = saOptimizer.optimizeSolution( initialSolution );
    stopTime = System.currentTimeMillis();
    System.out.println( "MRTA COST: " + optimizedSolution.computeCost() + " ELAPSED TIME MS: " + ( stopTime - startTime ) );
  }
}
