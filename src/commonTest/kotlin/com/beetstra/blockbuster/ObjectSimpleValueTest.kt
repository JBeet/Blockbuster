package com.beetstra.blockbuster

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectSimpleValueTest {
    @Test
    fun emptyIsOk() {
        val deserializer = buildDeserializer {}
        val s = "{}"
        Json.decodeFromString(deserializer, s)
    }

    @Test
    fun aSimpleStringFieldIsParsedWhenSpecified() {
        val list = mutableListOf<String>()
        val deserializer = buildDeserializer {
            field("key", list::add)
        }
        val s = """{"key":"value"}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf("value"), list)
    }

    @Test
    fun twoSimpleStringFieldsAreParsedWhenSpecified() {
        val list = mutableListOf<String>()
        val deserializer = buildDeserializer {
            field("key1", list::add)
            field("key2", list::add)
        }
        val s = """{"key2":"valueA","key1":"valueB"}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf("valueA", "valueB"), list)
    }

    @Test
    fun aSimpleIntFieldIsParsedWhenSpecified() {
        val list = mutableListOf<Any>()
        val deserializer = buildDeserializer {
            field<Int>("i", list::add)
            field<String>("s", list::add)
        }
        val s = """{"s":"value1","i":2}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf<Any>("value1", 2), list)
    }

    @Test
    fun aComplexFieldIsParsedWhenSpecified() {
        val list = mutableListOf<TestItem>()
        val deserializer = buildDeserializer {
            field("c", list::add)
        }
        val s = """{"c":{"s":"value1","i":2}}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(TestItem("value1", 2)), list)
    }

    @Test
    fun aFloatField() {
        val list = mutableListOf<Double>()
        val deserializer = buildDeserializer {
            field("f", list::add)
        }
        val s = """{"f":1.5}"""
        Json.decodeFromString(deserializer, s)
        assertEquals(listOf(1.5), list)
    }
}
