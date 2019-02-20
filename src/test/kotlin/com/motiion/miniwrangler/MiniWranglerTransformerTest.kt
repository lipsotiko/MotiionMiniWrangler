package com.motiion.miniwrangler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MiniWranglerTransformerTest {

  private val fieldConfig = DslConfig()
  private var fieldParameters: MutableList<FieldParameter> = mutableListOf()

  @Test
  fun string_transformation() {
    val sampleCsv = "Awesome Possum\\ncool string"
    setFieldParams(FieldParameter("Awesome Possum", "STRING", "CoolColumn"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{CoolColumn=cool string}")
  }

  @Test
  fun integer_transformation() {
    val sampleCsv = "Superman\\n123123"
    setFieldParams(FieldParameter("Superman", "INTEGER", "NumCol"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{NumCol=123123}")
  }

  @Test
  fun big_decimal_transformation() {
    val sampleCsv = "Maryland\\n9999999.99\\n9999999.99"
    setFieldParams(FieldParameter("Maryland", "BIGDECIMAL", "BigDecimalCol"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{BigDecimalCol=9999999.99}")
    assertThat(transformedData[1]).isEqualTo("{BigDecimalCol=9999999.99}")
  }

  @Test
  fun multiple_quoted_big_decimal_transformations_with_commas() {
    val sampleCsv = "Maryland,Ohio\\n\"9,999,999.99\",\"9,999,999.99\"\\n\"9,999,999.99\",\"9,999,999.99\""
    setFieldParams(
      FieldParameter("Maryland", "BIGDECIMAL", "MdBigDecimalCol"),
      FieldParameter("Ohio", "BIGDECIMAL", "OhBigDecimalCol"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{MdBigDecimalCol=9999999.99, OhBigDecimalCol=9999999.99}")
    assertThat(transformedData[1]).isEqualTo("{MdBigDecimalCol=9999999.99, OhBigDecimalCol=9999999.99}")
  }

  @Test
  fun derived_column_is_added_with_default_value_during_transformation() {
    val sampleCsv = "To Be\\n or not to be"
    setFieldParams(FieldParameter("default=MrWhite", "STRING", "DerivedCol"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{DerivedCol=MrWhite}")
  }

  @Test
  fun date_transformation_by_merging_columns_with_an_expression() {
    val sampleCsv = "Year,Month,Day\\n2018,4,28"
    setFieldParams(FieldParameter("\${Year}-\${Month}-\${Day}", "DATE", "DateCol"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{DateCol=2018-4-28}")
  }

  @Test
  fun attempting_to_transform_a_non_big_decimal_value_will_skip_the_record() {
    val sampleCsv = "Maryland\\n Not a Big Decimal\\n Not a Big Decimal"
    setFieldParams(FieldParameter("Maryland", "BIGDECIMAL", "BigDecimalCol"))
    assertThat(MiniWranglerTransformer(fieldConfig).process(sampleCsv).size).isEqualTo(0)
  }

  @Test
  fun attempting_to_transform_a_non_integer_value_will_skip_the_record() {
    val sampleCsv = "Superman\\n Not an Integer"
    setFieldParams(FieldParameter("Superman", "INTEGER", "NumCol"))
    assertThat(MiniWranglerTransformer(fieldConfig).process(sampleCsv).size).isEqualTo(0)
  }

  @Test
  fun records_with_errors_are_omitted_while_records_with_no_errors_are_kept() {
    val sampleCsv = "Maryland,Ohio,Delaware\\n Not a Big Decimal,George,Anna\\n9999999,Steve,John"
    setFieldParams(
      FieldParameter("Maryland", "BIGDECIMAL", "SomeBadData"),
      FieldParameter("Ohio", "STRING", "GoodData"),
      FieldParameter("Delaware", "STRING", "ReallyGoodData"))
    val transformedData = MiniWranglerTransformer(fieldConfig).process(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{SomeBadData=9999999, GoodData=Steve, ReallyGoodData=John}")
  }

  private fun setFieldParams(vararg newFieldParams: FieldParameter) {
    for (fieldParam in newFieldParams) fieldParameters.add(fieldParam)
    fieldConfig.fieldParameters = fieldParameters
  }

}
