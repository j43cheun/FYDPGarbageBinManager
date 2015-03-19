// For removing elements from array...
Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

/**
 * Garbage MAP JavaScript Source
 */
var gmaps_map;
var gmaps_activeMarker;
var gmaps_tempMarker;

function GarbageSpot( garbageSpotID, name, gmaps_marker, description, garbageClusterID ) {
  this.garbageSpotID = garbageSpotID;
  this.name = name;
  this.gmaps_marker = gmaps_marker;
  this.description = description;
  this.garbageClusterID = garbageClusterID;
}

function ServiceStation( serviceStationID, name, gmaps_marker, description ) {
  this.serviceStationID = serviceStationID;
  this.name = name;
  this.gmaps_marker = gmaps_marker;
  this.description = description;
}

function GarbageCluster( garbageClusterID, gmaps_marker ) {
  this.garbageClusterID = garbageClusterID;
  this.gmaps_marker = gmaps_marker;
}

function GarbageBin( garbageBinID, gmaps_marker, garbageBinMaxVolume, garbageBinCurrentVolume, garbageBinPercentFreeVolume, IP, clusterID, gmaps_infoWindow ) {
  this.garbageBinID = garbageBinID;
  this.gmaps_marker = gmaps_marker;
  this.garbageBinMaxVolume = garbageBinMaxVolume;
  this.garbageBinCurrentVolume = garbageBinCurrentVolume;
  this.garbageBinPercentFreeVolume = garbageBinPercentFreeVolume;
  this.IP = IP;
  this.clusterID = clusterID;
  this.gmaps_infoWindow = gmaps_infoWindow;
}

GarbageBin.prototype.setClusterID = function( clusterID ) {
  this.clusterID = clusterID;
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h5>Garbage Bin</h5>' +
    '<table class="table">' +
    '<tbody>' +
    '<tr>' +
    '<td>ID</td>' +
    '<td>' + this.garbageBinID + '</td>' +
    '</tr>' +
    '<tr>' +
    '<tr>' +
    '<td>IP Address</td>' +
    '<td>' + this.IP + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Latitude, Longitude</td>' +
    '<td>' + this.gmaps_marker.getPosition().toUrlValue() + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Container Volume</td>' +
    '<td>' + this.garbageBinMaxVolume + ' L</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Garbage Volume</td>' +
    '<td>' + this.garbageBinCurrentVolume + ' L</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Percent Remaining Space</td>' +
    '<td>' + this.garbageBinPercentFreeVolume + '%</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Patrol Cluster</td>' +
    '<td>' + 
    '<button type="submit" class="btn btn-primary">' + 
    this.clusterID +
    '</button>' +
    '</td>' +
    '</tr>' +
    '</tbody>' +
    '</table>' +
    '</div>';
  
  this.gmaps_infoWindow.setContent( gmaps_contentString );
};

// TODO: GarbageCluster

var availableGarbageSpots = [];
var availableGarbageBins = [];
var availableGarbageClusters = [];

