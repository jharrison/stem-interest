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
    	
    }
}
