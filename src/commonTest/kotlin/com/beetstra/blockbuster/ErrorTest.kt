package com.beetstra.blockbuster

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.fail

class ErrorTest {
    private val json = Json {
        ignoreUnknownKeys = false
    }

    @Test
    fun anUnknownFieldGivesAnException() {
        val deserializer = buildDeserializer {
            arrayField("i") { i: Int -> fail("found $i") }
        }
        val s = """{"s":[1,2,3,5]}"""
        assertFailsWith(SerializationException::class) {
            json.decodeFromString(deserializer, s)
        }
    }
}
