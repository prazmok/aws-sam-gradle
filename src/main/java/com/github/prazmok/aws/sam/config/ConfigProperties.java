package com.github.prazmok.aws.sam.config;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigProperties {
    public File samTemplatePath;
    public String samTemplateFile;
    public String awsRegion;
    public String awsProfile;
    public String kmsKeyId;
    public String s3Bucket;
    public String s3Prefix;
    public String stackName;
    public String roleArn;
    public Boolean forceUpload;
    public Boolean useJson;
    public Boolean debug;
    public Boolean noExecuteChangeset;
    public Boolean failOnEmptyChangeset;
    public Boolean noFailOnEmptyChangeset;
    public Boolean confirmChangeset;
    public List<String> notificationArns;
    public List<String> tags;
    public Map<String, Object> parameterOverrides;
}
