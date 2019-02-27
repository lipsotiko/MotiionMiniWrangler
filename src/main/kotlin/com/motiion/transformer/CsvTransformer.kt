package com.motiion.transformer

import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeParseException

const val HEADER_INDEX = 0
const val DATA_START_INDEX = 1

class CsvTransformer(private val fieldConfigParameters: MutableList<FieldConfigParameter>,
                     private val columnDelimiter: String = ",",
                     private val rowDelimiter: String = "\n") : Transformer {

  private val log = LoggerFactory.getLogger(CsvTransformer::class.java)

  private val transformedData: MutableList<String> = mutableListOf()
  private val transformedRow: MutableMap<String, String> = mutableMapOf()

  override fun transform(fileContents: String): List<String> {
    val allRows = fileContents.split(rowDelimiter)
    val columnHeaders = allRows[HEADER_INDEX].split(columnDelimiter)
    var rowIndex = DATA_START_INDEX

    while (rowIndex < allRows.size) {
      val row = removeCommasFromQuotedValues(allRows[rowIndex])
      val rowValues = row.split(columnDelimiter)
      var fieldConfigIndex = 0
      var finalFieldValue: String

      while (fieldConfigIndex < fieldConfigParameters.size) {
        val initialField = fieldConfigParameters[fieldConfigIndex].initialField
        val destinationField = fieldConfigParameters[fieldConfigIndex].destinationField
        val fieldType = fieldConfigParameters[fieldConfigIndex].fieldType
        val columnIndex = columnHeaders.indexOf(initialField)

        when {
          initialField.startsWith("default=") ->
            finalFieldValue = initialField.replace("default=", "")
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

        if (invalidFieldType(fieldType, finalFieldValue, rowIndex, initialField, destinationField)) break

        transformedRow[destinationField] = finalFieldValue
        fieldConfigIndex++
      }

      val rowAsJson = jsonify(transformedRow)
      if (transformedRow.isNotEmpty()) transformedData.add(rowAsJson)
      rowIndex++
    }

    return transformedData
  }

  private fun invalidFieldType(fieldType: String, finalFieldValue: String, rowIndex: Int, initialField: String, destinationField: String): Boolean {
    try {
      if (fieldType == "INTEGER") finalFieldValue.toInt()
      if (fieldType == "BIGDECIMAL") finalFieldValue.toBigDecimal()
      if (fieldType == "DATE") LocalDate.parse(finalFieldValue)
    } catch (nfe: NumberFormatException) {
      logError(rowIndex, initialField, destinationField, fieldType)
      return true
    } catch (e: DateTimeParseException) {
      logError(rowIndex, initialField, destinationField, fieldType)
      return true
    }
    return false
  }

  private fun zeroPadMonthAndDay(unformattedDate: String): String {
    if (!unformattedDate.contains("-")) return unformattedDate
    val splitDate = unformattedDate.split("-")
    val year = splitDate[0]
    var month = splitDate[1]
    var day = splitDate[2]

    if (month.length == 1) month = "0$month"
    if (day.length == 1) day = "0$day"

    return "$year-$month-$day"
  }

  private fun jsonify(rowValues: MutableMap<String, String>): String {
    val jsonifiedMap: MutableMap<String, String> = mutableMapOf()

    fieldConfigParameters.forEach { field ->
      val targetKey = "\"" + field.destinationField + "\""
      var targetVal = rowValues[field.destinationField]

      if (field.fieldType == "STRING" || field.fieldType == "DATE") {
        targetVal = "\"" + rowValues[field.destinationField] + "\""
      }

      if (targetVal != null) jsonifiedMap[targetKey] = targetVal
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

  private fun logError(rowIndex: Int, initialField: String, destinationField: String, fieldType: String) {
    log.error("Error: Failed to translate row $rowIndex $initialField to $destinationField as a $fieldType")
  }
}
