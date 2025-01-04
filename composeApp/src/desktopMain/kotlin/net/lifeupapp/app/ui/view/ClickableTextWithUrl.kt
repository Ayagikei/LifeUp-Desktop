package net.lifeupapp.app.ui.view

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import java.awt.Desktop
import java.net.URI

@Composable
fun ClickableTextWithUrl(
    text: String,
    style: TextStyle = MaterialTheme.typography.body1,
    urlColor: Color = MaterialTheme.colors.primary.copy(alpha = 0.7f),
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    modifier: Modifier = Modifier
) {
    val urlPattern = Regex("""(\w+://\S+)""")
    val matches = urlPattern.findAll(text)

    if (matches.any()) {
        val annotatedString = buildAnnotatedString {
            var lastIndex = 0
            for (match in matches) {
                // Add non-URL part
                append(text.substring(lastIndex, match.range.first))

                // Add clickable URL
                addStyle(
                    style = SpanStyle(
                        color = urlColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = length,
                    end = length + match.value.length
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = match.value,
                    start = length,
                    end = length + match.value.length
                )
                append(match.value)

                lastIndex = match.range.last + 1
            }

            // Add the last part of the text
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }

        ClickableText(
            text = annotatedString,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            modifier = modifier,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        try {
                            Desktop.getDesktop().browse(URI(annotation.item))
                        } catch (e: Exception) {
                            println("Unable to open link: ${annotation.item}")
                        }
                    }
            }
        )
    } else {
        // If there is no URL, display the text normally
        ClickableText(
            text = buildAnnotatedString { append(text) },
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            modifier = modifier,
            onClick = {}
        )
    }
} 