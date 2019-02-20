package com.motiion.miniwrangler

import org.springframework.core.io.ClassPathResource
import java.io.IOException
import org.apache.commons.io.FileUtils

@Throws(IOException::class)
fun getSampleDataFromResource(path: String, file: String): String =
  FileUtils.readFileToString(ClassPathResource("$path/$file").file, "UTF-8")