var gmaps_serviceStationTable = {}
var gmaps_garbageSpotTable = {}
var gmaps_garbageClusterTable = {}
var gmaps_garbageBinTable = {}

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
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action: "getServiceStations" }, function( jsonDataResponseObject ) {
    for( var key in jsonDataResponseObject ) {
      var serviceStationJSONString = jsonDataResponseObject[key];
      var serviceStationJSONObject = jQuery.parseJSON( serviceStationJSONString );
      
      var serviceStationID = serviceStationJSONObject.serviceStationID;
      var name = serviceStationJSONObject.name;
      var latitude = serviceStationJSONObject.latitude;
      var longitude = serviceStationJSONObject.longitude;
      var description = serviceStationJSONObject.description;
      
      var gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
      
      if( gmaps_latLng != null ) {
        loadServiceStation( serviceStationID, name, gmaps_latLng, description )
      }
    }
  } );
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action: "getGarbageBins" }, function( jsonDataResponseObject ) {
    alert( JSON.stringify(jsonDataResponseObject) );
    
    for( var key in jsonDataResponseObject ) {
      
      var garbageBinJSONString = jsonDataResponseObject[key];
      var garbageBinJSONObject = jQuery.parseJSON( garbageBinJSONString );
      
      var garbageBinID = garbageBinJSONObject.garbageBinID;
      var garbageBinMaxVolume = garbageBinJSONObject.garbageBinMaxVolume;
      var garbageBinCurrentVolume = garbageBinJSONObject.garbageBinCurrentVolume;
      var garbageBinPercentFreeVolume = garbageBinJSONObject.garbageBinPercentFreeVolume;
      
      var latitude = garbageBinJSONObject.latitude;
      var longitude = garbageBinJSONObject.longitude;
      var gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
      
      var IP = garbageBinJSONObject.IP;
      
      if( gmaps_latLng != null ) {
        loadGarbageBin( garbageBinID, garbageBinMaxVolume, garbageBinCurrentVolume, garbageBinPercentFreeVolume, gmaps_latLng, IP );
      }
    }
  } );
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"getGarbageSpots" }, function( jsonDataResponseObject ) {
    for( var key in jsonDataResponseObject ) {
      var garbageSpotJSONString = jsonDataResponseObject[key];
      var garbageSpotJSONObject = jQuery.parseJSON( garbageSpotJSONString );
      var garbageSpotID = garbageSpotJSONObject.garbageSpotID;
      var garbageClusterID = garbageSpotJSONObject.garbageClusterID;
      var name = garbageSpotJSONObject.name;
      var latitude = garbageSpotJSONObject.latitude;
      var longitude = garbageSpotJSONObject.longitude;
      var description = garbageSpotJSONObject.description;
      var gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
      
      if( gmaps_latLng != null ) {
        loadGarbageSpot( garbageSpotID, name, gmaps_latLng, description, garbageClusterID );
      }
    }
  } );
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action: "getGarbageClusters" }, function( jsonDataResponseObject ) {
    for( var key in jsonDataResponseObject ) {
      var garbageClusterJSONString = jsonDataResponseObject[key];
      var garbageClusterJSONObject = jQuery.parseJSON( garbageClusterJSONString );
      var garbageClusterID = garbageClusterJSONObject.garbageClusterID;
      var latitude = garbageClusterJSONObject.latitude;
      var longitude = garbageClusterJSONObject.longitude;
      var gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
      
      if( gmaps_latLng != null ) {
        loadGarbageCluster( garbageClusterID, gmaps_latLng );
      }
    }
  } );
}

function gmaps_identifyActiveMarker( gmaps_latLng ) {
  if( gmaps_activeMarker != null ) {
    gmaps_activeMarker.setMap( null );
  }
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h5>Current Position</h5>' +
    '<table class="table">' +
    '<tbody>' +
    '<tr>' +
    '<td>Latitude, Longitude</td>' +
    '<td>' + gmaps_latLng.toUrlValue() + '</td>' +
    '</tr>' +
    '</tbody>' +
    '</table>' +
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
      $( '#addGarbageSpotModal' ).modal( 'toggle' );
    }
  }
  else {
    alertString =
      'Please enter GPS coordinates below!';
    alert( alertString );
  }
  
  return false;
}

function loadAllocateGarbageBinsModal( object, event ) {
  
  $( '#allocateGarbageBinsModal' ).modal( 'toggle' );
  
  
  
  return false;
}

