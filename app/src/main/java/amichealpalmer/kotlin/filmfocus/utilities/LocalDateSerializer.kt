package amichealpalmer.kotlin.filmfocus.utilities

import com.google.gson.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type

// Used to serialize the Joda Time LocalDate class for GSON so we can store it in sharedPrefs. Usage: registerTypeAdapter()
class LocalDateSerializer : JsonDeserializer<LocalDate?>, JsonSerializer<LocalDate?> {
    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type?,
                             jdc: JsonDeserializationContext?): LocalDate? {
        val dateAsString = je.asString
        return if (dateAsString.length == 0) {
            null
        } else {
            DATE_FORMAT.parseLocalDate(dateAsString)
        }
    }

    override fun serialize(src: LocalDate?, typeOfSrc: Type?,
                           context: JsonSerializationContext?): JsonElement {
        val retVal: String
        retVal = if (src == null) {
            ""
        } else {
            DATE_FORMAT.print(src)
        }
        return JsonPrimitive(retVal)
    }

    companion object {
        private val DATE_FORMAT: DateTimeFormatter = ISODateTimeFormat.date()
    }
}