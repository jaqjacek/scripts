import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.user.ApplicationUser
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger
@Field Logger log = Logger.getLogger("CopyValuesToSubTasks: ")
log.setLevel(Level.DEBUG)
Issue srcIssue = issue
//Issue srcIssue = ComponentAccessor.issueManager.getIssueObject("NAD-874")
List<String> allowedProjectsKeys=["SMCPUA","SMCPI","GODO","DEAD2"]
if(!allowedProjectsKeys.contains(srcIssue.projectObject.key)) return

if(srcIssue.subTaskObjects.size() == 0) {

    Issue parentIssue = srcIssue.parentObject
    copyValuseFromParentToSubTask(parentIssue,srcIssue as MutableIssue)
    return
}

void copyValuseFromParentToSubTask(Issue srcIssue,MutableIssue subTask) {
    List<Long> customfieldsID=[25390L]
    for (Long cfID : customfieldsID) {
        CustomField cf = ComponentAccessor.customFieldManager.getCustomFieldObject(cfID)
        if(srcIssue.getCustomFieldValue(cf) == null && subTask.getCustomFieldValue(cf) == null ) continue
        if(srcIssue.getCustomFieldValue(cf) == null &&  subTask.getCustomFieldValue(cf) != null) {
            subTask.setCustomFieldValue(cf,null)
            ComponentAccessor.issueManager.updateIssue(ComponentAccessor.jiraAuthenticationContext.loggedInUser
                ,subTask
                , EventDispatchOption.ISSUE_UPDATED
                ,false
            )
            continue
        }
        String subValue =  subTask.getCustomFieldValue(cf) == null ? "ąśćż" :  subTask.getCustomFieldValue(cf).toString()
        if(srcIssue.getCustomFieldValue(cf).toString() != subValue) {
            subTask.setCustomFieldValue(cf,srcIssue.getCustomFieldValue(cf))
            ComponentAccessor.issueManager.updateIssue(ComponentAccessor.jiraAuthenticationContext.loggedInUser
                ,subTask
                , EventDispatchOption.ISSUE_UPDATED
                ,false
            )
        }
    }
}

//if issue is parrent issue and event is updated
if(srcIssue.subTaskObjects.size() > 0) {
    for (Issue subTask : srcIssue.subTaskObjects) {
        MutableIssue mSubTask = ComponentAccessor.issueManager.getIssueObject(subTask.key)
        copyValuseFromParentToSubTask(srcIssue,mSubTask)
    }
}

