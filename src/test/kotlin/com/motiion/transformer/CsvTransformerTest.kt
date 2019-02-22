package com.motiion.transformer

import com.motiion.miniwrangler.getSampleDataFromResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CsvTransformerTest {

  private var fieldConfigParameters: MutableList<FieldConfigParameter> = mutableListOf()

  @Test
  fun string_transformation() {
    val sampleCsv = "Awesome Possum\ncool string"
    setFieldParams(FieldConfigParameter("Awesome Possum", "STRING", "CoolColumn"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"CoolColumn\":\"cool string\"}")
  }

  @Test
  fun integer_transformation() {
    val sampleCsv = "Superman\n123123"
    setFieldParams(FieldConfigParameter("Superman", "INTEGER", "NumCol"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"NumCol\":123123}")
  }

  @Test
  fun big_decimal_transformation() {
    val sampleCsv = "Maryland\n9999999.99\n\"5,250.50\""
    setFieldParams(FieldConfigParameter("Maryland", "BIGDECIMAL", "BigDecimalCol"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"BigDecimalCol\":9999999.99}")
    assertThat(transformedData[1]).isEqualTo("{\"BigDecimalCol\":5250.50}")
  }

  @Test
  fun transforms_multiple_quoted_big_decimal_fields_with_commas() {
    val sampleCsv = "Maryland,Ohio\n\"9,999,999.99\",\"9,999,999.99\"\n\"9,999,999.99\",\"9,999,999.99\""
    setFieldParams(
      FieldConfigParameter("Maryland", "BIGDECIMAL", "MdBigDecimalCol"),
      FieldConfigParameter("Ohio", "BIGDECIMAL", "OhBigDecimalCol"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"MdBigDecimalCol\":9999999.99, \"OhBigDecimalCol\":9999999.99}")
    assertThat(transformedData[1]).isEqualTo("{\"MdBigDecimalCol\":9999999.99, \"OhBigDecimalCol\":9999999.99}")
  }

  @Test
  fun default_column_and_value_is_added_during_transformation() {
    val sampleCsv = "To Be\n or not to be"
    setFieldParams(FieldConfigParameter("default=MrWhite", "STRING", "DerivedCol"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"DerivedCol\":\"MrWhite\"}")
  }

  @Test
  fun derived_column_as_date_transformation_utilizing_an_expression() {
    val sampleCsv = "Year,Month,Day\n2018,4,28"
    setFieldParams(FieldConfigParameter("\${Year}-\${Month}-\${Day}", "DATE", "DateCol"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"DateCol\":\"2018-04-28\"}")
  }

  @Test
  fun attempting_to_transform_a_non_big_decimal_value_will_skip_the_record() {
    val sampleCsv = "Maryland\n Not a Big Decimal\n Not a Big Decimal"
    setFieldParams(FieldConfigParameter("Maryland", "BIGDECIMAL", "BigDecimalCol"))
    assertThat(CsvTransformer(fieldConfigParameters).transform(sampleCsv).size).isEqualTo(0)
  }

  @Test
  fun attempting_to_transform_a_non_integer_value_will_skip_the_record() {
    val sampleCsv = "Superman\n Not an Integer"
    setFieldParams(FieldConfigParameter("Superman", "INTEGER", "NumCol"))
    assertThat(CsvTransformer(fieldConfigParameters).transform(sampleCsv).size).isEqualTo(0)
  }

  @Test
  fun records_with_errors_are_omitted_while_records_with_no_errors_are_returned() {
    val sampleCsv = "Maryland,Ohio,Delaware\n Not a Big Decimal,George,Anna\n9999999,Steve,John"
    setFieldParams(
      FieldConfigParameter("Maryland", "BIGDECIMAL", "SomeBadData"),
      FieldConfigParameter("Ohio", "STRING", "GoodData"),
      FieldConfigParameter("Delaware", "STRING", "ReallyGoodData"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0])
      .isEqualTo("{\"SomeBadData\":9999999, \"GoodData\":\"Steve\", \"ReallyGoodData\":\"John\"}")
  }

  @Test
  fun sample_orders_csv() {
    val sampleCsv = getSampleDataFromResource("fixtures", "orders.csv")
    setFieldParams(
      FieldConfigParameter("Order Number", "INTEGER", "orderId"),
      FieldConfigParameter("\${Year}-\${Month}-\${Day}", "DATE", "orderDate"),
      FieldConfigParameter("Product Number", "STRING", "productId"),
      FieldConfigParameter("Product Name", "STRING", "productName"),
      FieldConfigParameter("Count", "BIGDECIMAL", "quantity"),
      FieldConfigParameter("default=kg", "STRING", "unit"))
    val transformedData = CsvTransformer(fieldConfigParameters).transform(sampleCsv)
    assertThat(transformedData[0]).isEqualTo("{\"orderId\":1000, \"orderDate\":\"2018-01-01\", \"productId\":\"P-10001\", \"productName\":\"Arugola\", \"quantity\":5250.50, \"unit\":\"kg\"}")
    assertThat(transformedData[1]).isEqualTo("{\"orderId\":1001, \"orderDate\":\"2017-12-12\", \"productId\":\"P-10002\", \"productName\":\"Iceberg lettuce\", \"quantity\":500.00, \"unit\":\"kg\"}")
  }

  private fun setFieldParams(vararg newFieldConfigParams: FieldConfigParameter) {
    for (fieldParam in newFieldConfigParams) fieldConfigParameters.add(fieldParam)
  }

}
