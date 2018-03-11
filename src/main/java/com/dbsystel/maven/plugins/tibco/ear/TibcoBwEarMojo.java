package com.dbsystel.maven.plugins.tibco.ear;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.dbsystel.maven.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.cli.EarBuilderCommand;
import com.dbsystel.maven.plugins.tibco.commons.AbstractTibcoBwEarMojo;

@Mojo(name = "bw-ear", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true,
        executionStrategy = "once-per-session", requiresDependencyResolution = ResolutionScope.TEST)
public class TibcoBwEarMojo extends AbstractTibcoBwEarMojo implements PropertyNames {
    protected final static String TIBCO_EAR_BINARY_NOTFOUND = "The TIBCO buildear can't be found.";

    /**
     * Path to the LibBuilder relatively to the BusinessWorks project path.
     */
    @Parameter(required = true)
    private String libBuilder;

    /**
     * Path to the TIBCO Designer "buildlibrary" binary.
     * 
     */
    @Parameter(property = PropertyNames.BUILDEAR_UTILITY_PATH, required = false)
    private File buildEar;

    /**
     * TraFile
     */
    @Parameter(property = PropertyNames.BUILDEAR_UTILITY_TRA_PATH, required = false)
    private String buildEarTraFile;

    /**
     * Location of the fileAliases
     * 
     * @parameter default-value="${project.build.directory}/fileAliases.properties"
     */
    @Parameter(defaultValue = "${project.build.directory}/fileAliases.properties")
    private File aliasesFile;

    /**
     * Extension for the binary.
     * 
     * @readOnly
     */
    @Parameter(property = PropertyNames.SCRIPT_EXTENSION)
    protected String fileExtension;

    /**
     * 
     * @throws MojoExecutionException
     */
    private void checkUtilities() throws MojoExecutionException {

        this.getLog().debug("Check " + PropertyNames.BUILDEAR_UTILITY + " in... " + tibcoHome);

        if (SystemUtils.IS_OS_WINDOWS) {
            fileExtension = ".exe";
        } else {
            fileExtension = "";
        }

        if (tibcoHome != null && buildEar == null) {
            buildEar = new File(tibcoHome + System.getProperty("file.separator") + "tra"
                    + System.getProperty("file.separator") + tibcoVersion + System.getProperty("file.separator")
                    + "bin" + System.getProperty("file.separator") + PropertyNames.BUILDEAR_UTILITY + fileExtension);

        }

        if (buildEar == null || !buildEar.exists() || !buildEar.isFile()) {
            throw new MojoExecutionException(TIBCO_EAR_BINARY_NOTFOUND);
        }

        if (buildEarTraFile == null) {
            if (fileExtension.equals("")) {
                buildEarTraFile = buildEar.getAbsolutePath().concat(".tra");
            } else {
                buildEarTraFile = buildEar.getAbsolutePath().replace(fileExtension, ".tra");
            }
            this.getLog().debug("EAR_TRAFILE" + "::" + buildEarTraFile);
        }
    }

    /**
     * 
     * @param archiveResource
     * @throws MojoExecutionException
     */
    private void createTibcoArchive(File archiveResource) throws MojoExecutionException {
        getLog().debug("Creating project archive for: " + archiveResource.getAbsolutePath());

        String uri = archiveResource.getAbsolutePath().substring(getOutputDirectory().getAbsolutePath().length());

        uri = uri.replaceAll("\\\\", "/");

        File artifactName = new File(getOutputDirectory(), project.getArtifact().getArtifactId() + "-"
                + project.getArtifact().getVersion() + ".ear");

        EarBuilderCommand command = new EarBuilderCommand(buildEar.getAbsolutePath(), buildEarTraFile,
                tibcoBuildDirectory, uri, artifactName, aliasesFile.getAbsolutePath());

        try {
            this.getLog().debug(this.getClass().getName() + " execute ..." + command.arguments());
            command.execute();
        } catch (ExecuteException e) {
            throw new MojoExecutionException("Can't create project archive", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Can't create project archive", e);
        }

        project.getArtifact().setFile(artifactName);
    }

    private Collection<File> findArchiveResources() throws MojoExecutionException {
        this.getLog().debug(this.getClass().getName() + " findArchiveResources...");
        return findTibcoBuilderResources(".archive");
    }

    /**
     * 
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info(SKIPPING);
            return;
        }
        getLog().debug(this.getClass().getName() + " build ear... ");
        checkUtilities();
        Collection<File> archiveResources = findArchiveResources();

        for (File archiveResource : archiveResources) {
            createTibcoArchive(archiveResource);
        }
    }
}
