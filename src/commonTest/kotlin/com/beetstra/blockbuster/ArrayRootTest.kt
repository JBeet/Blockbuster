package com.beetstra.blockbuster

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayRootTest {
    @Test
    fun aListOfIntsIsParsedIncrementally() {
        val list = mutableListOf<Int>()
        val deserializer = buildArrayDeserializer(list::add)
        val s = """[1,2,3,5]"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(1, 2, 3, 5), list)
    }

    @Test
    fun aListOfComplexItemsIsParsedIncrementally() {
        val list = mutableListOf<TestItem>()
        val deserializer = buildArrayDeserializer(list::add)
        val s = """[{"s":"a","i":1},{"s":"b","i":2}]"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(TestItem("a", 1), TestItem("b", 2)), list)
    }
}
