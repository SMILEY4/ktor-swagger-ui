package io.github.smiley4.ktorswaggerui.spec.schema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.victools.jsonschema.generator.FieldScope
import com.github.victools.jsonschema.generator.SchemaGenerationContext
import com.github.victools.jsonschema.generator.TypeAttributeOverrideV2
import com.github.victools.jsonschema.generator.TypeScope
import io.github.smiley4.ktorswaggerui.dsl.Example
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Customizes the generates json-schema by adding fields from annotations ([Schema], [Example])
 */
class SchemaTypeAttributeOverride : TypeAttributeOverrideV2 {

    override fun overrideTypeAttributes(objectNode: ObjectNode, scope: TypeScope?, context: SchemaGenerationContext?) {
        if (scope is FieldScope) {
            scope.getAnnotation(Schema::class.java)?.also { annotation ->
                if (annotation.example != "") {
                    objectNode.put("example", annotation.example)
                }
            }
            scope.getAnnotation(Example::class.java)?.also { annotation ->
                objectNode.put("example", annotation.value)
            }
        }
    }

}