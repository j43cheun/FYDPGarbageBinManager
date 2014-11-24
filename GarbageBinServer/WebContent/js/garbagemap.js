/**
 * Garbage MAP JavaScript Source
 */
var gmaps_map;
var gmaps_activeMarker;
var gmaps_tempMarker;

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
    title: 'Current Position'
  } );
  
  google.maps.event.addListener(gmaps_activeMarker, 'click', function() {
    gmaps_activeMarker.setAnimation( null );
    gmaps_infoWindow.open( gmaps_map, gmaps_activeMarker );
  } );
}

function gmaps_parseInputCoordinates( coordinatesString ) {
  var alertString;
  
  var gmaps_latLng = null;
  var latLngPair = coordinatesString.split( ',' );
  
  if( latLngPair.length != 2 ) {
    alertString = 
      'The specified latitude and longitude coordinates are incorrectly formatted!';
    alert( alertString );
  }
  else {
    var lat = parseFloat( latLngPair[0].trim() );
    var lng = parseFloat( latLngPair[1].trim() );
    
    if( isNaN( lat ) || isNaN( lng ) ) {
      alertString = 
        'Both the specified latitude and longitude coordinates must be numbers!'
      alert( alertString );
    }
    else {
      if( lat >= -90 && lat <= 90 && lng >= -180 && lng <=180 ) {
        gmaps_latLng = new google.maps.LatLng( lat, lng );
      }
      else {
        alertString = 
          'The specified latitude and longitude coordinates are out of bounds!';
        alert( alertString );
      }
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
  if( gmaps_activeMarker != null ) {
    var addGarbageSpotCoordinatesInputElement = document.getElementById( 'addGarbageSpotCoordinatesInput' );
    addGarbageSpotCoordinatesInputElement.value = gmaps_activeMarker.getPosition().toUrlValue();
  }
  
  $('#addGarbageSpotModal').modal( 'toggle' );
  return false;
}

function addGarbageSpot( object, event ) {
  var addGarbageSpotCoordinatesInputElement = document.getElementById( 'addGarbageSpotCoordinatesInput' );
  var gmaps_latLng = gmaps_parseInputCoordinates( addGarbageSpotCoordinatesInputElement.value );
  
  if( gmaps_latLng != null ) {
    gmaps_tempMarker = gmaps_activeMarker;
    gmaps_activeMarker = null;
    gmaps_tempMarker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
    $('#addGarbageSpotModal').modal( 'toggle' );
  }
  
  return false;
}

google.maps.event.addDomListener( window, 'load', gmaps_initialize );