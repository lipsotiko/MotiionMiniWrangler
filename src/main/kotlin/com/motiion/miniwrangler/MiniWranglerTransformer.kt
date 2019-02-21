package com.motiion.miniwrangler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

const val COLUMN_DELIMITER = ","
const val ROW_DELIMITER = "\n"

@Component
class MiniWranglerTransformer(val domainSpecificLanguagelConfig: DomainSpecificLanguagelConfig) : Transformer {

  private val log = LoggerFactory.getLogger(MiniWranglerTransformer::class.java)

  override fun processCsv(fileContents: String): List<String> {
    val allRows = fileContents.split(ROW_DELIMITER)
    val columnHeaders = allRows[0].split(COLUMN_DELIMITER)
    val transformedData: MutableList<String> = mutableListOf()
    val transformedRow: MutableMap<String, String> = mutableMapOf()
    var rowIndex = 1

    while (rowIndex < allRows.size) {
      val row = removeCommasFromQuotedValues(allRows[rowIndex])
      val rowValues = row.split(COLUMN_DELIMITER)
      var fieldConfigIndex = 0
      var finalFieldValue: String

      while (fieldConfigIndex < domainSpecificLanguagelConfig.fieldConfigParameters.size) {
        val initialField = domainSpecificLanguagelConfig.fieldConfigParameters[fieldConfigIndex].initialField
        val destinationField = domainSpecificLanguagelConfig.fieldConfigParameters[fieldConfigIndex].destinationField
        val fieldType = domainSpecificLanguagelConfig.fieldConfigParameters[fieldConfigIndex].fieldType
        val columnIndex = columnHeaders.indexOf(initialField)

        when {
          initialField.startsWith("default=") -> finalFieldValue = initialField.replace("default=", "")
          initialField.contains("$") -> {
            var derivedValue = initialField

            columnHeaders.forEach { header ->
              derivedValue = derivedValue.replace("\${$header}", rowValues[columnHeaders.indexOf(header)])
            }
            finalFieldValue = derivedValue
          }
          else -> finalFieldValue = rowValues[columnIndex]
        }

        if (fieldType == "DATE") finalFieldValue = zeroPadMonthAndDay(finalFieldValue)

        try {
          if (fieldType == "INTEGER") rowValues[columnIndex].toInt()
          if (fieldType == "BIGDECIMAL") rowValues[columnIndex].toBigDecimal()
        } catch (nfe: NumberFormatException) {
          log.error("Error: Failed to translate row $rowIndex $initialField to $destinationField as a $fieldType")
          break
        }

        transformedRow[destinationField] = finalFieldValue
        fieldConfigIndex++
      }

      val rowAsJson = jsonify(transformedRow)
      if (transformedRow.isNotEmpty()) transformedData.add(rowAsJson)
      rowIndex++
    }

    return transformedData
  }

  private fun zeroPadMonthAndDay(unformattedDate: String): String {
    val splitDate = unformattedDate.split("-")
    val year = splitDate[0]
    var month = splitDate[1]
    var day = splitDate[2]

    if (month.length == 1) {
      month = "0$month"
    }

    if (day.length == 1) {
      day = "0$day"
    }

    return "$year-$month-$day"
  }

  private fun jsonify(row: MutableMap<String, String>): String {
    val jsonifiedMap: MutableMap<String, String> = mutableMapOf()

    domainSpecificLanguagelConfig.fieldConfigParameters.forEach { field ->
      val targetKey = "\"" + field.destinationField + "\""
      var targetVal = row[field.destinationField]

      if (field.fieldType == "STRING" || field.fieldType == "DATE") {
        targetVal = "\"" + row[field.destinationField] + "\""
      }

      if (targetVal != null) {
        jsonifiedMap[targetKey] = targetVal
      }
    }

    return jsonifiedMap.toString().replace("=", ":")
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
