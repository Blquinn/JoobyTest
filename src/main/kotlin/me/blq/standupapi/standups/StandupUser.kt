package me.blq.standupapi.standups

import me.blq.standupapi.users.User
import javax.persistence.*

@Entity
@Table(name = "standup_users")
class StandupUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standup_id")
    var standup: Standup,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
)
