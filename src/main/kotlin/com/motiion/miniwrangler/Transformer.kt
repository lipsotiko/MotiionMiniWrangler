package com.motiion.miniwrangler

interface Transformer {
  fun process(fileContents: String): Map<String, String>
}
