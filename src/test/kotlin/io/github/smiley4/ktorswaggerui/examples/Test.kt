package io.github.smiley4.ktorswaggerui.examples

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.core.util.Json
import kotlinx.datetime.Instant


fun main() {

    val kotlinMapper = jacksonObjectMapper()
    val defaultMapper = ObjectMapper()
    val swaggerMapper = Json.mapper()

    registerCustomDeserializer(kotlinMapper)
    registerCustomDeserializer(defaultMapper)
    registerCustomDeserializer(swaggerMapper)

    testInstant(kotlinMapper)
    testInstant(defaultMapper)
    testInstant(swaggerMapper)

    testObject(kotlinMapper)
    testObject(defaultMapper)
    testObject(swaggerMapper)

}


fun registerCustomDeserializer(mapper: ObjectMapper) {
    SimpleModule().addSerializer(Instant::class.java, ItemSerializer()).also {
        mapper.registerModule(it)
    }
}

class ItemSerializer : StdSerializer<Instant>(Instant::class.java) {
    override fun serialize(value: Instant, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeNumber(value.toEpochMilliseconds())
    }
}

fun testInstant(mapper: ObjectMapper) {
    withoutException {
        println(
            mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(Instant.fromEpochMilliseconds(System.currentTimeMillis()))
        )
    }
}


fun testObject(mapper: ObjectMapper) {
    withoutException {
        println(
            mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                    DataWithInstant(
                        text = "test-object",
                        counter = 42,
                        timestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis())
                    )
                )
        )
    }
}

class DataWithInstant(
    val text: String,
    val counter: Int,
    val timestamp: Instant
)


fun withoutException(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}