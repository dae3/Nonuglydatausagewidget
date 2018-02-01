package com.example.dever.nonUglyDataUsageWidget;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by daniel.everett on 1/02/2018.
 */

public class GregorianCalendarDefaultLocale extends GregorianCalendar {

    public GregorianCalendarDefaultLocale() {
        super(Locale.getDefault());
    }

    public GregorianCalendarDefaultLocale(Locale aLocale) {
        super(aLocale);
    }

    public GregorianCalendarDefaultLocale(TimeZone zone, Locale aLocale) {
        super(zone, aLocale);
    }

    public GregorianCalendarDefaultLocale(int year, int month, int dayOfMonth) {
        this(year, month, dayOfMonth, 0, 0);
    }

    public GregorianCalendarDefaultLocale(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this(year, month, dayOfMonth, hourOfDay, minute, 0);
    }

    public GregorianCalendarDefaultLocale(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        super(Locale.getDefault());
        set(Calendar.YEAR, year);
        set(Calendar.MONTH, month);
        set(Calendar.DAY_OF_MONTH, dayOfMonth);
        set(Calendar.HOUR_OF_DAY, hourOfDay);
        set(Calendar.MINUTE, minute);
        set(Calendar.SECOND, second);

        complete();
    }

}
