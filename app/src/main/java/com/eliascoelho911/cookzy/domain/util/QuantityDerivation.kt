package com.eliascoelho911.cookzy.domain.util

import java.math.BigDecimal
import java.math.RoundingMode

data class QuantityDerivationResult(
    val value: BigDecimal,
    val range: IntRange
)

private val allowedFractionDenominators = setOf(2, 3, 4, 8)
private val unicodeFractionComponents = mapOf(
    '½' to (1 to 2),
    '⅓' to (1 to 3),
    '⅔' to (2 to 3),
    '¼' to (1 to 4),
    '¾' to (3 to 4),
    '⅛' to (1 to 8)
)

private val rangePattern = Regex("\\d+\\s*[-–—]\\s*\\d+")
private val rangeWordPattern = Regex("\\d+\\s+a\\s+\\d+", RegexOption.IGNORE_CASE)
private val multiplierPattern = Regex("\\d+\\s*[x×]\\s*\\d+", RegexOption.IGNORE_CASE)
private val listPattern = Regex("\\d+,\\s+\\d+")
private val danglingCommaPattern = Regex("\\d+,\\s*(?!\\d)")
private val danglingDotPattern = Regex("\\d+\\.\\s*(?!\\d)")

private val mixedUnicodeNoSpacePattern = Regex("\\b(\\d+)([½¼¾⅓⅔⅛])")
private val mixedUnicodePattern = Regex("\\b(\\d+)\\s+([½¼¾⅓⅔⅛])")
private val mixedAsciiPattern = Regex("\\b(\\d+)\\s+(\\d+)/(\\d+)\\b")
private val unicodeFractionPattern = Regex("([½¼¾⅓⅔⅛])")
private val asciiFractionPattern = Regex("\\b(\\d+)/(\\d+)\\b")
private val decimalCommaPattern = Regex("\\b\\d+,\\d+\\b")
private val decimalDotPattern = Regex("\\b\\d+\\.\\d+\\b")
private val integerPattern = Regex("\\b\\d+\\b")

/**
 * Derives a decimal quantity from a free-form ingredient description.
 * Returns null when the text is ambiguous or no supported numeric pattern is found.
 */
fun deriveQuantity(rawText: String): QuantityDerivationResult? {
    if (rawText.isBlank()) return null

    val text = rawText

    val sanitizedForAmbiguity = rawText.trim().lowercase()
    if (rangePattern.containsMatchIn(sanitizedForAmbiguity)) return null
    if (rangeWordPattern.containsMatchIn(sanitizedForAmbiguity)) return null
    if (multiplierPattern.containsMatchIn(sanitizedForAmbiguity)) return null
    if (listPattern.containsMatchIn(sanitizedForAmbiguity)) return null
    if (danglingCommaPattern.containsMatchIn(sanitizedForAmbiguity)) return null
    if (danglingDotPattern.containsMatchIn(sanitizedForAmbiguity)) return null

    return detectMixedUnicodeNoSpace(text)
        ?: detectMixedUnicode(text)
        ?: detectMixedAscii(text)
        ?: detectUnicodeFraction(text)
        ?: detectAsciiFraction(text)
        ?: detectDecimalComma(text)
        ?: detectDecimalDot(text)
        ?: detectInteger(text)
}

private fun detectMixedUnicodeNoSpace(text: String): QuantityDerivationResult? {
    val match = mixedUnicodeNoSpacePattern.find(text) ?: return null
    val integer = BigDecimal(match.groupValues[1])
    val char = match.groupValues[2].first()
    val fraction = fractionValueForUnicode(char) ?: return null
    return QuantityDerivationResult(
        value = integer + fraction,
        range = match.range
    )
}

private fun detectMixedUnicode(text: String): QuantityDerivationResult? {
    val match = mixedUnicodePattern.find(text) ?: return null
    val integer = BigDecimal(match.groupValues[1])
    val char = match.groupValues[2].first()
    val fraction = fractionValueForUnicode(char) ?: return null
    return QuantityDerivationResult(
        value = integer + fraction,
        range = match.range
    )
}

private fun detectMixedAscii(text: String): QuantityDerivationResult? {
    val match = mixedAsciiPattern.find(text) ?: return null
    val denominator = match.groupValues[3].toIntOrNull() ?: return null
    if (denominator !in allowedFractionDenominators || denominator == 0) return null
    val integer = BigDecimal(match.groupValues[1])
    val numerator = match.groupValues[2].toIntOrNull() ?: return null
    val fraction = BigDecimal.valueOf(numerator.toLong()).divide(
        BigDecimal.valueOf(denominator.toLong()),
        3,
        RoundingMode.DOWN
    )
    return QuantityDerivationResult(
        value = (integer + fraction).normalizeScale(),
        range = match.range
    )
}

private fun detectUnicodeFraction(text: String): QuantityDerivationResult? {
    val match = unicodeFractionPattern.find(text) ?: return null
    val char = match.groupValues[1].first()
    val fraction = fractionValueForUnicode(char) ?: return null
    return QuantityDerivationResult(
        value = fraction,
        range = match.range
    )
}

private fun detectAsciiFraction(text: String): QuantityDerivationResult? {
    val matches = asciiFractionPattern.findAll(text)
    val match = matches.firstOrNull { candidate ->
        val denominator = candidate.groupValues[2].toIntOrNull() ?: return@firstOrNull false
        denominator in allowedFractionDenominators && denominator != 0
    } ?: return null

    val numerator = match.groupValues[1].toIntOrNull() ?: return null
    val denominator = match.groupValues[2].toIntOrNull() ?: return null

    val fraction = BigDecimal.valueOf(numerator.toLong()).divide(
        BigDecimal.valueOf(denominator.toLong()),
        3,
        RoundingMode.DOWN
    )
    return QuantityDerivationResult(
        value = fraction.normalizeScale(),
        range = match.range
    )
}

private fun detectDecimalComma(text: String): QuantityDerivationResult? {
    val match = decimalCommaPattern.find(text) ?: return null
    val normalized = match.value.replace(',', '.')
    val value = normalized.toBigDecimalOrNull() ?: return null
    return QuantityDerivationResult(
        value = value.normalizeScale(),
        range = match.range
    )
}

private fun detectDecimalDot(text: String): QuantityDerivationResult? {
    val match = decimalDotPattern.find(text) ?: return null
    val value = match.value.toBigDecimalOrNull() ?: return null
    return QuantityDerivationResult(
        value = value.normalizeScale(),
        range = match.range
    )
}

private fun detectInteger(text: String): QuantityDerivationResult? {
    val match = integerPattern.find(text) ?: return null
    val value = match.value.toBigDecimalOrNull() ?: return null
    return QuantityDerivationResult(
        value = value.normalizeScale(),
        range = match.range
    )
}

private fun fractionValueForUnicode(char: Char): BigDecimal? {
    val (numerator, denominator) = unicodeFractionComponents[char] ?: return null
    return BigDecimal.valueOf(numerator.toLong()).divide(
        BigDecimal.valueOf(denominator.toLong()),
        3,
        RoundingMode.DOWN
    ).normalizeScale()
}

private fun BigDecimal.normalizeScale(): BigDecimal {
    val stripped = stripTrailingZeros()
    return if (stripped.scale() < 0) stripped.setScale(0) else stripped
}
