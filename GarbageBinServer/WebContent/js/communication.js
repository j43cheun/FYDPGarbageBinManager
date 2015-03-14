/**
 * This is a utility class that provides functions
 * to contact the backend server and retrieve information on the garbage bins.
 */

/**
 * This just makes a backend call to get all the bins. The callback function should
 * be expecting a json object that maps each binId to a status.
 */
function getAllBins(callback){
	$.getJSON("http://localhost:8080/GarbageBinServer/GarbageBinServlet", {}, callback);
};