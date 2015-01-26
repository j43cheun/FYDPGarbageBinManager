package com.garbagebinserver.servlets;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdk.nashorn.internal.parser.JSONParser;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.garbagebinserver.data.GarbageBinDataStore;
import com.garbagebinserver.data.GarbageBinStatus;

/**
 * Servlet implementation class GarbageBinServlet
 */
@WebServlet("/GarbageBinServlet")
public class GarbageBinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GarbageBinServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * If this servlet is called with a post, then it likely means that a garbage bin is sending back
	 * its information to the server.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			System.out.println("Post received.");
		    JSONObject jsonDataRequestObject;
		    BufferedReader in = request.getReader();
		    jsonDataRequestObject = ( JSONObject )JSONValue.parse(in);
		    GarbageBinStatus gbStatus = GarbageBinStatus.getStatusObjectFromJsonObject(jsonDataRequestObject);
		    System.out.println(jsonDataRequestObject);
		    System.out.println(gbStatus.toString());
		    
		    // We add the bin status to our global status store.
		    GarbageBinDataStore.addStatus(gbStatus);
		    System.out.println(GarbageBinDataStore.getJSONObjectString());
		}

}
