package com.motiion.miniwrangler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MiniWranglerTransformer(val dslConfig: DslConfig) : Transformer {

  private val log = LoggerFactory.getLogger(MiniWranglerTransformer::class.java)

  override fun process(fileContents: String): Map<String, String> {
    val allRows = fileContents.split("\\n")
    val columnHeaders = allRows[0].split(",")

    val transformedRows: MutableMap<String, String> = mutableMapOf()
    for (fieldParam in dslConfig.fieldParameters) {
      val fieldIndex = columnHeaders.indexOf(fieldParam.initialField)
      var rowIndex = 1
      while (rowIndex < allRows.size) {
        val row = removeCommasFromQuotedValues(allRows[rowIndex])
        val rowValues = row.split(",")

        try {
          if (fieldParam.fieldType == "INTEGER") rowValues[fieldIndex].toInt()
          if (fieldParam.fieldType == "BIGDECIMAL") rowValues[fieldIndex].toBigDecimal()
        } catch (nfe: NumberFormatException) {
          log.error("Error: Failed to translate row $rowIndex ${fieldParam.initialField} to ${fieldParam.destinationField} as a ${fieldParam.fieldType}")
          rowIndex++
          continue
        }

        if (fieldParam.initialField.contains("$")) {
          var derivedValue = fieldParam.initialField;

          columnHeaders.forEach { header ->
            derivedValue = derivedValue.replace("\${$header}", rowValues[columnHeaders.indexOf(header)])
          }
          transformedRows[fieldParam.destinationField] = derivedValue
          rowIndex++
          continue
        }

        if (fieldParam.initialField.startsWith("default=")) {
          transformedRows[fieldParam.destinationField] = fieldParam.initialField.replace("default=", "")
          rowIndex++
          continue
        }

        transformedRows[fieldParam.destinationField] = rowValues[fieldIndex]
        rowIndex++
      }
    }

    println(transformedRows.toString())

    return transformedRows
  }

  private fun removeCommasFromQuotedValues(rowValues: String): String {
    var formattedRow = rowValues

    while (formattedRow.contains("\"")) {
      val firstCommaIndex = formattedRow.indexOf("\"")
      val secondCommaIndex = formattedRow.indexOf("\"", firstCommaIndex + 1)
      val originalQuotedValue = formattedRow.substring(firstCommaIndex, secondCommaIndex + 1)
      val formattedValue = originalQuotedValue.replace(",","").replace("\"","")
      formattedRow = formattedRow.replace(originalQuotedValue, formattedValue)
    }
    return formattedRow
  }
}
