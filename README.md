#  Serverless Search

An adaptation of Arseny Yankovsky lucene-serverless with the following aims

✔️ No servers

✔️ No fixed costs

✔️ Low (250-300ms) cold starts

✔️ Production Ready

Notable Changes

* Refactor to support Java 17, the build environment will remain as Java 11 until Corretto 17 is available as a Lambda environment
* Multi-module packaging that separates the core indexing service, rest api, and asynchronous processing of index requests
* Replaced serverless with AWS SAM

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

ServerlessSearchApi: The REST API for the 

### Index a document

URL: `https://<ServerlessSearchApi>/index`

HTTP method: POST

Example request body:

```json
{
  "indexName": "books",
  "documents": [
    {
      "name": "The Foundation",
      "author": "Isaac Asimov"
    }
  ]
}
```

### Query documents

URL: `https://<api-id>.execute-api.<region>.amazonaws.com/dev/query`

HTTP method: POST

Example request body:

```json
{
   "indexName": "books",
   "query": "author:isaac"
}
```

Example response body:

```json
{
  "totalDocuments": "1",
  "documents": [
    {
      "author": "Isaac Asimov",
      "name": "The Foundation"
    }
  ]
}
```

