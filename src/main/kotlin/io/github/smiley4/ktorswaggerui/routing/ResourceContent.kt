package io.github.smiley4.ktorswaggerui.routing

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import java.net.URL

class ResourceContent(private val resource: URL) : OutgoingContent.ByteArrayContent() {

    private val contentTypes = mapOf(
        "html" to ContentType.Text.Html,
        "css" to ContentType.Text.CSS,
        "js" to ContentType.Application.JavaScript,
        "json" to ContentType.Application.Json.withCharset(Charsets.UTF_8),
        "png" to ContentType.Image.PNG
    )

    private val bytes by lazy { resource.readBytes() }

    override val contentType: ContentType? by lazy {
        val extension = resource.file.substring(resource.file.lastIndexOf('.') + 1)
        contentTypes[extension] ?: ContentType.Text.Html
    }

    override val contentLength: Long? by lazy {
        bytes.size.toLong()
    }

    override fun bytes(): ByteArray = bytes

    override fun toString() = "ResourceContent \"$resource\""
}
