package io.github.smiley4.ktorswaggerui.dsl

@Deprecated(
    "Use BodyTypeDescriptor instead",
    ReplaceWith("BodyTypeDescriptor.custom(schemaId)")
)
fun obj(schemaId: String) = BodyTypeDescriptor.custom(schemaId)


@Deprecated(
    "Use BodyTypeDescriptor instead",
    ReplaceWith("BodyTypeDescriptor.multipleOf(BodyTypeDescriptor.custom(schemaId))")
)
fun array(schemaId: String) = BodyTypeDescriptor.multipleOf(BodyTypeDescriptor.custom(schemaId))
