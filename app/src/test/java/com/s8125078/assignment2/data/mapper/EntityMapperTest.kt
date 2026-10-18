package com.s8125078.assignment2.data.mapper

import com.s8125078.assignment2.data.model.EntityField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntityMapperTest {

    private val mapper = EntityMapper()

    // numbers are Doubles here because that's how Gson parses them
    private val worldWarTwo = linkedMapOf<String, Any?>(
        "event" to "World War II",
        "startYear" to 1939.0,
        "endYear" to 1945.0,
        "location" to "Global",
        "keyFigure" to "Winston Churchill",
        "description" to "A global war that involved the vast majority of the world's countries."
    )

    @Test
    fun `history entity gets title, period, duration and fields`() {
        val entity = mapper.map(worldWarTwo, 0)

        assertEquals("World War II", entity.title)
        assertEquals("1939 – 1945", entity.period)
        assertEquals(6, entity.durationYears)
        assertEquals(
            listOf(EntityField("Location", "Global"), EntityField("Key figure", "Winston Churchill")),
            entity.fields
        )
        assertEquals("A global war that involved the vast majority of the world's countries.", entity.description)
    }

    @Test
    fun `summary leaves out the description`() {
        val entity = mapper.map(worldWarTwo, 0)

        assertEquals("Global · Winston Churchill", entity.summary)
    }

    @Test
    fun `same start and end year shows a single year`() {
        val berlinWall = linkedMapOf<String, Any?>(
            "event" to "Fall of the Berlin Wall",
            "startYear" to 1989.0,
            "endYear" to 1989.0
        )

        val entity = mapper.map(berlinWall, 0)

        assertEquals("1989", entity.period)
        assertEquals(0, entity.durationYears)
    }

    @Test
    fun `other topics use the first text field as the title`() {
        val animal = linkedMapOf<String, Any?>(
            "species" to "Tiger",
            "habitat" to "Forest",
            "endangered" to true,
            "description" to "Big cat"
        )

        val entity = mapper.map(animal, 0)

        assertEquals("Tiger", entity.title)
        assertNull(entity.period)
        assertEquals(
            listOf(EntityField("Habitat", "Forest"), EntityField("Endangered", "Yes")),
            entity.fields
        )
    }

    @Test
    fun `entity without any text uses a numbered fallback title`() {
        val entity = mapper.map(mapOf("count" to 3.0), 4)

        assertEquals("Item 5", entity.title)
        assertNull(entity.description)
    }

    @Test
    fun `blank values are skipped`() {
        val entity = mapper.map(mapOf("event" to "Renaissance", "location" to " ", "keyFigure" to null), 0)

        assertEquals(emptyList<EntityField>(), entity.fields)
    }

    @Test
    fun `labels are turned into readable words`() {
        assertEquals("Key figure", mapper.formatLabel("keyFigure"))
        assertEquals("Start year", mapper.formatLabel("start_year"))
        assertEquals("Location", mapper.formatLabel("location"))
    }

    @Test
    fun `values are formatted for display`() {
        assertEquals("1939", mapper.formatValue(1939.0))
        assertEquals("2.5", mapper.formatValue(2.5))
        assertEquals("Yes", mapper.formatValue(true))
        assertEquals("a, b", mapper.formatValue(listOf("a", "b")))
        assertNull(mapper.formatValue(null))
    }

    @Test
    fun `mapAll keeps the original order`() {
        val entities = mapper.mapAll(listOf(worldWarTwo, mapOf("event" to "Cold War")))

        assertEquals(listOf("World War II", "Cold War"), entities.map { it.title })
    }
}
