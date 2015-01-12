package com.garbagebinserver.allocator;

public interface SolutionModel {
  public double computeCost();
  public SolutionModel generateNeighboringSolution();
}
