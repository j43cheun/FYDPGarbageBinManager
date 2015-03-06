package com.garbagebinserver.network;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.garbagebinserver.workers.PollingController;

public class GarbageNavigatorContextListener implements ServletContextListener {

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
	  PollingController.stopPollingThread();
  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
	  PollingController.setPollingTime(5 * 1000);
  }

}
