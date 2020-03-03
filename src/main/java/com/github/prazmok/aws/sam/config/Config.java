package com.github.prazmok.aws.sam.config;

import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Config {
    private final Project project;
    private final AwsSamExtension extension;
    private final String[] environmentAliases = {"environment", "env", "stage"};

    public Config(Project project, AwsSamExtension extension) {
        this.project = project;
        this.extension = extension;
    }

    public String getEnvironmentProperty() {
        for (String alias : environmentAliases) {
            if (project.hasProperty(alias)) {
                return (String) project.getProperties().get("environment");
            }
        }

        return null;
    }

    public boolean isDryRunOption() {
        return project.hasProperty("dry-run") || project.hasProperty("dryrun");
    }

    public Environment getEnvironment() {
        String env = getEnvironmentProperty();
        Environment environment = extension.environments.findByName(env);

        if (environment != null) {
            return environment;
        }

        return new Environment(env);
    }

    public File getSamTemplate() {
        File template;

        if (getEnvironment().samTemplate != null) {
            template = getEnvironment().samTemplate;
        } else if (extension.samTemplate != null) {
            template = extension.samTemplate;
        } else {
            template = new File(project.getRootDir() + File.separator + "template.yml");
        }

        return template;
    }

    public File getPackagedTemplate() {
        File packaged;

        if (getEnvironment().samPackagedTemplate != null) {
            packaged = getEnvironment().samPackagedTemplate;
        } else if (extension.samPackagedTemplate != null) {
            packaged = extension.samPackagedTemplate;
        } else {
            packaged = new File(getSamTemplate().getParentFile() + File.separator + "packaged.yml");
        }

        return packaged;
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

    public String getS3Prefix() {
        if (getEnvironment().s3Prefix != null) {
            return getEnvironment().s3Prefix;
        } else if (extension.s3Prefix != null) {
            return extension.s3Prefix;
        }

        return null;
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

    public List<String> getCapabilities() {
        if (getEnvironment().capabilities != null) {
            return getEnvironment().capabilities;
        } else if (extension.capabilities != null) {
            return extension.capabilities;
        }

        return new LinkedList<String>() {{
            add("CAPABILITY_IAM");
        }};
    }

    public List<String> getNotificationArns() {
        if (getEnvironment().notificationArns != null) {
            return getEnvironment().notificationArns;
        } else if (extension.notificationArns != null) {
            return extension.notificationArns;
        }

        return new LinkedList<>();
    }
}
