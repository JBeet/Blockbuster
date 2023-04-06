package com.beetstra.blockbuster

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.serializer

fun buildDeserializer(block: DeserializerBuilder.() -> Unit) =
    DeserializerBuilder().apply { block() }.build()

inline fun <reified T> buildArrayDeserializer(noinline handler: (T) -> Unit): DeserializationStrategy<Unit> =
    buildArrayDeserializer(serializer<T>(), handler)

fun <T> buildArrayDeserializer(deserializer: DeserializationStrategy<T>, handler: (T) -> Unit) =
    arrayDeserializer(SimpleValueHandler(deserializer, handler))

fun arrayDeserializer(itemHandler: DeserializationStrategy<Unit>): DeserializationStrategy<Unit> =
    ArrayValueHandler(itemHandler)

class DeserializerBuilder {
    private val fields = mutableListOf<FieldHandler>()

    fun build(): DeserializationStrategy<Unit> = WrobbleDeserializationStrategy("Wrobble", fields)

    fun <T> field(name: String, deserializer: DeserializationStrategy<T>, handler: (T) -> Unit) =
        addField(name, SimpleValueHandler(deserializer, handler))

    fun <T> arrayField(name: String, deserializer: DeserializationStrategy<T>, handler: (T) -> Unit) =
        addField(name, buildArrayDeserializer(deserializer, handler))

    fun objectField(name: String, block: DeserializerBuilder.() -> Unit) =
        addField(name, buildDeserializer(block))

    private fun addField(name: String, handler: DeserializationStrategy<Unit>) {
        fields.add(FieldHandler(name, handler))
    }
}

inline fun <reified T> DeserializerBuilder.field(name: String, noinline handler: (T) -> Unit) =
    field(name, serializer<T>(), handler)

inline fun <reified T> DeserializerBuilder.arrayField(name: String, noinline handler: (T) -> Unit) =
    arrayField(name, serializer<T>(), handler)

private class WrobbleDeserializationStrategy(name: String, private val fields: List<FieldHandler>) :
    DeserializationStrategy<Unit> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        fields.forEach { it.addDescriptor(this) }
    }

    override fun deserialize(decoder: Decoder): Unit = decoder.decodeStructure(descriptor) {
        while (true) {
            val index = decodeElementIndex(descriptor)
            when {
                index == CompositeDecoder.DECODE_DONE -> break
                0 <= index && index < fields.size -> fields[index].decode(this, descriptor, index)
                else -> error("Unexpected index: $index")
            }
        }
    }
}

private class FieldHandler(private val name: String, private val valueHandler: DeserializationStrategy<Unit>) {
    fun addDescriptor(builder: ClassSerialDescriptorBuilder) =
        builder.element(name, valueHandler.descriptor)

    fun decode(decoder: CompositeDecoder, descriptor: SerialDescriptor, index: Int) =
        decoder.decodeSerializableElement(descriptor, index, valueHandler)
}

private class SimpleValueHandler<T>(
    private val deserializer: DeserializationStrategy<T>,
    private val handler: (T) -> Unit
) : DeserializationStrategy<Unit> {
    override val descriptor: SerialDescriptor = deserializer.descriptor
    override fun deserialize(decoder: Decoder) = handler(deserializer.deserialize(decoder))
}

private class ArrayValueHandler(private val itemHandler: DeserializationStrategy<Unit>) :
    DeserializationStrategy<Unit> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = listSerialDescriptor(itemHandler.descriptor)
    override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        while (true) {
            val index = decodeElementIndex(descriptor)
            if (index == CompositeDecoder.DECODE_DONE)
                break
            decodeSerializableElement(descriptor, index, itemHandler)
        }
    }
}
