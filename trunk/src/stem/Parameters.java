package stem;

import ec.util.ParameterDatabase;

public class Parameters
{
	StemStudents model;
//	public double coordinationLevel = 0.0;
	public int run = 0;
    
    public Parameters(StemStudents model, String[] args)
    {
    	this.model = model;
        if (args != null)
            loadParameters(ParamUtils.openParameterDatabase(args));
    }
    
    private void loadParameters(ParameterDatabase paramDB)
    {
    	run = ParamUtils.getInt(paramDB, "Run", run);
    	model.coordinationLevel = ParamUtils.getDouble(paramDB, "CoordinationLevel", model.coordinationLevel);
    	model.outputFilename = ParamUtils.getString(paramDB, "OutputFilename", "");

    	model.interestThreshold = ParamUtils.getDouble(paramDB, "InterestThreshold", model.interestThreshold);
    	model.interestThresholdNoise = ParamUtils.getDouble(paramDB, "InterestThresholdNoise", model.interestThresholdNoise);
    	
    	model.leaderExpertise = ParamUtils.getDouble(paramDB, "LeaderExpertise", model.leaderExpertise);
    	model.leaderExpertiseNoise = ParamUtils.getDouble(paramDB, "LeaderExpertiseNoise", model.leaderExpertiseNoise);
    	model.leaderPassion = ParamUtils.getDouble(paramDB, "LeaderPassion", model.leaderPassion);
    	model.leaderPassionNoise = ParamUtils.getDouble(paramDB, "LeaderPassionNoise", model.leaderPassionNoise);

    	model.expertiseThreshold = ParamUtils.getDouble(paramDB, "ExpertiseThreshold", model.expertiseThreshold);
    	model.expertiseThresholdNoise = ParamUtils.getDouble(paramDB, "ExpertiseThresholNoise", model.expertiseThresholdNoise);
    	model.passionThreshold = ParamUtils.getDouble(paramDB, "PassionThreshold", model.passionThreshold);
    	model.passionThresholdNoise = ParamUtils.getDouble(paramDB, "PassionThresholNoise", model.passionThresholdNoise);
    	
    	model.activityMatchingMethod = ParamUtils.getInt(paramDB, "ActivityMatchingMethod", model.activityMatchingMethod);
    	
    	model.mentorProbability = ParamUtils.getDouble(paramDB, "MentorProbability", model.mentorProbability);
    }
}
