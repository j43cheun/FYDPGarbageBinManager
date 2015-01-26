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

function getData( object, event )
{
	$.getJSON("http://localhost:3000/status/laststatus", {},  function( jsonDataResponseObject ) 
	{
		$('#binText1').val(JSON.stringify(jsonDataResponseObject));
	});
}