package com.dbsystel.maven.plugins.tibco.cli;

import java.io.File;

public class EarBuilderCommand extends AbstractBuilderCommand {
    private File outputEarFile;

    private String uri;

    private String pathToAliases;

    private String traFile;

    private File projectDir;

    private String archive;

    /**
     * 
     * @param command
     * @param traFile
     * @param projectDir
     * @param uri
     * @param outputEarFile
     * @param pathToAliases
     */
    public EarBuilderCommand(String command, String traFile, File projectDir, String archive, File outputEarFile,
            String pathToAliases) {
        super(command);
        this.projectDir = projectDir;
        this.traFile = traFile;
        this.projectDir = projectDir;
        this.archive = archive;
        this.outputEarFile = outputEarFile;
        this.pathToAliases = pathToAliases;
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
        return outputEarFile;
    }

    @Override
    public boolean hideLibraries() {
        return false;
    }

    @Override
    public String pathToAliases() {
        return pathToAliases;
    }

    @Override
    public boolean getOverwriteOutputFile() {
        return false;
    }

    @Override
    public String getPropertiesFile() {
        return traFile;
    }

    @Override
    public String arguments() {
        StringBuffer buf = super.defaultArguments();
        if (archive != null) {
            buf.append(" -ear ");
            buf.append(archive);
        }

        buf.append(" -x ");
        //        buf.append(" -v ");
        buf.append(" -s");
        return buf.toString();
    }
}
