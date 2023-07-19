package com.example.supertestapplication.intents

import android.os.Parcelable
import com.example.supertestapplication.repository.model.UserDetail
import kotlinx.android.parcel.Parcelize

// Events that user performed
sealed class Event {
    object OnLoginToGithub : Event()
    object OnRequestToken : Event()
    data class OnGetTokenFromCode(val code: String?) : Event()
    data class OnSearchForUser(val username: String) : Event()
    object OnGetUserDate : Event()
}

//UI State
@Parcelize
data class UIState(
    val loggedOut: Boolean = true,
    val showGitLogin: Boolean = false,
    val searching: Boolean = false,
    val searchingError: Boolean = false,
    val searchingNetworkError: Boolean = false,
    val userData: UserDetail? = null,
    val userDataError: Boolean = false
) : Parcelable {}

sealed class UISideEffects {
    object NavigateNextScreen : UISideEffects()
}
