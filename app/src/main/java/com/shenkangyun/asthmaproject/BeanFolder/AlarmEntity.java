package com.shenkangyun.asthmaproject.BeanFolder;

public class AlarmEntity {

    private int alarmID;
    private int flag;
    private int week;
    private int tips;
    private int hourNum;
    private int minNum;
    private boolean enabled;
    private int soundOrVibrator;

    public int getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getTips() {
        return tips;
    }

    public void setTips(int tips) {
        this.tips = tips;
    }

    public int getHourNum() {
        return hourNum;
    }

    public void setHourNum(int hourNum) {
        this.hourNum = hourNum;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSoundOrVibrator() {
        return soundOrVibrator;
    }

    public void setSoundOrVibrator(int soundOrVibrator) {
        this.soundOrVibrator = soundOrVibrator;
    }
}
