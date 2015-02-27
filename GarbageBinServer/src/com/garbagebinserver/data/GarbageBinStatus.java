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

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public Long getBinID() {
		return binID;
	}
	
	public GarbageBinStatus(
			long binID,
			GPSCoordinates coordinate,
			double battery,
			double capacity,
			String ip,
			Long port)
	{
		this.binID = binID;
		this.coordinate = coordinate;
		this.battery = battery;
		this.capacity = capacity;
		this.ip = ip;
		this.port = port;
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
		double capacity = (Double) jsonStatusMap.get(GarbageBinJSONConstants.CAPACITY);
		String ip = (String) jsonStatusMap.get(GarbageBinJSONConstants.IP);
		long port = (Long) jsonStatusMap.get(GarbageBinJSONConstants.PORT);
		return new GarbageBinStatus(binID,coordinates, battery, capacity, ip, port);
	}
	
	/**
	 * The previous function takes in a org.json.simple.JSONObject. This one takes in a 
	 * org.json.JSONObject. /Shrug
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
		double capacity = jsonObject.getDouble(GarbageBinJSONConstants.CAPACITY);
		String ip = jsonObject.getString(GarbageBinJSONConstants.IP);
		long port = jsonObject.getLong(GarbageBinJSONConstants.PORT);
		String date = jsonObject.getString(GarbageBinJSONConstants.TIMESTAMP);
		
		//I couldn't find a built in formatter that worked, so had to roll my own. :(
		//2015-02-11T08:15:52.778Z
		DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("y-M-d'T'H:m:s'.'n'Z'");
								
		LocalDateTime parsedTimeStamp = LocalDateTime.parse(date, formatter);
		System.out.println(parsedTimeStamp.toString());
		return new GarbageBinStatus(binID,coordinates, battery, capacity, ip, port);
	}
	
	@Override
	public String toString()
	{
		String toFormat = "Bin ID: %d \nLocation: \n\tLatitude: %f \n\tLongitude: %f "
				+ "\nBattery: %f \nCapacity: %f \nIP: %s \nPort: %d";
		return String.format(toFormat, binID, coordinate.getLatitude(),
				coordinate.getLongitude(), battery, capacity, ip, port);
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
		returnJSONObject.put(GarbageBinJSONConstants.CAPACITY, capacity);
		returnJSONObject.put(GarbageBinJSONConstants.IP, ip);
		returnJSONObject.put(GarbageBinJSONConstants.PORT, port);
		return returnJSONObject;
	}
	
	private GPSCoordinates coordinate;
	private double battery;
	private double capacity;
	private Long binID;
	private String ip;
	private Long port;
}
