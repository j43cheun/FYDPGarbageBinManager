/**
 * 
 */

function displayAllSpots( object, event ) {
	console.log("Received at the method");
	var jsonDataRequestObject = new Object();
	
	jsonDataRequestObject.name = "diplayAll";
	
	var jsonDataRequestString = JSON.stringify( jsonDataRequestObject );
	
    $.getJSON( "/GarbageBinServer/GarbageSpotsServlet", { action:"displaySpots", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
    	 var response = jsonDataResponseObject.response;
    	 console.log(response);
        
      } );
    
	return false;
}