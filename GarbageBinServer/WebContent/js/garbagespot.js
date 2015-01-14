/**
 * 
 */

function displayAllSpots( object, event ) {
	console.log("Received at the method");
	
	/*
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

        loadGarbageSpot( garbageSpotID, name, gmaps_latLng, description, -1 );
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
    } );
    
    */
	return false;
}