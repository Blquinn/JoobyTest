package me.blq.standupapi.standups

import me.blq.standupapi.commons.Page
import me.blq.standupapi.commons.PageRequest

data class StandupFilters(
    val page: PageRequest,
    val name: String? = null,
) {
    companion object {
        fun fromDto(dto: StandupFiltersDto): StandupFilters {
            return StandupFilters(
                PageRequest(dto.page ?: 1, dto.pageSize ?: 30),
                dto.name,
            )
        }
    }
}

data class StandupFiltersDto(
    var page: Int? = null,
    var pageSize: Int? = null,
    var name: String? = null,
)

interface StandupRepository {
    fun listStandups(filters: StandupFilters): Page<Standup>
    fun getStandupByName(name: String): Standup?
    fun getStandupById(id: Long): Standup?
    fun save(s: Standup): Standup
}
