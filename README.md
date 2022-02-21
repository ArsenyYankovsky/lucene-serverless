#  Serverless Search

An adaptation of Arseny Yankovsky lucene-serverless with the following aims

* No servers
* No fixed costs 
* Low (250-300ms) cold starts
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

ServerlessSearchApi: The REST API Endpoint

## Open API 

```
---
openapi: 3.0.3
info:
  title: serverless-search-api API
  version: 1.0-SNAPSHOT
paths:
  /health:
    get:
      tags:
      - Health Handler
      responses:
        "200":
          description: OK
  /{index}/_doc:
    post:
      tags:
      - Index Handler
      parameters:
      - name: index
        in: path
        required: true
        schema:
          type: string
      requestBody:
        content:
          application/json:
            schema:
              type: object
              additionalProperties:
                type: object
      responses:
        "200":
          description: OK
  /{index}/_doc/{id}:
    put:
      tags:
      - Index Handler
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      - name: index
        in: path
        required: true
        schema:
          type: string
      requestBody:
        content:
          application/json:
            schema:
              type: object
              additionalProperties:
                type: object
      responses:
        "200":
          description: OK
    post:
      tags:
      - Index Handler
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      - name: index
        in: path
        required: true
        schema:
          type: string
      requestBody:
        content:
          application/json:
            schema:
              type: object
              additionalProperties:
                type: object
      responses:
        "200":
          description: OK
  /{index}/_search:
    get:
      tags:
      - Search Handler
      parameters:
      - name: index
        in: path
        required: true
        schema:
          type: string
      - name: q
        in: query
        schema:
          type: string
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SearchResults'
components:
  schemas:
    Hit:
      type: object
      properties:
        _index:
          type: string
        _type:
          type: string
        _id:
          type: string
        _score:
          format: float
          type: number
        _source:
          type: object
          additionalProperties:
            type: object
    Hits:
      type: object
      properties:
        total:
          $ref: '#/components/schemas/Total'
        hits:
          type: array
          items:
            $ref: '#/components/schemas/Hit'
    SearchResults:
      type: object
      properties:
        took:
          format: int64
          type: integer
        hits:
          $ref: '#/components/schemas/Hits'
    Total:
      type: object
      properties:
        value:
          format: int64
          type: integer
        relation:
          type: string

```