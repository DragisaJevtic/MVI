package com.example.supertestapplication.view.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.supertestapplication.R
import com.example.supertestapplication.databinding.FragmentLoginBinding
import com.example.supertestapplication.intents.Event
import com.example.supertestapplication.intents.UISideEffects
import com.example.supertestapplication.intents.UIState
import com.example.supertestapplication.repository.network.GitHubConstants
import com.example.supertestapplication.view.invisible
import com.example.supertestapplication.view.utils.GithubWebViewClient
import com.example.supertestapplication.view.viewmodels.MainViewModel
import com.example.supertestapplication.view.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val viewModel: MainViewModel by viewModels()

    private lateinit var gitHubAuthURLFull: String
    private lateinit var gitHubDialog: Dialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.container.stateFlow.collect { render(it) }
                }
                launch {
                    viewModel.container.sideEffectFlow.collect { handleSideEffect(it) }
                }
            }
        }
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestToken()

        binding.githubLoginBtn.setOnClickListener {
            viewModel.setEvent(Event.OnLoginToGithub)
        }

        val mImage = requireContext().getDrawable(R.drawable.github_mark_white)
        binding.githubLoginBtn.setCompoundDrawablesWithIntrinsicBounds(mImage, null, null, null)
    }

    private fun requestToken() {
        viewModel.setEvent(Event.OnRequestToken)
        updateLoadingState(true)
    }

    private fun render(state: UIState) {
        if (state.loggedOut && !state.showGitLogin) {
            updateLoadingState(false)
        } else if (state.loggedOut && state.showGitLogin) {
            prepareAndShowWebViewLoginDialog()
        }
    }

    private fun handleSideEffect(sideEffect: UISideEffects) {
        when (sideEffect) {
            UISideEffects.NavigateNextScreen -> findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSearchFragment())
        }
    }

    private fun updateLoadingState(isVisible: Boolean) {
        if (isVisible) {
            binding.githubLoginBtn.invisible()
            binding.progressGroup.visible()
        } else {
            binding.githubLoginBtn.visible()
            binding.progressGroup.invisible()
        }
    }

    /**
     * This is preferred way of GitHub to do login to their API
     */
    private fun prepareAndShowWebViewLoginDialog() {
        gitHubAuthURLFull = GitHubConstants.FULL_AUTH_URL
        gitHubDialog = Dialog(requireContext())
        val webView = WebView(requireContext())
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = GithubWebViewClient(::onGitWebViewAction)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(gitHubAuthURLFull)
        gitHubDialog.setContentView(webView)
        gitHubDialog.show()
    }

    private fun onGitWebViewAction(webResponse: String?) {
        if (webResponse.isNullOrEmpty()) {
            gitHubDialog.dismiss()
        } else {
            viewModel.setEvent(Event.OnGetTokenFromCode(webResponse))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
