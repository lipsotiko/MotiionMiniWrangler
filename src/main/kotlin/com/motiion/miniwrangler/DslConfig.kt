package com.motiion.miniwrangler

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "motiion.domain-specific-language")
class DslConfig {
  var fieldConfigParameters: MutableList<FieldConfigParameter> = mutableListOf()
}

class FieldConfigParameter(var initialField: String = "",
                           var fieldType: String = "",
                           var destinationField: String = "")
