package co.ec.helper.utils

import android.text.Html
import android.text.Spanned
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import co.ec.helper.CnsynApp


fun html(html: String): AnnotatedString {
    val spanned: Spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)

    return buildAnnotatedString {
        append(spanned.toString())

        spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)

            when (span) {
                is android.text.style.StyleSpan -> {
                    when (span.style) {
                        android.graphics.Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                        android.graphics.Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    }
                }
                is android.text.style.ForegroundColorSpan -> {
                    addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
                }
                is android.text.style.URLSpan -> {
                    addStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline), start, end)
                    addStringAnnotation(tag = "URL", annotation = span.url, start = start, end = end)
                }
            }
        }
    }
}

fun lang(name: String, defText: String? = null): String {
    CnsynApp.contextCheck()?.let {
        val resId: Int = it.resources.getIdentifier(name, "string", it.packageName)
        return if (resId != 0) {
            it.getString(resId)
        } else {
            defText ?: name
        }
    }
    return defText ?: name
}
