package com.motiion.miniwrangler

interface Transformer {
  fun processCsv(fileContents: String): List<String>
}
