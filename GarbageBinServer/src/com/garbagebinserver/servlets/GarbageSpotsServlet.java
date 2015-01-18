package com.garbagebinserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		        jsonDataRequestString = ( String )request.getParameter( "json" );
		        jsonDataRequestObject = ( JSONObject )JSONValue.parse( jsonDataRequestString );
		        jsonDataResponseObject.put( "response", "Hello from servlet" );
		        
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
