package me.blq.standupapi.users

import me.blq.standupapi.commons.EntityNotFoundException
import me.blq.standupapi.commons.ValidationException
import me.blq.standupapi.commons.withHandle
import me.blq.standupapi.commons.withTransaction
import javax.persistence.EntityManagerFactory

class UserService(
    private val emf: EntityManagerFactory,
    private val userRepository: UserRepository,
) {
    fun getById(id: Long): UserDto {
        return emf.withHandle {
            val user = userRepository.getById(id)
                ?: throw EntityNotFoundException("User")
            user.toDto()
        }
    }

    fun create(user: UserCreateDto): UserDto {
        return emf.withTransaction {
            if (userRepository.getByUsername(user.username) != null)
                throw ValidationException("User with username ${user.username} already exists.")

            userRepository.create(User.fromCreateDto(user)).toDto()
        }
    }
}
