import reposize.config.RepoConfig
import reposize.model.RepoHelper
import reposize.utils.RepoUtils
import groovy.transform.Field
import org.apache.log4j.Logger



@Field Logger log = Logger.getLogger("RepoUtils: ")

RepoHelper rh = new RepoHelper()
rh.repoID="1"
rh.repoPath = "${RepoConfig.reposPath}${rh.repoID}"
log.warn("fillRepoLFSSize start")
RepoUtils ru = new RepoUtils()
log.warn(ru.getBranches(rh))

String lastCommit = ru.getLatestCommitForBranch(rh,"develop")
log.warn(lastCommit)

ru.getFilesListWithPath(rh,lastCommit,"develop")