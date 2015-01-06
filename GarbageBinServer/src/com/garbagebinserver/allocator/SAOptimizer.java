package com.garbagebinserver.allocator;

import java.util.Random;

public class SAOptimizer implements Optimizer {
  
  private double m_coolingFactor;
  private double m_initialTemperature;
  private double m_finalTemperature;
  private double m_numIterPerTempInstance;
  private double m_maxIterations;
  private Random m_randomNumberGenerator;
  
  public SAOptimizer( double coolingFactor, 
                      double initialTemperature, 
                      double finalTemperature, 
                      double numIterPerTempInstance, 
                      double maxIterations ) {
    
    if( coolingFactor >= 1 || coolingFactor <= 0 ) {
      throw new IllegalArgumentException( "The cooling factor must be in the range (0,1)!" );
    }
    else if( initialTemperature <= finalTemperature ) {
      throw new IllegalArgumentException( "The initial temperature must be greater than the final temperature!" );
    }
    else if( numIterPerTempInstance <= 0 ) {
      throw new IllegalArgumentException( "The number of iterations per temperature must be an integer greater than 0!" );
    }
    else if( maxIterations <= 0 ) {
      throw new IllegalArgumentException( "The maximum number of iterations must be an integer greater than 0!" );
    }
    
    m_coolingFactor = coolingFactor;
    m_initialTemperature = initialTemperature;
    m_finalTemperature = finalTemperature;
    m_numIterPerTempInstance = numIterPerTempInstance;
    m_maxIterations = maxIterations;
    m_randomNumberGenerator = new Random( System.currentTimeMillis() );
  }

  @Override
  public SolutionModel optimizeSolution( SolutionModel initialSolution ) {
    if( initialSolution == null ) {
      throw new IllegalArgumentException( "The initial solution cannot be null!" );
    }
    
    SolutionModel currentSolution = initialSolution;
    SolutionModel optimalSolution = initialSolution;
    
    double initialSolutionCost = initialSolution.computeCost();
    double currentSolutionCost = initialSolutionCost;
    double optimalSolutionCost = initialSolutionCost;
    double currentTemperature = m_initialTemperature;
    
    int currentIterationTotal = 0;
    
    while( currentTemperature > m_finalTemperature && currentIterationTotal < m_maxIterations ) {
      for( int idx = 0; idx < m_numIterPerTempInstance; ++idx ) {
        SolutionModel neighboringSolution = currentSolution.generateNeighboringSolution();
        double neighboringSolutionCost = neighboringSolution.computeCost();
        
        if( neighboringSolutionCost < currentSolutionCost ) {
          currentSolution = neighboringSolution;
          currentSolutionCost = neighboringSolutionCost;
          
          if( neighboringSolutionCost < optimalSolutionCost ) {
            optimalSolution = neighboringSolution;
            optimalSolutionCost = neighboringSolutionCost;
          }
        }
        else {
          double randomNumber = m_randomNumberGenerator.nextDouble();
          double transitionProbability = Math.exp( -1 * ( neighboringSolutionCost - currentSolutionCost ) / currentTemperature );
          
          if( transitionProbability > randomNumber ) {
            currentSolution = neighboringSolution;
            currentSolutionCost = neighboringSolutionCost;
          }
        }
      }
      currentTemperature  *= m_coolingFactor;
      currentIterationTotal++;
    }
    
    return optimalSolution;
  }
}