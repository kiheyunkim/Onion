package db

import com.google.common.collect.Maps
import model.User

class DataBase {
    companion object {
        private val users = Maps.newHashMap<String, User>()

        fun addUser(user: User) {
            users[user.userId] = user
        }

        fun findUserById(userId: String): User? {
            return users[userId]
        }

        fun findAll(): Collection<User> {
            return users.values
        }
    }
}