package com.example.supertestapplication.view.utils

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.supertestapplication.repository.network.GitHubConstants

class GithubWebViewClient(private val webViewActions: (String?) -> Unit) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (request!!.url.toString().startsWith(GitHubConstants.REDIRECT_URI)) {
            handleUrl(request.url.toString())
            // Close the dialog after getting the authorization code
            if (request.url.toString().contains("code=")) {
                webViewActions(null)
            }
            return true
        }
        return false
    }

    // Check webview url for access token code or error
    private fun handleUrl(url: String) {
        val uri = Uri.parse(url)
        if (url.contains("code")) {
            val githubCode = uri.getQueryParameter("code") ?: ""
            webViewActions(githubCode)
        }
    }
}