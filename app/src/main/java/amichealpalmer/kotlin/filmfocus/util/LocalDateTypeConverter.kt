package amichealpalmer.kotlin.filmfocus.util


import androidx.room.TypeConverter
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

// Converter for LocalDate so the object can be stored in Room

object LocalDateTypeConverter {
    private val DATE_FORMAT: DateTimeFormatter = ISODateTimeFormat.date()

    @TypeConverter
    @JvmStatic
    fun dateToString(value: LocalDate?): String? {
        return DATE_FORMAT.print(value) ?: null
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return DATE_FORMAT.parseLocalDate(value) ?: null
    }

}