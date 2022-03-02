package me.blq.standupapi.standups

import me.blq.standupapi.users.UserDto
import org.hibernate.validator.constraints.Length
import javax.persistence.*

@Entity
@Table(name = "standup")
class Standup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,
    @Column(name = "name")
    var name: String,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "standup")
    val standupUsers: MutableSet<StandupUser> = mutableSetOf()
) {
    companion object {
        fun fromCreateDto(dto: StandupCreateDto) =
            Standup(name = dto.name)
    }

    fun toDto() = StandupDto(id, name)

    fun toDetailDto() = StandupDetailDto(id, name,
        standupUsers.map { it.user.toDto() })
}

data class StandupCreateDto(
    @field:Length(min = 1)
    val name: String,
)

open class StandupDto(
    val id: Long,
    val name: String,
)

class StandupDetailDto(
    id: Long,
    name: String,
    val users: Collection<UserDto>,
) : StandupDto(id, name)
