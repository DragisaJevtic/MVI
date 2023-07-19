package com.example.supertestapplication.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.supertestapplication.databinding.FragmentUserDetailesBinding
import com.example.supertestapplication.intents.Event
import com.example.supertestapplication.intents.UISideEffects
import com.example.supertestapplication.intents.UIState
import com.example.supertestapplication.repository.model.UserDetail
import com.example.supertestapplication.view.checkProvidedDate
import com.example.supertestapplication.view.prepareDate
import com.example.supertestapplication.view.viewmodels.MainViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailesBinding? = null
    private val viewModel: MainViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val TAG = "UserDetailsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailesBinding.inflate(inflater, container, false)
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
        viewModel.setEvent(Event.OnGetUserDate)
    }

    private fun render(state: UIState) {
        if (state.userData != null) {
            fillData(state.userData)
        }
        if (state.userDataError) {
            Toast.makeText(requireContext(), "Unable to obtain data for user", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun handleSideEffect(sideEffect: UISideEffects) {
        when (sideEffect) {
            UISideEffects.NavigateNextScreen -> Log.d(TAG, "No Navigation further here")
        }
    }

    private fun fillData(userDetails: UserDetail?) {
        if (userDetails == null) {
            return
        }
        binding.name.text = userDetails.name.checkProvidedDate()
        binding.username.text = userDetails.login.checkProvidedDate()
        binding.date.text = userDetails.createdAt.prepareDate()
        Picasso.with(requireContext()).load(userDetails.avatarUrl).into(binding.avatar)
    }
}