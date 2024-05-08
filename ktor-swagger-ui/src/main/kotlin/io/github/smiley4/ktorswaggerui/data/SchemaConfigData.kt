package io.github.smiley4.ktorswaggerui.data

import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.withAutoTitle
import kotlin.reflect.KType


data class SchemaConfigData(
    val schemas: Map<String, TypeDescriptor>,
    val generator: (type: KType) -> CompiledSwaggerSchema,
    val overwrite: Map<KType, TypeDescriptor>
) {
    companion object {
        val DEFAULT = SchemaConfigData(
            schemas = emptyMap(),
            generator = { type ->
                type
                    .processReflection()
                    .generateSwaggerSchema()
                    .withAutoTitle(TitleType.SIMPLE)
                    .compileReferencingRoot()
            },
            overwrite = emptyMap()
        )
    }
}