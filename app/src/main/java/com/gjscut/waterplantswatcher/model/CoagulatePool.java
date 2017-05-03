package com.gjscut.waterplantswatcher.model;

/**
 * Created by guojun on 4/19/17.
 */

public class CoagulatePool extends Process {
    public float alumAmount;

    @Override
    public STATUS valid(String field) {
        STATUS status = super.valid(field);
        switch (field) {
            case "alumAmount":
                status = alumAmount > 50 ? STATUS.HIGHER : alumAmount >= 0 ? STATUS.NORMAL : STATUS.LOWER;
                break;
        }
        return status;
    }
}
