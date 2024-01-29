package reposize.rest

import reposize.config.RepoConfig
import reposize.model.FileSizeHelper
import reposize.model.RepoHelper
import reposize.utils.RepoUtils
import reposize.utils.ResponseUtils
import com.atlassian.bitbucket.project.Project
import com.atlassian.bitbucket.project.ProjectService
import com.atlassian.bitbucket.repository.Repository
import com.atlassian.bitbucket.repository.RepositoryService
import com.atlassian.bitbucket.util.Page
import com.atlassian.bitbucket.util.PageRequest
import com.atlassian.bitbucket.util.PageRequestImpl
import com.atlassian.sal.api.component.ComponentLocator
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.transform.BaseScript

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response


/*
standard dump of all repos as one json. Mostly for test purpuses
 */
@BaseScript CustomEndpointDelegate delegate2
getAll(
        httpMethod: "GET", groups: RepoConfig.rest_allowed_groups
) { MultivaluedMap queryParams, String body ->

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
            RepoHelper rh = ru.createRepoHelper(repository)
            repos.add(rh)
        }

    }
    return ResponseUtils.getResponseByParams(queryParams, [repos] as List<RepoHelper>, "all-repos")
}


@BaseScript CustomEndpointDelegate delegate
getForRepo(
        httpMethod: "GET", groups: RepoConfig.rest_allowed_groups
) { MultivaluedMap queryParams, String body ->
    if (queryParams.containsKey("repoid")) {
        String rawID = queryParams.getFirst("repoid")
        def repositoryService = ComponentLocator.getComponent(RepositoryService)
        Repository repository = repositoryService.getById(Integer.parseInt(rawID))
        RepoUtils ru = new RepoUtils()
        RepoHelper rh = ru.createRepoHelper(repository)
        return ResponseUtils.getResponseByParams(queryParams, [rh], repository.name + "_" + repository.id)
    }
    return Response.ok("""{"error":"Please add parameter to this url .../rest/scriptrunner/latest/custom/getForRepo?repoid=1   """).build()
}

@BaseScript CustomEndpointDelegate delegate3
getForProject(
        httpMethod: "GET", groups: RepoConfig.rest_allowed_groups
) { MultivaluedMap queryParams, String body ->
    if (queryParams.containsKey("projectid")) {
        String rawID = queryParams.getFirst("projectid")
        def repositoryService = ComponentLocator.getComponent(RepositoryService)
        def projectService = ComponentLocator.getComponent(ProjectService)
        def page = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT)
        List<RepoHelper> repos = []
        RepoUtils ru = new RepoUtils()
        Project project = projectService.getById(Integer.parseInt(rawID))

        Page<Repository> repositories = repositoryService.findByProjectKey(project.key, page)
        for (Repository repository : repositories.getValues()) {
            RepoHelper rh = ru.createRepoHelper(repository)
            repos.add(rh)
        }

        return ResponseUtils.getResponseByParams(queryParams, repos, project.name)
    }

    return Response.ok("""{"error":"Please add parameter to this url .../rest/scriptrunner/latest/custom/getForProject?projectid"} """).build()
}

@BaseScript CustomEndpointDelegate delegate4
downloadTotalReport(
        httpMethod: "GET", groups: RepoConfig.rest_allowed_groups
) { MultivaluedMap queryParams, String body ->
    return ResponseUtils.getTotalReport("total.csv")
}


@BaseScript CustomEndpointDelegate delegate5
getFilesForRepo(
        httpMethod: "GET", groups: RepoConfig.rest_allowed_groups
) { MultivaluedMap queryParams, String body ->
    if (queryParams.containsKey("repoid")) {
        String rawID = queryParams.getFirst("repoid")
        def repositoryService = ComponentLocator.getComponent(RepositoryService)
        Repository repository = repositoryService.getById(Integer.parseInt(rawID))
        RepoUtils ru = new RepoUtils()
        RepoHelper rh = ru.createRepoHelper(repository)
        List<FileSizeHelper> fss = ru.getFileListForRepo(rh)
        return ResponseUtils.getFilesResponseByParams(queryParams, fss, repository.name + "_" + repository.id)

    }
    return Response.ok("""{"error":"Please add parameter to this url .../rest/scriptrunner/latest/custom/getFilesForRepo?repoid=1 "}  """).build()
}

@BaseScript CustomEndpointDelegate delegate33
getFilesForProject(
        httpMethod: "GET", groups: RepoConfig.rest_allowed_groups
) { MultivaluedMap queryParams, String body ->
    if (queryParams.containsKey("projectid")) {
        String rawID = queryParams.getFirst("projectid")
        def repositoryService = ComponentLocator.getComponent(RepositoryService)
        def projectService = ComponentLocator.getComponent(ProjectService)
        def page = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT)
        RepoUtils ru = new RepoUtils()
        Project project = projectService.getById(Integer.parseInt(rawID))

        Page<Repository> repositories = repositoryService.findByProjectKey(project.key, page)
        List<FileSizeHelper> projectFss = []
        for (Repository repository : repositories.getValues()) {
            RepoHelper rh = ru.createRepoHelper(repository)
            List<FileSizeHelper> fss = ru.getFileListForRepo(rh)
            projectFss.addAll(fss)
        }


        return ResponseUtils.getFilesResponseByParams(queryParams, projectFss, project.name + "_" + project.id)
    }

    return Response.ok("""{"error":"Please add parameter to this url .../rest/scriptrunner/latest/custom/getFilesForProject?projectid"} """).build()
}


