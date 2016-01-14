package stem.tuning;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import stem.StemStudents;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class ParameterTuningProblem extends Problem implements SimpleProblemForm
{
	private static final long serialVersionUID = 1L;
	
	StemStudents model;
	private enum FitnessMetric { KS, ABE };
	private FitnessMetric fitnessMetric = FitnessMetric.KS;

	@Override
	public void setup(final EvolutionState state, final Parameter ecParams)
	{
		super.setup(state, ecParams);
		System.err.println("Phase1Problem.setup()");
		
		/** with MASON model instatiation **/
		String[] args = null;
		model = new StemStudents(System.currentTimeMillis(), args);
		
		// Read some params
		String fm = state.parameters.getStringWithDefault(new Parameter("fitness-metric"), null, "KS");
		fitnessMetric = FitnessMetric.valueOf(fm);
//		System.out.format("Fitness-metric: %s\n", fitnessMetric);
	}
	
    public double calcInvertedKolmogorovSmirnovStatistic(double[] a, double[] b, MersenneTwisterFast random) {
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest(new MersenneTwisterFastApache(random));
        double ksStatistic = Double.NaN;
        
        try {
            ksStatistic = ksTest.kolmogorovSmirnovStatistic(a, b);
        }
        catch(Exception e) {}
        
        return (1 - ksStatistic);
    }
    
    private double calcFitness() {

    	double val = Double.NaN;
    	switch (fitnessMetric) {
    	case KS:	
    		val = model.compareStudents_KS(model.students, "7th grader (after simulation)", model.students7th, "7th grader (from data)", false);
    		break;
    	case ABE:
    		val = model.compareStudents_ABE(model.students, "7th grader (after simulation)", model.students7th, "7th grader (from data)", false);
    		break;
    	}
    	    		
    	return -val;
    }

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) 
	{
		if (!ind.evaluated) {
			if( !(ind instanceof DoubleVectorIndividual))
				state.output.fatal("Phase1Problem.evaluate() :" 
						+ " DoubleVectorIndividual expected");
			
			double fitness = 0.0;
			double[] genome = ((DoubleVectorIndividual)ind).genome;

			// decode the genome and initialize model parameters
//			0) interestThreshold
//			1) interestThresholdNoise
//			2) interestChangeRate
//			3) participationChangeRate
//			4) participationMultiplier
//			5) leaderExpertise
//			6) leaderExpertiseNoise
//			7) leaderPassion
//			8) leaderPassionNoise
//			9) friendRuleWeight
//			10) choiceRuleV2Weight
//			11) parentRuleWeight
//			12) leaderRuleV2Weight
			
			model.interestThreshold 		= genome[0];
			model.interestThresholdNoise 	= genome[1];
			model.interestChangeRate 		= genome[2];
			model.participationChangeRate 	= genome[3];
			model.participationMultiplier 	= genome[4];
			model.leaderExpertise 			= genome[5];
			model.leaderExpertiseNoise 		= genome[6];
			model.leaderPassion 			= genome[7];
			model.leaderPassionNoise 		= genome[8];
			model.friendRuleWeight 			= genome[9];
			model.choiceRuleWeight	 		= genome[10];
			model.parentRuleWeight 			= genome[11];
			model.leaderRuleWeight	 		= genome[12];
			
			int numTests = 5;
			double totalFitness = 0;
			
			model.printFitness = false;
			
			
			for (int testNum = 0; testNum < numTests; testNum++) {
			
				// Run the model
				model.start();
				for (int i = 0; i < 365; i++) {
					model.schedule.step(model);
				}
				model.finish();			
	
				// Calculate fitness
				totalFitness += calcFitness();
			}
			fitness = totalFitness / numTests;
			
			// Set the individual's fitness
			((SimpleFitness)ind.fitness).setFitness(state, fitness, false);
			ind.evaluated = true ;
			
		}

	}

}
