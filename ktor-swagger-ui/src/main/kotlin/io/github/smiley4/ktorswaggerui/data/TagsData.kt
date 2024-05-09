package io.github.smiley4.ktorswaggerui.data

data class TagsData(
    val tags: List<TagData>,
    val generator: TagGenerator,
) {

    companion object {
        val DEFAULT = TagsData(
            tags = emptyList(),
            generator = { emptyList() }
        )
    }

}