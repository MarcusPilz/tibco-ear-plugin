package com.dbsystel.maven.plugins.tibco.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBuilderCommand {
    /**
     * 
     */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 
     */
    private String command;

    /**
     * 
     */
    private List<String> arguments;

    /**
     * 
     */
    protected DefaultExecuteResultHandler resultHandler;

    /**
     * 
     * @param command
     */
    public AbstractBuilderCommand(String command) {
        this.command = command;
        arguments = new ArrayList<String>();
        resultHandler = new DefaultExecuteResultHandler();
    }

    /**
     * 
     * @param key
     */
    public void addArgument(String key) {
        arguments.add(key);
    }

    /**
     * 
     * @return
     */
    public abstract File getProjectFolder();

    /**
     * 
     * @return
     */
    public abstract String getUri();

    /**
     * 
     * @return
     */
    public abstract File getOutputFile();

    /**
     * 
     * @return
     */
    public abstract boolean hideLibraries();

    /**
     * 
     * @return
     */
    public abstract String pathToAliases();

    /**
     * 
     * @return
     */
    public abstract boolean getOverwriteOutputFile();

    /**
     * 
     * @return
     */
    public abstract String getPropertiesFile();

    /**
     * 
     * @return int exitValue - 0 if successul
     * @throws ExecuteException
     * @throws IOException
     */
    public int execute() throws ExecuteException, IOException {
        int retCode = 0;
        String command = arguments();

        CommandLine cmdLine = CommandLine.parse(command);
        Executor executor = new DefaultExecutor();
        //executor.execute(cmdLine, resultHandler);
        retCode = executor.execute(cmdLine);
        //executor.setExitValue(0);
        //retCode = resultHandler.getExitValue();

        return retCode;
    }

    /**
     * 
     * @return
     */
    public abstract String arguments();

    /**
     * @TODO CHECK IF THIS PARAMETERS SHOULD BE CALLED EVERY APPMANAGE CMD
     * @return a list with common arguments for appmanage
     */
    public StringBuffer defaultArguments() {
        StringBuffer buf = new StringBuffer();
        buf.append(command);
        buf.append(" --propFile ");
        buf.append(getPropertiesFile());
        String pathToAliases = pathToAliases();
        if (pathToAliases != null) {
            buf.append(" -a ");
            buf.append(pathToAliases); // path to fileAliases.properties
        }
        String uri = getUri();
        if (uri != null) {
            buf.append(" -lib "); // path of the libbuilder relative to the BW project path
            buf.append(uri);
        }
        File outputFile = getOutputFile();
        if (outputFile != null) {
            buf.append(" -o "); // output file
            buf.append(getOutputFile());
        }
        buf.append(" -p ");
        buf.append(getProjectFolder()); // BW project path
        if (getOverwriteOutputFile()) {
            buf.append(" -x "); // overwrite the output
        }

        //        if (hideLibraries()) {
        //            buf.append(" -v ");
        //        }

        return buf;
    }

}
