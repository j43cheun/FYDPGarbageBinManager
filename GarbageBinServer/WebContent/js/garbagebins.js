var binStatusTableFormat = ["binID", "capacity", "battery", "location.latitude", "location.longitude"];
var updateSpeed = 100000;
var intervalID;
var updating = false;

/**
 * When the document is ready, we set a timer for the page to query and get up to date garbage
 * bin status info from the server.
 */
$( document ).ready(function() {
	// Do the initial population of data assuming the server has some. If it doesn't,
	// we will just get an empty table.
	requestUpdateFromBackEnd();
	intervalID = window.setInterval(requestUpdateFromBackEnd, updateSpeed);
	updating = true;
});

// Taken from http://stackoverflow.com/a/8052100
// This function takes an object and a string which contains the property that will be
// retrieved from the object. This function allows for the string to be something like
// parentProperty.childProperty.childOfChild.
function getPropertyFromString(objectToQuery, stringToQueryAgainst){
	var queryArray = stringToQueryAgainst.split(".");
	while(queryArray.length && (objectToQuery = objectToQuery[queryArray.shift()]));
	return objectToQuery;
};

// This turns the auto update on or off.
function toggleAutoUpdate(){
	updating = !updating;
	if (!updating){
		//If we are no longer updating, we clear the refresh interval.
		window.clearInterval(intervalID);
		//Turn the button red and change the text.
		$('#refreshToggleButtonSpan').text('Turn on Auto Refresh.');
		$('#refreshToggleButton').toggleClass('btn-primary', false);//This removes btn-primary.
		$('#refreshToggleButton').toggleClass('btn-warning', true);//This adds btn-warning.
	}
	else{
		intervalID = window.setInterval(requestUpdateFromBackEnd, updateSpeed);
		$('#refreshToggleButtonSpan').text('Turn off Auto Refresh.');
		$('#refreshToggleButton').toggleClass('btn-warning', false);//This removes btn-warning.
		$('#refreshToggleButton').toggleClass('btn-primary', true);//This adds btn-primary.
	}
}

/**
 * This function adds a new to a table. This is generic and knows nothing about the row
 * being added. objectToRowify is an objectual representation of the row. tableFormat
 * is an array that lists out all the keys that need to be displayed and the order
 * to be displayed in. tableToAddTo is a reference (via document.getElementByID) to 
 * a table or table body. 
 * @param objectToRowify
 * @param tableFormat
 * @param tableToAddTo
 * @returns
 */
function addTableRow(objectToRowify, tableFormat, tableToAddTo){
	var newRow = tableToAddTo.insertRow(tableToAddTo.rows.length);
	for(var i = 0; i < tableFormat.length; i++){
		var newCell = newRow.insertCell(i);
		console.log(objectToRowify);
		if (i == 1){
			var newText = document.createTextNode(getPropertyFromString(objectToRowify, tableFormat[i]) + "%");
		}
		else{
			var newText = document.createTextNode(getPropertyFromString(objectToRowify, tableFormat[i]));
		}
		
		newCell.appendChild(newText);
	}
	return newRow;
}

/**
 * This takes a row and adds in a status update (or refresh) button.
 */
function addStatusUpdateButton(binID, contactInfo, rowToModify){
	var newCell = rowToModify.insertCell(rowToModify.cells.length);
	var refreshSpan = $('<span></span>').attr({
		'class':"glyphicon glyphicon-refresh",
		'aria-hidden':"false"
	});
	var newButton = $('<button />').attr({
		type:"button",
		id:"button_" + binID,
		'class':"btn btn-primary",
	}).append(refreshSpan);
	console.log(contactInfo)
	newButton.click(function(){
		$(this).prop('disabled', true);
		askForUpdate(contactInfo);
	});
	newCell.appendChild(newButton.get(0));
	return newCell;
}

/**
 * This is a helper function that formats an IP and Port
 * for display in the more info section.
 * @param jsonStatus
 * @returns {String}
 */
function formatAdditionalInfo(jsonStatus){
	var IP = jsonStatus.ip;
	var Port = jsonStatus.port;
	
    var garbageBinCurrentDepth = jsonStatus.current_depth;
    var garbageBinMaxDepth = jsonStatus.max_depth;
    var garbageBinMaxVolume = jsonStatus.volume;
    var garbageBinCurrentVolume = garbageBinCurrentDepth * garbageBinMaxVolume / garbageBinMaxDepth;

	var volumeString = "Max Volume: " + garbageBinMaxVolume + "L <br /> Current Volume: " + garbageBinCurrentVolume + "L";
	var ipString = "IP: " + IP + "<br />" + "Port: " + Port.toString();
	var returnString = ipString + "<br />" + volumeString;
	
	return returnString;
}

