package com.garbagebinserver.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.garbagebinserver.clusteranalysis.Coordinates;
import com.garbagebinserver.clusteranalysis.GPSCoordinates;
import com.garbagebinserver.clusteranalysis.KMeansCluster;

public class GarbageNavData {
  
  private static GarbageNavData m_instance = null;
  
  // TODO Have server and database provide these IDs.
  private int m_nextGarbageSpotID;
  private int m_nextServiceStationID;
  
  // Temporarily keeps track of garbage clusters. Do not place in database.
  private int m_nextGarbageClusterID;
  
  // Keeps track of garbage spots and garbage clusteres.
  private LinkedHashMap<Integer, GarbageSpot> m_garbageSpotTable;
  private LinkedHashMap<Integer, KMeansCluster> m_kmeansClusterTable;
  
  // Used to keep track of garbage bin allocation.
  private LinkedHashMap<Integer, Integer> m_allocationTable;
  
  // Used to keep track of available gb spots, clusters, and garbage bins.
  private LinkedHashSet<Integer> m_availableClustersByID;
  private LinkedHashSet<Integer> m_availableGarbageSpotsByID;
  
  // Used to keep track of service stations.
  private LinkedHashSet<ServiceStation> m_serviceStations;
  
  private GarbageNavData() {
    m_nextGarbageSpotID = 1;
    m_nextGarbageClusterID = 1;
    m_nextServiceStationID = 1;
    m_garbageSpotTable = new LinkedHashMap<Integer, GarbageSpot>();
    m_kmeansClusterTable = new LinkedHashMap<Integer, KMeansCluster>();
    m_availableGarbageSpotsByID = new LinkedHashSet<Integer>();
    m_serviceStations = new LinkedHashSet<ServiceStation>();
    m_availableClustersByID = new LinkedHashSet<Integer>();
    
    m_allocationTable = new LinkedHashMap<Integer, Integer>();
    
    // EUGENE's CODE. DO NOT DELETE!
    /*
    // Initialize garbage spot list.
    Connection conn = null;
    Statement statement = null;
    ResultSet results; 
    //Create a new SQL test statement
    String constructing = "SELECT * FROM `garbagespot` WHERE 1";
    JSONObject finalObject = new JSONObject();
  
    try {
      try {
        Class.forName("com.mysql.jdbc.Driver");
            
        //Set up the connection to the database on port 3306 (default)
        //To database robot1
        //With username root and password fydp
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/trash", "root", "");

        //Perform a query
        statement = conn.createStatement();
        results = statement.executeQuery(constructing);
        JSONArray allSpots = new JSONArray();
        while (results.next()) {                
            int garbageSpotId = results.getInt("id");
            String garbageSpotName = results.getString("name");
            double garbageSpotLat = results.getDouble("latitude");
            double garbageSpotLong = results.getDouble("longitude");
            
            GarbageSpot newSpot = new GarbageSpot(garbageSpotId, 
                    garbageSpotName, garbageSpotLat, garbageSpotLong, "");
            m_garbageSpotTable.put(results.getInt("id"), newSpot);
        }               
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    catch( Exception e ) {
        e.printStackTrace();
    }
    */
  }
  
  public static GarbageNavData getInstance() {
    if( m_instance == null ) {
      m_instance = new GarbageNavData();
    }
    
    return m_instance;
  }
  
  public int addServiceStation( final String name, final double latitude, final double longitude, final String description ) {
    int serviceStationID = m_nextServiceStationID++;
    ServiceStation serviceStation = new ServiceStation( serviceStationID, name, latitude, longitude, description );
    m_serviceStations.add( serviceStation );
    return serviceStationID;
  }
  
  public int addGarbageSpot( final String name, final double latitude, final double longitude, final String description ) {
    // TODO
    // <latitude, longitude, name> tuple needs to be unique! Verify through SQL.
    // Adding a garbage spot will automatically add it to SQL database.
    // Return -1 if unable to add garbage spot!
    // Will need Evgeny's help with this!
    // Also need to check boundaries!
    
    int garbageSpotID = m_nextGarbageSpotID;
    GarbageSpot garbageSpot = new GarbageSpot( garbageSpotID, name, latitude, longitude, description );
    m_garbageSpotTable.put( garbageSpotID, garbageSpot );
    m_availableGarbageSpotsByID.add( garbageSpotID );
    
    // TEMP
    m_nextGarbageSpotID++;
    
    //Doing DB stuff here
    Connection conn = null;
    //Create a new SQL test statement
    String constructing = "INSERT INTO `garbagespot`(`name`, `description`, `latitude`, `longitude`) "
            + "VALUES (?,?,?,?)";
    
    try {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            //Set up the connection to the database on port 3306 (default)
            //With username root and password fydp
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/trash", "root", "");

            //Perform a query
            PreparedStatement preparedStatement = conn.prepareStatement(constructing);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, latitude);
            preparedStatement.setDouble(4, longitude);
            preparedStatement.executeUpdate();
            
            //If succesful, increment counter
            m_nextGarbageSpotID++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } catch (ClassNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
    return garbageSpotID;
  }
  
