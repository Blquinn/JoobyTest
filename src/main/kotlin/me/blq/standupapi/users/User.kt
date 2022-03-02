package me.blq.standupapi.users

import me.blq.standupapi.standups.StandupUser
import org.hibernate.validator.constraints.Length
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,
    @Column(name = "username", unique = true, nullable = false)
    var username: String,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    val standupUsers: MutableSet<StandupUser> = mutableSetOf()
) {
    companion object {
        fun fromCreateDto(dto: UserCreateDto) = User(username = dto.username)
    }

    fun toDto() = UserDto(id, username)
}

data class UserDto(
    var id: Long,
    var username: String,
)

data class UserCreateDto(
    @field:Length(min = 1, max = 100)
    val username: String
)
