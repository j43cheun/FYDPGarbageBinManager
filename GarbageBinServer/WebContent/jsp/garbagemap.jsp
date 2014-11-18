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
    .h3-align {
  margin-top: -4px;
  margin-bottom: 0px;
 }
  </style>
</head>
<body>
  <jsp:include page="navheader.jsp"/>
  <div id="map-canvas"></div>
  <div class="navbar navbar-default navbar-fixed-bottom">
    <div class="container">
      <div class="collapse navbar-collapse navHeaderCollapse">
      <form class="navbar-form navbar-left">
        <div class="input-group">
          <input type="text" class="form-control" name="username" placeholder="GPS Coordinates">
          <div class="input-group-btn">
            <button type="submit" class="btn btn-info">
              <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
              Search
            </button>
            <button type="submit" class="btn btn-success">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
              Add
            </button>
          </div>
        </div>
      </form>
      <ul class="nav navbar-nav navbar-right">
        <label class="checkbox-inline">
          <input type="checkbox" id="inlineCheckbox1" value="option1"> 1
        </label>
        <label class="checkbox-inline">
          <input type="checkbox" id="inlineCheckbox2" value="option2"> 2
        </label>
        <label class="checkbox-inline">
          <input type="checkbox" id="inlineCheckbox3" value="option3"> 3
        </label>  
      </ul>
      </div>
    </div>
  </div>
  <!-- Import JQuery JavaScript Library -->
  <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <!-- Import Bootstrap JavaScript Library -->
  <script src="../lib/bootstrap/js/bootstrap.js"></script>
</body>
</html>