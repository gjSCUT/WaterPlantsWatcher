package com.gjscut.waterplantswatcher.model;


public class Process {
    public float phIn;
    public float waterTemperIn;
    public float turbidityIn;
    public float amlN2In;
    public float codIn;
    public float tocIn;
    public float flowIn;
    public float phOut;
    public float waterTemperOut;
    public float turbidityOut;
    public float amlN2Out;
    public float codOut;
    public float tocOut;
    public float flowOut;
    public String createTime;
    public String updateTIme;

    public STATUS valid(String field) {
        STATUS status = STATUS.INVALID;
        switch (field) {
            case "phIn":
                status = phIn > 8 ? STATUS.HIGHER : phIn >= 6 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "waterTemperIn":
                status = waterTemperIn > 30 ? STATUS.HIGHER : waterTemperIn >= -10 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "turbidityIn":
                status = turbidityIn > 150 ? STATUS.HIGHER : turbidityIn >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "amlN2In":
                status = amlN2In > 20 ? STATUS.HIGHER : amlN2In >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "codIn":
                status = codIn > 50 ? STATUS.HIGHER : codIn >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "tocIn":
                status = tocIn > 30 ? STATUS.HIGHER : tocIn >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "flowIn":
                status = flowIn > 12000 ? STATUS.HIGHER : flowIn >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "phOut":
                status = phOut > 8 ? STATUS.HIGHER : phOut >= 6 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "waterTemperOut":
                status = waterTemperOut > 30 ? STATUS.HIGHER : waterTemperOut >= -10 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "turbidityOut":
                status = turbidityOut > 150 ? STATUS.HIGHER : turbidityOut >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "amlN2Out":
                status = amlN2Out > 20 ? STATUS.HIGHER : amlN2Out >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "codOut":
                status = codOut > 50 ? STATUS.HIGHER : codOut >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "tocOut":
                status = tocOut > 30 ? STATUS.HIGHER : tocOut >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
            case "flowOut":
                status = flowOut > 12000 ? STATUS.HIGHER : flowOut >= 0 ? STATUS.NORMAL : STATUS.LOWER;
            break;
        }
        return status;
    }

    public enum STATUS {
        NORMAL,
        LOWER,
        HIGHER,
        INVALID
    }
}