/**
 * This function adds a hidden "accordian" row that can be opened to display
 * more relevant information.
 * @param jsonStatus
 * @param colspan
 * @param tableRef
 */
function addAccordianRow(jsonStatus, colspan, tableToAddTo){
	var strBinID = jsonStatus['binID'].toString();
	//We collapse or fold a class and not an id because we don't know
	//if down the line we are going to use more divs (more td blocks)
	//and we might want them collapsing together.
	var hiddenDiv = $('<div></div>').attr({
		'class':"panel-collapse collapse accordion_" + strBinID,
		'id':"accordion_" + strBinID
	}).html(formatAdditionalInfo(jsonStatus));
	
	var newRow = tableToAddTo.insertRow(tableToAddTo.rows.length);
	newRow.setAttribute('id',"hiddenRow_" + strBinID);
	
	var newColumn = newRow.insertCell(0);
	newColumn.setAttribute('colspan', colspan.toString());
	newColumn.setAttribute('class', 'hiddenRow');
	newColumn.appendChild(hiddenDiv.get(0));
	
	hiddenDiv = $('<div></div>').attr({
		'class':"panel-collapse collapse accordion_" + strBinID
	}).html(formatAdditionalInfo(jsonStatus));
	
	/*
	var secondColumn = newRow.insertCell(1);
	secondColumn.setAttribute('colspan', (colspan).toString() );
	secondColumn.setAttribute('class', 'hiddenRow');
	secondColumn.appendChild(hiddenDiv.get(0));
	*/
	
	return newRow;
}

//Returns true if the garbage bin status has error set to true.
function hasError(jsonStatus){
	if (jsonStatus.hasOwnProperty('error')){
		var errorValue = jsonStatus.error;
	}
	else{
		var errorValue = false;
	}
	return errorValue;
}

//Returns true if the garbage bin status indicates a full garbage bin.
//We define full to be 90% full.
function isFull(jsonStatus){
	if (jsonStatus.capacity < 10.0){
		return true;
	} 
	return false;
}

/**
 * This function is called repeatedly by a timer. This function will make a get call to the
 * garbage bin server backend and retrieve whatever trash information the server has.
 * This info will then be displayed on the UI.
 */
function requestUpdateFromBackEnd(){
	//$.getJSON("http://localhost:8080/GarbageBinServer/GarbageBinServlet", {},  function( jsonDataResponseObject ){
	getAllBins(function(jsonDataResponseObject){
		//First we go through the divs and make note of the ones that are open. We can
		//then reopen them when the table is reloaded.
		var listOfOpenDivIDs = [];
		$('#trashTableBody div').each(function() {
			var divClasses = this.classList;
			var id = this.id;
			$.each(divClasses, function(index, value) {
				if (value == "in") {
					listOfOpenDivIDs.push(id);
					return false; // Breaks out of the .each loop.
				}
			});
		});
		$("#trashTableBody tr").remove(); //Remove all rows.
		var tableRef = document.getElementById("trashTableBody");
		
		for (var binID in jsonDataResponseObject){
			var currentlyOpen = false;
			var jsonStatus = jsonDataResponseObject[binID];
			var str_binID = binID.toString();
			//var existingRow = $('#' + str_binID);
			var newRow = addTableRow(jsonStatus, binStatusTableFormat, tableRef);
			newRow.setAttribute('id',binID.toString());
			newRow.setAttribute('data-toggle', "collapse");
			newRow.setAttribute('data-target', ".accordion_" + str_binID);
			newRow.setAttribute('data-parent', "#parentDiv");
			
			//This is the class the row will get set to.
			var classString = "accordion-toggle";
			
			if (hasError(jsonStatus)){
				classString = classString + " danger";
			}
			else{
				if (isFull(jsonStatus)){
					classString = classString + " warning";
				}
			}
			
			newRow.setAttribute('class', classString);
			
			var contactInfo = {
					'ip':jsonStatus.ip,
					'port':jsonStatus.port
			}
			console.log(contactInfo);
			
			addStatusUpdateButton(binID, contactInfo, newRow);
			addAccordianRow(jsonStatus, binStatusTableFormat.length, tableRef);
		}
		//Reopen the divs that were open before.
		for(var i = 0; i < listOfOpenDivIDs.length; i++){
			$('#' + listOfOpenDivIDs[i]).collapse('show');
		}
	});
}

/**
 * This function makes a post call to a trash bin asking it to update itself.
 * The contactInfo object needs an IP and a port.
 * @param contactInfo
 */
function askForUpdate( contactInfo ){
	console.log("asked for update.");
	var IP = contactInfo.ip;
	var Port = contactInfo.port;
	console.log(contactInfo);
	console.log(Port)
	var url = "http://" + IP + ":"+ Port +"/status/updatestatusPost";
	$.post(url, {}, function(data, textStatus, jqXHR){
	});
}

