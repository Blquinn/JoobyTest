package me.blq.standupapi.standups

import me.blq.standupapi.commons.Page
import me.blq.standupapi.commons.EntityNotFoundException
import me.blq.standupapi.commons.ValidationException
import me.blq.standupapi.commons.withHandle
import me.blq.standupapi.commons.withTransaction
import javax.persistence.EntityManagerFactory

class StandupService(
    private val entityManagerFactory: EntityManagerFactory,
    private val standupRepository: StandupRepository,
) {
    fun listStandups(filters: StandupFilters): Page<StandupDto> {
        return entityManagerFactory.withHandle {
            standupRepository.listStandups(filters).map { it.toDto() }
        }
    }

    fun getStandup(id: Long): StandupDetailDto {
        return entityManagerFactory.withHandle {
            standupRepository.getStandupById(id)
                ?: throw EntityNotFoundException("Standup")
        }.toDetailDto()
    }

    fun createStandup(s: StandupCreateDto): StandupDto {
        return entityManagerFactory.withTransaction {
            if (standupRepository.getStandupByName(s.name) != null)
                throw ValidationException("Standup '${s.name}' already exists.")

            standupRepository.save(Standup.fromCreateDto(s)).toDto()
        }
    }
}
