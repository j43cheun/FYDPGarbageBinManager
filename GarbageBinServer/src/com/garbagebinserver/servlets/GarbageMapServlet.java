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
	    
	    switch( action ) {
	      case "addGarbageSpot":
	        final String jsonDataRequestString = ( String )request.getParameter( "json" );
	        final JSONObject jsonDataRequestObject = ( JSONObject )JSONValue.parse( jsonDataRequestString );
	        addGarbageSpot( jsonDataRequestObject, jsonDataResponseObject );
	        break;
	      case "getGarbageSpots":
	        getGarbageSpots( jsonDataResponseObject );
	        break;
	      case "allocateGarbageBins":
	        // TODO
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
	
	protected void getGarbageSpots( JSONObject jsonDataResponseObject ) {
	  LinkedHashSet<GarbageSpot> garbageSpots = GarbageNavData.getInstance().getGarbageSpots();
	  
	  for( GarbageSpot garbageSpot : garbageSpots ) {
	    JSONObject garbageSpotJSONObject = new JSONObject();
	    
	    garbageSpotJSONObject.put( "garbageSpotID", garbageSpot.getGarbageSpotID() );
	    garbageSpotJSONObject.put( "name", garbageSpot.getName() );
	    garbageSpotJSONObject.put( "latitude", garbageSpot.getLatitude() );
	    garbageSpotJSONObject.put( "longitude", garbageSpot.getLongitude() );
	    garbageSpotJSONObject.put( "description", garbageSpot.getDescription() );
	    
	    String garbageSpotJSONString = JSONObject.toJSONString( garbageSpotJSONObject );
	    jsonDataResponseObject.put( garbageSpot.getGarbageSpotID(), garbageSpotJSONString );
	  }
	}
}
