package com.garbagebinserver.workers;

import java.util.Timer;
import java.util.TimerTask;

import com.garbagebinserver.network.GarbageBinLink;

/**
 * This class provides functions to control the background
 * polling thread.
 * It allows you to:
 * 	-Modify the frequency at which the background thread runs.
 *  -Stop the background thread.
 *  -NOTE: This controller provides absolutely no guarantees about when
 *  a thread will be stopped or modified. That is to say, in general, if it is modified
 *  while running, it will finish running and only the next scheduled task will be modified.
 * @author Zored
 *
 */
public class PollingController {

	/**
	 * delayAndPeriod IS IN MILLISECONDS!
	 * Note since it is the same variable, we just mean that
	 * the call will happen every delayAndPeriod milliseconds and
	 * the first one will be scheduled to happen delayAndPeriod milliseconds
	 * from now.
	 * @param delayAndPeriod
	 */
	public static void setPollingTime(long delayAndPeriod)
	{
		//Cancel the old timer.
		timerObject.cancel();
		
		//Create a new timer.
		timerObject = new Timer();
		//Reference: http://stackoverflow.com/a/4044793
		timerObject.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  //Do polling!
				  //Ask the bins to update themselves.
				  GarbageBinLink.requestUpdateForAllBins();
			  }},
			  delayAndPeriod, delayAndPeriod);
	}
	
	/**
	 * Note: If currently executing, it will finish, but the next time, it will stop.
	 */
	public static void stopPollingThread()
	{
		timerObject.cancel();
	}

	//We have globally used timer instances. This may be bad programming practice,
	//but meh.
	private static Timer timerObject = new Timer();
	
}

