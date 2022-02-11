package au.qut.edu.eresearch.serverlesssearch

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import spock.lang.*

class HappyPathIntegrationTest extends Specification {
    def jsonSlurper = new JsonSlurper()

    @Shared
    def client = LambdaClient.builder()
            .httpClient(ApacheHttpClient.builder().build())
            .region(Region.EU_NORTH_1).build()

    private deleteTestIndex() {
        def response = client.invoke(InvokeRequest.builder()
                .functionName("dev-lucene-serverless-delete-index")
                .payload(SdkBytes.fromUtf8String(JsonOutput.toJson([
                        body: JsonOutput.toJson([
                                indexName: 'books',
                        ])
                ]))).build())
    }

    def setupSpec() {
        deleteTestIndex()
    }

    def cleanupSpec() {
        deleteTestIndex()
    }

    def "simple write-read test"() {
        when:
        client.invoke(InvokeRequest.builder()
                .functionName("dev-lucene-serverless-enqueue-index")
                .payload(SdkBytes.fromUtf8String(JsonOutput.toJson([
                        body: JsonOutput.toJson([
                                indexName: 'books',
                                documents: [[
                                                    name  : 'Foundation',
                                                    author: 'Isaac Asimov',
                                            ]]
                        ])
                ]))).build())

        sleep(60000)

        def response = client.invoke(InvokeRequest.builder()
                .functionName("dev-lucene-serverless-query")
                .payload(SdkBytes.fromUtf8String(JsonOutput.toJson([
                        body: JsonOutput.toJson([
                                indexName: 'books',
                                query    : 'author:isaac',
                        ])
                ]))).build())

        def result = jsonSlurper.parseText(response.payload().asUtf8String())
        def body = jsonSlurper.parseText(result.body)

        then:
        response.statusCode() == 200
        body.documents[0].author == "Isaac Asimov"
        body.documents[0].name == "Foundation"
    }
}
