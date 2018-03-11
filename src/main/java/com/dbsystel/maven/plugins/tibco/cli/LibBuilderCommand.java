package com.dbsystel.maven.plugins.tibco.cli;

import java.io.File;

public class LibBuilderCommand extends AbstractBuilderCommand {
    private File outputLibFile;

    private String uri;

    private String pathToAliases;

    private String traFile;

    private File projectDir;

    public LibBuilderCommand(String command, String traFile, File projectDir, String uri, File outputLibFile,
            String pathToAliases) {
        super(command);
        this.traFile = traFile;
        this.pathToAliases = pathToAliases;
        //          addArgument("-p", projectDir);
        this.outputLibFile = outputLibFile;
        this.uri = uri;
        this.projectDir = projectDir;

    }

    @Override
    public File getProjectFolder() {
        return projectDir;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public File getOutputFile() {
        return outputLibFile;
    }

    @Override
    public boolean hideLibraries() {
        return true;
    }

    @Override
    public String pathToAliases() {
        return pathToAliases;
    }

    @Override
    public boolean getOverwriteOutputFile() {
        return true;
    }

    @Override
    public String getPropertiesFile() {
        return traFile;
    }

    @Override
    public String arguments() {
        StringBuffer buf = super.defaultArguments();

        return buf.toString();
    }

}
