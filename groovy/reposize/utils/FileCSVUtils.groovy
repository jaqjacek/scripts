package reposize.utils

import reposize.config.RepoConfig
import reposize.model.FileSizeHelper

class FileCSVUtils {

    public static String getHeaders() {
        String headerString =
                "filePath" + RepoConfig.csvDelimiter +
                        "size" + RepoConfig.csvDelimiter +
                        "lfsFile" + RepoConfig.csvDelimiter +
                        "branch" + RepoConfig.csvDelimiter +
                        "repoName" + RepoConfig.csvDelimiter +
                        "projectName"
        return headerString
    }

    public static String fileSizeToCSV(FileSizeHelper fsh, Boolean withHeaders = false) {
        String resultString =
                "${fsh.pathName}${RepoConfig.csvDelimiter}" +
                        "${fsh.size}${RepoConfig.csvDelimiter}" +
                        "${fsh.isLfs}${RepoConfig.csvDelimiter}" +
                        "${fsh.branch}${RepoConfig.csvDelimiter}" +
                        "${fsh.repoName}${RepoConfig.csvDelimiter}" +
                        "${fsh.projectName}"
        if (withHeaders) {
            resultString = getHeaders() + "\n" + resultString
        }
        return resultString
    }

    public static String fileSizeToCSV(List<FileSizeHelper> files, Boolean withHeaders = false) {
        String resultString = ""
        int reposAmount = files.size()
        for (int i = 0; i < reposAmount; i++) {
            resultString += fileSizeToCSV(files[i])
            if (i < reposAmount - 1) {
                resultString += "\n"
            }
        }
        if (withHeaders) {
            resultString = getHeaders() + "\n" + resultString
        }
        return resultString
    }

}