function loadClusterGarbageSpotsModal( object, event ) {
  var alertString;
  var numGarbageSpots = Object.keys( gmaps_garbageSpotTable ).length;
  
  if( numGarbageSpots > 0 ) {
    var tdClusterNumGarbageBinsElement = document.getElementById( 'clusterNumGarbageBins' );
    var tdClusterNumGarbageSpotsElement = document.getElementById( 'clusterNumGarbageSpots' );
    var clusterOption1RadioBtn = $( '#clusterOption1RadioBtn' );
    var clusterOption2RadioBtn = $( '#clusterOption2RadioBtn' );
	
    // Set OPTION1 radio to TRUE; OPTION2 radio to FALSE.
    clusterOption1RadioBtn.bootstrapSwitch( 'state', true );
    clusterOption2RadioBtn.bootstrapSwitch( 'state', false );
    
    // Initialize statistics table to reflect statistics from OPTION1.
    tdClusterNumGarbageBinsElement.innerHTML = 'TODO';
    tdClusterNumGarbageSpotsElement.innerHTML = availableGarbageSpots.length;
    
    // Set switch change event on OPTION1 radio. This event handles switch 
    // change for OPTION1 radio as well.
    clusterOption1RadioBtn.on( 'switchChange.bootstrapSwitch', function( event, data ) {
      if( clusterOption1RadioBtn.bootstrapSwitch( 'state' ) == true ) {
        tdClusterNumGarbageBinsElement.innerHTML = 'TODO';
        tdClusterNumGarbageSpotsElement.innerHTML = availableGarbageSpots.length;
      }
      else {
        tdClusterNumGarbageBinsElement.innerHTML = 'TODO';
        tdClusterNumGarbageSpotsElement.innerHTML = Object.keys( gmaps_garbageSpotTable ).length;
      }
    } );
    
    $( '#clusterGarbageSpotsModal' ).modal( 'toggle' ); 
  }
  else {
    alertString =
      'There are no garbage spots available! At least x1 garbage spot is required to perform clustering!';
    alert( alertString );
  }
  
  return false;
}

function addServiceStation( object, event ) {
  var addServiceStationNameInputElement = document.getElementById( 'addGarbageSpotNameInput' );
  var addServiceStationCoordinatesInputElement = document.getElementById( 'addGarbageSpotCoordinatesInput' );
  var addServiceStationDescriptionInputElement = document.getElementById( 'addGarbageSpotDescriptionInput' );
  var name = addServiceStationNameInputElement.value;
  var gmaps_latLng = gmaps_parseInputCoordinates( addServiceStationCoordinatesInputElement.value );
  var description = addServiceStationDescriptionInputElement.value;
  
  if( name != "" && gmaps_latLng != null ) {
    var jsonDataRequestObject = new Object();
    
    jsonDataRequestObject.name = name;
    jsonDataRequestObject.latitude = gmaps_latLng.lat();
    jsonDataRequestObject.longitude = gmaps_latLng.lng();
    jsonDataRequestObject.description = description;
    
    var jsonDataRequestString = JSON.stringify( jsonDataRequestObject );
    
    $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"addServiceStation", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
      var serviceStationID = jsonDataResponseObject.serviceStationID;
      
      if( serviceStationID != -1 ) {
        if( gmaps_activeMarker != null ) {
          var serviceStationCoordinatesInputElement = document.getElementById( 'footerCoordinatesInput' );
          gmaps_activeMarker.setMap( null );
          serviceStationCoordinatesInputElement.value = "";
        }
        
        loadServiceStation( serviceStationID, name, gmaps_latLng, description );
        $( '#addGarbageSpotModal' ).modal( 'toggle' );
        
        addServiceStationNameInputElement.value = "";
        addServiceStationCoordinatesInputElement.value = "";
        addServiceStationDescriptionInputElement.value = "";
      }
      else {
        alertString = 
          'Cannot add a service station that already exists!';
        alert( alertString );
      }
    } );
  }
  else {
    alertString = 
      'Invalid name and/or latitude and longitude input!';
    alert( alertString );
  }
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
    console.log("About to make a request");
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
  }
  else {
    alertString = 
      'Invalid name and/or latitude and longitude input!';
    alert( alertString );
  }
  
  return false;
}

