/**
 * Garbage MAP JavaScript Source
 */
var gmaps_map;
var gmaps_activeMarker;
var gmaps_tempMarker;

function GarbageSpot( garbageSpotID, name, gmaps_marker, description ) {
  this.garbageSpotID = garbageSpotID;
  this.name = name;
  this.gmaps_marker = gmaps_marker;
  this.description = description;
}

var gmaps_garbageSpotTable = {}

function gmaps_initialize() {
  var gmaps_initialLatLng = new google.maps.LatLng( 43.4689, -80.5400 );
  var gmaps_mapOptions = {
    center: gmaps_initialLatLng,
	  zoom: 18
  };
  
  gmaps_map = new google.maps.Map( document.getElementById( 'map-canvas' ), gmaps_mapOptions );
  
  google.maps.event.addListener( gmaps_map, 'click', function( event ) {
    var garbageCoordinatesInputElement = document.getElementById( "footerCoordinatesInput" );
    gmaps_identifyActiveMarker( event.latLng );
    garbageCoordinatesInputElement.value = event.latLng.toUrlValue();
  } );
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"getGarbageSpots" }, function( jsonDataResponseObject ) {
    for( var key in jsonDataResponseObject ) {
      var garbageSpotJSONString = jsonDataResponseObject[key];
      var garbageSpotJSONObject = jQuery.parseJSON( garbageSpotJSONString );
      var garbageSpotID = garbageSpotJSONObject.garbageSpotID;
      var name = garbageSpotJSONObject.name;
      var latitude = garbageSpotJSONObject.latitude;
      var longitude = garbageSpotJSONObject.longitude;
      var description = garbageSpotJSONObject.description;
      var gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
      
      if( gmaps_latLng != null ) {
        loadGarbageSpot( garbageSpotID, name, gmaps_latLng, description );
      }
    }
  } );
}

function gmaps_identifyActiveMarker( gmaps_latLng ) {
  if( gmaps_activeMarker != null ) {
    gmaps_activeMarker.setMap( null );
  }
  
  var gmaps_contentString = '<div id="content">' +
    '<h6 id="activeMarkerHeading">Current Position</h6>' +
    '<div id="bodyContent">' +
    '<p>' +
    gmaps_latLng.toUrlValue() +
    '</p>' +
    '</div>' +
    '</div>';

  var gmaps_infoWindow = new google.maps.InfoWindow( {
    content: gmaps_contentString
  } );
  
  gmaps_activeMarker = new google.maps.Marker( {
    position: gmaps_latLng,
    map: gmaps_map,
    title: 'Current Position',
    animation: google.maps.Animation.DROP
  } );
  
  google.maps.event.addListener(gmaps_activeMarker, 'click', function() {
    gmaps_activeMarker.setAnimation( null );
    gmaps_infoWindow.open( gmaps_map, gmaps_activeMarker );
  } );
}

function gmaps_parseInputCoordinates( coordinatesString ) {
  var gmaps_latLng = null;
  var latLngPair = coordinatesString.split( ',' );
  
  if( latLngPair.length != 2 ) {
    alertString = 
      'The specified latitude and longitude coordinates are incorrectly formatted!';
    alert( alertString );
  }
  else {
    var latitude = parseFloat( latLngPair[0].trim() );
    var longitude = parseFloat( latLngPair[1].trim() );
    gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
  }
  
  return gmaps_latLng;
}

function gmaps_latLngFactory( latitude, longitude ) {
  var alertString;
  var gmaps_latLng = null;
  
  if( isNaN( latitude ) || isNaN( longitude ) ) {
    alertString = 
      'Both the specified latitude and longitude coordinates must be numbers!'
    alert( alertString );
  }
  else {
    if( latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <=180 ) {
      gmaps_latLng = new google.maps.LatLng( latitude, longitude );
    }
    else {
      alertString = 
        'The specified latitude and longitude coordinates are out of bounds!';
      alert( alertString );
    }
  }
  
  return gmaps_latLng;
}

function identifyInputCoordinates( element ) {
  var coordinatesString = element.value;
  var gmaps_latLng = gmaps_parseInputCoordinates( coordinatesString );
  
  if( gmaps_latLng != null ) {
    gmaps_identifyActiveMarker( gmaps_latLng );
    gmaps_map.setCenter( gmaps_latLng );
    gmaps_activeMarker.setAnimation(google.maps.Animation.BOUNCE);
  }
}

