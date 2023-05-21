package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.victools.jsonschema.generator.FieldScope
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerationContext
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import com.github.victools.jsonschema.generator.TypeScope
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.swagger2.Swagger2Module
import io.github.smiley4.ktorswaggerui.dsl.Example
import io.swagger.v3.oas.annotations.media.Schema

object JsonSchemaConfig {

    var schemaGeneratorConfigBuilder: SchemaGeneratorConfigBuilder =
        SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
            .with(JacksonModule())
            .with(Swagger2Module())
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .with(Option.ALLOF_CLEANUP_AT_THE_END)
            .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
            .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.DEFINITION_FOR_MAIN_SCHEMA)
            .without(Option.INLINE_ALL_SCHEMAS)
            .also {
                it.forTypesInGeneral()
                    .withTypeAttributeOverride { objectNode: ObjectNode, typeScope: TypeScope, _: SchemaGenerationContext ->
                        if (typeScope is FieldScope) {
                            typeScope.getAnnotation(Schema::class.java)?.also { annotation ->
                                if (annotation.example != "") {
                                    objectNode.put("example", annotation.example)
                                }
                            }
                            typeScope.getAnnotation(Example::class.java)?.also { annotation ->
                                objectNode.put("example", annotation.value)
                            }
                        }
                    }
            }

}