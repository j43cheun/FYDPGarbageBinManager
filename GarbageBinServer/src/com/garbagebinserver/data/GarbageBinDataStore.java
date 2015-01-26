package com.garbagebinserver.data;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;

/**
 * This is a simple class that provides a simple api for accessing the 
 * global bin status data store. This data store is used to populate the front end site.
 * @author Zored
 *
 */
public class GarbageBinDataStore {
	
	public static ConcurrentHashMap<Long, GarbageBinStatus> getStatusMap()
	{
		return statusMap;
	}
	
	/**
	 * This will add a garbage bin's status to the map. In the process of doing
	 * so, it will also replace any status that may have existed for that map before.
	 * In that sense, use with some caution.
	 * @param binStatus
	 */
	public static void addStatus(GarbageBinStatus binStatus)
	{
		Long binID = binStatus.getBinID();
		statusMap.put(binID, binStatus);
	}
	
	/**
	 * This function returns a large JSONObject containing every known bin status.
	 * That is to say, it returns a json object
	 * containing all of the bin status'es mapped to individual bin ids.
	 * Note that the return result is likely NOT SORTED. Sorting has to be done by someone else.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getJSONObject()
	{
		JSONObject jsonObjectToReturn = new JSONObject();
		
		for(Entry<Long, GarbageBinStatus> entry : statusMap.entrySet())
		{
			Long binID = entry.getKey();
			GarbageBinStatus binStatus = entry.getValue();
			jsonObjectToReturn.put(binID, binStatus.convertToJSON());
		}
		
		return jsonObjectToReturn;
	}
	
	/**
	 * This returns a JSON object string representation
	 * of every bin that we have and its status that can be passed to the front end or sent to
	 * a different server.
	 */
	public static String getJSONObjectString()
	{
		return getJSONObject().toJSONString();
	}
	
	// This is a global hash map that allows for the servlets to access the in memory
	// garbage status data. It maps a bin id to a status.
	private static ConcurrentHashMap<Long, GarbageBinStatus> statusMap = new ConcurrentHashMap<Long, GarbageBinStatus>();
}
