import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService

import com.atlassian.jira.util.ImportUtils
import com.novomatic.jira.scripts.JiraUtils
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil

Logger log = Logger.getLogger("update: ")
log.setLevel(Level.DEBUG)

MutableIssue srcIssue = ComponentAccessor.issueManager.getIssueObject("COMZ-1")
MutableIssue dstIssue = ComponentAccessor.issueManager.getIssueObject("COMZ-2")

log.debug(srcIssue.securityLevel)
log.debug(dstIssue.securityLevel)

dstIssue.setSecurityLevelId(srcIssue.securityLevelId)
ComponentAccessor.issueManager.updateIssue(ComponentAccessor.jiraAuthenticationContext.loggedInUser, dstIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
