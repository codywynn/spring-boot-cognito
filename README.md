# spring-boot-cognito

Spring Boot REST API application using AWS Cognito for user authentication and DynamoDB for data storage.

Technologies:

- [Spring](https://spring.io/) for the web framework
- [AWS Cognito](https://aws.amazon.com/cognito/) for user authentication
- [AWS DynamoDB](https://aws.amazon.com/dynamodb/) for the database
- [Swagger](https://swagger.io/) for API documentation
- [Gradle](https://gradle.org/) for build management

### Configuration

Set the Cognito configuration variables in applications.example.properties and rename it to applications.properties.

```
aws.cognito.clientId=<client_id>
aws.cognito.userPoolId=<pool_id>
aws.cognito.identityPoolId=<identity_pool_id>
aws.cognito.region=<region>
aws.cognito.userNameField=<username>
aws.cognito.groupsField=<group>
```

### Quick Start

```
$ gradle build

$ gradle bootRun
```

When your application is up and running, go to localhost:5000/swagger-ui.html to view the API documentation.
