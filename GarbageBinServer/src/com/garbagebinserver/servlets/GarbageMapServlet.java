package com.garbagebinserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzer;
import com.garbagebinserver.clusteranalysis.KMeansAnalyzerFactory;
import com.garbagebinserver.data.GarbageNavData;
import com.garbagebinserver.data.GarbageSpot;


/**
 * Servlet implementation class GarbageMapServlet
 */
@WebServlet("/garbagemapServlet")
public class GarbageMapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
	
	protected void computeGarbageClusters( JSONObject jsonDataRequestObject ) {
	  final String allocationOption = ( String )jsonDataRequestObject.get( "allocationOption" );
	  final int numGarbageClusters = Integer.parseInt( ( String )jsonDataRequestObject.get( "numGarbageClusters" ) );
	  final int numClusterIterations = Integer.parseInt( ( String )jsonDataRequestObject.get( "numClusterIterations" ) );
	  
	  LinkedHashSet<Coordinates> garbageSpotSet = null;
	  KMeansAnalyzer kmeansAnalyzer = null;
	  
	  switch( allocationOption ) {
	    case "OPTION1":
	      garbageSpotSet = GarbageNavData.getInstance().getAvailableGarbageSpots();
	      kmeansAnalyzer = KMeansAnalyzerFactory.findClusters( numGarbageClusters, numClusterIterations, garbageSpotSet );
	      GarbageNavData.getInstance().addGarbageClusters( kmeansAnalyzer.getClusters() );
	      break;
	    case "OPTION2":
	      garbageSpotSet = GarbageNavData.getInstance().getAllGarbageSpots();
	      kmeansAnalyzer = KMeansAnalyzerFactory.findClusters( numGarbageClusters, numClusterIterations, garbageSpotSet );
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
}
