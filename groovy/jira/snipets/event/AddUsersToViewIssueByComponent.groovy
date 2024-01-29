import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.user.ApplicationUser
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Field Logger log = Logger.getLogger("AddUsersToViewIssueByComponent: ")
log.setLevel(Level.DEBUG)

MutableIssue srcIssue = issue
//Issue srcIssue = ComponentAccessor.issueManager.getIssueObject("JCPS-18")

ApplicationUser jiraUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
CustomField viewIssueCF = ComponentAccessor.customFieldManager.getCustomFieldObject(10150L)


List<ConfigurationObject> allConfiguration = new ArrayList<>()

ConfigurationObject caConfig= new ConfigurationObject("CA","Purchase",["pmglej","mgrudzien", "kduda" ])
ConfigurationObject caConfig2= new ConfigurationObject("JCPS","Purchase",["pmglej","mgrudzien", "kduda"])
allConfiguration.add(caConfig)
allConfiguration.add(caConfig2)

List<ProjectComponent> currentComponents = srcIssue.getComponents() as ArrayList
List<ApplicationUser> usersToAdd = new ArrayList<>()
for (ConfigurationObject config : allConfiguration) {
    if(srcIssue.getProjectObject().key.equals(config.projectKey)) {
        def currentViewIssues = srcIssue.getCustomFieldValue(viewIssueCF) as ArrayList
        currentViewIssues = currentViewIssues == null ? new ArrayList<ApplicationUser>() : currentViewIssues
        for (ProjectComponent component : currentComponents) {
            if(component.getName().equals(config.component)) {
                for (String user : config.users) {
                    if(currentViewIssues.indexOf(user) == -1) {
                        usersToAdd.add(ComponentAccessor.userManager.getUserByKey(user))
                    }
                }

            }
        }
        if(usersToAdd.size() > 0) {
            log.info("adding Users to viewIssue "+srcIssue.key+" : "+usersToAdd)
            usersToAdd.addAll(currentViewIssues)
            srcIssue.setCustomFieldValue(viewIssueCF,usersToAdd)
            usersToAdd = new ArrayList<>()
            ComponentAccessor.issueManager.updateIssue(jiraUser, srcIssue, EventDispatchOption.DO_NOT_DISPATCH, false)
        }
    }
}


class ConfigurationObject {
    public String projectKey
    public String component
    public List<String> users

    public ConfigurationObject(String pk, String cp, List<String> usr) {
        this.projectKey = pk
        this.component = cp
        this.users = usr
    }

}