package com.gjscut.waterplantswatcher.model;

/**
 * Created by guojun on 4/19/17.
 */

public class ChlorineAddPool extends Process {
    public float chlorineAmount;

    @Override
    public STATUS valid(String field) {
        STATUS status = super.valid(field);
        switch (field) {
            case "chlorineAmount":
                status = chlorineAmount > 50 ? STATUS.HIGHER : chlorineAmount >= 0 ? STATUS.NORMAL : STATUS.LOWER;
                break;
        }
        return status;
    }
}
