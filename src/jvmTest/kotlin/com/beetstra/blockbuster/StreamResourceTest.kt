package com.beetstra.blockbuster

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.FileInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class StreamResourceTest {
    @Test
    fun `a resource file can be parsed`() {
        val items = mutableListOf<Any>()
        val deserializer: DeserializationStrategy<Unit> = buildDeserializer {
            field("description") { description: Text -> items.add(description) }
            objectField("table") {
                arrayField("columnNames") { name: String -> items.add(name) }
                arrayField("rows") { row: Row ->
                    assertEquals(3, row.cells.size)
                    assertEquals(row.cells[0] * 2, row.cells[1])
                    assertEquals(row.cells[0] * 10, row.cells[2])
                    items.add(row.cells[0])
                }
            }
        }

        val stream = assertNotNull(javaClass.getResourceAsStream("/test.json"))
        Json.decodeFromStream(deserializer, stream)
        assertEquals(Text("Test table", "Description of the test table"), items[0])
        assertEquals(listOf<Any>("once", "twice", "10x"), items.subList(1, 4))
        assertEquals(listOf<Any>(1, 2, 5), items.subList(4, items.size))
    }
}

@Serializable
private data class Text(val title: String, val body: String)

@Serializable
private data class Row(val dateAdded: String, val cells: List<Int>)
