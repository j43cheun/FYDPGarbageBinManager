/**
 * This JS is designated for garbagespots.jsp page
 */

function displayAllSpots( object, event ) {
	var jsonDataRequestObject = new Object();
	jsonDataRequestObject.name = "diplayAll";
	
	var jsonDataRequestString = JSON.stringify( jsonDataRequestObject );
	
	//This request contacts the backend servlet, gets the data in JSON, and populates the list with currently existing spots
	$.getJSON( "/GarbageBinServer/GarbageSpotsServlet", { action:"displaySpots", json:jsonDataRequestString }, function( jsonDataResponseObject ) {
		var response = jsonDataResponseObject.response;
		$("#spotTableRows").empty();
		$("#spotTableRows").append('<tr></tr>');
		 
		for (i=0;i<response.allSpots.length; i++) {
			$('#spotTableRows tr:last').after(
			'<tr><td>' + response.allSpots[i].id+
			'</td><td>' + response.allSpots[i].name +
			'</td><td>' + response.allSpots[i].latitude +
			'</td><td>' + response.allSpots[i].longitude +
			'</td></tr>'
			);
		}
	});
    
	return false;
}