  public LinkedHashSet<Coordinates> getAllGarbageSpots() {
    return new LinkedHashSet<Coordinates>( m_garbageSpotTable.values() );
  }
  
  public LinkedHashSet<Coordinates> getAvailableGarbageSpots() {
    final LinkedHashSet<Coordinates> availableGarbageSpots = new LinkedHashSet<Coordinates>();
    
    for( Integer availableGarbageSpotID : m_availableGarbageSpotsByID ) {
      // Note: All garbage spot IDs MUST have a corresponding garbage spot!
      availableGarbageSpots.add( m_garbageSpotTable.get( availableGarbageSpotID ) );
    }
    
    return availableGarbageSpots;
  }
  
  public LinkedHashSet<Integer> getAvailableGarbageSpotsByID() {
    return m_availableGarbageSpotsByID;
  }
  
  public LinkedHashSet<KMeansCluster> getGarbageClusters() {
    return new LinkedHashSet<KMeansCluster>( m_kmeansClusterTable.values() );
  }
  
  public void addGarbageClusters( final LinkedHashSet<KMeansCluster> garbageClusters ) {
    for( KMeansCluster kmeansCluster : garbageClusters ) {
      m_kmeansClusterTable.put( kmeansCluster.getClusterID() , kmeansCluster );
      
      for( Coordinates coordinates : kmeansCluster.getClusterPoints() ) {
        GarbageSpot garbageSpot = ( GarbageSpot ) coordinates;
        m_availableGarbageSpotsByID.remove( garbageSpot.getGarbageSpotID() );
      }
    }
  }
  
  public void setGarbageClusters( final LinkedHashSet<KMeansCluster> garbageClusters ) {
    m_kmeansClusterTable.clear();
    m_availableClustersByID.clear();
    m_availableGarbageSpotsByID.clear();
    
    for( KMeansCluster kmeansCluster : garbageClusters ) {
      m_kmeansClusterTable.put( kmeansCluster.getClusterID() , kmeansCluster );
      m_availableClustersByID.add( kmeansCluster.getClusterID() );
      
      for( Coordinates coordinates : kmeansCluster.getClusterPoints() ) {
        GarbageSpot gbSpot = ( GarbageSpot ) coordinates;
        m_availableGarbageSpotsByID.remove( gbSpot.getGarbageSpotID() );
      }
    }
  }
  
  public void addServiceStation( final ServiceStation serviceStation ) {
    m_serviceStations.add( serviceStation );
  }
  
  public LinkedHashSet<ServiceStation> getServiceStations() {
    return m_serviceStations;
  }
  
  public int getNextGarbageClusterID() {
    int nextGarbageClusterID = m_nextGarbageClusterID;
    
    m_nextGarbageClusterID++;
    
    return nextGarbageClusterID;
  }
  
  public void resetGarbageClusterID() {
    m_nextGarbageClusterID = 1;
  }
  
  public boolean isGarbageBinAvailable( int garbageBinID ) {
    return m_allocationTable.get( garbageBinID ) == null;
  }
  
  public void clearGarbageBinAssignmentTable() {
    m_allocationTable.clear();
  }
  
  public void addAllocation( int garbageBinID, int clusterID ) {
    m_allocationTable.put( garbageBinID , clusterID );
  }
  
  public ArrayList<KMeansCluster> getAvailableClusters() {
    ArrayList<KMeansCluster> clusters = new ArrayList<KMeansCluster>();
    
    for( int clusterID : m_kmeansClusterTable.keySet() ) {
      if( m_availableClustersByID.contains( clusterID ) ) {
        clusters.add( m_kmeansClusterTable.get( clusterID ) );
      }
    }
    
    return clusters;
  }
  
  public void removeAvailableCluster( int clusterID ) {
    m_availableClustersByID.remove( clusterID );
  }
  
  public void makeClusterAvailable( int clusterID ) {
    m_availableClustersByID.add( clusterID );
  }
  
  public void makeClusterUnavailable( int clusterID ) {
    m_availableClustersByID.remove( clusterID );
  }
  
  public boolean isClusterAvailable( int clusterID ) {
    return m_availableClustersByID.contains( clusterID );
  }
}
