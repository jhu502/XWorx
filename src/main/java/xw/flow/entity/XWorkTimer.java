package xw.flow.entity;

import org.flowable.engine.history.HistoricActivityInstance;

import jakarta.persistence.*;

import java.io.Serial;
import java.sql.Timestamp;

@Entity
@Table(name = "XWorkTimer")
public class XWorkTimer extends XExecutionObject {
	@Serial
    private static final long serialVersionUID = 1L;
    @Basic
    @Column(name = "timestamp")
    private Timestamp timestamp;

    public static XWorkTimer newInstance(XWorkInstance workInstance, HistoricActivityInstance history) {
        XWorkTimer timer = new XWorkTimer();
        timer.setInstance(workInstance);
        timer.setActInstId(history.getId());
        timer.setActivityId(history.getActivityId());
        timer.setExecutionId(history.getExecutionId());

        return timer;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