function identifyFooterInputCoordinates( object, event ) {
  var footerCoordinatesInputElement = document.getElementById( 'footerCoordinatesInput' );
  identifyInputCoordinates( footerCoordinatesInputElement );
  return false;
}

function identifyAddGarbageSpotInputCoordinates( object, event ) {
  var addGarbageSpotCoordinatesInputElement = document.getElementById( 'addGarbageSpotCoordinatesInput' );
  identifyInputCoordinates( addGarbageSpotCoordinatesInputElement );
  return false;
}

function loadAddGarbageSpotModal( object, event ) {
  var alertString;
  var garbageCoordinatesInputElement = document.getElementById( "footerCoordinatesInput" );
  
  if( garbageCoordinatesInputElement.value != "" ) {
    var gmaps_latLng = gmaps_parseInputCoordinates( garbageCoordinatesInputElement.value );
    
    if( gmaps_latLng != null ) {
      var addGarbageSpotCoordinatesInputElement = document.getElementById( 'addGarbageSpotCoordinatesInput' );
      addGarbageSpotCoordinatesInputElement.value = gmaps_latLng.toUrlValue();
      $('#addGarbageSpotModal').modal( 'toggle' );
    }
  }
  else {
    alertString =
      'Please enter GPS coordinates below!';
    alert( alertString );
  }
  
  return false;
}

function addGarbageSpot( object, event ) {
  var addGarbageSpotNameInputElement = document.getElementById( 'addGarbageSpotNameInput' );
  var addGarbageSpotCoordinatesInputElement = document.getElementById( 'addGarbageSpotCoordinatesInput' );
  var addGarbageSpotDescriptionInputElement = document.getElementById( 'addGarbageSpotDescriptionInput' );
  var name = addGarbageSpotNameInputElement.value
  var gmaps_latLng = gmaps_parseInputCoordinates( addGarbageSpotCoordinatesInputElement.value );
  var description = addGarbageSpotDescriptionInputElement.value;
  
  if( name != "" && gmaps_latLng != null ) {
    var jsonDataRequestObject = new Object();
    
    jsonDataRequestObject.name = name;
    jsonDataRequestObject.latitude = gmaps_latLng.lat();
    jsonDataRequestObject.longitude = gmaps_latLng.lng();
    jsonDataRequestObject.description = description;
    
    var jsonDataRequestString = JSON.stringify( jsonDataRequestObject );
    
    $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"addGarbageSpot", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
      var garbageSpotID = jsonDataResponseObject.garbageSpotID;
      
      if( garbageSpotID != -1 ) {
        
        if( gmaps_activeMarker != null ) {
          var garbageCoordinatesInputElement = document.getElementById( "footerCoordinatesInput" );
          gmaps_activeMarker.setMap( null );
          garbageCoordinatesInputElement.value = "";
        }

        loadGarbageSpot( garbageSpotID, name, gmaps_latLng, description );
        $('#addGarbageSpotModal').modal( 'toggle' );
        
        addGarbageSpotNameInputElement.value = "";
        addGarbageSpotCoordinatesInputElement.value = "";
        addGarbageSpotDescriptionInputElement.value = "";
      }
      else {
        alertString = 
          'Cannot add a garbage SPOT that already exists!';
        alert( alertString );
      }
    })
  }
  else {
    alertString = 
      'Invalid name and/or latitude and longitude input!';
    alert( alertString );
  }
  
  return false;
}

function loadGarbageSpot( garbageSpotID, name, gmaps_latLng, description ) {
  var gmaps_marker = new google.maps.Marker( {
    position: gmaps_latLng,
    map: gmaps_map,
    title: name,
    animation: google.maps.Animation.DROP
  } );
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h6 id="activeMarkerHeading">' + name + '</h6>' +
    '<div id="bodyContent">' +
    '<p>' +
    gmaps_latLng.toUrlValue() +
    '</p>' +
    '</div>' +
    '</div>';
  
  var gmaps_infoWindow = new google.maps.InfoWindow( {
    content: gmaps_contentString
  } );
  
  google.maps.event.addListener(gmaps_marker, 'click', function() {
    gmaps_infoWindow.open( gmaps_map, gmaps_marker );
  } );
  
  gmaps_marker.setIcon('../icons/green_flag.png');
  gmaps_garbageSpotTable[garbageSpotID] = new GarbageSpot( garbageSpotID, name, gmaps_marker, description );
}

google.maps.event.addDomListener( window, 'load', gmaps_initialize );