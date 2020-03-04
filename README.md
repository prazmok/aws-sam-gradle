# AWS SAM plugin for Gradle

[![Build Status](https://travis-ci.org/prazmok/aws-sam-gradle.svg?branch=master)](https://travis-ci.org/prazmok/aws-sam-gradle)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/prazmok/aws-sam-gradle/blob/master/LICENCE)

Gradle plugin for deploying AWS Serverless applications using AWS SAM CLI.

## Prerequisites

Under the hood this plugin uses AWS SAM CLI so make sure you have installed:

* AWS CLI ([installation guidelines](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html))
* AWS SAM CLI (minimum version 0.30.0 - [installation guidelines](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html))
* Docker ([installation overview](https://docs.docker.com/install/))

## Plugin initialization

Initialize plugin using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

```groovy
plugins {
  id "com.github.prazmok.aws.sam" version "0.0.14"
}
```

Or using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.github.prazmok:aws-sam-gradle:0.0.14"
  }
}

apply plugin: "com.github.prazmok.aws.sam"
```

## Configuration

The default configuration should be defined in the `deployent` extension block. It also may be extended for specific environment by defining/overwriting properties in `environments` extension map.

Example configuration:

```groovy
deployment {
    awsRegion = "eu-west-1"
    s3Bucket = "default-bucket"
    stackName = "default-stack"
    parameterOverrides = [
        ExampleParam: "ExampleValue"
    ]
    // ...    

    environments {
        staging { // Where "staging" is being considered as an ENVIRONMENT_NAME
            debug = true
            stackName = "extended-dev-stack"
            // ...
        }
        production {
            noExecuteChangeset = true
            parameterOverrides = [
                ExampleParam: "ExtendedExampleValue"
            ]
            // ...
        }
    }
}
```

Configuration reference:

| **Property name**      	| **Type**            	| **Required** 	| **Default**        	| **Description** 
|------------------------	|---------------------	|--------------	|--------------------	|---------------
| samTemplate            	| File                	| No           	| template.yml       	| The path where your AWS SAM template is located.
| samPackagedTemplate    	| File                	| No           	| packaged.yml       	| The path to the file where the command writes the packaged template.
| awsRegion              	| String              	| Yes          	| -                  	| Sets the AWS Region of the service (for example, us-east-1).
| awsProfile             	| String              	| No           	| Null               	| Select a specific profile from your credential file to get AWS credentials.
| kmsKeyId               	| String              	| No           	| Null               	| The ID of an AWS KMS key used to encrypt artifacts that are at rest in the Amazon S3 bucket.
| s3Bucket               	| String              	| Yes          	| -                  	| The name of the S3 bucket where this command uploads the artifacts that are referenced in your template.
| s3Prefix               	| String              	| No           	| Null               	| Prefix added to the artifacts name that are uploaded to the Amazon S3 bucket. The prefix name is a path name (folder name) for the Amazon S3 bucket.
| stackName              	| String              	| Yes          	| -                  	| The name of the AWS CloudFormation stack you're deploying to. If you specify an existing stack, the command updates the stack. If you specify a new stack, the command creates it.
| roleArn                	| String              	| No           	| Null               	| The Amazon Resource Name (ARN) of an AWS Identity and Access Management (IAM) role that AWS CloudFormation assumes when executing the change set.
| forceUpload            	| Boolean             	| No           	| True               	| Override existing files in the Amazon S3 bucket. Specify this flag to upload artifacts even if they match existing artifacts in the Amazon S3 bucket.
| useJson                	| Boolean             	| No           	| False              	| Output JSON for the AWS CloudFormation template. YAML is used by default.
| debug                  	| Boolean             	| No           	| False              	| Turns on debug logging.
| noExecuteChangeset     	| Boolean             	| No           	| False              	| Indicates whether to execute the change set. Specify this flag if you want to view your stack changes before executing the change set. This command creates an AWS CloudFormation change set and then exits without executing the change set.
| failOnEmptyChangeset   	| Boolean             	| No           	| True               	| Specify whether to return a non-zero exit code if there are no changes to be made to the stack. Using this excludes the use of "noFailOnEmptyChangeset".
| noFailOnEmptyChangeset 	| Boolean             	| No           	| False              	| Specify whether to return a zero exit code if there are no changes to be made to the stack. Using this excludes the use of "failOnEmptyChangeset".
| capabilities           	| List[String]        	| No           	| ["CAPABILITY_IAM"] 	| A list of capabilities that you must specify to allow AWS CloudFormation to create certain stacks.
| notificationArns       	| List[String]        	| No           	| []                 	| Amazon Simple Notification Service topic Amazon Resource Names (ARNs) that AWS CloudFormation associates with the stack.
| tags                   	| List[String]        	| No           	| []                 	| A list of tags to associate with the stack that is created or updated. AWS CloudFormation also propagates these tags to resources in the stack if the resource supports it.
| parameterOverrides     	| Map[String, Object] 	| No           	| {}                 	| A map that contains AWS CloudFormation parameter overrides encoded at the end as key=value pairs.

## Available tasks overview

Usage:

```bash
./gradlew [TASK_NAME] [OPTIONS]
```

Available options:

* `-Penvironment=<ENVIRONMENT_NAME>` # Name of environment from configuration (e.g. `staging`)
* `-Pdry-run` # Dry run prints SAM CLI commands at the output

#### Task: `validateSam`

Validates an AWS SAM template - [sam validate reference](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-cli-command-reference-sam-validate.html).

```bash
./gradlew validateSam
```

> Applies Gradle Incremental Builds with SAM template as an input. 

#### Task: `packageSam`

Packages an AWS SAM application. It creates a JAR file of your code and dependencies, and uploads it to Amazon S3. It then returns a copy of your AWS SAM template, replacing references to local artifacts with the Amazon S3 location where the command uploaded the artifacts - [sam package reference](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-cli-command-reference-sam-package.html).

```bash
./gradlew packageSam
```

> Applies Gradle Incremental Builds with SAM template as an input and generated JAR files collection.

#### Task: `deploySam`

Deploys an AWS SAM application - [sam deploy reference](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-cli-command-reference-sam-deploy.html).

```bash
./gradlew deploySam
```

> This task DOESN'T apply Gradle Incremental Builds

## Code of Conduct & Contributing guidelines

* [Code of Conduct for contributors](docs/CODE_OF_CONDUCT.md).
* [Contribution guidelines for this project](docs/CONTRIBUTING.md).
