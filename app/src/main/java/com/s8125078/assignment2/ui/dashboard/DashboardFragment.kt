package com.s8125078.assignment2.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.s8125078.assignment2.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: EntityAdapter

    private lateinit var subtitleText: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: CircularProgressIndicator
    private lateinit var slowHintText: TextView
    private lateinit var errorText: TextView
    private lateinit var retryButton: MaterialButton
    private lateinit var emptyText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun bindViews(view: View) {
        subtitleText = view.findViewById(R.id.tvDashboardSubtitle)
        logoutButton = view.findViewById(R.id.btnLogout)
        recyclerView = view.findViewById(R.id.rvEntities)
        loadingIndicator = view.findViewById(R.id.progressDashboard)
        slowHintText = view.findViewById(R.id.tvDashboardSlowHint)
        errorText = view.findViewById(R.id.tvDashboardError)
        retryButton = view.findViewById(R.id.btnRetry)
        emptyText = view.findViewById(R.id.tvDashboardEmpty)
    }

    private fun setupRecyclerView() {
        adapter = EntityAdapter { _ ->
            // TODO: open the details screen
        }
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        retryButton.setOnClickListener { viewModel.loadEntities() }
        logoutButton.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToLoginFragment())
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: DashboardUiState) {
        loadingIndicator.isVisible = state.isLoading
        slowHintText.isVisible = state.isLoading && state.showSlowHint

        errorText.isVisible = state.errorMessage != null
        retryButton.isVisible = state.errorMessage != null
        state.errorMessage?.let { errorText.setText(it) }

        emptyText.isVisible = state.isEmpty
        recyclerView.isVisible = state.entities.isNotEmpty()
        adapter.setData(state.entities)

        val hasData = state.entities.isNotEmpty()
        subtitleText.isVisible = hasData
        if (hasData) {
            val topic = state.topic.replaceFirstChar { it.uppercase() }
            subtitleText.text = resources.getQuantityString(
                R.plurals.dashboard_entity_count, state.total, state.total, topic
            )
        }
    }
}
