package com.dbsystel.maven.plugins.tibco.projlib;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.dbsystel.maven.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.cli.LibBuilderCommand;
import com.dbsystel.maven.plugins.tibco.commons.AbstractTibcoBwEarMojo;

@Mojo(name = "package-projlib", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class TibcoBwLibBuilderMojo extends AbstractTibcoBwEarMojo implements PropertyNames {
    protected final static String TIBCO_DESIGNER_BINARY_NOTFOUND = "The TIBCO buidllibrary can't be found.";

    protected final static String PROJLIB_EXTENSION = ".projlib";

    protected final static String BUILD_PROJLIB_FAILED = "The build of the Projlib file has failed.";

    protected final static String BUILDING_PROJLIB = "Building the Projlib...";

    /**
     * The source directories containing the sources to be compiled.
     */
    @Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
    private List<String> compileSourceRoots;

    /**
     * Projects main artifact.
     *
     */
    @Parameter(defaultValue = "${project.artifact}", readonly = true, required = true)
    private Artifact projectArtifact;

    /**
     * Path to the TIBCO Designer "buildlibrary" binary.
     * 
     */
    @Parameter(property = PropertyNames.BUILDLIBRARY_UTILITY_PATH)
    private File buildLibrary;

    /**
     * TraFile
     */
    @Parameter(property = PropertyNames.BUILDLIBRARY_UTILITY_TRA_PATH)
    private String buildLibraryTraFile;

    /**
     * Location of the fileAliases
     * 
     * @parameter default-value="${project.build.directory}/fileAliases.properties"
     */
    @Parameter(defaultValue = "${project.build.directory}/fileAliases.properties")
    private File aliasesFile;

    /**
     * 
     * @throws MojoExecutionException
     */
    private void checkDesignerBuildLibrary() throws MojoExecutionException {

        this.getLog().debug("Check " + PropertyNames.BUILDLIBRARY_UTILITY + " in... " + tibcoHome);

        String fileExtension = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            fileExtension = ".exe";
        } else {
            fileExtension = "";
        }

        if (tibcoHome != null && buildLibrary == null) {
            buildLibrary = new File(tibcoHome + System.getProperty("file.separator") + "designer"
                    + System.getProperty("file.separator") + tibcoVersion + System.getProperty("file.separator")
                    + "bin" + System.getProperty("file.separator") + PropertyNames.BUILDLIBRARY_UTILITY
                    + fileExtension);

        }

        if (buildLibrary == null || !buildLibrary.exists() || !buildLibrary.isFile()) {
            throw new MojoExecutionException(TIBCO_DESIGNER_BINARY_NOTFOUND);
        }

        if (buildLibraryTraFile == null) {
            if (fileExtension.equals("")) {
                buildLibraryTraFile = buildLibrary.getAbsolutePath().concat(".tra");
            } else {
                buildLibraryTraFile = buildLibrary.getAbsolutePath().replace(fileExtension, ".tra");
            }
            this.getLog().debug("buildLibrary_TRAFILE" + "::" + buildLibraryTraFile);
        }
    }

    /**
     * 
     */
    protected String getArtifactFileExtension() {
        return PROJLIB_EXTENSION;
    }

    /**
     * Generating a projlib
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("Genearating projlib for " + project.getArtifactId());

        if (skip) {
            getLog().info(SKIPPING);
            return;
        }
        checkDesignerBuildLibrary();
        Collection<File> libBuilderResources = findLibBuilderResources();

        this.getLog().debug("Project to build ::" + libBuilderResources.size());

        for (File libBuilderResource : libBuilderResources) {
            this.getLog().debug(BUILDING_PROJLIB + " for ..." + libBuilderResource);
            createTibcoProjlib(libBuilderResource);
        }

    }

    /**
     * 
     * @param libBuilderResource
     * @throws MojoExecutionException
     */
    private void createTibcoProjlib(File libBuilderResource) throws MojoExecutionException {
        getLog().info("Creating project library for: " + libBuilderResource.getAbsolutePath());

        String uri = libBuilderResource.getAbsolutePath().substring(getOutputDirectory().getAbsolutePath().length());
        // Under windows folders are with '\' separators, while uri should be with '/'
        uri = uri.replaceAll("\\\\", "/");

        File artifactName = new File(getOutputDirectory(), project.getArtifact().getArtifactId() + "-"
                + project.getArtifact().getBaseVersion() + ".projlib");

        LibBuilderCommand command = new LibBuilderCommand(buildLibrary.getAbsolutePath(), buildLibraryTraFile,
                getOutputDirectory(), uri, artifactName, aliasesFile.getAbsolutePath());

        try {
            command.execute();
        } catch (ExecuteException e) {
            throw new MojoExecutionException(BUILD_PROJLIB_FAILED, e);
        } catch (IOException e) {
            throw new MojoExecutionException(BUILD_PROJLIB_FAILED, e);
        }

        project.getArtifact().setFile(artifactName);
    }

    /**
     * 
     * @return a collection of files containing .libbuilder sources
     * @throws MojoExecutionException
     */
    private Collection<File> findLibBuilderResources() throws MojoExecutionException {
        return findTibcoBuilderResources(".libbuilder");
    }

}
