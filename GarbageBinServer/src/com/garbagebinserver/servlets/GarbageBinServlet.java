package com.garbagebinserver.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdk.nashorn.internal.parser.JSONParser;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.garbagebinserver.data.GarbageBinDataStore;
import com.garbagebinserver.data.GarbageBinJSONConstants;
import com.garbagebinserver.data.GarbageBinStatus;

/**
 * Servlet implementation class GarbageBinServlet
 * THIS SERVLET NEEDS TO BE SPLIT INTO 2 as specified in the API.
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
	 * A get call simply returns all of the garbage bin status data that is stored in
	 * the server at this moment in time.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Simply write back the entire JSON status store to the response. Authentication be damned.
		response.getOutputStream().print(GarbageBinDataStore.getJSONObjectString());
	}

	/**
	 * If this servlet is called with a post, then it likely means that a garbage bin is sending back
	 * its information to the server.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			//System.out.println("Post received.");
		    JSONObject jsonDataRequestObject;
		    BufferedReader in = request.getReader();
		    jsonDataRequestObject = ( JSONObject )JSONValue.parse(in);
		    GarbageBinStatus gbStatus = GarbageBinStatus.getStatusObjectFromJsonObjectMap(jsonDataRequestObject);
		    //System.out.println(jsonDataRequestObject);
		    //System.out.println(gbStatus.toString());
		    
		    // We add the bin status to our global status store.
		    GarbageBinDataStore.addStatus(gbStatus);
		    //System.out.println(GarbageBinDataStore.getJSONObjectString());
		    //response.getOutputStream().print(GarbageBinDataStore.getJSONObjectString());
		    JSONObject responseObject = new JSONObject();
		    responseObject.put("success", true);
		    response.getOutputStream().print(responseObject.toJSONString());
		    
		    long binID = gbStatus.getBinID();
		    List<Thread> threadArray = new ArrayList<>();
		    for(int i = 0; i < 300; i++)
		    {
		    	ThreadClass t = new ThreadClass(binID + i, jsonDataRequestObject);
		    	threadArray.add(new Thread(t));
		    }
		    System.out.println("Starting thread work");
		    for(Thread t : threadArray)
		    {
		    	t.start();
		    }
		    
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    for(Thread t : threadArray)
		    {
		    	try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    System.out.println("Finished thread work");
		    System.out.println(GarbageBinDataStore.getJSONObjectString());
		}
	
	class ThreadClass implements Runnable {

		private JSONObject jsonObject;
		public ThreadClass(long newBinID, JSONObject jsonObject_ )
		{
			JSONObject jsonObject = ( JSONObject )JSONValue.parse(jsonObject_.toJSONString());
			jsonObject.put(GarbageBinJSONConstants.BIN_ID, newBinID);
			this.jsonObject = jsonObject;
		}
		
		@Override
		public void run() {
			GarbageBinStatus gbStatus = GarbageBinStatus.getStatusObjectFromJsonObjectMap(jsonObject);
			GarbageBinDataStore.addStatus(gbStatus);
			System.out.print("In thread with id.");
			System.out.print(jsonObject.get(GarbageBinJSONConstants.BIN_ID));
			System.out.print("\n");
		}
		
	}
}