function allocateGarbageBins( object, event ) {
  var initialTemperatureInput = document.getElementById( 'initialTemp' );
  var finalTemperatureInput = document.getElementById( 'finalTemp' );
  var coolingFactorInput = document.getElementById( 'coolingFactor' );
  var nestedIterationsInput = document.getElementById( 'nestedIter' );
  var maxIterationsInput = document.getElementById( 'maxIter' );
  
  var initialTemperature = parseFloat( initialTemperatureInput.value );
  var finalTemperature = parseFloat( finalTemperatureInput.value );
  var coolingFactor = parseFloat( coolingFactorInput.value );
  var nestedIterations = parseInt( nestedIterationsInput.value, 10 );
  var maxIterations = parseInt( maxIterationsInput.value, 10 );
  
  console.log( coolingFactor );
  
  // TODO Argument checking
  
  var jsonDataRequestObject = new Object();
  jsonDataRequestObject.initialTemperature = initialTemperature;
  jsonDataRequestObject.finalTemperature = finalTemperature;
  jsonDataRequestObject.coolingFactor = coolingFactor;
  jsonDataRequestObject.nestedIterations = nestedIterations;
  jsonDataRequestObject.maxIterations = maxIterations;
  
  var jsonDataRequestString = JSON.stringify( jsonDataRequestObject );
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"allocateGarbageBins", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
    var mystr = JSON.stringify( jsonDataResponseObject );
    alert( mystr );
    for( var key in jsonDataResponseObject ) {
      var garbageBin = gmaps_garbageBinTable[key];
      var clusterID = jsonDataResponseObject[key];
      console.log( clusterID );
      garbageBin.setClusterID( clusterID );
    }
  } );
  
  $('#allocateGarbageBinsModal').modal( 'toggle' );
  
  return false;
}

function clusterGarbageBins( object, event ) {
  var alertString;
  
  var tdAllocationNumGarbageBinsElement = document.getElementById( 'allocationNumGarbageBins' );
  var tdAllocationNumGarbageSpotsElement = document.getElementById( 'allocationNumGarbageSpots' );
  
  var numGarbageClusters = document.getElementById( 'allocationNumClusters' ).value;
  var numClusterIterations = document.getElementById( 'allocationNumClusterIterations' ).value;
  
  if( numGarbageClusters != parseInt( numGarbageClusters, 10 ) ) {
    alertString = 'The quantity of garbage clusters MUST be an integer!';
    alert( alertString );
  }
  else if( numGarbageClusters <= 0 ) {
    alertString = 'The quantity of garbage clusters MUST be greater than 0!';
    alert( alertString );
    return false;
  }
  
  if( numClusterIterations != parseInt( numClusterIterations, 10 ) ) {
    alertString = 'The quantity of cluster iterations MUST be an integer!';
    alert( alertString );
    return false;
  }
  else if( numClusterIterations <= 0 ) {
    alertString = 'The quantity of cluster iterations MUST be greater than 0!';
    alert( alertString );
    return false;
  }
  
  var allocationOption1RadioBtn = $( '#allocationOption1' );
  var allocationOption;
  
  var jsonDataRequestObject = new Object();
  
  if( allocationOption1RadioBtn.bootstrapSwitch( 'state' ) == true ) {
    if( numGarbageClusters > availableGarbageSpots.length ) {
      alertString = 
        'The quantity of garbage clusters MUST be less than BOTH the quantity ' +
        'of available garbage spots and the quantity of available garbage bins!';
      alert( alertString );
      return false;
    }
    
    allocationOption = 'OPTION1';
  }
  else {
    if( numGarbageClusters > Object.keys( gmaps_garbageSpotTable ).length ) {
      alertString = 
        'The quantity of garbage clusters MUST be less than BOTH the quantity ' +
        'of garbage spots and the quantity of garbage bins!';
      alert( alertString )
      return false;
    }
    
    allocationOption = 'OPTION2';
    
    for( var garbageClusterID in gmaps_garbageClusterTable ) {
      if( gmaps_garbageClusterTable.hasOwnProperty( garbageClusterID ) ) {
        var garbageCluster = gmaps_garbageClusterTable[garbageClusterID];
        var gmaps_marker = garbageCluster.gmaps_marker;
        gmaps_marker.setMap( null );
        delete gmaps_garbageClusterTable[garbageClusterID];
      }
    }
  }
  
  // Compute clusters.
  var jsonDataRequestObject = new Object();
  
  jsonDataRequestObject.allocationOption = allocationOption;
  jsonDataRequestObject.numGarbageClusters = numGarbageClusters;
  jsonDataRequestObject.numClusterIterations = numClusterIterations;
  
  var jsonDataRequestString = JSON.stringify( jsonDataRequestObject );
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"computeGarbageClusters", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
    // NO JSON DATA RESPONSE OBJECT RETURNED!
  } );
  
  /*
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action:"allocateGarbageBins", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
    // NO JSON DATA RESPONSE OBJECT RETURNED!
  } );
  */
  
  $.getJSON( "/GarbageBinServer/garbagemapServlet", { action: "getGarbageClusters" }, function( jsonDataResponseObject ) {
    for( var key in jsonDataResponseObject ) {
      var garbageClusterJSONString = jsonDataResponseObject[key];
      var garbageClusterJSONObject = jQuery.parseJSON( garbageClusterJSONString );
      var garbageClusterID = garbageClusterJSONObject.garbageClusterID;
      var latitude = garbageClusterJSONObject.latitude;
      var longitude = garbageClusterJSONObject.longitude;
      var gmaps_latLng = gmaps_latLngFactory( latitude, longitude );
      
      if( gmaps_latLng != null ) {
        loadGarbageCluster( garbageClusterID, gmaps_latLng );
      }
    }
  } );
  
  // Clear available garbage spots array.
  while( availableGarbageSpots.length > 0 ) {
    availableGarbageSpots.pop();
  }
  
  $( '#clusterGarbageSpotsModal' ).modal( 'hide' );
  
  google.maps.event.trigger(gmaps_map, 'resize');
  
  return true;
}

