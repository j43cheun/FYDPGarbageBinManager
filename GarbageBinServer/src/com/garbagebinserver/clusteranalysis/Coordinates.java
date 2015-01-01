package com.garbagebinserver.clusteranalysis;

public interface Coordinates {
  int getClusterID();
  void setClusterID( int clusterID );
  double getDistance(Coordinates coordinates);
}
