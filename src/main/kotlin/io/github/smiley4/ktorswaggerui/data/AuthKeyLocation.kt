package io.github.smiley4.ktorswaggerui.data

import io.swagger.v3.oas.models.security.SecurityScheme

enum class AuthKeyLocation(val swaggerType: SecurityScheme.In) {
    QUERY(SecurityScheme.In.QUERY),
    HEADER(SecurityScheme.In.HEADER),
    COOKIE(SecurityScheme.In.COOKIE)
}
