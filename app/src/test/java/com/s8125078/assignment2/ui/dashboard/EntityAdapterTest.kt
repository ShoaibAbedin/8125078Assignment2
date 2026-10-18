package com.s8125078.assignment2.ui.dashboard

import com.s8125078.assignment2.data.model.Entity
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EntityAdapterTest {

    private lateinit var adapter: EntityAdapter

    private val entity = Entity(
        title = "World War II",
        period = "1939 – 1945",
        durationYears = 6,
        fields = emptyList(),
        description = null
    )

    @Before
    fun setUp() {
        // spy so notifyDataSetChanged can be stubbed (it needs a real RecyclerView otherwise)
        adapter = spyk(EntityAdapter(onEntityClick = {}))
        every { adapter.notifyDataSetChanged() } returns Unit
    }

    @Test
    fun `starts empty`() {
        assertEquals(0, adapter.getItemCount())
    }

    @Test
    fun `setData updates the item count and refreshes the list`() {
        adapter.setData(listOf(entity, entity.copy(title = "Cold War"), entity.copy(title = "Renaissance")))

        assertEquals(3, adapter.getItemCount())
        verify { adapter.notifyDataSetChanged() }
    }

    @Test
    fun `setData replaces the old list`() {
        adapter.setData(listOf(entity, entity))
        adapter.setData(listOf(entity))

        assertEquals(1, adapter.getItemCount())
    }
}
