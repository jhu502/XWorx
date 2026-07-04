package xw.flow.entity;

import xw.flow.IFlowTimer;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "XFlowTimer", uniqueConstraints = {})
public class XFlowTimer extends XFlowNode implements IFlowTimer {
	private static final long serialVersionUID = 1L;
	@Basic
    @Column(name = "years")
    private Integer years = 0;
    @Basic
    @Column(name = "months")
    private Integer months = 0;
    @Basic
    @Column(name = "days")
    private Integer days = 0;
    @Basic
    @Column(name = "hours")
    private Integer hours = 0;
    @Basic
    @Column(name = "minutes")
    private Integer minutes = 0;
    @Basic
    @Column(name = "seconds")
    private Integer seconds = 0;

    public static XFlowTimer newInstance(String nodeId, XFlowDefinition definition) {
        XFlowTimer flowTimer = new XFlowTimer();
        flowTimer.setXFlowDefinition(definition);
        flowTimer.setNodeId(nodeId);

        return flowTimer;
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
