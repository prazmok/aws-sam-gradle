package com.github.prazmok.aws.sam.config;

import java.io.File;
import java.util.List;
import java.util.Map;

abstract class ConfigProperties {
    File samTemplatePath;
    String samTemplateFile;
    String awsRegion;
    String awsProfile;
    String kmsKeyId;
    String s3Bucket;
    String s3Prefix;
    String stackName;
    String roleArn;
    Boolean forceUpload;
    Boolean useJson;
    Boolean debug;
    Boolean noExecuteChangeset;
    Boolean failOnEmptyChangeset;
    Boolean noFailOnEmptyChangeset;
    Boolean confirmChangeset;
    List<String> notificationArns;
    List<String> tags;
    Map<String, Object> parameterOverrides;
}
