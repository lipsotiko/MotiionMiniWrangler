package com.motiion.miniwrangler

interface Translator {
    fun translate(fileContents: String): String
}