function loadServiceStation( serviceStationID, name, gmaps_latLng, description ) {
  var gmaps_marker = new google.maps.Marker( {
    position: gmaps_latLng,
    map: gmaps_map,
    title: name,
    animation: google.maps.Animation.DROP
  } );
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h5>' + name + '</h5>' +
    '<table class="table">' +
    '<tbody>' +
    '<tr>' +
    '<td>ID</td>' +
    '<td>' + serviceStationID + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Latitude, Longitude</td>' +
    '<td>' + gmaps_latLng.toUrlValue() + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Description</td>' +
    '<td>' + description + '</td>' +
    '</tr>' +
    '</tbody>' +
    '</table>' +
    '</div>';
  
  var gmaps_infoWindow = new google.maps.InfoWindow( {
    content: gmaps_contentString
  } );
  
  google.maps.event.addListener(gmaps_marker, 'click', function() {
    gmaps_infoWindow.open( gmaps_map, gmaps_marker );
  } );
  
  gmaps_marker.setIcon('../icons/home.png');
  gmaps_garbageSpotTable[serviceStationID] = new ServiceStation( serviceStationID, name, gmaps_marker, description );
}

function loadGarbageSpot( garbageSpotID, name, gmaps_latLng, description, garbageClusterID ) {
  var gmaps_marker = new google.maps.Marker( {
    position: gmaps_latLng,
    map: gmaps_map,
    title: name,
    animation: google.maps.Animation.DROP
  } );
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h5>' + name + '</h5>' +
    '<table class="table">' +
    '<tbody>' +
    '<tr>' +
    '<td>ID</td>' +
    '<td>' + garbageSpotID + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Latitude, Longitude</td>' +
    '<td>' + gmaps_latLng.toUrlValue() + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Description</td>' +
    '<td>' + description + '</td>' +
    '</tr>' +
    '</tbody>' +
    '</table>' +
    '</div>';
  
  var gmaps_infoWindow = new google.maps.InfoWindow( {
    content: gmaps_contentString
  } );
  
  google.maps.event.addListener(gmaps_marker, 'click', function() {
    gmaps_infoWindow.open( gmaps_map, gmaps_marker );
  } );
  
  gmaps_marker.setIcon('../icons/green_flag.png');
  gmaps_garbageSpotTable[garbageSpotID] = new GarbageSpot( garbageSpotID, name, gmaps_marker, description, garbageClusterID );
  
  if( garbageClusterID == -1 ) {
    availableGarbageSpots.push( garbageSpotID );
  }
}

