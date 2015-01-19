package com.garbagebinserver.servlets;

import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Servlet implementation class GarbageSpotsServlet
 */
@WebServlet("/GarbageSpotsServlet")
public class GarbageSpotsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GarbageSpotsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest( request, response );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		      case "displaySpots":
		    	
		    	Connection conn = null;
		    	Statement statement = null;
		    	ResultSet results; 
		    	//Create a new SQL test statement
				String constructing = "SELECT * FROM `garbagespot` WHERE 1";
				JSONObject finalObject = new JSONObject();

				try {
					Class.forName("com.mysql.jdbc.Driver");
					
					//Set up the connection to the database on port 3306 (default)
					//To database robot1
					//With username root and password fydp
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/trash", "root", "");

					//Perform a query
					statement = conn.createStatement();
					results = statement.executeQuery(constructing);
					JSONArray allSpots = new JSONArray();
					while (results.next()) {
					    JSONObject garbageSpot = new JSONObject();
					    
					    garbageSpot.put("id", results.getInt("id"));
					    garbageSpot.put("name", results.getString("name"));
					    garbageSpot.put("latitude", results.getDouble("latitude"));
					    garbageSpot.put("longitude", results.getDouble("longitude"));
					    
					    allSpots.add(garbageSpot);
					    
					}
					finalObject.put("allSpots", allSpots);
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	   
		        jsonDataRequestString = ( String )request.getParameter( "json" );
		        jsonDataRequestObject = ( JSONObject )JSONValue.parse( jsonDataRequestString );
		        
		        jsonDataResponseObject.put( "response", finalObject );
		        
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

}
