package com.example.clothessuggester.util.extensions

fun List<String>.getRandomExcept(except: String): String {
    return this.filter {
        it != except
    }.random()
}