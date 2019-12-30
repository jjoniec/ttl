package com.example

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spark.SparkLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.log4j.BasicConfigurator
import org.slf4j.LoggerFactory
import spark.Spark.*


class LambdaHandler @Throws(ContainerInitializationException::class)
constructor() : RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private val handler = SparkLambdaContainerHandler.getAwsProxyHandler()
    private var initialized = false
    private val log = LoggerFactory.getLogger(LambdaHandler::class.java)

    override fun handleRequest(awsProxyRequest: AwsProxyRequest, contex: Context?): AwsProxyResponse {
        if (!initialized) {
            defineRoutes()
            initialized = true
        }

        return handler.proxy(awsProxyRequest, contex)
    }

    private fun defineRoutes() {
        BasicConfigurator.configure()
        initExceptionHandler { e ->
            log.error("Spark init failure", e)
            System.exit(100)
        }

        get("/hello") {_ , _  -> "GET World"}
        post("/hello") {_ , _  -> "POST World"}
        put("/hello") {_ , _  -> "PUT World"}
        patch("/hello") {_ , _  -> "PATCH World"}
        delete("/hello") {_ , _  -> "DELETE World"}
    }
}