package stem;

import java.io.File;
import java.io.IOException;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class ParamUtils
{
    /** The default parameters file CLI argument */
    private final static String A_FILE = "-file";
    
    /**
     * Initialize parameter database from file
     *
     * If there exists an command line argument '-file', create a parameter
     * database from the file specified. Otherwise create an empty parameter
     * database.
     *
     * @param args contains command line arguments
     * @return newly created parameter data base
     *
     */
    public static ParameterDatabase openParameterDatabase(String[] args)
    {
        ParameterDatabase parameters = null;
        for (int x = 0; x < args.length - 1; x++)
        {
            if (args[x].equals(A_FILE))
            {
                try
                {
                    File parameterDatabaseFile = new File(args[x + 1]);
//                    parameters = new ParameterDatabase(parameterDatabaseFile.getAbsoluteFile());
                    parameters = new ParameterDatabase(parameterDatabaseFile.getAbsoluteFile(), args);
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
                break;
            }
        }
        if (parameters == null)
        {
            System.out.println("\nNo parameter file was specified;"
                    + "\n-default programmatic settings engaged,"
                    + "\n-command line -p parameters ignored."
                    + "\nConsider using: -file foo.params [-p bar1=val1 [-p bar2=val2 ... ]]\n");
            parameters = new ParameterDatabase();
        }
        return parameters;
    }
    /**
     * Convenience function for getting an integer value from the parameter
     * database
     *
     * @param parameterName name of parameter
     * @param defaultValue value to return if database doesn't know about that
     * parameter
     *
     * @return the value stored for that parameter, or 'default' if database
     * doesn't have that value
     *
     */
    public static int getInt(ParameterDatabase paramDB, String parameterName, int defaultValue)
    {
        return paramDB.getIntWithDefault(new Parameter(parameterName), null, defaultValue);
    }

    /**
     * Convenience function for getting a boolean value from the parameter
     * database
     *
     * @param parameterName
     * @param defaultValue
     *
     * @return the value stored for that parameter, or 'default' if database
     * doesn't have that value
     *
     */
    public static boolean getBoolean(ParameterDatabase paramDB, String parameterName, boolean defaultValue)
    {
        return paramDB.getBoolean(new Parameter(parameterName), null, defaultValue);
    }

    /**
     * Convenience function for getting a double value from the parameter
     * database
     *
     * @param parameterName name of parameter
     * @param defaultValue value to return if database doesn't know about that
     * parameter
     *
     * @return the value stored for that parameter, or 'default' if database
     * doesn't have that value
     *
     */
    public static double getDouble(ParameterDatabase paramDB, String parameterName, double defaultValue)
    {
        return paramDB.getDoubleWithDefault(new Parameter(parameterName), null, defaultValue);
    }

    /**
     * Convenience function for getting a String value from the parameter
     * database
     *
     * @param parameterName name of parameter
     * @param defaultValue value to return if database doesn't know about that
     * parameter
     *
     * @return the value stored for that parameter, or 'default' if database
     * doesn't have that value
     *
     */
    public static String getString(ParameterDatabase paramDB, String parameterName, String defaultValue)
    {
        return paramDB.getStringWithDefault(new Parameter(parameterName), null, defaultValue);
    }
}
