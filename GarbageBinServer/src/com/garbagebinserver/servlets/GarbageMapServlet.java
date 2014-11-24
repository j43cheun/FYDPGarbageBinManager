package com.garbagebinserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


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
	  System.out.println("I am initialized!");
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
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		
		System.out.println("Attempting retrieval...");
		
		try {
		  String action = request.getParameter( "action" );
		  String json = request.getParameter( "json" );
		  JSONObject jsonData = ( JSONObject ) JSONValue.parse( json );
		  String helloMsg = ( String )jsonData.get( "helloMsg" );
		  System.out.println( helloMsg );
		  
		  JSONObject jsonResponse = new JSONObject();
		  jsonResponse.put( "message", "Hello from JSP!" );
		  
		  String jsonResponseStr = JSONObject.toJSONString( jsonResponse );
		  out.println( jsonResponseStr );
		}
		catch( Exception e ) {
		  e.printStackTrace();
		}
		finally {
		  out.flush();
		  out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	   RequestDispatcher view = request.getRequestDispatcher("jsp/garbagemap.jsp");
	   view.forward(request, response);
	}

}
