package com.howard.project.extension

fun ByteArray.toHexString() = joinToString("") { String.format("%02x", it) }
