//NAD-282
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.worklog.Worklog
import com.atlassian.jira.mail.Email
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.mail.queue.SingleMailQueueItem
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

Map<Integer,List<Integer>> holidays = new HashMap<>()

holidays.put(1,[1,6])   //Styczeń
holidays.put(2,[])      //Luty
holidays.put(3,[])      //Marzec
holidays.put(4,[])      //Kwiecień
holidays.put(5,[1,3])   //maj
holidays.put(6,[11])    //Czerwiec
holidays.put(7,[])      //Lipiec
holidays.put(8,[])      //Sierpień
holidays.put(9,[])      //Wrzesień
holidays.put(10,[])     //Październik
holidays.put(11,[])     //Listopad
holidays.put(12,[25,26])//Grudzień

@Field Logger log = Logger.getLogger("ConfluencePageWithBugList: ")
log.setLevel(Level.DEBUG)
@Field ApplicationUser jiraUser = ComponentAccessor.userManager.getUserByKey("admin_jt")

String groupName = "NTP-PAM"

List<ApplicationUser> employes = ComponentAccessor.groupManager.getUsersInGroup(groupName)

LocalDateTime currentDate = LocalDateTime.now()//.minusMonths(4)
currentDate = currentDate.minusHours(currentDate.hour).minusMinutes(currentDate.minute).minusSeconds(currentDate.second)

LocalDateTime startDate = currentDate.minusDays(currentDate.dayOfMonth).plusHours(23)

DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
String startDateString = startDate.format(dtf)
String currentDateString = currentDate.format(dtf)

Date startDateDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant())
Date currentDateDate = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant())

String jqlString = "worklogDate >= ${startDateString} and worklogDate < ${currentDateString} and worklogAuthor="

//fill set wit

Set<Integer>  weekendsDays = []

LocalDateTime weekendsDate = startDate.plusDays(1)
while (weekendsDate.isBefore(currentDate)) {
    if(weekendsDate.dayOfWeek == DayOfWeek.SATURDAY || weekendsDate.dayOfWeek == DayOfWeek.SUNDAY ) {
        weekendsDays.add(weekendsDate.dayOfMonth)
    }
    weekendsDate = weekendsDate.plusDays(1)
}


for (ApplicationUser singleUser : employes) {
    if(!singleUser.active) continue
    log.info(singleUser.key)
   List<Issue> issues = getIssuesFromQuery(jqlString+singleUser.key,jiraUser)
    Set<Integer> daysWorked = new HashSet<>()
    List<Worklog> worklogs = []
    //getAllWorklogsForIssue
    for (Issue issue : issues) {
       List<Worklog> toCheck = ComponentAccessor.worklogManager.getByIssue(issue)
       toCheck =  toCheck.stream().filter{ w-> w.startDate.after(startDateDate) && w.startDate.before(currentDateDate)}
           .filter{w-> w.authorKey == singleUser.key}
           .collect(Collectors.toList())
        worklogs.addAll(toCheck)
    }
    for (Worklog worklog : worklogs) {
        LocalDateTime d =  worklog.startDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        daysWorked.add(d.dayOfMonth)
    }
    daysWorked.addAll(weekendsDays)
    daysWorked.addAll(holidays.get(currentDate.monthValue))
    List<Integer> unloggedDays= getUnnloggedDays(currentDate,daysWorked)
    if(!unloggedDays.isEmpty()) {

        log.info("unloged days for ${singleUser.key}: ${unloggedDays}")
       return sentNotofication(singleUser.emailAddress,singleUser.displayName,"jtomczak@novomatic-tech.com",unloggedDays,currentDate)
    }
}

List<Integer> getUnnloggedDays(LocalDateTime date,Set<Integer> workedDays) {
    List<Integer> unworkedDays = []
    for (int i=1;i<date.month.length(date.year%4==0);i++) {
        if(i >= date.dayOfMonth) break
        if(!workedDays.contains(i)) {
            unworkedDays.add(i)
        }
    }
    return unworkedDays
}



//utils functions--------------------------------------------------------

public static List<MutableIssue> getIssuesFromQuery(String jqlSearch, ApplicationUser jiraUser) {
    SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
    IssueManager issueManager = ComponentAccessor.getIssueManager()
    SearchService.ParseResult parseResult = searchService.parseQuery(jiraUser,jqlSearch)

    def searchResult = searchService.search(jiraUser, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
    List<MutableIssue> issues = searchResult.results.collect {issueManager.getIssueObject(it.id)}
    return  issues
}

public void sentNotofication(String recipientEmail, String recipientDisplayName, String cc, List<Integer> nonLoggedDays,LocalDateTime currentDate) {

    String dayString = workDaysToString(nonLoggedDays,currentDate)
    Email email = new Email(recipientEmail)
    email.setMimeType("text/html");
    if (cc !="")
        email.setCc(cc);
    email.setSubject("Test UnLogged Days notification");
//css style for email
    String    emailBody = "Missing worklogs for days: <br/>"
//body for email
    emailBody += """Szanowny ${recipientDisplayName} <br/>
    w ciągu tego miesiąc nie msz worklogów na dzień/dni:<br/>
    ${dayString}
    <br/> proszę o pilne uregulowanie sprawy inaczej...
"""

    email.setBody(emailBody)
    SingleMailQueueItem singleMailQueueItem = new SingleMailQueueItem(email);
    ComponentAccessor.getMailQueue().addItem(singleMailQueueItem)

}

String workDaysToString(List<Integer> nonLoggedDays,LocalDateTime currentDate ) {
    String resultString = ""
    String daysString = currentDate.getYear().toString()+"-"+currentDate.getMonthValue().toString()+"-"
    for (int day  : nonLoggedDays) {
        resultString+=daysString+day.toString()+"<br/>"
    }
    return resultString
}