package com.gjscut.waterplantswatcher.model;

/**
 * Created by guojun on 4/19/17.
 */

public class Pump {
    public int order;
    public float frequency;
    public float head;
    public float flow;

    public Process.STATUS valid(String field) {
        Process.STATUS status = Process.STATUS.INVALID;
        switch (field) {
            case "frequency":
                status = frequency > 90 ? Process.STATUS.HIGHER : frequency >= 40 ? Process.STATUS.NORMAL : Process.STATUS.LOWER;
                break;
            case "head":
                status = head > 7 ? Process.STATUS.HIGHER : head >= 3 ? Process.STATUS.NORMAL : Process.STATUS.LOWER;
                break;
            case "flow":
                status = flow > 4000 ? Process.STATUS.HIGHER : flow >= 0 ? Process.STATUS.NORMAL : Process.STATUS.LOWER;
                break;
        }
        return status;
    }
}
