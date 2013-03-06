package com.BibleQuote.utils;

import android.os.Build;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 01.03.13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */

/**
 * Идея взята из исходников CoolReader, но инициализация MANUFACTURER и MODEL сделана иначе.
 * */
public class DeviceInfo {
    public final static String MANUFACTURER;
    public final static String MODEL;

    static {
        MANUFACTURER = Build.MANUFACTURER;
        MODEL = Build.MODEL;
    }

    public static boolean isEInkSonyPRST() {
        return MANUFACTURER.toLowerCase().contentEquals("sony") && MODEL.startsWith("PRS-T");
    }
}
