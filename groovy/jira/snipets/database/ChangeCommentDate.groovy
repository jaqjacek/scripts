import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil
import org.apache.log4j.Level
import org.apache.log4j.Logger

import java.sql.Timestamp
import java.text.SimpleDateFormat

//comment-1971645

def log = Logger.getLogger("SetTimeSpent")
log.setLevel(Level.DEBUG)

SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);
//czyli 2022-11-18 10:00
String dateInString = "2022-11-18 10:00";
Date date = formatter.parse(dateInString);

Timestamp ts=new Timestamp(date.getTime());
log.debug(ts.toString())
String sqlRaw = "update jiraaction set created='${ts.toString()}' where id = 1971645"
log.debug(sqlRaw)
DatabaseUtil.withSql("default") { sql -> sql.executeUpdate(sqlRaw)}
