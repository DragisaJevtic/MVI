package com.example.supertestapplication.repository

import com.example.supertestapplication.repository.db.User
import com.example.supertestapplication.repository.model.UserDetail
import com.example.supertestapplication.repository.network.responses.UserDetailResponse

object DataMapper {

    fun mapUserResponse(userData: UserDetailResponse): UserDetail {
        return UserDetail(
            avatarUrl = userData.avatarUrl,
            name = userData.name,
            login = userData.login,
            createdAt = userData.createdAt
        )
    }

    fun toDbModel(userData: UserDetail): User {
        return User(
            0,
            name = userData.name,
            login = userData.login,
            avatarUrl = userData.avatarUrl,
            createdAt = userData.createdAt
        )
    }

    fun toUserDetails(user: User): UserDetail? {
        return UserDetail(
            login = user.login,
            avatarUrl = user.avatarUrl,
            name = user.name,
            createdAt = user.createdAt
        )
    }

}