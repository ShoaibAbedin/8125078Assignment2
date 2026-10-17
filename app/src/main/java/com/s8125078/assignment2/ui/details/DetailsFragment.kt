package com.s8125078.assignment2.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.model.Entity
import com.s8125078.assignment2.data.model.EntityField
import dagger.hilt.android.AndroidEntryPoint

// Shows everything about the selected entity, including the description.
// No ViewModel needed - the entity arrives complete through Safe Args.
@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.toolbarDetails).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        showEntity(view, args.entity)
    }

    private fun showEntity(view: View, entity: Entity) {
        val periodText = view.findViewById<TextView>(R.id.tvDetailsPeriod)
        val periodLine = buildPeriodLine(entity)
        periodText.isVisible = periodLine != null
        periodText.text = periodLine

        view.findViewById<TextView>(R.id.tvDetailsTitle).text = entity.title

        val fieldsGrid = view.findViewById<GridLayout>(R.id.gridDetailsFields)
        fieldsGrid.isVisible = entity.fields.isNotEmpty()
        addFieldTiles(fieldsGrid, entity.fields)

        val hasDescription = !entity.description.isNullOrBlank()
        view.findViewById<TextView>(R.id.tvDetailsAboutLabel).isVisible = hasDescription
        view.findViewById<TextView>(R.id.tvDetailsDescription).apply {
            isVisible = hasDescription
            text = entity.description
        }
    }

    // "1939 – 1945 · 6 years"
    private fun buildPeriodLine(entity: Entity): String? {
        val period = entity.period ?: return null
        val years = entity.durationYears
        return if (years != null && years > 0) {
            period + " · " + resources.getQuantityString(R.plurals.details_duration, years, years)
        } else {
            period
        }
    }

    // two tiles per row, each taking half the width
    private fun addFieldTiles(grid: GridLayout, fields: List<EntityField>) {
        grid.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val margin = resources.getDimensionPixelSize(R.dimen.space_xs)

        fields.forEach { field ->
            val tile = inflater.inflate(R.layout.item_detail_field, grid, false)
            tile.findViewById<TextView>(R.id.tvFieldLabel).text = field.label
            tile.findViewById<TextView>(R.id.tvFieldValue).text = field.value
            tile.layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            ).apply {
                width = 0
                setMargins(margin, margin, margin, margin)
            }
            grid.addView(tile)
        }
    }
}
