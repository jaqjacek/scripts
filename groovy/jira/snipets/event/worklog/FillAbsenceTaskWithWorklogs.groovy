import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.worklog.Worklog
import com.atlassian.jira.issue.worklog.WorklogImpl
import com.atlassian.jira.issue.worklog.WorklogImpl2
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.util.ImportUtils
import com.atlassian.jira.web.bean.PagerFilter
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

import com.mroczek.jira.plugins.financialreport.service.workload.TempoWorkloadService
import com.mroczek.jira.plugins.financialreport.service.workload.WorkingDaysService

@Field Logger log = Logger.getLogger("ConfluencePageWithBugList: ")
log.setLevel(Level.DEBUG)

@WithPlugin("com.mroczek.jira.plugin.financial-report")
@PluginModule
TempoWorkloadService tempoWorkloadService

@PluginModule
WorkingDaysService workingDaysService




String jqlQuery = "project = Ab and status = Approved and \"Absence Type\" in (L4,Urlop,\"Urlop na żądanie\") and \"Start Date\" > 2022-12-31"
//String jqlQuery = "project = AB AND  issuekey  = AB-300"
log.debug(jqlQuery)

CustomField startDateCF = ComponentAccessor.customFieldManager.getCustomFieldObject(12003L)
CustomField endDateCF = ComponentAccessor.customFieldManager.getCustomFieldObject(12004L)

List<MutableIssue> issues= getIssuesFromQuery(jqlQuery,ComponentAccessor.jiraAuthenticationContext.loggedInUser)
String dateString = "yyyy-MM-dd"
SimpleDateFormat sdf = new SimpleDateFormat(dateString)
CustomField absentTimeCF = ComponentAccessor.customFieldManager.getCustomFieldObject(12300L)
for (MutableIssue issue1 : issues) {
    Long startDate = startDateCF.getValue(issue1).time
    Long endDate = endDateCF.getValue(issue1).time
    LocalDateTime startLDT =  LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), TimeZone
        .getDefault().toZoneId());

    LocalDateTime endLDT =  LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), TimeZone
        .getDefault().toZoneId());
    List<LocalDate> days =  workingDaysService.getWorkingDays(startLDT.toLocalDate(),endLDT.toLocalDate())
    List< Worklog> worklogs = ComponentAccessor.worklogManager.getByIssue(issue1)
    Long totalTimeForAbsence=0L;
    def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
    for (LocalDate day : days) {
        Boolean worklogExist = false

        String dayString = sdf.format(day.toDate())
        Long seconds = tempoWorkloadService.getWorkloadForUser(issue1.reporter.username,day)
        for (Worklog worklog1 : worklogs) {
            for (Worklog worklog : worklogs) {
                String worklogDateString = sdf.format(worklog.startDate)
                if (worklogDateString == dayString) {
                    worklogExist = true
                    break;
                }
            }
            log.debug("${worklog1.authorKey},${worklog1.author},${worklog1.groupLevel} ${worklog1.roleLevel}")
        }
        totalTimeForAbsence+=seconds;
        continue;
        if(worklogExist) {
            continue
        }
        //Long getWorkloadForUser(String username,LocalDate date);

        Long hours = seconds/3600
        log.debug("${issue1.reporter} ${day.toString()} ${hours}")
        if(seconds == 0) {
            log.debug("no Scheme for ${issue1.reporter.displayName}")
            continue
        }
        addWorklogToIssue(issue1,day.toDate(),seconds)
        fixTimeSpent(issue1)

        ImportUtils.setIndexIssues(true)
        issueIndexingService.reIndex(issue1)
        ImportUtils.setIndexIssues(false)

    }

    log.debug(issue1.key)
    totalTimeForAbsence = totalTimeForAbsence/3600
    log.debug(totalTimeForAbsence)
    issue1.setCustomFieldValue(absentTimeCF,totalTimeForAbsence.doubleValue())
    ComponentAccessor.issueManager.updateIssue(issue1.reporter, issue1, EventDispatchOption.ISSUE_UPDATED, false)
    ImportUtils.setIndexIssues(true)
    issueIndexingService.reIndex(issue1)
    ImportUtils.setIndexIssues(false)

}

List<MutableIssue> getIssuesFromQuery(String jqlSearch, ApplicationUser jiraUser) {
    SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
    IssueManager issueManager = ComponentAccessor.getIssueManager()
    SearchService.ParseResult parseResult = searchService.parseQuery(jiraUser,jqlSearch)

    def searchResult = searchService.search(jiraUser, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
    List<MutableIssue> issues = searchResult.results.collect {issueManager.getIssueObject(it.id)}
    return  issues
}


def addWorklogToIssue(Issue issue,Date date, Long seconds) {
    Worklog worklog = new WorklogImpl2(
        issue,
        null,
        ApplicationUsers.getKeyFor(issue.reporter),
        "Absence Worklog",
        date,
        null,
        null,
        seconds,
        null,
    )

    ComponentAccessor.worklogManager.create(issue.reporter,worklog,issue.originalEstimate,true)
}


def fixTimeSpent(Issue issue) {
    Long timeFromWorklogs =0L
    for (Worklog worklog : ComponentAccessor.worklogManager.getByIssue(issue)) {
        timeFromWorklogs+=worklog.timeSpent
    }
    if(timeFromWorklogs != issue.timeSpent) {
        MutableIssue mi = ComponentAccessor.issueManager.getIssueObject(issue.key)
        mi.setTimeSpent(timeFromWorklogs)
        mi.store()
    }
}


