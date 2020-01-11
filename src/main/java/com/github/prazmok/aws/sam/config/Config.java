package com.github.prazmok.aws.sam.config;

import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin;
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.internal.TaskOutputsInternal;

import java.io.File;
import java.util.*;

public class Config {
    private final Project project;
    private final AwsSamExtension extension;
    private final String environment;

    public Config(Project project, AwsSamExtension extension, String environment) {
        this.project = project;
        this.extension = extension;
        this.environment = environment;
    }

    public Environment getEnvironment() {
        Environment env;

        try {
            env = extension.environments.getByName(environment);
        } catch (UnknownDomainObjectException e) {
            env = new Environment(environment);
        }

        return env;
    }

    public File getSamTemplatePath() {
        if (getEnvironment().samTemplatePath != null) {
            return getEnvironment().samTemplatePath;
        } else if (extension.samTemplatePath != null) {
            return extension.samTemplatePath;
        }

        return project.getRootDir();
    }

    public String getSamTemplateFile() throws MissingConfigurationException {
        if (getEnvironment().samTemplateFile != null) {
            return getEnvironment().samTemplateFile;
        } else if (extension.samTemplateFile != null) {
            return extension.samTemplateFile;
        }

        throw new MissingConfigurationException("samTemplateFile");
    }

    public String getAwsRegion() throws MissingConfigurationException {
        if (getEnvironment().awsRegion != null) {
            return getEnvironment().awsRegion;
        } else if (extension.awsRegion != null) {
            return extension.awsRegion;
        }

        throw new MissingConfigurationException("awsRegion");
    }

    public String getAwsProfile() {
        if (getEnvironment().awsProfile != null) {
            return getEnvironment().awsProfile;
        } else if (extension.awsProfile != null) {
            return extension.awsProfile;
        }

        return null;
    }

    public String getKmsKeyId() {
        if (getEnvironment().kmsKeyId != null) {
            return getEnvironment().kmsKeyId;
        } else if (extension.kmsKeyId != null) {
            return extension.kmsKeyId;
        }

        return null;
    }

    public String getS3Bucket() throws MissingConfigurationException {
        if (getEnvironment().s3Bucket != null) {
            return getEnvironment().s3Bucket;
        } else if (extension.s3Bucket != null) {
            return extension.s3Bucket;
        }

        throw new MissingConfigurationException("s3Bucket");
    }

    public String getS3Prefix() throws MissingConfigurationException {
        if (getEnvironment().s3Prefix != null) {
            return getEnvironment().s3Prefix;
        } else if (extension.s3Prefix != null) {
            return extension.s3Prefix;
        }

        throw new MissingConfigurationException("s3Prefix");
    }

    public String getStackName() throws MissingConfigurationException {
        if (getEnvironment().stackName != null) {
            return getEnvironment().stackName;
        } else if (extension.stackName != null) {
            return extension.stackName;
        }

        throw new MissingConfigurationException("stackName");
    }

    public String getRoleArn() {
        if (getEnvironment().roleArn != null) {
            return getEnvironment().roleArn;
        } else if (extension.roleArn != null) {
            return extension.roleArn;
        }

        return null;
    }

    public boolean forceUpload() {
        if (getEnvironment().forceUpload != null) {
            return getEnvironment().forceUpload;
        } else if (extension.forceUpload != null) {
            return extension.forceUpload;
        }

        return true;
    }

    public boolean useJson() {
        if (getEnvironment().useJson != null) {
            return getEnvironment().useJson;
        } else if (extension.useJson != null) {
            return extension.useJson;
        }

        return false;
    }

    public boolean debug() {
        if (getEnvironment().debug != null) {
            return getEnvironment().debug;
        } else if (extension.debug != null) {
            return extension.debug;
        }

        return false;
    }

    public boolean noExecuteChangeset() {
        if (getEnvironment().noExecuteChangeset != null) {
            return getEnvironment().noExecuteChangeset;
        } else if (extension.noExecuteChangeset != null) {
            return extension.noExecuteChangeset;
        }

        return false;
    }

    public boolean failOnEmptyChangeset() {
        if (getEnvironment().failOnEmptyChangeset != null) {
            return getEnvironment().failOnEmptyChangeset;
        } else if (extension.failOnEmptyChangeset != null) {
            return extension.failOnEmptyChangeset;
        }

        return true;
    }

    public boolean noFailOnEmptyChangeset() {
        if (failOnEmptyChangeset()) {
            return false;
        } else if (getEnvironment().noFailOnEmptyChangeset != null) {
            return getEnvironment().noFailOnEmptyChangeset;
        } else if (extension.noFailOnEmptyChangeset != null) {
            return extension.noFailOnEmptyChangeset;
        }

        return false;
    }

    public boolean confirmChangeset() {
        if (getEnvironment().confirmChangeset != null) {
            return getEnvironment().confirmChangeset;
        } else if (extension.confirmChangeset != null) {
            return extension.confirmChangeset;
        }

        return false;
    }

    public Map<String, Object> getParameterOverrides() {
        if (getEnvironment().parameterOverrides != null) {
            return getEnvironment().parameterOverrides;
        } else if (extension.parameterOverrides != null) {
            return extension.parameterOverrides;
        }

        return new LinkedHashMap<>();
    }

    public List<String> getTags() {
        if (getEnvironment().tags != null) {
            return getEnvironment().tags;
        } else if (extension.tags != null) {
            return extension.tags;
        }

        return new LinkedList<>();
    }

    public List<String> getNotificationArns() {
        if (getEnvironment().notificationArns != null) {
            return getEnvironment().notificationArns;
        } else if (extension.notificationArns != null) {
            return extension.notificationArns;
        }

        return new LinkedList<>();
    }

    public File getSamTmpDir() {
        return new File(project.getBuildDir() + File.separator + "tmp" + File.separator + "sam");
    }

    public File getSamTemplate() throws MissingConfigurationException {
        return new File(getSamTemplatePath() + File.separator + getSamTemplateFile());
    }

    public File getGeneratedSamTemplate() throws MissingConfigurationException {
        return new File(getSamTmpDir() + File.separator + "generated." + getSamTemplateFile());
    }

    public File getOutputSamTemplate() throws MissingConfigurationException {
        return new File(getSamTmpDir() + File.separator + "packaged." + getSamTemplateFile());
    }

    public File getShadowJarFile() {
        ShadowJar shadowJar = (ShadowJar) project.getTasks().findByName(ShadowJavaPlugin.getSHADOW_JAR_TASK_NAME());
        TaskOutputsInternal outputs = Objects.requireNonNull(shadowJar).getOutputs();

        return outputs.getFiles().getSingleFile();
    }
}
