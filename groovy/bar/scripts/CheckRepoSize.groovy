import bar.reposize.config.RepoConfig
import bar.reposize.model.RepoHelper
import bar.reposize.utils.RepoUtils
import groovy.transform.Field
import org.apache.log4j.Logger



@Field Logger log = Logger.getLogger("RepoUtils: ")

RepoHelper repo = new RepoHelper()
repo.repoID="1"
repo.repoPath = "${RepoConfig.reposPath}${repo.repoID}"
log.warn("fillRepoLFSSize start")
RepoUtils ru = new RepoUtils()
ru.fillRepoSize(repo)
log.warn(repo.repoSize)