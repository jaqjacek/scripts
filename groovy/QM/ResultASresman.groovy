package QM


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import groovy.json.JsonBuilder

/*********************************
 Name: Script Runner REST Endpoint - QsS - RestAPI for ASresman - Request Hardware Revision Informations
 Version 1.5
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
    public String shortTextDE;
    public String shortTextEN;
    public String longTextDE;
    public String longTextEN;
    public String issueKey;
    private String shortTextGERCF = "customfield_15944"
    private String longTextGERCF = "customfield_15945"
    private String shortTextENCF = "customfield_15946"
    private String longTextENCF = "customfield_15947"
    private String hardwareUpgradeCF = "customfield_15961"

//    Firmware changed
//    Channel description changed
//    Configuration description changed

    public ResultASresman(Issue issue,String materialParam,String versionParam) {
        issueKey=issue.key
        material = materialParam
        version = versionParam
        firmwareChange =false;
        channelDescriptionChanged = false;
        configurationDescriptionChanged = false;

        CustomField cf = ComponentAccessor.customFieldManager.getCustomFieldObject(hardwareUpgradeCF)
        String hardwareText = cf.getValue(issue) != null ? cf.getValue(issue) : ""


        if(hardwareText.contains("Firmware changed")) firmwareChange = true
        if(hardwareText.contains("Channel description changed")) channelDescriptionChanged = true
        if(hardwareText.contains("Configuration description changed")) configurationDescriptionChanged = true


        cf = ComponentAccessor.customFieldManager.getCustomFieldObject(shortTextGERCF)
        shortTextDE = cf.getValue(issue) != null ? cf.getValue(issue) : ""

        cf = ComponentAccessor.customFieldManager.getCustomFieldObject(longTextGERCF)
        longTextDE = cf.getValue(issue) != null ? cf.getValue(issue) : ""

        cf = ComponentAccessor.customFieldManager.getCustomFieldObject(shortTextENCF)
        shortTextEN = cf.getValue(issue) != null ? cf.getValue(issue) : ""

        cf = ComponentAccessor.customFieldManager.getCustomFieldObject(longTextENCF)
        longTextEN = cf.getValue(issue) != null ? cf.getValue(issue) : ""

    }

    public String toJson() {
        return new JsonBuilder(this).toPrettyString()
    }

    @Override
    public String toString() {
        return  toJson()
    }
}