# Lucene Serverless

This project demonstrates a proof-of-concept serverless full-text search solution built with Apache Lucene and Quarkus framework.

✔️No servers
✔️No fixed costs
✔️Low (250-300ms) cold starts

## Prerequisites
- [Serverless framework >= 1.56.1](https://serverless.com/framework/docs/getting-started/)
- AWS account

## Run it
1. Replace region, vpc id and subnets in the `serverless.yml` file

2. Deploy the stack
   `sls deploy`

3. Don't forget to remove it if you're not planning to use it
   `sls remove`


## Build native image
`./mvn clean package`


