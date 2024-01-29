import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Field Logger log = Logger.getLogger("MoveStoryToInProgress: ")
log.setLevel(Level.DEBUG)

int START_PROGRESS_TRANSITION_ID = 4

IssueEvent srcEvent = event
//log.info(srcEvent.toString())
Issue issue = event.issue as Issue
//log.info('issueKey: '+issue.key)
//log.info('issue status '+issue.status.name)

ApplicationUser jiraUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

MutableIssue parentIssue = ComponentAccessor.issueManager.getIssueObject(issue.parentId)

if(parentIssue==null) {
    return
}
//log.info(parentIssue)
//log.info(parentIssue.status.name)
//log.info(parentIssue.issueType.getName())
//log.info(issue.status.name)
if(parentIssue != null && parentIssue.status.name.equals("Open") && parentIssue.issueType.getName().equals("Story") && (issue.status.name.equals("In Progress") || issue.status.name.equals("In Review")) ) {
//Actions to do StartProgress: 4
    def transitionValidationResult = ComponentAccessor.issueService.validateTransition(jiraUser, parentIssue.id, START_PROGRESS_TRANSITION_ID,new IssueInputParametersImpl())
//    log.info(transitionValidationResult.isValid().toString())
    if(transitionValidationResult.valid) {
        def transitionResult = ComponentAccessor.issueService.transition(jiraUser, transitionValidationResult)
    }
    else {
        log.info("Can't do transition for: "+parentIssue.key)
    }

}

//ComponentAccessor.getComponent(EventPublisher.class).publish(ClearCacheEvent.INSTANCE)
