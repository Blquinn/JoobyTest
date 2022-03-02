package me.blq.standupapi.standups

import io.jooby.annotations.*
import io.swagger.v3.oas.annotations.tags.Tag
import me.blq.standupapi.commons.Page

@Tag(name = "Standups")
@Path("/standups")
class StandupController(
    private val standupService: StandupService,
) {
    @GET
    fun list(@QueryParam filters: StandupFiltersDto): Page<StandupDto> {
        return standupService.listStandups(StandupFilters.fromDto(filters))
    }

    @GET("/{id}")
    fun getById(@PathParam id: Long): StandupDetailDto {
        return standupService.getStandup(id)
    }

    @POST
    fun create(standupCreateDto: StandupCreateDto): StandupDto {
        return standupService.createStandup(standupCreateDto)
    }
}
