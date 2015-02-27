package com.garbagebinserver.servlets;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.garbagebinserver.data.GarbageBinDataStore;
import com.garbagebinserver.data.GarbageBinJSONConstants;
import com.garbagebinserver.data.GarbageBinStatus;
import com.garbagebinserver.network.GarbageBinLink;

/**
 * Servlet implementation class GarbageBinRegistrationServlet
 */
@WebServlet("/GarbageBinRegistrationServlet")
public class GarbageBinRegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GarbageBinRegistrationServlet() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	/**
	 * When a post request to this servlet is made, a trash can is asking if it can be registered
	 * to this server. If registered, the trash can will be polled periodically by this server.
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Registration Post received.");
	    JSONObject jsonDataRequestObject;
	    BufferedReader in = request.getReader();
	    jsonDataRequestObject = ( JSONObject )JSONValue.parse(in);
	    System.out.println(jsonDataRequestObject.toString());
	    response.getWriter().print(getSuccessObject().toJSONString());
	    
	    //We ask for the current status.
	    String ip = (String) jsonDataRequestObject.get(GarbageBinJSONConstants.IP);
	    long port = (long) jsonDataRequestObject.get(GarbageBinJSONConstants.PORT);
	    
	    //GarbageBinStatus binStatus = GarbageBinLink.getLastKnownStatus(ip, port);
	    //GarbageBinDataStore.addStatus(binStatus);
	    //System.out.println(GarbageBinDataStore.getJSONObjectString());
	    
	    //We ask for an update to the status.
	    GarbageBinLink.requestUpdateToStatus(ip, port);
	}

	/**
	 * Move this out to a utility class.
	 * @return
	 */
	private JSONObject getSuccessObject()
	{
		JSONObject successObject = new JSONObject();
		successObject.put("success", true);
		return successObject;
	}
	
}
