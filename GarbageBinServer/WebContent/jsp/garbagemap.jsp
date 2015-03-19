<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <!-- Enable responsive Bootstrap site -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Garbage NAV - Garbage MAP</title>  
  <!-- Import Bootstrap Core CSS -->
  <link href="../lib/bootstrap/css/bootstrap.css" rel="stylesheet">
  <!-- Import Bootstrap Switch Core CSS -->
  <link href="../lib/bootstrap-switch/css/bootstrap-switch.css" rel="stylesheet">
  <!-- Import custom CSS for Garbage Bin Navigator -->
  <link href="../css/styles.css" rel="stylesheet">
  <!-- Import Google Maps API -->
  <style type="text/css">
    html, body, #map-canvas { height: 100%; margin: 0; padding: 0;}
  </style>
  <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAoLJ1FT47jNg_UUdJqU2ekjjaHqjhbdrY"></script>
  <style>
    body{
      padding-top:50px;
      padding-bottom: 50px;
    }
  </style>
  
</head>
<body> 
  <div class="navbar navbar-inverse navbar-fixed-top" id="navheader">
    <div class="container">
      <a href="home.jsp" class="navbar-brand"> 
        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
        Garbage NAV
      </a>
      <button class="navbar-toggle" data-toggle="collapse"
        data-target=".navHeaderCollapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <div class="collapse navbar-collapse navHeaderCollapse">
        <ul class="nav navbar-nav navbar-right">
          <li><a href="garbagebins.jsp">Garbage BINS</a></li>
          <li class="active"><a href="garbagemap.jsp">Garbage MAP</a></li>
          <li><a href="garbagespots.jsp">Garbage SPOTS</a></li>
        </ul>
      </div>
    </div>
  </div>
  <div id="map-canvas"></div>
  <div class="navbar navbar-inverse navbar-fixed-bottom">
    <div class="container">
      <form role="form">
        <div class="input-group" style="padding-top: 7px">
          <span class="input-group-addon">
            <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>
          </span>
          <input id="footerCoordinatesInput" type="text" class="form-control" name="footerCoordinatesInput" placeholder="Latitude, Longitude">
          <div class="input-group-btn">
            <button type="submit" class="btn btn-primary" onclick="return identifyFooterInputCoordinates( this, event )">
              <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
              Identify
            </button>
            <button type="submit" class="btn btn-success" onclick="return loadAddGarbageSpotModal( this, event )">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
              Add
            </button>
            <button type="submit" class="btn btn-warning" onclick="return loadClusterGarbageSpotsModal( this, event );">
              <span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>
              Cluster
            </button>
            <button type="submit" class="btn btn-danger" onclick="return loadAllocateGarbageBinsModal( this, event );">
              <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
              Allocate
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
  <div id="addGarbageSpotModal" class="modal fade">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add</h4>
        </div>
        <div class="modal-body">
          <p>Add the following entry?</p>
          <div class="input-group" style="padding-bottom: 15px">
            <span class="input-group-addon">
              <span class="glyphicon glyphicon-font" aria-hidden="true"></span>
            </span>
            <input id="addGarbageSpotNameInput" type="text" class="form-control" name="addGarbageSpotNameInput" placeholder="Name">
          </div>
          <div class="input-group" style="padding-bottom: 15px">
            <span class="input-group-addon">
              <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>
            </span>
            <input id="addGarbageSpotCoordinatesInput" type="text" class="form-control" name="addGarbageSpotCoordinatesInput" placeholder="Latitude, Longitude">
            <span class="input-group-btn">
              <button class="btn btn-primary" type="button" onclick="return identifyAddGarbageSpotInputCoordinates( this, event )">
              <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                Identify
              </button>
            </span>
          </div>
          <textarea id="addGarbageSpotDescriptionInput" class="form-control" name="addGarbageSpotDescriptionInput" rows="3" placeholder="Description"></textarea>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-info" onclick="return addServiceStation( this, event )">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
            Service Station
          </button>
          <button type="button" class="btn btn-success" onclick="return addGarbageSpot( this, event )">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
            Garbage SPOT
          </button>
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        </div>
      </div>
    </div>
  </div>
  <div id="allocateGarbageBinsModal" class="modal fade">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Allocate Garbage BINS</h4>
        </div>
        <div class="modal-body">
          <form role="form">
            <table class="table">
              <thead>
                <tr>
                  <th>Statistics</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Garbage Bins</td>
                  <td id="allocationNumGarbageBins" class="text-right">1</td>
                </tr>
                <tr>   
                  <td>Garbage Spots</td>
                  <td id="allocationNumGarbageSpots" class="text-right">144</td>
                </tr>
                <tr>   
                  <td>Service Stations</td>
                  <td id="allocationNumServiceStations" class="text-right">144</td>
                </tr>
              </tbody>
            </table>
            <p>Specify a positive real number as the initial temperature.</p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Initial Temperature</div>
              <input type="number" class="form-control" id="initialTemp" value="100.01">
            </div>
            <p>
              Specify a positive real number as the final temperature. The 
              final temperature MUST be less than the initial temperature.
            </p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Final Temperature</div>
              <input type="number" class="form-control" id="finalTemp" value="1.01">
            </div>
            <p>
              Specify a positive real number between 0 and 1 exclusive as the cooling factor.
            </p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Cooling Factor</div>
              <input type="number" class="form-control" id="coolingFactor" value="0.1">
            </div>
            <p>
              Specify a positive integer for the number of nested iterations.
            </p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Nested Iterations</div>
              <input type="number" class="form-control" id="nestedIter" value="1">
            </div>
            <p>
              Specify a positive integer for the max number of iterations.
            </p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Max Iterations</div>
              <input type="number" class="form-control" id="maxIter" value="300">
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-danger" onclick="return allocateGarbageBins( this, event )">
            <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
            Allocate Garbage Bins
          </button>
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        </div>
      </div>
    </div>
  </div>
  <div id="clusterGarbageSpotsModal" class="modal fade">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Allocate Garbage Bins</h4>
        </div>
        <div class="modal-body">
          <p>
            Select an option for garbage spots clustering:
          </p>
          <form role="form">
            <table class="table">
              <thead>
                <tr>
                  <th>Options</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
              <tr>
                <td>Only perform clustering on unclustered garbage spots</td>
                <td>
                  <input id="clusterOption1RadioBtn" type="radio" name="allocationOptions" class="bootstrap-switch" data-size="small" data-on-color="warning">
                </td>
              </tr>
              <tr>
                <td>Perform clustering on all garbage spots</td>
                <td>
                  <input id="clusterOption2RadioBtn" type="radio" name="allocationOptions" class="bootstrap-switch" data-size="small" data-on-color="warning">
                </td>
              </tr>
            </tbody>
            </table>
            <table class="table">
              <thead>
                <tr>
                  <th>Statistics</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Garbage Bins</td>
                  <td id="clusterNumGarbageBins" class="text-right">1</td>
                </tr>
                <tr>   
                  <td>Garbage Spots</td>
                  <td id="clusterNumGarbageSpots" class="text-right">144</td>
                </tr>
                <tr>   
                  <td>Service Stations</td>
                  <td id="clusterNumServiceStations" class="text-right">144</td>
                </tr>
              </tbody>
            </table>
            <p>
              Specify the quantity of garbage clusters to be generated. This value MUST be 
              greater than 0 AND CANNOT exceed the quantity of garbage bins NOR garbage spots.
            </p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Garbage Clusters</div>
              <input type="number" class="form-control" id="allocationNumClusters" value="1">
            </div>
            <p>
              Specify the quantity of iterations to be performed by the 
              clustering algorithm. This value MUST be greater than 0.
            </p>
            <div class="input-group" style="padding-bottom: 15px">
              <div class="input-group-addon">Cluster Iterations</div>
              <input type="number" class="form-control" id="allocationNumClusterIterations" value="1">
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-warning" onclick="return clusterGarbageBins( this, event )">
            <span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>
            Cluster Garbage Spots
          </button>
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        </div>
      </div>
    </div>
    </div>
    <div id="allocateGarbageBinsProgressModal" class="modal fade">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
          	<h4 class="modal-title">Allocate Garbage Bins</h4>
          </div>
          <div class="modal-body">
          	<div class="progress">
              <div class="progress-bar progress-bar-danger progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
                <span class="sr-only"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
  </div>
  <!-- Import JQuery JavaScript Library -->
  <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <!-- Import Bootstrap JavaScript Library -->
  <script src="../lib/bootstrap/js/bootstrap.js"></script>
  <!-- Import Bootstrap Switch JavaScript Library -->
  <script src="../lib/bootstrap-switch/js/bootstrap-switch.js"></script>
  <!-- Import Garbage MAP JavaScript Functions -->
  <!-- <script type="text/javascript" src="../js/googlemaps.js"></script> -->
  <script type="text/javascript" src="../js/garbagemap.js"></script>
</body>
</html>