package com.example.supertestapplication.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.supertestapplication.R
import com.example.supertestapplication.databinding.FragmentSearchGitUserBinding
import com.example.supertestapplication.intents.Event
import com.example.supertestapplication.intents.UISideEffects
import com.example.supertestapplication.intents.UIState
import com.example.supertestapplication.view.invisible
import com.example.supertestapplication.view.viewmodels.MainViewModel
import com.example.supertestapplication.view.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchGitUserFragment : Fragment() {

    private var _binding: FragmentSearchGitUserBinding? = null
    private val viewModel: MainViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchGitUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.searchButton.setOnClickListener {
            viewModel.setEvent(Event.OnSearchForUser(binding.searchText.text.toString()))
        }
        binding.searchText.addTextChangedListener {
            it?.let {
                binding.searchButton.isEnabled = it.isNotEmpty()
            }
        }
        findNavController().clearBackStack(R.id.LoginFragment)
    }

    private fun render(state: UIState) {
        updateLoadingState(state.searching)
        if (state.searchingNetworkError) {
            Toast.makeText(
                requireContext(),
                "We have problem with accessing GitHub!",
                Toast.LENGTH_LONG
            ).show()
        }
        if (state.searchingError) {
            Toast.makeText(requireContext(), "Unable to find requested user", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun handleSideEffect(sideEffect: UISideEffects) {
        when (sideEffect) {
            UISideEffects.NavigateNextScreen -> findNavController().navigate(R.id.action_SearchFragment_to_UserDetailsFragment)
        }
    }

    private fun updateLoadingState(isVisible: Boolean) {
        if (isVisible) {
            binding.searchProgress.visible()
        } else {
            binding.searchProgress.invisible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}