package com.motiion.miniwrangler;

import com.motiion.transformer.CsvTransformer
import com.motiion.transformer.Transformer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MiniWranglerConfig(val domainSpecificLanguageConfig: DomainSpecificLanguageConfig) {

  @Bean
  fun getTransformer(): Transformer {
    return CsvTransformer(domainSpecificLanguageConfig.fieldConfigParameters)
  }
}
