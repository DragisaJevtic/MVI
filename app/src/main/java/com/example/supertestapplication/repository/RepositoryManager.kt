package com.example.supertestapplication.repository

import android.content.Context
import androidx.room.Room
import com.example.supertestapplication.repository.db.AppDatabase
import com.example.supertestapplication.repository.model.UserDetail
import com.example.supertestapplication.repository.network.GitHubConstants
import com.example.supertestapplication.repository.network.GitHubConstants.GITHUB_URL
import com.example.supertestapplication.repository.network.Network
import com.example.supertestapplication.repository.network.NetworkService
import com.example.supertestapplication.repository.network.utils.ResultState
import com.example.supertestapplication.repository.store.TokenStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RepositoryManager(
    private val context: Context,
    private val tokenReceived: (Boolean) -> Unit
) {

    private val gitHubApi: NetworkService by lazy {
        Network.retrofitClient().create(NetworkService::class.java)
    }
    private val gitHubWeb: NetworkService by lazy {
        Network.retrofitClient(GITHUB_URL).create(NetworkService::class.java)
    }

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "userDatabase"
    ).build()
    var dataStore = TokenStore(context)

    private fun storeToken(token: String) {
        dataStore.saveToken(token)
    }

    fun getToken(): String? {
        return dataStore.getAccessToken
    }

    fun getTokenFromCode(githubCode: String) {
        GlobalScope.launch(Dispatchers.Default) {
            val item = gitHubWeb.getAccessToken(
                client_id = GitHubConstants.CLIENT_ID,
                client_secret = GitHubConstants.CLIENT_SECRET,
                code = githubCode
            )
            storeToken(item.accessToken)
            tokenReceived(true)
        }
    }

    suspend fun getUserFromApi(username: String): Flow<ResultState<UserDetail>> {
        return flow {
            try {
                val response = gitHubApi.getDetailUser(username, prepareBearerToken())
                val dataMapped = response?.let { userData ->
                    DataMapper.mapUserResponse(userData)
                }
                emit(ResultState.Success(dataMapped))
            } catch (e: Exception) {
                emit(ResultState.Error(e.toString(), 500))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun prepareBearerToken(): String {
        return "Bearer ${dataStore.getAccessToken}"
    }

    fun storeUserData(data: UserDetail?) {
        //This is relatively dumb solution but as we need just as example it will
        // work to maintain just one record in database
        db.userDao().nukeTable()
        //Store user in database
        data?.let {
            db.userDao().insertAll(DataMapper.toDbModel(it))
        }
    }

    fun getUserData(): UserDetail? {
        val users = db.userDao().getAll()
        if (users.isNotEmpty()) {
            return DataMapper.toUserDetails(users[0])
        }
        return null
    }

}