package me.blq.standupapi.users

interface UserRepository {
    fun create(user: User): User
    fun getById(id: Long): User?
    fun getByUsername(username: String): User?
}
