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

int START_PROGRESS_TRANSITION_ID = 31

IssueEvent srcEvent = event
Issue srcIssue = event.issue as Issue

ApplicationUser jiraUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

if (srcIssue.projectObject.key!="NAD") return
if(srcEvent.worklog == null) return

if(!srcIssue.status.name.equals("Open")) return


def transitionValidationResult = ComponentAccessor.issueService.validateTransition(jiraUser, srcIssue.id, START_PROGRESS_TRANSITION_ID,new IssueInputParametersImpl())

if(transitionValidationResult.valid) {
    def transitionResult = ComponentAccessor.issueService.transition(jiraUser, transitionValidationResult)
}
else {
    log.info("Can't do transition for: "+srcIssue.key)
}

