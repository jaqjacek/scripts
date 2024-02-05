import reposize.config.RepoConfig
import reposize.model.FileSizeHelper
import reposize.model.RepoHelper
import reposize.utils.RepoUtils
import groovy.transform.Field
import org.apache.log4j.Logger

import java.util.stream.Collectors


@Field Logger log = Logger.getLogger("RepoUtils: ")
String a;
a.startsWith()
RepoHelper rh = new RepoHelper()
rh.repoID="1"
rh.repoPath = "${RepoConfig.reposPath}${rh.repoID}"
RepoUtils ru = new RepoUtils()
List<FileSizeHelper> files = ru.getFileListForRepo(rh).stream().filter {it -> it.isLfs > 0}.collect(Collectors.toList())
for (FileSizeHelper fileSizeHelper : files) {
    log.warn(fileSizeHelper.pathName)

    log.warn(rawCMD)



    log.warn("--------------------------------------------------")

}

for (FileSizeHelper fileSizeHelper : files) {
    log.warn("${fileSizeHelper.pathName} -> ${fileSizeHelper.size}")
}
