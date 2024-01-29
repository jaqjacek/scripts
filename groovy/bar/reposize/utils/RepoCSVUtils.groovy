package bar.reposize.utils

import bar.reposize.config.RepoConfig
import bar.reposize.model.RepoHelper
import com.atlassian.bitbucket.repository.Repository
import org.apache.log4j.Logger


class RepoCSVUtils {

    public static String getHeaders() {
        String headerString=
                 "id"+RepoConfig.csvDelimiter+
                "name"+RepoConfig.csvDelimiter+
                "url"+RepoConfig.csvDelimiter+
                "diskSize"+RepoConfig.csvDelimiter+
                "lfsSize"+RepoConfig.csvDelimiter+
                "totalSize"+RepoConfig.csvDelimiter+
                "projectKey"+RepoConfig.csvDelimiter+
                "projectName"+RepoConfig.csvDelimiter+
                "diskPath"
        return headerString
    }

    public static String repoToCSV(RepoHelper rh,Boolean withHeaders=false) {
        String resultString=
                "${rh.repoID}${RepoConfig.csvDelimiter}"+
                "${rh.repoName}${RepoConfig.csvDelimiter}"+
                "${rh.repoUrl}${RepoConfig.csvDelimiter}"+
                "${rh.repoSize}${RepoConfig.csvDelimiter}"+
                "${rh.lfsSize}${RepoConfig.csvDelimiter}"+
                "${rh.totalSize}${RepoConfig.csvDelimiter}"+
                "${rh.projectKey}${RepoConfig.csvDelimiter}"+
                "${rh.projectName}"
        if(withHeaders ) {
            resultString = getHeaders()+"\n"+resultString
        }
        return resultString
    }

    public static String repoToCSV(List<RepoHelper> rhs,Boolean withHeaders=false) {
        String resultString=""
        int reposAmount= rhs.size()
        for (int i=0;i<reposAmount;i++) {
            resultString+=repoToCSV(rhs[i])
            if(i<reposAmount-1) {
                resultString+="\n"
            }
        }
        if(withHeaders ) {
            resultString = getHeaders()+"\n"+resultString
        }
        return resultString
    }

}