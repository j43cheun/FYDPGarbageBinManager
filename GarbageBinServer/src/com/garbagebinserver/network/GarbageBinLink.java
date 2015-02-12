package com.garbagebinserver.network;

import org.json.JSONObject;

import com.garbagebinserver.data.GarbageBinStatus;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * This class provides utilities to query a given garbage bin.
 * @author Zored
 *
 */
public class GarbageBinLink {
	/**
	 * This function returns the last known status for a garbage bin by contacting it
	 * at its last known status endpoint.
	 * @param Destination The ip or host name of the bin.
	 * @param Port The port the bin server uses.
	 * @return {@link GarbageBinStatus} object
	 */
	public static GarbageBinStatus getLastKnownStatus(String Destination, long Port)
	{
		JSONObject jsonStatusObject;
		try {
			HttpResponse<JsonNode> binResponse = Unirest.get(String.format("http://%s:%d/status/{method}", Destination, Port))
			  .routeParam("method", "laststatus")
			  .asJson();
			jsonStatusObject = binResponse.getBody().getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return GarbageBinStatus.getStatusObjectFromJsonObject(jsonStatusObject);
	}
	
	public static boolean requestUpdateToStatus(String destination, long port)
	{
		JSONObject jsonResponse;
		try {
			HttpResponse<JsonNode> binResponse = Unirest.post(String.format("http://%s:%d/status/{method}", destination, port))
			  .header("accept", "application/json")
			  .routeParam("method", "updatestatusPost")
			  .asJson();
			jsonResponse = binResponse.getBody().getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if (!jsonResponse.getString("status").equals("working"))
		{
			System.out.println("Not working!?");
			System.out.println(jsonResponse.getString("status"));
			return false;
		}
		return true;
	}
}