function loadGarbageCluster( garbageClusterID, gmaps_latLng ) {
  var garbageClusterTitle = 'Garbage Cluster ' + garbageClusterID;
  var gmaps_marker = new google.maps.Marker( {
    position: gmaps_latLng,
    map: gmaps_map,
    title: garbageClusterTitle,
    animation: google.maps.Animation.DROP
  } );
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h5>Garbage Cluster</h5>' +
    '<table class="table">' +
    '<tbody>' +
    '<tr>' +
    '<td>ID</td>' +
    '<td>' + garbageClusterID + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Latitude, Longitude</td>' +
    '<td>' + gmaps_latLng.toUrlValue() + '</td>' +
    '</tbody>' +
    '</table>' +
    '</div>';
  
  var gmaps_infoWindow = new google.maps.InfoWindow( {
    content: gmaps_contentString
  } );
  
  google.maps.event.addListener( gmaps_marker, 'click', function() {
    gmaps_infoWindow.open( gmaps_map, gmaps_marker );
  } );
  
  gmaps_marker.setIcon('../icons/azure_flag.png');
  gmaps_garbageClusterTable[garbageClusterID] = new GarbageCluster( garbageClusterID, gmaps_marker );
}

function loadGarbageBin( garbageBinID, garbageBinMaxVolume, garbageBinCurrentVolume, garbageBinPercentFreeVolume, gmaps_latLng, IP, clusterID ) {
  var garbageBinTitle = 'Garbage Bin ' + garbageBinID;
  var gmaps_marker = new google.maps.Marker( {
    position: gmaps_latLng,
    map: gmaps_map,
    title: garbageBinTitle,
    animation: google.maps.Animation.DROP
  } );
  
  var gmaps_contentString = 
    '<div id="content">' +
    '<h5>Garbage Bin</h5>' +
    '<table class="table">' +
    '<tbody>' +
    '<tr>' +
    '<td>ID</td>' +
    '<td>' + garbageBinID + '</td>' +
    '</tr>' +
    '<tr>' +
    '<tr>' +
    '<td>IP Address</td>' +
    '<td>' + IP + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Latitude, Longitude</td>' +
    '<td>' + gmaps_latLng.toUrlValue() + '</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Container Volume</td>' +
    '<td>' + garbageBinMaxVolume + ' L</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Garbage Volume</td>' +
    '<td>' + garbageBinCurrentVolume + ' L</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Percent Remaining Space</td>' +
    '<td>' + garbageBinPercentFreeVolume + '%</td>' +
    '</tr>' +
    '<tr>' +
    '<td>Patrol Cluster</td>' +
    '<td>' + 'None' + '</td>' +
    '</tr>' +
    '</tbody>' +
    '</table>' +
    '</div>';
  
  var gmaps_infoWindow = new google.maps.InfoWindow( {
    content: gmaps_contentString,
    maxWidth: 500
  } );
  
  google.maps.event.addListener( gmaps_marker, 'click', function() {
    gmaps_infoWindow.open( gmaps_map, gmaps_marker );
  } );
  
  gmaps_marker.setIcon( '../icons/garbage_bin.png' );
  gmaps_garbageBinTable[garbageBinID] = new GarbageBin( garbageBinID, gmaps_marker, garbageBinMaxVolume, garbageBinCurrentVolume, garbageBinPercentFreeVolume, IP, clusterID, gmaps_infoWindow );
}

google.maps.event.addDomListener( window, 'load', gmaps_initialize );

// Initialize bootstrap-switch
$('.bootstrap-switch').bootstrapSwitch('state', true);