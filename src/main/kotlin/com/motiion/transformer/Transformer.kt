package com.motiion.transformer

interface Transformer {
  fun transform(fileContents: String): List<String>
}
