package com.garbagebinserver.network;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.garbagebinserver.data.GarbageBinDataStore;
import com.garbagebinserver.data.GarbageBinStatus;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * This class provides utilities to query a given garbage bin or all garbage bins.
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
	
	/**
	 * This function requests an update from all garbage bins. If it fails for some reason,
	 * this function will set the error field in the JSON to true.
	 * This won't stop future attempts to contact the garbage bin, but they will just all
	 * fail as well. The administration ui will have a remove all disconnected bins feature.
	 * Note that this function does not actually get the updated status, it simply requests
	 * all bins to send it to us.
	 */
	public static void requestUpdateForAllBins()
	{
		ConcurrentHashMap<Long, GarbageBinStatus> binMap = GarbageBinDataStore.getStatusMap();
		Set<Entry<Long, GarbageBinStatus>> binEntrySet = binMap.entrySet();
		for(Entry<Long, GarbageBinStatus> statusSet : binEntrySet)
		{
			GarbageBinStatus binStatus = statusSet.getValue();
			String destination = binStatus.getIp();
			long port = binStatus.getPort();
			
			//We attempt to request an update to the status.
			if (requestUpdateToStatus(destination, port))
			{
				binStatus.setError(false);
			}
			else
			{
				//We encountered an error asking for a status update for reasons
				//we don't really care about. :/
				binStatus.setError(true);
			}
		}
	}
}
