<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <!-- Enable responsive Bootstrap site -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Garbage NAVIGATOR - Garbage MAP</title>  
  <!-- Import Bootstrap Core CSS -->
  <link href="../lib/bootstrap/css/bootstrap.css" rel="stylesheet">
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
        Garbage NAVIGATOR
      </a>
      <button class="navbar-toggle" data-toggle="collapse"
        data-target=".navHeaderCollapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <div class="collapse navbar-collapse navHeaderCollapse">
        <ul class="nav navbar-nav navbar-right">
          <li><a href="#TODO">Garbage BINS</a></li>
          <li class="active"><a href="garbagemap.jsp">Garbage MAP</a></li>
          <li><a href="#TODO">Garbage SPOTS</a></li>
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
            <button type="submit" class="btn btn-warning" onclick="return loadAddGarbageSpotModal( this, event )">
              <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
              Remove
            </button>
            <button type="submit" class="btn btn-danger" onclick="return submitForm(this, event);">
              <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
              Advanced
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
          <h4 class="modal-title">Add Garbage SPOT</h4>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to add the following position to Garbage SPOTS?</p>
        </div>
        <div class="input-group" style="padding: 15px">
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
        <div class="modal-footer">
          <button type="button" class="btn btn-success" onclick="return addGarbageSpot( this, event )">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
            Add Garbage SPOT
          </button>
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        </div>
      </div>
    </div>
  </div>
  <!-- Import JQuery JavaScript Library -->
  <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <!-- Import Bootstrap JavaScript Library -->
  <script src="../lib/bootstrap/js/bootstrap.js"></script>
  <!-- Import Garbage MAP JavaScript Functions -->
  <!-- <script type="text/javascript" src="../js/googlemaps.js"></script> -->
  <script type="text/javascript" src="../js/garbagemap.js"></script>
</body>
</html>