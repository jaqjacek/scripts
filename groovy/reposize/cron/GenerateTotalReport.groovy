package reposize.cron

import groovy.transform.Field
import org.apache.log4j.Logger

/*
Script class that/s is called by cron or others methods
its run full process for whole bitbucket
 */

import reposize.config.RepoConfig
import reposize.model.FileSizeHelper
import reposize.model.RepoHelper
import reposize.utils.FileCSVUtils
import reposize.utils.RepoUtils
import com.atlassian.bitbucket.project.Project
import com.atlassian.bitbucket.project.ProjectService
import com.atlassian.bitbucket.repository.Repository
import com.atlassian.bitbucket.repository.RepositoryService
import com.atlassian.bitbucket.util.Page
import com.atlassian.bitbucket.util.PageRequest
import com.atlassian.bitbucket.util.PageRequestImpl
import com.atlassian.sal.api.component.ComponentLocator
import java.text.SimpleDateFormat

class GenerateTotalReport {

    public static String  run() {
      Logger log = Logger.getLogger("GenerateTotalReport: ")
        if(RepoUtils.createCache() != "ok") {
             return  "no cache dir !!! ${RepoConfig.cachePath}"
        }
        def repositoryService = ComponentLocator.getComponent(RepositoryService)
        def projectService = ComponentLocator.getComponent(ProjectService)
        def page = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT)
        List<RepoHelper> repos = []
        List<String> projects = projectService.findAllKeys()
        RepoUtils ru = new RepoUtils()
        List<FileSizeHelper> projectFss = []
        for (String projectKey : projects) {
            Project project = projectService.getByKey(projectKey)

            Page<Repository> repositories = repositoryService.findByProjectKey(project.key, page)
            List<Repository> reposz = repositories.getValues().toList()
            reposz.forEach {  repository ->
                RepoHelper rh = ru.createRepoHelper(repository)
                List<FileSizeHelper> fss = ru.getFileListForRepo(rh)
                projectFss.addAll(fss)
            }
        }
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RepoConfig.dateFormat)
        String reportName = "total_" + sdf.format(d) + ".csv"
        File report = new File(RepoConfig.cachePath + reportName)
        String output = FileCSVUtils.fileSizeToCSV(projectFss, true)
        ru.saveReportFile(output, reportName)
        ru.saveReportFile(output, RepoConfig.totalReportFile.replace(RepoConfig.cachePath, ""))
        report.write(output)
        return report.size();

    }
}