import com.atlassian.jira.auditing.AuditingFilter
import com.atlassian.jira.auditing.AuditingManager
import com.atlassian.jira.component.ComponentAccessor
import java.text.SimpleDateFormat
import org.apache.commons.lang.StringEscapeUtils
import com.atlassian.jira.auditing.ChangedValue
import org.apache.log4j.Logger
import groovy.transform.Field

@Field Logger log = Logger.getLogger("Logger: ")

def auditingManager = ComponentAccessor.getComponent(AuditingManager.class)
def now = new Date()
def filter = AuditingFilter.builder().fromTimestamp(now.getTime() - 60*60*1000).build()
def formattedDate = new SimpleDateFormat("yyyy-MM-d_HH:mm").format(now)
def path = "JIRA_HOME" //directory with jira logs
def fileName = "audit_log_${formattedDate}.csv"
def records = auditingManager.getRecords(null, null, null, 0, filter)
def results = records.getResults()

new File(path + fileName).withWriter { writer ->
    writer.writeLine "Id,Date,Author,Remote Address,Category,Summary,Operation Details"
    results.each { record ->
        writer.writeLine "${record.id},${record.created},${record.authorKey},${record.remoteAddr},${record.category},${record.summary},\"${createOperationDetailsText(record.values)}\""
    }
}

String createOperationDetailsText(Iterable<ChangedValue> changedValues) {
    List operationDetails = []
    changedValues.each {
        def name = StringEscapeUtils.escapeHtml(changedValues.first().name)
        def toValue = StringEscapeUtils.escapeHtml(changedValues.first().to)
        operationDetails.add("[${name}] was changed to [${toValue}]")
    }
    return operationDetails.toString()
}
