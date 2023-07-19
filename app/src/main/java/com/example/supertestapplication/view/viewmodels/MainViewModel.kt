package com.example.supertestapplication.view.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supertestapplication.intents.Event
import com.example.supertestapplication.intents.UISideEffects
import com.example.supertestapplication.intents.UIState
import com.example.supertestapplication.repository.RepositoryManager
import com.example.supertestapplication.repository.network.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.viewmodel.container
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application
) : ViewModel(), ContainerHost<UIState, UISideEffects> {

    override val container = container<UIState, UISideEffects>(UIState(loggedOut = true))

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    private val event = _event.asSharedFlow()

    private val repositoryManager: RepositoryManager =
        RepositoryManager(app.applicationContext, ::tokenReceived)

    private val scopeIO = CoroutineScope(Job() + Dispatchers.IO)

    init {
        subscribeEvents()
    }

    private fun tokenReceived(received: Boolean) {
        intent {
            if (received) {
                reduce {
                    state.copy(loggedOut = false, showGitLogin = false)
                }
                postSideEffect(sideEffect = UISideEffects.NavigateNextScreen)
            }
        }
    }

    /**
     * Set new Event
     */
    fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    /**
     * Start listening to Event
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Handle each event
     */
    private fun handleEvent(event: Event) {
        when (event) {
            Event.OnLoginToGithub -> requestLoginToGitHub()
            Event.OnRequestToken -> requestToken()
            is Event.OnGetTokenFromCode -> getTokenFromCode(event.code)
            is Event.OnSearchForUser -> getUserFromApi(event.username)
            Event.OnGetUserDate -> getUserData()
        }
    }

    /**
     * Perform check do we already have token in storage. In case token is there
     * we are just skipping to next screen where we search for GitHub users
     */
    private fun requestToken() {
        if (repositoryManager.getToken() != null) {
            intent {
                postSideEffect(sideEffect = UISideEffects.NavigateNextScreen)
            }
        }
    }

    private fun requestLoginToGitHub() {
        intent {
            reduce {
                state.copy(loggedOut = true, showGitLogin = true)
            }
        }
    }

    private fun getTokenFromCode(githubCode: String?) {
        githubCode?.let {
            repositoryManager.getTokenFromCode(it)
        }
    }


    private fun getUserData() {
        intent {
            reduce {
                state.copy(userData = repositoryManager.getUserData())
            }
        }
    }

    private fun getUserFromApi(username: String) {
        intent {
            reduce {
                state.copy(searching = true)
            }
        }
        viewModelScope.launch {
            repositoryManager.getUserFromApi(username).collect {
                when (it) {
                    is ResultState.Success -> {
                        scopeIO.launch {
                            repositoryManager.storeUserData(it.data)
                        }
                        intent {
                            reduce {
                                state.copy(
                                    searching = false,
                                    searchingNetworkError = false,
                                    searchingError = false
                                )
                            }
                            postSideEffect(UISideEffects.NavigateNextScreen)
                        }
                    }
                    is ResultState.Error -> {
                        intent {
                            reduce {
                                state.copy(searching = false, searchingError = true)
                            }
                        }
                    }
                    is ResultState.NetworkError -> {
                        intent {
                            reduce {
                                state.copy(searching = false, searchingNetworkError = true)
                            }
                        }
                    }
                }
            }
        }
    }
}