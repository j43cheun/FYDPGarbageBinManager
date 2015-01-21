<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <!-- Enable responsive Bootstrap site -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Garbage NAV - Garbage SPOTS</title>  
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
          <li><a href="#TODO">Garbage BINS</a></li>
          <li><a href="garbagemap.jsp">Garbage MAP</a></li>
          <li class="active"><a href="garbagespots.jsp">Garbage SPOTS</a></li>
        </ul>
      </div>
    </div>
  </div>
 <div class="input-group-btn">
	<button type="submit" class="btn btn-primary" onclick="displayAllSpots( this, event )">
    <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
   		Current Garbage Spots
 	</button>
</div>
<table class="table table-striped">
      <thead>
          <tr>
              <th>Id</th>
              <th>Name</th>
              <th>Latitude</th>
              <th>Longitude</th>
          </tr>
      </thead>
      <tbody id="spotTableRows">
          <tr>

          </tr>
      </tbody>
  </table>
  
  <!-- Import JQuery JavaScript Library -->
  <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <!-- Import Bootstrap JavaScript Library -->
  <script src="../lib/bootstrap/js/bootstrap.js"></script>
  <!-- Import Bootstrap Switch JavaScript Library -->
  <script src="../lib/bootstrap-switch/js/bootstrap-switch.js"></script>
  <script type="text/javascript" src="../js/garbagespot.js"></script>
</body>
</html>