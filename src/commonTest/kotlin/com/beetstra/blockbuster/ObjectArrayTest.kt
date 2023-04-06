package com.beetstra.blockbuster

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectArrayTest {
    @Test
    fun aListOfIntsIsParsedDirectlyWhenSpecified() {
        val list = mutableListOf<Int>()
        val deserializer = buildDeserializer {
            field("i", list::addAll)
        }
        val s = """{"i":[1,2,3,5]}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(1, 2, 3, 5), list)
    }

    @Test
    fun aListOfIntsIsParsedIncrementallyWhenSpecified() {
        val list = mutableListOf<Int>()
        val deserializer = buildDeserializer {
            arrayField("i", list::add)
        }
        val s = """{"i":[1,2,3,5]}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(1, 2, 3, 5), list)
    }

    @Test
    fun aListOfStringsIsParsedIncrementallyWhenSpecified() {
        val list = mutableListOf<String>()
        val deserializer = buildDeserializer {
            arrayField("s", list::add)
        }
        val s = """{"s":["a","b","c","e"]}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf("a", "b", "c", "e"), list)
    }

    @Test
    fun aListOfSerializablesIsParsedIncrementallyWhenSpecified() {
        val list = mutableListOf<TestItem>()
        val deserializer = buildDeserializer {
            arrayField("c", list::add)
        }
        val s = """{"c":[{"s":"a","i":1},{"s":"b","i":2}]}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(TestItem("a", 1), TestItem("b", 2)), list)
    }
}
