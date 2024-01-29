import com.atlassian.jira.issue.MutableIssue

import com.atlassian.jira.issue.index.IssueIndexingService

import com.atlassian.jira.util.ImportUtils
import com.novomatic.jira.scripts.JiraUtils
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil

Logger log = Logger.getLogger("update: ")
log.setLevel(Level.DEBUG)

//CustomField extidCF = ComponentAccessor.customFieldManager.getCustomFieldObject(11700L)
//if(extidCF == null) return "missing custom Field"

String projectKey="GGG"
String jql = "project = GGG AND status in (Created) "
List<MutableIssue> issues = JiraUtils.getIssuesFromQueryByCurrentUser(jql)
for (MutableIssue issue : issues) {
    log.debug(issue.status.id)
    issue.setStatusId("1")
    issue.store()

}



