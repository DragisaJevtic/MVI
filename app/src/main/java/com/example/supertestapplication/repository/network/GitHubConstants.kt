package com.example.supertestapplication.repository.network

import java.util.*

object GitHubConstants {
    //constants
    val CLIENT_ID = "a7f73bf59c9f85acfd97"
    val CLIENT_SECRET = "454ea37241d37bd67289dbaca7986b4fdd86fc87"
    val REDIRECT_URI = "https://dragisajevtic.com"
    val SCOPE = ""
    val STATE = UUID.randomUUID().toString()
    //URL-s
    val GITHUB_URL = "https://github.com/"
    val AUTHURL = "https://github.com/login/oauth/authorize"
    val FULL_AUTH_URL = "$AUTHURL?client_id=$CLIENT_ID&scope=$SCOPE&state=$STATE"
}