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
  <script type="text/javascript"
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAoLJ1FT47jNg_UUdJqU2ekjjaHqjhbdrY">
  </script>
  <script type="text/javascript">
    var map; 
    function initialize() {
      var mapOptions = {
        center: { lat: 43.4689, lng: -80.5400},
        zoom: 8
      };
      map = new google.maps.Map(document.getElementById('map-canvas'),
        mapOptions);
      }
      google.maps.event.addDomListener(window, 'load', initialize);
    </script>
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
          <input type="text" class="form-control" name="garbageMapForm" placeholder="Latitude, Longitude">
          <div class="input-group-btn">
            <button type="submit" class="btn btn-primary">
              <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
              Search
            </button>
            <button type="submit" class="btn btn-success">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
              Add
            </button>
            <button type="submit" class="btn btn-warning">
              <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
              Controls
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
  <!-- Import JQuery JavaScript Library -->
  <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <!-- Import Bootstrap JavaScript Library -->
  <script src="../lib/bootstrap/js/bootstrap.js"></script>
</body>
</html>