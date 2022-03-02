package me.blq.standupapi.commons

data class PageRequest(
    val page: Int,
    val pageSize: Int,
)

class Page<T>(
    val nextPage: Int?,
    val contents: List<T>
) {
    fun <R> map(fn: (T) -> R): Page<R> = Page(nextPage, this.contents.map(fn))
}
