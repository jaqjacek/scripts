package QM
import com.adaptavist.hapi.jira.issues.Issues
import com.atlassian.jira.issue.Issue
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import groovy.transform.Field
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

/*********************************
 Name: Script Runner REST Endpoint - QsS - RestAPI for ASresman - Request Hardware Revision Informations
 Version 1.4
 Created 2023 by Jacek Tomczak
 Util Class

 Installation Notice -
 example of use for APIKey: https://jira-dev.br-automation.com/rest/scriptrunner/latest/custom/getAutomation?apiKey=12345678910&material=1&version=4

 Requirement:
 => 	FSJO-56
 *********************************/


class ResultASresman {
    public String material;
    public String version;
    public Boolean firmwareChange;
    public Boolean channelDescriptionChanged
    public Boolean configurationDescriptionChanged
    public String titleDE;
    public String titleEN;
    public String detailsDE;
    public String detailsEN;
    public String issueKey;

    public ResultASresman(Issue issue,String materialParam,String versionParam) {
        issueKey=issue.key
        material = materialParam
        version = versionParam
        titleDE = issue.summary;
        titleEN = issue.summary;
        detailsDE = issue.description;
        detailsEN = issue.description;
        firmwareChange =false;
        channelDescriptionChanged = false;
        configurationDescriptionChanged = false;
    }

    public String toJson() {
        return new JsonBuilder(this).toPrettyString()
    }

    @Override
    public String toString() {
        return  toJson()
    }
}