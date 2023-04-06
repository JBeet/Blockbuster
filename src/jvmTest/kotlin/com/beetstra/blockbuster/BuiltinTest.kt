package com.beetstra.blockbuster

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class)
class BuiltinTest {
    @Test
    fun decodeToSequenceWorksWithAList() {
        val s = """[1,2,3,4,5]"""
        val seq: Sequence<Int> = Json.decodeToSequence(ByteArrayInputStream(s.toByteArray()))
        assertEquals(listOf(1, 2, 3, 4, 5), seq.toList())
    }

    @Test
    fun decodeToSequenceFailsWithAnObject() {
        val s = """{"items":[1,2,3,4,5]}"""
        assertFailsWith(SerializationException::class) {
            val seq: Sequence<Int> = Json.decodeToSequence(ByteArrayInputStream(s.toByteArray()))
            seq.toList()
        }
    }
}
