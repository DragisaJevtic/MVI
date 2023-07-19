package com.example.supertestapplication.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetail(
    val login: String?,
    val name: String?,
    val avatarUrl: String?,
    val createdAt: String?,
) : Parcelable {}
