#  Serverless Search

An adaptation of Arseny Yankovsky lucene-serverless with the following aims

* No servers
* No fixed costs 
* Low (250-300ms) cold starts
* Secure
* [OpenSearch](https://github.com/opensearch-project/OpenSearch) compatible signatures (WIP)
* Production Ready (WIP)

## Prerequisites
- [Configure quarkus for native builds](https://quarkus.io/guides/building-native-image)
- [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- AWS account

## Build native images
`./mvnw clean install -Dquarkus.package.type=native`

## Deploy the stack

```
sam deploy --stack-name <stack_name> --s3-bucket <code_bucket> --capabilities CAPABILITY_IAM \
--parameter-overrides VpcId=<VPC_ID> MountTarget1Subnet=<SUBNET_ID_1> MountTarget2Subnet=<SUBNET_ID_2> MountTarget3Subnet=subnet-<SUBNET_ID_3>
```

## Stack outputs

ServerlessSearchApi: The API Endpoint

## Open API 

The open api specification can be accessed from 

```
https://<ServerlessSearchApi>/q/openapi
```