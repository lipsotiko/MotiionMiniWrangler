package com.motiion.miniwrangler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MiniWranglerTransformer(val dslConfig: DslConfig) : Transformer {

  private val log = LoggerFactory.getLogger(MiniWranglerTransformer::class.java)

  override fun processCsv(fileContents: String): List<String> {
    val allRows = fileContents.split("\n")

    val transformedData: MutableList<String> = mutableListOf()

    val transformedRow: MutableMap<String, String> = mutableMapOf()
    var rowIndex = 1
    while (rowIndex < allRows.size) {
      val row = removeCommasFromQuotedValues(allRows[rowIndex])
      val rowValues = row.split(",")

      var fieldIndex = 0

      while (fieldIndex < dslConfig.fieldConfigParameters.size) {
        val initialField = dslConfig.fieldConfigParameters[fieldIndex].initialField
        val destinationField = dslConfig.fieldConfigParameters[fieldIndex].destinationField
        val fieldType = dslConfig.fieldConfigParameters[fieldIndex].fieldType

        try {
          if (fieldType == "INTEGER") rowValues[fieldIndex].toInt()
          if (fieldType == "BIGDECIMAL") rowValues[fieldIndex].toBigDecimal()
        } catch (nfe: NumberFormatException) {
          log.error("Error: Failed to translate row $rowIndex $initialField to $destinationField as a $fieldType")
          break
        }

        if (initialField.startsWith("default=")) {
          transformedRow[destinationField] = initialField.replace("default=", "")
          fieldIndex++
          continue
        }

        if (initialField.contains("$")) {
          var derivedValue = initialField
          val columnHeaders = allRows[0].split(",")
          columnHeaders.forEach { header ->
            derivedValue = derivedValue.replace("\${$header}", rowValues[columnHeaders.indexOf(header)])
          }
          transformedRow[destinationField] = derivedValue
          fieldIndex++
          continue
        }

        transformedRow[destinationField] = rowValues[fieldIndex]
        fieldIndex++
      }

      if(transformedRow.isNotEmpty()) transformedData.add(transformedRow.toString())
      rowIndex++
    }

    return transformedData
  }

  private fun removeCommasFromQuotedValues(rowValues: String): String {
    var formattedRow = rowValues

    while (formattedRow.contains("\"")) {
      val firstCommaIndex = formattedRow.indexOf("\"")
      val secondCommaIndex = formattedRow.indexOf("\"", firstCommaIndex + 1)
      val originalQuotedValue = formattedRow.substring(firstCommaIndex, secondCommaIndex + 1)
      val formattedValue = originalQuotedValue.replace(",", "").replace("\"", "")
      formattedRow = formattedRow.replace(originalQuotedValue, formattedValue)
    }
    return formattedRow
  }
}
