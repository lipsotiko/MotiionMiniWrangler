package com.motiion.miniwrangler

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "motiion.domain-specific-language")
class DslConfig {
  var fieldParameters: MutableList<FieldParameter> = mutableListOf()
}

class FieldParameter(var initialField: String = "",
                     var fieldType: String,
                     var destinationField: String)
