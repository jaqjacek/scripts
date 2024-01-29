import bar.reposize.config.RepoConfig
import bar.reposize.model.RepoHelper
import groovy.transform.Field
import org.apache.log4j.Logger



@Field Logger log = Logger.getLogger("RepoUtils: ")

RepoHelper repo = new RepoHelper()
repo.repoID="1"
log.warn("fillRepoLFSSize start")
String thisRepoPath="${RepoConfig.reposPath}${repo.repoID}"
repo.repoPath = thisRepoPath
//git log -p --since="2024-01-01" --before="2024-12-31" |grep sha256 -a1 | grep size |cut -d" " -f2


//String cmd = """git --git-dir=${thisRepoPath} log -p | grep sha256 -a1 |grep size |cut -d' ' -f2 """
String cmd = """git --git-dir=${repo.repoPath} log -p  """
log.warn(cmd)
def p  = cmd.execute()
List<String> lines = p.inputStream.readLines()

Long lfsSize=0
for (int i =0 ; i< lines.size(); i ++) {
    if(lines[i].contains("oid sha256")) {

        lfsSize+= getSizeFromLine(lines[i+1])
    }
}
repo.lfsSize=lfsSize


Long getSizeFromLine(String rawLine) {
    if(rawLine.startsWith("+size")) {
        return Long.parseLong(rawLine.split(" ")[1])
    }
    return  0L;
}

log.warn(repo.toJson())
log.warn(getFirstCommitDate(repo))


String getFirstCommitDate(RepoHelper rh) {
    def p = "git --git-dir=${rh.repoPath} rev-list --max-parents=0 HEAD".execute()
    p.waitFor()
    String commitString =p.text
    ProcessBuilder pb = new ProcessBuilder("git","--git-dir=${rh.repoPath}","log",commitString,"--date=format:format-local")
    p = pb.start()
    log.warn(p.errorStream.text)
    p.waitFor()
//    --date='format:%Y-%m-%d'
    log.warn(p.exitValue())
    return p.text

}

//log.warn(p.exitValue())
//log.warn(outputText)
//repo.lfsSize = outputText.replace(thisRepoPath,"").trim()