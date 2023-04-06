package com.beetstra.blockbuster

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class NestedObjectTest {
    @Test
    fun objectsCanBeNestedAndParsedIncrementally() {
        val list = mutableListOf<String>()
        val deserializer = buildDeserializer {
            objectField("data") {
                arrayField("items", list::add)
            }
        }
        val s = """{"data":{"items":["a","b","c","e"]}}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf("a", "b", "c", "e"), list)
    }

    @Test
    fun arraysCanBeNestedAndParsedIncrementally() {
        val list = mutableListOf<String>()
        val deserializer = arrayDeserializer(buildArrayDeserializer { s: String -> list.add(s) })
        val s = """[["a","b"],["c","e"]]"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf("a", "b", "c", "e"), list)
    }
}
