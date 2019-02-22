package com.motiion.miniwrangler

import com.motiion.transformer.FieldConfigParameter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "motiion.domain-specific-language")
class DomainSpecificLanguageConfig {
  var fieldConfigParameters: MutableList<FieldConfigParameter> = mutableListOf()
}
