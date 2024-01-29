import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkRequestFactory
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.link.RemoteIssueLink
import com.atlassian.jira.issue.link.RemoteIssueLinkBuilder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.net.Request
import com.atlassian.sal.api.net.Request.MethodType
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import com.atlassian.sal.api.net.ResponseHandler
import com.atlassian.sal.api.net.ReturningResponseHandler
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.Field
import org.apache.bcel.generic.ARETURN
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.codehaus.jackson.map.ObjectMapper

@Field Logger log = Logger.getLogger("whatToDO: ")
log.setLevel(Level.DEBUG)


Issue srcIssue = issue as Issue
//ApplicationUser user =event.user

//Issue srcIssue = ComponentAccessor.getIssueManager().getIssueObject("PLMYACP-8")


if(srcIssue == null) return

CustomField randdDocumentationCF = ComponentAccessor.customFieldManager.getCustomFieldObject(19400L)

Boolean createDocumentation = false;
if(randdDocumentationCF != null) {
    createDocumentation = randdDocumentationCF.getValue(srcIssue) != null ? randdDocumentationCF.getValue(srcIssue).toString().contains("Yes"):false;
}

if(!createDocumentation) return "No Docuemtation requierd"


ApplicationUser user = ComponentAccessor.jiraAuthenticationContext.loggedInUser

ApplicationLink getAuthenticatedRequestFactory(String remoteServiceName) {
    ApplicationLink confluenceLink
    def applicationLinkService = ComponentLocator.getComponent(ApplicationLinkService.class)
    for (def l : applicationLinkService.applicationLinks)
    {
        if(l.name.equals(remoteServiceName)) {
            confluenceLink = l
            break
        }
    }
    if(confluenceLink == null) {
        throw new Error("Wrong Remote ServiceName")
    }
    return confluenceLink

}

String linkConfluencePageToIssue(Issue issue,String pageId,String pageTitle,ApplicationUser user) {
    def remoteIssueLinkService = ComponentAccessor.getComponentOfType(RemoteIssueLinkService.class)
    def links = remoteIssueLinkService.getRemoteIssueLinksForIssue(user, issue).remoteIssueLinks
    if (links != null && links.size() > 0) {
        for (def s : links) {
            if (s.title == pageTitle)
                return  "link already exists"
        }
    }
    def linkBuilder = new RemoteIssueLinkBuilder()
    linkBuilder.relationship("Wiki Page")
    linkBuilder.issueId(issue.id)
    linkBuilder.applicationName(confluenceLink.name)
    linkBuilder.applicationType(RemoteIssueLink.APPLICATION_TYPE_CONFLUENCE)
    linkBuilder.title("Wiki Page")
    linkBuilder.globalId("appId=${confluenceLink.id}&pageId=${pageId}")
    linkBuilder.url(confluenceLink.displayUrl.toString()+"/pages/viewpage.action?pageId="+pageId)
    linkBuilder.iconTitle("Page")
    def r = linkBuilder.build()
    def validationResult = remoteIssueLinkService.validateCreate(user, r)
    if(validationResult.valid) {
        remoteIssueLinkService.create(user, validationResult)
    }
}


@Field ApplicationLink confluenceLink =  getAuthenticatedRequestFactory("Central Confluence")
@Field ApplicationLinkRequestFactory requestFactory = confluenceLink.createAuthenticatedRequestFactory()

String url = "https://central-confluence.novomatic-tech.com/rest/scriptrunner/latest/custom/createPageFromTitle"
//log.info(srcIssue.status.name)

String requestBody = """{"issueSummary":"${srcIssue.summary}","issueType":"${srcIssue.issueType}","issueProject":"${srcIssue.projectObject.name}"}"""

Response response
def request  = requestFactory
    .createRequest(MethodType.POST, url)
    .addHeader("Content-Type", "application/json")


    request.setRequestBody(requestBody)

response = request.executeAndReturn(new ReturningResponseHandler() {

    Response handle(Response response1) throws ResponseException {
        return response1
    }
}) as Response

def mapper = new ObjectMapper()
def params = mapper.readValue(response.responseBodyAsString, Map)
assert  params.pageID


linkConfluencePageToIssue(srcIssue,params.pageID,params.pageTitle,ComponentAccessor.jiraAuthenticationContext.loggedInUser)
linkConfluencePageToIssue(srcIssue,params.factsheetID,"Factsheet",ComponentAccessor.jiraAuthenticationContext.loggedInUser)

