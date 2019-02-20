package com.motiion.miniwrangler;

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MiniWranglerTransformerTest {

  private val fieldConfig = DslConfig()
  lateinit var transformer: Transformer
  private var fieldParameters: MutableList<FieldParameter> = mutableListOf()

  @Test
  fun big_decimal_transformation() {
    val sampleCsv = "Maryland\\n9999999.99"
    setFieldParams(FieldParameter("Maryland", "BIGDECIMAL", "BigDecimalCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    assertThat(transformer.process(sampleCsv)["BigDecimalCol"]).isEqualTo("9999999.99")
  }

  @Test
  fun multiple_quoted_big_decimal_transformations_with_commas() {
    val sampleCsv = "Maryland,Ohio\\n\"9,999,999.99\",\"9,999,999.99\""
    setFieldParams(
      FieldParameter("Maryland", "BIGDECIMAL", "MdBigDecimalCol"),
      FieldParameter("Ohio", "BIGDECIMAL", "OhBigDecimalCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    val transformedData = transformer.process(sampleCsv)
    assertThat(transformedData["MdBigDecimalCol"]).isEqualTo("9999999.99")
    assertThat(transformedData["OhBigDecimalCol"]).isEqualTo("9999999.99")
  }

  @Test
  fun attempting_to_transform_a_non_big_decimal_value_will_skip_the_record() {
    val sampleCsv = "Maryland\\n Not a Big Decimal"
    setFieldParams(FieldParameter("Maryland", "BIGDECIMAL", "BigDecimalCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    assertThat(transformer.process(sampleCsv).size).isEqualTo(0)
  }

  @Test
  fun derived_column_is_added_with_default_value_during_transformation() {
    val sampleCsv = "To Be\\n or not to be"
    setFieldParams(FieldParameter("default=MrWhite", "STRING", "DerivedCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    assertThat(transformer.process(sampleCsv)["DerivedCol"]).isEqualTo("MrWhite")
  }

  @Test
  fun string_transformation() {
    val sampleCsv = "Awesome Possum\\ncool string"
    setFieldParams(FieldParameter("Awesome Possum", "STRING", "CoolColumn"))
    transformer = MiniWranglerTransformer(fieldConfig)
    assertThat(transformer.process(sampleCsv)["CoolColumn"]).isEqualTo("cool string")
  }

  @Test
  fun integer_transformation() {
    val sampleCsv = "Superman\\n123123"
    setFieldParams(FieldParameter("Superman", "INTEGER", "NumCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    assertThat(transformer.process(sampleCsv)["NumCol"]).isEqualTo("123123")
  }

  @Test
  fun attempting_to_transform_a_non_integer_value_will_skip_the_record() {
    val sampleCsv = "Superman\\n Not an Integer"
    setFieldParams(FieldParameter("Superman", "INTEGER", "NumCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    assertThat(transformer.process(sampleCsv).size).isEqualTo(0)
  }

  @Test
  fun date_transformation_by_merging_columns_with_an_expression() {
    val sampleCsv = "Year,Month,Day\\n2018,4,28"
    setFieldParams(FieldParameter("\${Year}-\${Month}-\${Day}", "DATE", "DateCol"))
    transformer = MiniWranglerTransformer(fieldConfig)
    println(transformer.process(sampleCsv))
    //assertThat(transformer.process(sampleCsv)["DateCol"]).isEqualTo("2018-4-28")
  }

  private fun setFieldParams(vararg newFieldParams: FieldParameter) {
    fieldParameters.clear()
    for (fieldParam in newFieldParams) fieldParameters.add(fieldParam)
    fieldConfig.fieldParameters = fieldParameters
  }

}
