import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexManager
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.ImportUtils
import com.novomatic.jira.scripts.JiraUtils
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.util.json.JSONObject
import javax.ws.rs.POST

Logger log = Logger.getLogger("update: ")
log.setLevel(Level.DEBUG)
//String jql = "issuekey = isa-1"
String jql = "project = isa"

List<MutableIssue> issues = JiraUtils.getIssuesFromQueryByCurrentUser(jql)


Map<String,String> mapa = new HashMap<>()
mapa.put('aaa','aaa@aaaa')
ImportUtils.setIndexIssues(true)
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)

for (MutableIssue issue : issues) {
    String issueReporterID = issue.reporterId.trim()
    if(mapa.containsKey(issueReporterID)) {
        log.info(issue.key)
        ApplicationUser user = ComponentAccessor.userManager.getUserByName(mapa.get(issueReporterID))
        issue.setReporter(user)
        ComponentAccessor.issueManager.updateIssue(ComponentAccessor.jiraAuthenticationContext.loggedInUser,
        issue, EventDispatchOption.DO_NOT_DISPATCH,false)
        issueIndexingService.reIndex(ComponentAccessor.issueManager.getIssueObject(issue.id))
    }
}
ImportUtils.setIndexIssues(false)

return issues.size()
