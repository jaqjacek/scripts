import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.history.ChangeItemBean
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Field Logger log = Logger.getLogger("GetChangeLogsForIssue: ")
log.setLevel(Level.DEBUG)

Issue srcIssue = ComponentAccessor.issueManager.getIssueObject("ISA-1")

List<ChangeItemBean> a =
    ComponentAccessor.changeHistoryManager.getChangeItemsForField(srcIssue, "timeoriginalestimate")

for (ChangeItemBean changeItemBean : a) {
    log.debug(changeItemBean.toString())
}

if(a.size() > 0) {
    return Long.parseLong(a[0].to)
}



return null
