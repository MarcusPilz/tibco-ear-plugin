package com.dbsystel.maven.plugins.tibco.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dbsystel.maven.common.PropertyNames;

public abstract class AbstractTibcoBwEarMojo extends AbstractMojo implements PropertyNames {

    protected final static String SKIPPING = "Skipping...";

    /**
     * Path to the TIBCO home directory.
     * 
     * @readOnly
     */
    @Parameter(property = PropertyNames.TIBCO_HOME_DIR)
    protected File tibcoHome;

    /**
     * Used Tibco Version
     * 
     * @readOnly
     */
    @Parameter(property = PropertyNames.TIBCO_VERSION, required = false, defaultValue = "5.9")
    protected String tibcoVersion;

    // ------------------------
    // Configurables
    // ------------------------
    /**
     * Indicates weather the packaging will continue even if there are failures
     */
    @Parameter(property = "tibco.ear.failOnError", defaultValue = "true")
    private boolean failOnError;

    /**
     * Indicates whether or not the goal should be skipped.
     * 
     * @readOnly
     */
    @Parameter(defaultValue = "false", property = "tibco.ear.skip")
    protected boolean skip;

    /**
     * Name of the build folder containing the .libbuilder or .archive files
     * 
     * @parameter tibco.ear.buildfolder
     * @required
     */
    @Parameter(required = true)
    protected String tibcoBuilderFolder;

    /**
     * Name of the build folder containing the .libbuilder or .archive files
     * 
     * @parameter bw.project.Location
     * @required
     */
    @Parameter(property = "projectLocation", required = true)
    protected String projectLocation;

    /**
     * Directory containing the processes and resource files that should be packaged into the project library.
     *
     * @parameter default-value="${project.build.outputDirectory}
     * @required
     * @readonly
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = false, readonly = true)
    protected File tibcoBuildDirectory;

    // ------------------------
    // Read-only Parameters
    // ------------------------
    /**
     * The Maven project.
     * 
     * @required
     * @readOnly
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    /**
     * The current Maven session.
     * 
     * @required
     * @readOnly
     *
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    private File basedir;

    /**
     * Absolute path and filename of the Designer5.prefs file (usually in ~/.TIBCO)
     *
     * @parameter default-value="${user.home}/.TIBCO/Designer5.prefs"
     */
    @Parameter(defaultValue = "${user.home}${file.separator}.TIBCO${file.separator}Designer5.prefs")
    private String designerPrefsFile;

    /**
     * Directory containing the processes and resource files that should be packaged into the project library.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.finalName}")
    private String outputFilename;

    protected File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * 
     * @param type
     * @return
     * @throws MojoExecutionException
     */
    protected Collection<File> findTibcoBuilderResources(String type) throws MojoExecutionException {
        File tibcoBuildFolder = new File(tibcoBuilderFolder);

        this.getLog().debug("Looking for " + type + " in..." + tibcoBuildFolder.getAbsolutePath());

        Collection<File> buildResources = new ArrayList<File>();
        if (tibcoBuildFolder.exists() && tibcoBuildFolder.isDirectory()) {
            for (File tibcoBuildResource : tibcoBuildFolder.listFiles()) {
                if (tibcoBuildResource.getName().endsWith(type)) {

                    this.getLog().info(tibcoBuildResource.getAbsolutePath());
                    buildResources.add(tibcoBuildResource);
                }
            }
        } else {
            throw new MojoExecutionException("Can not find file : " + tibcoBuildFolder);
        }
        return buildResources;
    }
}
