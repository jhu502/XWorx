package xw.flow.repos;

import com.flame.orm.XObject;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.TaskInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xw.auths.entity.XUser;
import xw.flow.constants.FlowStatus;
import xw.flow.entity.*;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface XFlowRepository extends JpaRepository<XFlowDefinition, Long> {
    @Query(value = "select a from XFlowDefinition a, XFlowDefinitionMaster b where a.master.xid = b.xid and a.latest = true and b.name = :name")
    List<XFlowDefinition> findDefinitionByName(String name);

    @Query(value = "select a from XFlowDefinition a where a.latest = true")
    List<XFlowDefinition> findLatestDefinitions();

    @Query(value = "select a from XFlowEdge a where a.definition.xid = :#{#definition.xid}")
    List<XFlowEdge> findXFlowEdge(XFlowDefinition definition);

    @Query(value = "select a from XFlowEvent a where a.definition.xid = :#{#definition.xid}")
    List<XFlowEvent> findXFlowEvent(XFlowDefinition definition);

    @Query(value = "select a from XFlowGateway a where a.definition.xid = :#{#definition.xid}")
    List<XFlowGateway> findXFlowGateway(XFlowDefinition definition);

    @Query(value = "select a from XFlowUserTask a where a.definition.xid = :#{#definition.xid}")
    List<XFlowUserTask> findXFlowUserTask(XFlowDefinition definition);

    @Query(value = "select a from XFlowScriptTask a where a.definition.xid = :#{#definition.xid}")
    List<XFlowScriptTask> findXFlowScriptTask(XFlowDefinition definition);

    @Query(value = "select a from XFlowServiceTask a where a.definition.xid = :#{#definition.xid}")
    List<XFlowServiceTask> findXFlowServiceTask(XFlowDefinition definition);

    @Query(value = "select a from XFlowTimer a where a.definition.xid = :#{#definition.xid}")
    List<XFlowTimer> findXFlowTimer(XFlowDefinition definition);

    @Query(value = "select a from XFlowThing a where a.definition.xid = :#{#definition.xid}")
    List<XFlowThing> findXFlowThing(XFlowDefinition definition);

    @Query(value = "select a from XFlowEvent a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowEvent> findXFlowEventById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowUserTask a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowUserTask> findXFlowUserTaskById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowScriptTask a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowScriptTask> findXFlowScriptTaskById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowServiceTask a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowServiceTask> findXFlowServiceTaskById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowThing a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowThing> findXFlowThingById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowTimer a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowTimer> findXFlowTimerById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowGateway a where a.definition.xid = :#{#definition.xid} and a.nodeId = :nodeId")
    List<XFlowGateway> findXFlowGatewayById(XFlowDefinition definition, String nodeId);

    @Query(value = "select a from XFlowEdge a where a.definition.xid = :#{#definition.xid} and a.edgeId = :edgeId")
    List<XFlowEdge> findXFlowEdgeById(XFlowDefinition definition, String edgeId);

    @Query(value = "select a from XFlowEdge a where a.source = :source and a.target = :target")
    List<XFlowEdge> findXFlowEdgeById(String source, String target);

    @Query(value = "select a from XWorkInstance a where a.businessRef.className = :#{#pbo.xclass} and a.businessRef.id = :#{#pbo.xid}")
    List<XWorkInstance> findXWorkInstance(XObject pbo);

    @Query(value = "select a from XWorkActivity a where a.instance.xid = :#{#instance.xid}")
    List<XWorkActivity> findXWorkActivity(XWorkInstance instance);

    @Query(value = "select a from XWorkActivity a where a.instance.xid = :#{#instance.xid} and a.actInstId = :#{#historic.id}")
    List<XWorkActivity> findXWorkActivity(XWorkInstance instance, HistoricActivityInstance historic);

    @Query(value = "select a from XWorkActivity a where a.instance.xid = :#{#instance.xid} and a.activityId = :#{#execution.activityId} and a.status = :status")
    List<XWorkActivity> findXWorkActivity(XWorkInstance instance, Execution execution, FlowStatus status);

    @Query(value = "select a from XWorkTimer a where a.instance.xid = :#{#instance.xid} and a.actInstId = :#{#historic.id}")
    List<XWorkTimer> findXWorkTimer(XWorkInstance instance, HistoricActivityInstance historic);

    @Query(value = "select a from XWorkTimer a where a.status = :status and a.timestamp < :time ")
    List<XWorkTimer> findXWorkTimer(FlowStatus status, Timestamp time);

    @Query(value = "select a from XWorkTask a where a.assignee.xid = :#{#user.xid} and a.status = :#{#status}")
    List<XWorkTask> findXWorkTask(XUser user, FlowStatus status);

    @Query(value = "select a from XWorkTask a where a.instance.xid = :#{#instance.xid}")
    List<XWorkTask> findXWorkTask(XWorkInstance instance);

    @Query(value = "select a from XWorkTask a where a.taskId = :#{#taskInfo.id}")
    List<XWorkTask> findXWorkTask(TaskInfo taskInfo);

    @Query(value = "select a from XWorkTask a where a.activity.xid = :#{#activity.xid} and a.status = :status")
    List<XWorkTask> findXWorkTask(XWorkActivity activity, FlowStatus status);
}