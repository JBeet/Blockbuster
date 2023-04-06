# Blockbuster

Blockbuster is a helper library for Kotlin serialization to build deserializers that use callbacks.
This can be used for example when deserializing large JSON files.

* Can delegate to standard `@Serializable` (de)serializers. 
* Complete multiplatform support: JVM, JS and Native.

## Overview

Kotlin serialization does not natively support deserializing huge files.
There is the (badly documented) [`Json.decodeToSequence`][decodeToSequence] but that is only useful when
the JSON is just a plain array, and is completely specific to the JVM.

Blockbuster supports deserializing complex structures, where the parts specified are lazily deserialized.

## Example

    val deserializer: DeserializationStrategy<Unit> = buildDeserializer {
        field("description") { description: Text -> showText(description) }
        objectField("table") {
            arrayField("columnNames") { name: String -> addColumnName(name) }
            arrayField("rows") { row: Row -> processRow(row) }
        }
    }
    Json.decodeFromStream(deserializer, FileInputStream("huge-table.json"))

[decodeToSequence]: https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/decode-to-sequence.html
