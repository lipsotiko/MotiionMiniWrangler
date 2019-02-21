package com.motiion.miniwrangler

import org.apache.commons.io.FileUtils
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Throws(IOException::class)
fun getSampleDataFromResource(path: String, file: String): String =
  FileUtils.readFileToString(ClassPathResource("$path/$file").file, "UTF-8")

