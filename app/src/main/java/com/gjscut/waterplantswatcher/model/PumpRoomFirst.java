package com.gjscut.waterplantswatcher.model;

/**
 * Created by guojun on 4/19/17.
 */

public class PumpRoomFirst extends Process {
    public Pump[] pumps;
    public static String getType() {
        return PumpRoomFirst.class.toString();
    }
}
