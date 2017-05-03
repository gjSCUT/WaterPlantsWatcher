package com.gjscut.waterplantswatcher.model;

/**
 * Created by guojun on 4/19/17.
 */

public class PumpRoomOut extends Process {
    public Pump[] pumps;

    public STATUS valid(String field, int order) {
        STATUS status = this.valid(field);
        if (field.startsWith("pump")) {
            status = pumps[order].valid(field.substring(4));
        }
        return status;
    }
}
