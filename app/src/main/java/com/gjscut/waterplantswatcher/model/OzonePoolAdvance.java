package com.gjscut.waterplantswatcher.model;

/**
 * Created by guojun on 4/19/17.
 */

public class OzonePoolAdvance extends Process {
    public float zoneAmount;

    @Override
    public STATUS valid(String field) {
        STATUS status = super.valid(field);
        switch (field) {
            case "ozoneAmount":
                status = zoneAmount > 0.7 ? STATUS.HIGHER : zoneAmount >= 0 ? STATUS.NORMAL : STATUS.LOWER;
                break;
        }
        return status;
    }
}
