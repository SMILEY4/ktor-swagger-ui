package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.dsl.getSchemaType
import java.io.File

object TypeOverwrites {

    /**
     * overwrite the schemas of the given types with the given custom json-schemas instead of generating them.
     */
    val entries = mutableMapOf(
        getSchemaType<File>() to """{"type":"string", "format":"binary"}""",
    )


    /**
     * @return the type-overwrite-[entries]
     */
    fun get() = entries

}
