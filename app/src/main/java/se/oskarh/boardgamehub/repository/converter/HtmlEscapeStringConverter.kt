package se.oskarh.boardgamehub.repository.converter

import androidx.core.text.parseAsHtml
import com.tickaroo.tikxml.TypeConverter

class HtmlEscapeStringConverter : TypeConverter<String> {

    @Throws(Exception::class)
    override fun read(input: String): String =
        input.replace("&amp;#10;", "<br />")
            .replace("&amp;", "&")
            .parseAsHtml()
            .toString()
            .trim()

    @Throws(Exception::class)
    override fun write(output: String): String = output
}