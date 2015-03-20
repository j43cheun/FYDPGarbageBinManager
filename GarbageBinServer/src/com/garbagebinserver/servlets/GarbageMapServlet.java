package com.garbagebinserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.garbagebinserver.allocator.MRTASolutionModel;
import com.garbagebinserver.allocator.SAOptimizerFactory;
import com.garbagebinserver.allocator.SolutionModel;
import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzer;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzerFactory;
import com.garbagebinserver.clusteranalysis.KMeansCluster;
import com.garbagebinserver.data.GarbageBin;
import com.garbagebinserver.data.GarbageBinDataStore;
import com.garbagebinserver.data.GarbageBinStatus;
import com.garbagebinserver.data.GarbageClusterData;
import com.garbagebinserver.data.GarbageNavData;
import com.garbagebinserver.data.GarbageSpot;
import com.garbagebinserver.data.ServiceStation;

/**
 * Servlet implementation class GarbageMapServlet
 */
@WebServlet("/garbagemapServlet")
public class GarbageMapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static int idCounter = 10;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GarbageMapServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest( request, response );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   RequestDispatcher view = request.getRequestDispatcher("jsp/garbagemap.jsp");
	   view.forward(request, response);
	}

	protected void processRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException {
	  response.setContentType( "text/json" );
	  PrintWriter out = response.getWriter();
	  
	  try {
	    final String action = request.getParameter( "action" );
	    final JSONObject jsonDataResponseObject = new JSONObject();
	    
	    String jsonDataRequestString;
	    JSONObject jsonDataRequestObject;
	    
	    switch( action ) {
	      case "addGarbageSpot":
	        jsonDataRequestString = ( String )request.getParameter( "json" );
	        jsonDataRequestObject = ( JSONObject )JSONValue.parse( jsonDataRequestString );
	        addGarbageSpot( jsonDataRequestObject, jsonDataResponseObject );
	        break;
	      case "getGarbageSpots":
	        getGarbageSpots( jsonDataResponseObject );
	        break;
	      case "computeGarbageClusters":
	        jsonDataRequestString = ( String )request.getParameter( "json" );
            jsonDataRequestObject = ( JSONObject )JSONValue.parse( jsonDataRequestString );
	        computeGarbageClusters( jsonDataRequestObject );
	        break;
	      case "getGarbageClusters":
	        getGarbageClusters( jsonDataResponseObject );
	        break;
	      case "getGarbageBins":
	        getGarbageBins( jsonDataResponseObject );
	        break;
	      case "addServiceStation":
	        jsonDataRequestString = ( String ) request.getParameter( "json" );
	        jsonDataRequestObject = ( JSONObject ) JSONValue.parse( jsonDataRequestString );
	        addServiceStation( jsonDataRequestObject, jsonDataResponseObject );
	        break;
	      case "getServiceStations":
	        getServiceStations( jsonDataResponseObject );
	        break;
	      case "allocateGarbageBins":
	        jsonDataRequestString = ( String )request.getParameter( "json" );
            jsonDataRequestObject = ( JSONObject )JSONValue.parse( jsonDataRequestString );
	        allocateGarbageBins( jsonDataRequestObject, jsonDataResponseObject );
	        break;
	      default:
	        break;
	    }
	    
	    final String jsonDataResponseString = JSONObject.toJSONString( jsonDataResponseObject );
	    out.println( jsonDataResponseString );
	  }
	  catch( Exception e ) {
	    e.printStackTrace();
	  }
	  finally {
	    out.flush();
	    out.close();
	  }
	}
	
	protected void addGarbageSpot( JSONObject jsonDataRequestObject, JSONObject jsonDataResponseObject ) {
	  final String name = ( String )jsonDataRequestObject.get( "name" );
      final double latitude = ( double )jsonDataRequestObject.get( "latitude" );
      final double longitude = ( double )jsonDataRequestObject.get( "longitude" );
      final String description = ( String )jsonDataRequestObject.get( "description" );
      final int garbageSpotID = GarbageNavData.getInstance().addGarbageSpot( name, latitude, longitude, description );
      jsonDataResponseObject.put( "garbageSpotID", garbageSpotID );
	}
	
	protected void addServiceStation( JSONObject jsonDataRequestObject, JSONObject jsonDataResponseObject ) {
	  final String name = ( String ) jsonDataRequestObject.get( "name" );
	  final double latitude = ( double ) jsonDataRequestObject.get( "latitude" );
	  final double longitude = ( double ) jsonDataRequestObject.get( "longitude" );
	  final String description= ( String ) jsonDataRequestObject.get( "description" );
	  final int serviceStationID = GarbageNavData.getInstance().addServiceStation( name, latitude, longitude, description );
	  jsonDataResponseObject.put( "serviceStationID", serviceStationID );
	}
	
	protected void computeGarbageClusters( JSONObject jsonDataRequestObject ) {
	  final String allocationOption = ( String )jsonDataRequestObject.get( "allocationOption" );
	  final int numGarbageClusters = Integer.parseInt( ( String )jsonDataRequestObject.get( "numGarbageClusters" ) );
	  final int numClusterIterations = Integer.parseInt( ( String )jsonDataRequestObject.get( "numClusterIterations" ) );
	  
	  LinkedHashSet<Coordinates> garbageSpotSet = null;
	  KMeansAnalyzer kmeansAnalyzer = null;
	  
	  switch( allocationOption ) {
	    case "OPTION1":
	      garbageSpotSet = GarbageNavData.getInstance().getAvailableGarbageSpots();
	      kmeansAnalyzer = KMeansAnalyzerFactory.findClusters( numGarbageClusters, numClusterIterations, garbageSpotSet, GarbageNavData.getInstance().getNextGarbageClusterID() );
	      GarbageNavData.getInstance().addGarbageClusters( kmeansAnalyzer.getClusters() );
	      
	      for( KMeansCluster cluster : kmeansAnalyzer.getClusters() ) {
	        GarbageNavData.getInstance().makeClusterAvailable( cluster.getClusterID() );
            GPSCoordinates centroid = ( GPSCoordinates ) cluster.getCentroid();
          }
	      break;
	    case "OPTION2":
	      GarbageNavData.getInstance().resetGarbageClusterID();
	      garbageSpotSet = GarbageNavData.getInstance().getAllGarbageSpots();
	      kmeansAnalyzer = KMeansAnalyzerFactory.findClusters( numGarbageClusters, numClusterIterations, garbageSpotSet, GarbageNavData.getInstance().getNextGarbageClusterID() );
	      GarbageNavData.getInstance().setGarbageClusters( kmeansAnalyzer.getClusters() );
	      GarbageNavData.getInstance().clearGarbageBinAssignmentTable();
	      break;
	    default:
	      // Unreachable!
	      break;
	  }
	}
	
	protected void allocateGarbageBins( JSONObject jsonDataRequestObject, JSONObject jsonDataResponseObject ) {
	  ArrayList<GarbageBin> garbageBins = new ArrayList<GarbageBin>();
	  ArrayList<ServiceStation> serviceStations = new ArrayList<ServiceStation>( GarbageNavData.getInstance().getServiceStations() );
	  
	  ConcurrentHashMap<Long, GarbageBinStatus> garbageBinStatusTable = GarbageBinDataStore.getStatusMap();
	  
	  for( Long key : garbageBinStatusTable.keySet() ) {
	    int garbageBinID = key.intValue();
	    
	    if( GarbageNavData.getInstance().isGarbageBinAvailable( garbageBinID ) ) {
	      GarbageBinStatus gbStatus = garbageBinStatusTable.get( key );
	      double maxGarbageVolume = gbStatus.getVolume();
	      GPSCoordinates location = gbStatus.getCoordinate();
	      String ipAddr = gbStatus.getIp();
	      
	      GarbageBin garbageBin = new GarbageBin( garbageBinID, maxGarbageVolume, location, ipAddr );
	      garbageBins.add( garbageBin );
	    }
	  }
	  
	  ArrayList<GarbageClusterData> gbClusterDataElements = new ArrayList<GarbageClusterData>();
	  
	  for( KMeansCluster cluster : GarbageNavData.getInstance().getAvailableClusters() ) {
	    double totalAvgGarbageVolumeProduced = 0;
	    
	    for( Coordinates coordinates : cluster.getClusterPoints() ) {
	      GarbageSpot garbageSpot = ( GarbageSpot ) coordinates;
	      // TODO: Populate garbage volume data from database.
	    }
	    
	    GarbageClusterData gbClusterDataElement = new GarbageClusterData( cluster, totalAvgGarbageVolumeProduced );
	    gbClusterDataElements.add( gbClusterDataElement );
	  }
	  
	  System.out.println( JSONObject.toJSONString( jsonDataRequestObject ) );
	  
	  double coolingFactor = ( double ) jsonDataRequestObject.get( "coolingFactor" );
	  double initialTemperature = ( double ) jsonDataRequestObject.get( "initialTemperature" );
	  double finalTemperature = ( double ) jsonDataRequestObject.get( "finalTemperature" );
	  int nestedIterations = (( Long ) jsonDataRequestObject.get( "nestedIterations" )).intValue();
	  int maxIterations = (( Long ) jsonDataRequestObject.get( "maxIterations" )).intValue();
	  
	  MRTASolutionModel mrtaSolution = ( MRTASolutionModel ) SAOptimizerFactory.solveAndOptimize(garbageBins, gbClusterDataElements, serviceStations, coolingFactor, initialTemperature, finalTemperature, nestedIterations, maxIterations);
	  LinkedHashMap<Integer, Integer> mappedAllocation = mrtaSolution.getMappedAllocation();
	  
	  for( int garbageBinID : mappedAllocation.keySet() ) {
	    if( mappedAllocation.get( garbageBinID ) != -1 ) {
	      GarbageNavData.getInstance().addAllocation( garbageBinID, mappedAllocation.get( garbageBinID ) );
	      GarbageNavData.getInstance().makeClusterUnavailable( mappedAllocation.get( garbageBinID ) );
	      jsonDataResponseObject.put( garbageBinID, mappedAllocation.get( garbageBinID ) );
	    }
	  }
	}
	
	protected void getGarbageSpots( JSONObject jsonDataResponseObject ) {
	  LinkedHashSet<Coordinates> garbageSpotCoordinates = GarbageNavData.getInstance().getAllGarbageSpots();
	  
	  for( Coordinates garbageSpotCoordinate : garbageSpotCoordinates ) {
	    // All Coordinate instances are also GarbageSpot instances!
	    final GarbageSpot garbageSpot = ( GarbageSpot )garbageSpotCoordinate;
	    JSONObject garbageSpotJSONObject = new JSONObject();
	    
	    garbageSpotJSONObject.put( "garbageSpotID", garbageSpot.getGarbageSpotID() );
	    garbageSpotJSONObject.put( "garbageClusterID", garbageSpot.getClusterID() );
	    garbageSpotJSONObject.put( "name", garbageSpot.getName() );
	    garbageSpotJSONObject.put( "latitude", garbageSpot.getLatitude() );
	    garbageSpotJSONObject.put( "longitude", garbageSpot.getLongitude() );
	    garbageSpotJSONObject.put( "description", garbageSpot.getDescription() );
	    
	    String garbageSpotJSONString = JSONObject.toJSONString( garbageSpotJSONObject );
	    jsonDataResponseObject.put( garbageSpot.getGarbageSpotID(), garbageSpotJSONString );
	  }
	}
	
	protected void getGarbageClusters( JSONObject jsonDataResponseObject ) {
	  LinkedHashSet<KMeansCluster> garbageClusters = GarbageNavData.getInstance().getGarbageClusters();
	  
	  for( KMeansCluster garbageCluster : garbageClusters ) {
	    JSONObject garbageClusterJSONObject = new JSONObject();
	    GPSCoordinates clusterCentroid = ( GPSCoordinates )garbageCluster.getCentroid();
	    
	    garbageClusterJSONObject.put( "garbageClusterID", garbageCluster.getClusterID() );
	    garbageClusterJSONObject.put( "latitude", clusterCentroid.getLatitude() );
	    garbageClusterJSONObject.put( "longitude", clusterCentroid.getLongitude() );
	    
	    String garbageClusterJSONString = JSONObject.toJSONString( garbageClusterJSONObject );
	    jsonDataResponseObject.put( garbageCluster.getClusterID(), garbageClusterJSONString );
	  }
	}
	
	protected void getGarbageBins( JSONObject jsonDataResponseObject ) {
	  ConcurrentHashMap<Long, GarbageBinStatus> garbageBinStatusTable = GarbageBinDataStore.getStatusMap();
	  
	  for( Entry<Long, GarbageBinStatus> entrySet : garbageBinStatusTable.entrySet() )
	  {
	    Long garbageBinID = entrySet.getKey();
	    GarbageBinStatus garbageBinStatus = entrySet.getValue();
	    
	    JSONObject garbageBinJSONObject = new JSONObject();
	    
	    Double garbageBinCurrentDepth = garbageBinStatus.getCurrent_depth();
	    Double garbageBinMaxDepth = garbageBinStatus.getMax_depth();
	    Double garbageBinMaxVolume = garbageBinStatus.getVolume();
	    Double garbageBinCurrentVolume = ( garbageBinMaxDepth - garbageBinCurrentDepth ) * garbageBinMaxVolume / garbageBinMaxDepth;
	    Double garbageBinPercentFreeVolume = garbageBinCurrentDepth * 100 / garbageBinMaxDepth;
	    
	    if( garbageBinPercentFreeVolume > 10 ) {
	      GPSCoordinates garbageBinLocation = garbageBinStatus.getCoordinate();
	        
	      String garbageBinIP = garbageBinStatus.getIp();
	      
	      garbageBinJSONObject.put( "garbageBinID", garbageBinID );
	      garbageBinJSONObject.put( "garbageBinMaxVolume", garbageBinMaxVolume );
	      garbageBinJSONObject.put( "garbageBinCurrentVolume", garbageBinCurrentVolume );
	      garbageBinJSONObject.put( "garbageBinPercentFreeVolume", garbageBinPercentFreeVolume );
	      
	      garbageBinJSONObject.put( "maxDepth", garbageBinMaxDepth );
	      garbageBinJSONObject.put( "currentDepth", garbageBinCurrentDepth );
	      
	      garbageBinJSONObject.put( "latitude", garbageBinLocation.getLatitude() );
	      garbageBinJSONObject.put( "longitude", garbageBinLocation.getLongitude() );
	      garbageBinJSONObject.put( "IP", garbageBinIP );
	      
	      Integer assignedClusterID = null;
	      if( GarbageNavData.getInstance().getAssignedClusterID( garbageBinID.intValue() ) != null ) {
	        assignedClusterID = GarbageNavData.getInstance().getAssignedClusterID( garbageBinID.intValue() );
	      }
	      
	      garbageBinJSONObject.put( "clusterID", assignedClusterID );
	      
	      String garbageBinJSONString = JSONObject.toJSONString( garbageBinJSONObject );
	      jsonDataResponseObject.put( garbageBinID, garbageBinJSONString );
	    }
	  }
	}
	
	protected void getServiceStations( JSONObject jsonDataResponseObject ) {
	  LinkedHashSet<ServiceStation> serviceStations = GarbageNavData.getInstance().getServiceStations();
	  
	  for( ServiceStation serviceStation : serviceStations ) {
	    JSONObject serviceStationJSONObject = new JSONObject();
	    
	    serviceStationJSONObject.put( "serviceStationID", serviceStation.getServiceStationID() );
	    serviceStationJSONObject.put( "name", serviceStation.getName() );
	    serviceStationJSONObject.put( "latitude", serviceStation.getLatitude() );
	    serviceStationJSONObject.put( "longitude", serviceStation.getLongitude() );
	    serviceStationJSONObject.put( "description", serviceStation.getDescription() );
	    
	    String serviceStationJSONString = JSONObject.toJSONString( serviceStationJSONObject );
	    jsonDataResponseObject.put( serviceStation.getServiceStationID(), serviceStationJSONString );
	  }
	}
}
