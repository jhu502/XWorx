package xw.flow.bean;

import xw.flow.entity.XFlowTimer;

public class TimeRobotVO extends FlowNodeVO {
    private Integer years;
    private Integer months;
    private Integer days;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;

    public static TimeRobotVO newInstance(XFlowTimer timer, boolean movable) {
        TimeRobotVO timerBean = new TimeRobotVO(timer);
        timerBean.setMovable(movable);
        timerBean.setYears(timer.getYears());
        timerBean.setMonths(timer.getMonths());
        timerBean.setDays(timer.getDays());
        timerBean.setHours(timer.getHours());
        timerBean.setMinutes(timer.getMinutes());
        timerBean.setSeconds(timer.getSeconds());
        return timerBean;
    }

    public TimeRobotVO() {
    }

    public TimeRobotVO(XFlowTimer timer) {
        super(timer);
    }


    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }
}
