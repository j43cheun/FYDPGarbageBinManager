package utililities;

import java.util.List;
import java.util.Map;

import com.garbagebinserver.data.GarbageBinStatus;

/**
 * This class provides utilities to store and retrieve trash can information.
 * When trash can data needs to be stored, this is passed in a JSON status object.
 * When trash can information needs to be retrieved, this class can return either
 * a map of all trash cans mapped to their respective status'es. Or return 
 * @author Zored
 *
 */
public class TrashWorks {

	public TrashWorks()
	{
		
	}
	
	public boolean storeStatus(Map trashStatusMap)
	{
		System.out.println(trashStatusMap.toString());
		return true;
	}
	
	/**
	 * This function makes a call to the database and returns
	 * the trash can status for every trash can found in the DB.
	 * @return a List.
	 */
	public List<GarbageBinStatus> getEveryBinsStatusFromDB()
	{
		return null;
	}
	
	/**
	 * This can store a list of garbage bin status'es to the database.
	 * @param binStatusList
	 * @return
	 */
	public boolean storeManyBinsStatus(List<GarbageBinStatus> binStatusList)
	{
		return false;
	}
}
