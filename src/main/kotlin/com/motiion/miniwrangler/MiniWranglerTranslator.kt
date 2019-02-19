package com.motiion.miniwrangler

import org.springframework.stereotype.Component

@Component
class MiniWranglerTranslator : Translator {
    override fun translate(fileContents: String): String = ""
}
