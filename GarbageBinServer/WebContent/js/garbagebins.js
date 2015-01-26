/**
 * 
 */
function getData( object, event )
{
	$.getJSON("http://localhost:3000/status/laststatus", {},  function( jsonDataResponseObject ) 
	{
		$('#binText1').val(JSON.stringify(jsonDataResponseObject));
	});
};

function askForUpdate( object, event )
{
	/*
	$.getJSON("http://localhost:3000/status/updatestatus", {},  function( jsonDataResponseObject ) 
	{
		$('#binText1').val(JSON.stringify(jsonDataResponseObject));
	});
	*/
	$.post("http://localhost:3000/status/updatestatusPost", {},  function( data, textStatus, jqXHR ) 
	{
		//$('#binText1').val(JSON.stringify(jsonDataResponseObject));
	});
};

