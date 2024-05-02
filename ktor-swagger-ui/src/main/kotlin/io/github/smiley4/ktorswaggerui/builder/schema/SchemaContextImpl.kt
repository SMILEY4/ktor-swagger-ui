package io.github.smiley4.ktorswaggerui.builder.schema

import io.github.smiley4.ktorswaggerui.builder.route.RouteMeta
import io.github.smiley4.ktorswaggerui.data.ArrayTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.EmptyTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OneOfTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiMultipartBodyData
import io.github.smiley4.ktorswaggerui.data.OpenApiSimpleBodyData
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.schemakenerator.core.data.WildcardTypeData
import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.steps.SwaggerSchemaUtils
import io.github.smiley4.schemakenerator.swagger.withAutoTitle
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType

class SchemaContextImpl : SchemaContext {

    private val rootSchemas = mutableMapOf<TypeDescriptor, Schema<*>>()
    private val componentSchemas = mutableMapOf<String, Schema<*>>()


    fun add(routes: Collection<RouteMeta>) {
        collectTypeDescriptor(routes).forEach { typeDescriptor ->
            val schema = generateSchema(typeDescriptor)
            rootSchemas[typeDescriptor] = schema.swagger
            schema.componentSchemas.forEach { (k, v) ->
                componentSchemas[k.full()] = v
            }
        }
    }

    private fun generateSchema(typeDescriptor: TypeDescriptor): CompiledSwaggerSchema {
        return when (typeDescriptor) {
            is KTypeDescriptor -> {
                generateSchema(typeDescriptor.type)
            }
            is ArrayTypeDescriptor -> {
                val itemSchema = generateSchema(typeDescriptor.type)
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
                    swagger = SwaggerSchemaUtils().arraySchema(
                        itemSchema.swagger
                    ),
                    componentSchemas = itemSchema.componentSchemas
                )
            }
            is OneOfTypeDescriptor -> {
                val optionSchemas = typeDescriptor.types.map { generateSchema(it) }
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
                    swagger = SwaggerSchemaUtils().subtypesSchema(
                        optionSchemas.map { it.swagger }
                    ),
                    componentSchemas = buildMap {
                        optionSchemas.forEach { optionSchema ->
                            this.putAll(optionSchema.componentSchemas)
                        }
                    }
                )
            }
            is EmptyTypeDescriptor -> {
                CompiledSwaggerSchema(
                    typeData = WildcardTypeData(),
                    swagger = SwaggerSchemaUtils().anyObjectSchema(),
                    componentSchemas = emptyMap()
                )
            }
        }
    }

    private fun generateSchema(type: KType): CompiledSwaggerSchema {
        return listOf(type)
            .processReflection()
            .generateSwaggerSchema()
            .withAutoTitle(TitleType.SIMPLE)
            .compileReferencingRoot()
            .first()
    }

    private fun collectTypeDescriptor(routes: Collection<RouteMeta>): List<TypeDescriptor> {
        val descriptors = mutableListOf<TypeDescriptor>()
        routes
            .filter { !it.documentation.hidden }
            .forEach { route ->
                route.documentation.request.also { request ->
                    request.parameters.forEach { parameter ->
                        descriptors.add(parameter.type)
                    }
                    request.body?.also { body ->
                        when (body) {
                            is OpenApiSimpleBodyData -> {
                                descriptors.add(body.type)
                            }
                            is OpenApiMultipartBodyData -> {
                                body.parts.forEach { part ->
                                    descriptors.add(part.type)
                                }
                            }
                        }
                    }
                }
                route.documentation.responses.forEach { response ->
                    response.headers.forEach { (_, header) ->
                        header.type?.also { descriptors.add(it) }
                    }
                    response.body?.also { body ->
                        when (body) {
                            is OpenApiSimpleBodyData -> {
                                descriptors.add(body.type)
                            }
                            is OpenApiMultipartBodyData -> {
                                body.parts.forEach { part ->
                                    descriptors.add(part.type)
                                }
                            }
                        }
                    }
                }
            }
        return descriptors
    }

    override fun getSchema(typeDescriptor: TypeDescriptor): Schema<*> {
        return rootSchemas[typeDescriptor] ?: throw Exception("no root-schema for given type-descriptor")
    }

    override fun getComponentSection(): Map<String, Schema<*>> {
        return componentSchemas
    }

}