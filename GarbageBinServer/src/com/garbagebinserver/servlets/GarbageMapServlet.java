package com.garbagebinserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzer;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzerFactory;
import com.garbagebinserver.clusteranalysis.KMeansCluster;
import com.garbagebinserver.data.GarbageNavData;
import com.garbagebinserver.data.GarbageSpot;

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
      
		Connection conn = null;
		Statement statement = null;
		ResultSet results; 
		//Create a new SQL test statement
		String constructing = "INSERT INTO `garbagespot`(`id`, `name`, `latitude`, `longitude`) "
				+ "VALUES (?,?,?,?)";
		JSONObject finalObject = new JSONObject();
		System.out.println(constructing);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try {
				//Set up the connection to the database on port 3306 (default)
				//With username root and password fydp
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/trash", "root", "");

				//Perform a query
				PreparedStatement preparedStatement = conn.prepareStatement(constructing);
				preparedStatement.setInt(1, idCounter);
				preparedStatement.setString(2, name);
				preparedStatement.setDouble(3, latitude);
				preparedStatement.setDouble(4, longitude);
				preparedStatement.executeUpdate();
				
				//statement = conn.createStatement();
				//statement.executeUpdate(constructing);
				idCounter++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
          GPSCoordinates centroid = (GPSCoordinates)cluster.getCentroid();
          System.out.println("lat: " + centroid.getLatitude() + " long: " + centroid.getLongitude()); // FOR DEBUG
        }
	      break;
	    case "OPTION2":
	      GarbageNavData.getInstance().resetGarbageClusterID();
	      garbageSpotSet = GarbageNavData.getInstance().getAllGarbageSpots();
	      kmeansAnalyzer = KMeansAnalyzerFactory.findClusters( numGarbageClusters, numClusterIterations, garbageSpotSet, GarbageNavData.getInstance().getNextGarbageClusterID() );
	      GarbageNavData.getInstance().setGarbageClusters( kmeansAnalyzer.getClusters() );
	      break;
	    default:
	      // Unreachable!
	      break;
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
}
