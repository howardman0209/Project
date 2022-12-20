package com.howard.project.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Patterns
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


@Suppress("unused")
fun String.base64StringToBitmap(): Bitmap? {
    val pureBase64String = this.split(",").last() //remove "data:image/jpg;base64," in the front
    val decode: ByteArray = Base64.decode(pureBase64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decode, 0, decode.size)
}

/**
 * Email pattern checking
 */
fun String.isEmailValid(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * @return error res if username not valid; or null if valid
 */
fun String?.usernameError(): String? = if (!isNullOrBlank())
    null
else
    "Invalid username"

/**
 * Phone number checking
 */
fun String.isMobileNumValid(region: String = "HK"): Boolean {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val possibleNum = phoneUtil.parse(this, region)
        phoneUtil.isValidNumber(possibleNum)
    } catch (e: NumberParseException) {
        false
    }
}

fun String.formatPhoneNumber(region: String = "HK"): String? {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return if (this.isMobileNumValid(region)) {
        phoneUtil.format(phoneUtil.parse(this, region), INTERNATIONAL)
    } else
        null
}

/**
 * Check password with policy
 */
fun String.isPasswordValid(): Boolean {
    if (this.isEmpty() || this.length < ValidPasswordLength) {
        return false
    }

    val expressionCharacter = ".*[a-zA-Z].*"
    val expressionNumber = ".*[0-9].*"
    val patternCharacter = Pattern.compile(expressionCharacter, Pattern.CASE_INSENSITIVE)
    val matcherCharacter = patternCharacter.matcher(this)

    val patternNumber = Pattern.compile(expressionNumber, Pattern.CASE_INSENSITIVE)
    val matcherNumber = patternNumber.matcher(this)

    if (!matcherCharacter.matches() || !matcherNumber.matches()) {
        return false
    }

    return true
}

fun String.toDateFormat(inputFormat: String, displayFormat: String): String {
    val format = SimpleDateFormat(inputFormat, Locale.ENGLISH)
    val sdfDisplayFormat = SimpleDateFormat(displayFormat, Locale.ENGLISH)
    return try {
        val date = format.parse(this)
        if (date == null)
            this
        else
            sdfDisplayFormat.format(date)
    } catch (e: ParseException) {
        this
    }
}

fun String.toServerBase64String(): String {
    return "data:image/jpeg;base64,$this"
}

/**
 * Truncate string if too long and ended with ellipsis (…)
 */
fun String.truncateAndEllipsis(maxLength: Int = 15): String {
    return trim().run {
        if (length <= maxLength) this else "${take(maxLength)}…"
    }
}

@Suppress("unused")
fun Char.isAlphaNumeric(): Boolean {
    val regex = Regex("[a-zA-Z0-9]+?")
    return regex.matches(this.toString())
}

fun String.insert(insert: String, index: Int): String {
    val start = substring(0, index)
    val end = substring(index)
    return start + insert + end
}

fun String.hexToByteArray(): ByteArray {
    //Add leading zero in case of odd len
    val str = if (this.length and 1 == 1) {
        "0$this"
    } else
        this
    return str.trim().chunked(2).map { it.toInt(16).toByte() }.toByteArray()

}
