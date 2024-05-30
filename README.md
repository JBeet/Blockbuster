# Blockbuster

[![Kotlin](https://img.shields.io/badge/kotlin-2.0.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
![JVM](https://img.shields.io/badge/-JVM-gray.svg?style=flat)
![Android](https://img.shields.io/badge/-Android-gray.svg?style=flat)
![iOS](https://img.shields.io/badge/-iOS-gray.svg?style=flat)
![Windows](https://img.shields.io/badge/-Windows-gray.svg?style=flat)
![Linux](https://img.shields.io/badge/-Linux-gray.svg?style=flat)
![macOS](https://img.shields.io/badge/-macOS-gray.svg?style=flat)
![JS](https://img.shields.io/badge/-JS-gray.svg?style=flat)
![WASM](https://img.shields.io/badge/-WASM-gray.svg?style=flat)

Blockbuster is a multiplatform library to build Kotlin serialization deserializers that use callbacks.
This can be used for example when deserializing large JSON files.

## Features

* Can delegate to standard `@Serializable`/`KSerializer` deserializers. 
* Multiplatform support: JVM, Android, iOS, Javascript and Native.

## Overview

Kotlin serialization does not natively support deserializing huge files.
There is [`Json.decodeToSequence`][decodeToSequence] but that is only useful when
the JSON is just a plain array, and is completely specific to the JVM.

Blockbuster supports deserializing complex structures, where the parts specified are lazily deserialized.

## Usage

    val deserializer: DeserializationStrategy<Unit> = buildDeserializer {
        field("description") { description: Text -> showText(description) }
        objectField("table") {
            arrayField("columnNames") { name: String -> addColumnName(name) }
            arrayField("rows") { row: Row -> processRow(row) }
        }
    }
    Json.decodeFromStream(deserializer, FileInputStream("huge-table.json"))

[decodeToSequence]: https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/decode-to-sequence.html

## License

Blockbuster is released under the terms of the [MIT license](http://en.wikipedia.org/wiki/MIT_License).
