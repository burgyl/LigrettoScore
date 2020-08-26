package ch.lburgy.ligrettoscore.type_converter;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(long dateLong) {
        return dateLong == -1 ? null : new Date(dateLong);
    }

    @TypeConverter
    public static long fromDate(Date date) {
        return date == null ? -1 : date.getTime();
    }
}
