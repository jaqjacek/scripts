package reposize.rest

import reposize.config.RepoConfig
import reposize.model.RepoHelper
import reposize.utils.RepoUtils
import com.atlassian.bitbucket.project.Project
import com.atlassian.bitbucket.project.ProjectService
import com.atlassian.bitbucket.repository.Repository
import com.atlassian.bitbucket.repository.RepositoryService
import com.atlassian.bitbucket.util.Page
import com.atlassian.bitbucket.util.PageRequest
import com.atlassian.bitbucket.util.PageRequestImpl
import com.atlassian.sal.api.component.ComponentLocator
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("showRepoCommit: ")
log.setLevel(Level.DEBUG)


def repositoryService = ComponentLocator.getComponent(RepositoryService)
def projectService = ComponentLocator.getComponent(ProjectService)
def page = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT)
List<RepoHelper> repos = []
List<String> projects = projectService.findAllKeys()
RepoUtils ru = new RepoUtils()
for (String projectKey : projects) {
    Project project = projectService.getByKey(projectKey)

    Page<Repository> repositories = repositoryService.findByProjectKey(project.key, page)
    for (Repository repository : repositories.getValues()) {

        RepoHelper rh = new RepoHelper(repository.name,String.valueOf(repository.id),repository.slug,"0",projectKey,project.name)

        ru.fillRepoSize(rh)
        ru.fillRepoLFSSize(rh)
        repos.add(rh)
    }

}

log.warn(RepoConfig.reposPath)
return  repos