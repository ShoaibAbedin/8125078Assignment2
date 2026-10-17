package com.s8125078.assignment2.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.s8125078.assignment2.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var studentIdLayout: TextInputLayout
    private lateinit var studentIdInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var errorText: TextView
    private lateinit var slowHintText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupListeners()
        observeUiState()
    }

    private fun bindViews(view: View) {
        studentIdLayout = view.findViewById(R.id.tilStudentId)
        studentIdInput = view.findViewById(R.id.etStudentId)
        passwordLayout = view.findViewById(R.id.tilPassword)
        passwordInput = view.findViewById(R.id.etPassword)
        loginButton = view.findViewById(R.id.btnLogin)
        loadingIndicator = view.findViewById(R.id.progressLogin)
        errorText = view.findViewById(R.id.tvLoginError)
        slowHintText = view.findViewById(R.id.tvSlowHint)
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { submitLogin() }

        // pressing Done on the keyboard also logs in
        passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitLogin()
                true
            } else {
                false
            }
        }

        studentIdInput.doAfterTextChanged { viewModel.onInputChanged() }
        passwordInput.doAfterTextChanged { viewModel.onInputChanged() }
    }

    private fun submitLogin() {
        hideKeyboard()
        viewModel.login(
            studentId = studentIdInput.text?.toString().orEmpty(),
            firstName = passwordInput.text?.toString().orEmpty()
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: LoginUiState) {
        studentIdLayout.error = state.studentIdError?.let { getString(it) }
        passwordLayout.error = state.passwordError?.let { getString(it) }

        errorText.isVisible = state.errorMessage != null
        state.errorMessage?.let { errorText.setText(it) }

        studentIdInput.isEnabled = !state.isLoading
        passwordInput.isEnabled = !state.isLoading
        loginButton.isEnabled = !state.isLoading
        loginButton.text = if (state.isLoading) "" else getString(R.string.login_button)
        loadingIndicator.isVisible = state.isLoading
        slowHintText.isVisible = state.showSlowHint

        state.keypass?.let { navigateToDashboard(it) }
    }

    private fun navigateToDashboard(keypass: String) {
        viewModel.onNavigationHandled()
        val action = LoginFragmentDirections.actionLoginFragmentToDashboardFragment(keypass)
        findNavController().navigate(action)
    }

    private fun hideKeyboard() {
        val inputManager = requireContext().getSystemService(InputMethodManager::class.java)
        inputManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
