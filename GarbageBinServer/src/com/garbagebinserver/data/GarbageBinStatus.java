package com.garbagebinserver.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.json.simple.JSONObject;

import com.garbagebinserver.data.GarbageBinJSONConstants;
import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;

/**
 * This is a container class that encapsulates a Garbage Bin's current status
 * in a Java object. It also provides utilities for converting from a JSON object
 * to a Java object.
 * @author Zored
 *
 */
public class GarbageBinStatus {

	
	public double getVolume() {
		return volume;
	}

	public double getCurrent_depth() {
		return currentDepth;
	}

	public void setCurrent_depth(double current_depth) {
		this.currentDepth = current_depth;
	}

	public double getMax_depth() {
		return maxDepth;
	}

	public void setMax_depth(double max_depth) {
		this.maxDepth = max_depth;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public void setBinID(Long binID) {
		this.binID = binID;
	}
	
	public GPSCoordinates getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(GPSCoordinates coordinate) {
		this.coordinate = coordinate;
	}

	public double getBattery() {
		return battery;
	}

	public void setBattery(double battery) {
		this.battery = battery;
	}

	/**
	 * Note: Capacity refers to % volume free.
	 * @return
	 */
	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public Long getBinID() {
		return binID;
	}
	
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	
	public void setError(boolean error)
	{
		this.error = error;
	}
	
	public boolean hasError() {
		return error;
	}
	
	public GarbageBinStatus(
			long binID,
			GPSCoordinates coordinate,
			double battery,
			double volume,
			double maxDepth,
			double currentDepth,
			String ip,
			Long port,
			LocalDateTime timeStamp,
			boolean error)
	{
		this.binID = binID;
		this.coordinate = coordinate;
		this.battery = battery;
		this.volume = volume;
		this.maxDepth = maxDepth;
		this.currentDepth = currentDepth;
		this.capacity = calculateCapacity(volume, maxDepth, currentDepth);
		this.ip = ip;
		this.port = port;
		this.timeStamp = timeStamp;
		this.error = error;
	}
	
	private static double calculateCapacity(double garbageBinMaxVolume, double garbageBinMaxDepth, double garbageBinCurrentDepth)
	{
	    Double garbageBinPercentFreeVolume = ( garbageBinMaxDepth - garbageBinCurrentDepth ) * 100 / garbageBinMaxDepth;
		return garbageBinPercentFreeVolume;
	}
	
	public static GarbageBinStatus getStatusObjectFromJsonObjectMap(Map<Object, Object> jsonStatusMap)
	{
		Long binID = (Long) jsonStatusMap.get(GarbageBinJSONConstants.BIN_ID); 
		
		GPSCoordinates coordinates;
		{
			JSONObject jsonLocation = (JSONObject) jsonStatusMap.get(GarbageBinJSONConstants.LOCATION);
			double latitude = (Double) jsonLocation.get(GarbageBinJSONConstants.LATITUDE);
			double longitude = (Double) jsonLocation.get(GarbageBinJSONConstants.LONGITUDE);
			coordinates = new GPSCoordinates(latitude, longitude);
		}
		double battery = (Double) jsonStatusMap.get(GarbageBinJSONConstants.BATTERY);
		System.out.println(jsonStatusMap.toString());
		
		double volume = (Double) jsonStatusMap.get(GarbageBinJSONConstants.VOLUME);
		double maxDepth = (Double) jsonStatusMap.get(GarbageBinJSONConstants.MAX_DEPTH);
		double currentDepth = (Double) jsonStatusMap.get(GarbageBinJSONConstants.CURRENT_DEPTH);
		
		String ip = (String) jsonStatusMap.get(GarbageBinJSONConstants.IP);
		long port = (Long) jsonStatusMap.get(GarbageBinJSONConstants.PORT);
		
		String date = (String) jsonStatusMap.get(GarbageBinJSONConstants.TIMESTAMP);
		
		DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("y-M-d'T'H:m:s'.'n'Z'");
								
		LocalDateTime parsedTimeStamp = LocalDateTime.parse(date, formatter);
		
		boolean error;
		if (jsonStatusMap.containsKey(GarbageBinJSONConstants.ERROR))
		{
			error = (boolean)jsonStatusMap.get(GarbageBinJSONConstants.ERROR);
		}
		else
		{
			error = false; //Assume no error if the field is not present.
		}
		return new GarbageBinStatus(binID,coordinates, battery, volume, maxDepth, currentDepth, ip, port, parsedTimeStamp, error);
	}
	
	/**
	 * The previous function takes in a org.json.simple.JSONObject. This one takes in a 
	 * org.json.JSONObject. /Shrug
	 * Sorry about the confusion, but unfortunately, simple json is used in the servlet side of things
	 * but the request library which is used to make requests operates with org.json.JSONObject. :(
	 * Maybe, I should move the servlet to use org.JSON as well, but oh well. Not a big deal.
	 * @param jsonObject
	 * @return
	 */
	public static GarbageBinStatus getStatusObjectFromJsonObject(org.json.JSONObject jsonObject)
	{
		Long binID = jsonObject.getLong(GarbageBinJSONConstants.BIN_ID);
		GPSCoordinates coordinates;
		{
			org.json.JSONObject jsonLocation = jsonObject.getJSONObject(GarbageBinJSONConstants.LOCATION);
			double latitude = jsonLocation.getDouble(GarbageBinJSONConstants.LATITUDE);
			double longitude = jsonLocation.getDouble(GarbageBinJSONConstants.LONGITUDE);
			coordinates = new GPSCoordinates(latitude, longitude);
		}
		double battery = jsonObject.getDouble(GarbageBinJSONConstants.BATTERY);
		
		double volume = jsonObject.getDouble(GarbageBinJSONConstants.VOLUME);
		double maxDepth = jsonObject.getDouble(GarbageBinJSONConstants.MAX_DEPTH);
		double currentDepth = jsonObject.getDouble(GarbageBinJSONConstants.CURRENT_DEPTH);
		
		String ip = jsonObject.getString(GarbageBinJSONConstants.IP);
		long port = jsonObject.getLong(GarbageBinJSONConstants.PORT);
		String date = jsonObject.getString(GarbageBinJSONConstants.TIMESTAMP);
		
		//I couldn't find a built in formatter that worked, so had to roll my own. :(
		//2015-02-11T08:15:52.778Z
		DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("y-M-d'T'H:m:s'.'n'Z'");
								
		LocalDateTime parsedTimeStamp = LocalDateTime.parse(date, formatter);
		System.out.println(parsedTimeStamp.toString());

		boolean error;
		if (jsonObject.has(GarbageBinJSONConstants.ERROR))
		{
			error = jsonObject.getBoolean(GarbageBinJSONConstants.ERROR);
		}
		else
		{
			error = false; //Assume no error if the field is not present.
		}
		return new GarbageBinStatus(binID,coordinates, battery, volume, maxDepth, currentDepth, ip, port, parsedTimeStamp, error);
	}
	
	@Override
	public String toString()
	{
		String toFormat = "Bin ID: %d \nLocation: \n\tLatitude: %f \n\tLongitude: %f "
				+ "\nBattery: %f \nCapacity: %f \nIP: %s \nPort: %d \ntimeStamp : %s \nerror: %b";
		return String.format(toFormat, binID, coordinate.getLatitude(),
				coordinate.getLongitude(), battery, capacity, ip, port, timeStamp.toString(), error);
	}
	
	//There is probably a way around these generic warnings, but I dunno. ~_~ -Zored
	@SuppressWarnings("unchecked")
	public JSONObject convertToJSON()
	{
		JSONObject returnJSONObject = new JSONObject();
		returnJSONObject.put(GarbageBinJSONConstants.BIN_ID, binID);
		{
			JSONObject jsonLocation = new JSONObject();
			jsonLocation.put(GarbageBinJSONConstants.LATITUDE, coordinate.getLatitude());
			jsonLocation.put(GarbageBinJSONConstants.LONGITUDE, coordinate.getLongitude());
			returnJSONObject.put(GarbageBinJSONConstants.LOCATION, jsonLocation);
		}
		returnJSONObject.put(GarbageBinJSONConstants.BATTERY, battery);

		returnJSONObject.put(GarbageBinJSONConstants.VOLUME, volume);
		returnJSONObject.put(GarbageBinJSONConstants.MAX_DEPTH, maxDepth);
		returnJSONObject.put(GarbageBinJSONConstants.CURRENT_DEPTH, currentDepth);
		returnJSONObject.put(GarbageBinJSONConstants.CAPACITY, capacity);
		
		returnJSONObject.put(GarbageBinJSONConstants.IP, ip);
		returnJSONObject.put(GarbageBinJSONConstants.PORT, port);
		returnJSONObject.put(GarbageBinJSONConstants.ERROR, error);
		return returnJSONObject;
	}
	private double volume;
	private double currentDepth;
	private double maxDepth;
	private GPSCoordinates coordinate;
	private double battery;
	private double capacity;
	private Long binID;
	private String ip;
	private Long port;
	private LocalDateTime timeStamp;
	private boolean error;
}
