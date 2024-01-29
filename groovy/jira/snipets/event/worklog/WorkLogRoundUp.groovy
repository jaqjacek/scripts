import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.event.worklog.WorklogEvent
import com.atlassian.jira.issue.worklog.Worklog
import com.atlassian.jira.issue.worklog.WorklogImpl

Worklog workLog
if(event instanceof WorklogEvent) workLog = event.worklog
else
if(event instanceof IssueEvent) workLog  = event.getAt("worklog") as Worklog

if(workLog == null) return


//set it to Issue WorkLog Created i WorklogCreatedEvent



Long minutes15 = 15*60
Long minutes30 = 30*60

Boolean isFirstWorklog(Worklog wl) {
    int count = ComponentAccessor.worklogManager.getByIssue(wl.issue).stream().filter{w -> wl.authorKey == w.authorKey}.count()
    return  count <=1
}

Long minutesStep = isFirstWorklog(workLog) ? minutes15 : minutes30

Long leftOver =  workLog.getTimeSpent() % minutesStep
if(leftOver > 0) {
    Long newTime = workLog.getTimeSpent() - leftOver + minutesStep
    Worklog newWorkLog = new WorklogImpl(
        ComponentAccessor.worklogManager,
        workLog.issue,
        workLog.id,
        workLog.authorKey,
        workLog.comment,
        workLog.startDate,
        workLog.groupLevel,
        workLog.roleLevel,
        newTime
    )

    ComponentAccessor.worklogManager.update(workLog.authorObject,newWorkLog,
        newTime,false)
}
