# AWS Parameter Store Config Support

This module adds support for using the 
[AWS Parameter Store](https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html)
as a Spring Cloud configuration backend by creating a composite `PropertySource` at bootstrap time, similar to Spring 
Cloud's Consul support. 
It relies on the [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/welcome.html) to 
retrieve parameters from the Parameter Store. 

## Usage and Configuration

Simply add a dependency on this library from a Spring Cloud-enabled application to activate its support.
You can disable it by specifying a `aws.paramstore.enabled` property and setting it to `false`. 

Further configuration is documented in the `AwsParamStoreProperties` class. If you're using a single Parameter Store for
multiple deployment environments, then make sure to override the default `aws.paramstore.prefix` property with an
environment-specific value. 

## Configuring the `AWSSimpleSystemsManagement` client

Typically it's expected that the `AWSSimpleSystemsManagement` instance created by the 
`AwsParamStoreBootstrapConfiguration` will work correctly using its default configuration. 
Check its [documentation](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) to understand
where it looks for AWS credentials to connect to the Parameter Store. 

If you would like to override the client, you'd have to define 
[your own Spring Cloud bootstrap configuration](https://projects.spring.io/spring-cloud/spring-cloud.html#_customizing_the_bootstrap_configuration) 
to define your own instance. 
