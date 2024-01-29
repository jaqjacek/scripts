package QM
import com.adaptavist.hapi.jira.issues.Issues
import com.atlassian.jira.issue.Issue
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import groovy.transform.Field
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

/*********************************
 Name: Script Runner REST Endpoint - QsS - RestAPI for ASresman - Request Hardware Revision Informations
 Version 1.4
 Created 2023 by Jacek Tomczak
 Updated

 Installation Notice -
 example of use for APIKey: https://jira-dev.br-automation.com/rest/scriptrunner/latest/custom/getAutomation?apiKey=12345678910&material=1&version=4
 or version fixed for params allowed for given groups: https://jira-dev.br-automation.com/rest/scriptrunner/latest/custom/getRevInfo?material=Comp+2&version=Version+Test+1

 Requirement:
 => 	FSJO-56
 *********************************/



@Field String script_api_key="12345678910"
@BaseScript CustomEndpointDelegate delegate
getRevInfo(
        httpMethod: "GET", groups: ["G_Atlassian-Scriptrunner-API-Users","G_Atlassian-Admins","G_Atlassian-Scriptrunner-API-Users","RndPasAll"]
) { MultivaluedMap queryParams, String body ->

    /*
        This api key is sat as a simple authorization for hardware site
        It should be the same for every call. If it suppose to change some day remember to change it for jira and hardware site
     */
//    String apiKey = queryParams.getFirst("apiKey")
//    if(!isApiKeyCorrect(apiKey)) {
//        return Response.ok(getErrorString()).build()
//    }

    /*
        using same error message for all input parameters
     */
    String material = queryParams.getFirst("material")
    String version = queryParams.getFirst("version")
    if( (version==null || version=="")) {
        return Response.ok(getErrorStringVersion()).build()
    }

    if((material == null || material =="")) {
        return Response.ok(getErrorStringMaterial()).build()
    }

    List<ResultASresman> result = getASresManForParams(material,version)

    return Response.ok(result).build()

}

/*
only use for testing connection for hardware
TODO remove for production !!!!!
 */
@BaseScript CustomEndpointDelegate delegate2
getRevInfoApiKey(
) { MultivaluedMap queryParams, String body ->

    /*
        This api key is sat as a simple authorization for hardware site
        It should be the same for every call. If it suppose to change some day remember to change it for jira and hardware site
     */
    String apiKey = queryParams.getFirst("apiKey")
    if(!isApiKeyCorrect(apiKey)) {
        return Response.ok(getErrorString()).build()
    }

    /*
        using same error message for all input parameters
     */
    String material = queryParams.getFirst("material")
    String version = queryParams.getFirst("version")
    if( (version==null || version=="")) {
        return Response.ok(getErrorStringVersion()).build()
    }

    if((material == null || material =="")) {
        return Response.ok(getErrorStringMaterial()).build()
    }
    String rawResponseString=getDummyString(material,version)
    return Response.ok(rawResponseString).build()

}

@BaseScript CustomEndpointDelegate delegate3
getRevInfoTezt(
) { MultivaluedMap queryParams, String body ->

    String version = "Version+Test+1"
    String material = "Comp+2"

    String returnUrl = """https://jira-dev.br-automation.com/rest/scriptrunner/latest/custom/getRevInfo?material=${material}&version=${version}"""


    return Response.ok(returnUrl).build()

}

List<ResultASresman> getASresManForParams(String material, String version) {
    String rawSQL = """component = "__material__" and fixVersion = "__version__"   and category in ("RnD Product Room", "RnD Project Room", "RnD Service Room") """
    rawSQL = rawSQL.replace("__material__",material).replace("__version__",version)
    def issues = Issues.searchOverrideSecurity(rawSQL)
    List<ResultASresman> results = []
    while (issues.hasNext()) {
        results.add(new ResultASresman(issues.next(),material,version))
        break
    }
    return  results
}

String getDummyString(String material,String version) {
    return  """
[
{
    "material":"${material}",
    "version":"${version}",
    "firmwareChanged":false,
    "channelDescriptionChanged":false,
    "configurationDescriptionChanged":false,
    "titleDE":"titleDE",
    "detailsDE":"detailsDE",
    "titleEN":"titleEN",
    "detailsEN":"detailsEN",
    "issueKey":"AAA-4"
},
{
    "material":"${material}",
    "version":"${version}",
    "firmwareChanged":false,
    "channelDescriptionChanged":false,
    "configurationDescriptionChanged":false,
    "titleDE":"titleDE2",
    "detailsDE":"detailsDE2
    line
    break
    normal",
    "titleEN":"titleEN2",
    "detailsEN":"detailsEN2",
    "issueKey":"AAA-5"
},
{
    "material":"${material}",
    "version":"${version}",
    "firmwareChanged":false,
    "channelDescriptionChanged":false,
    "configurationDescriptionChanged":false,
    "titleDE":"titleDE3",
    "detailsDE":"detailsDE3",
    "titleEN":"titleEN3
    line
    break
    \n
    slash n",
    "detailsEN":"detailsEN3",
    "issueKey":"AAA-6"
}
]
"""
}

String getErrorStringVersion() {
    return """
 {
    errorMessage:"Wrong version"  
 }
"""
}

String getErrorStringMaterial() {
    return """
 {
    errorMessage:"Wrong material"  
 }
"""
}

String getErrorString() {
    return """
 {
    errorMessage:"Wrong input parameters"  
 }
"""
}

Boolean isApiKeyCorrect(String apiKeyFromParam) {
    return  apiKeyFromParam == script_api_key
}
