package reposize.config
/*
Configuration for whole process of generating information for whole repository size scripts
 */
class RepoConfig {
    public static String reposPath="/data/atlassian/application-data/bitbucket/shared/data/repositories/"
    public static String cachePath="/data/atlassian/application-data/bitbucket/shared/data/cache_size/"
//    public static String reposPath="/var/atlassian/application-data/bitbucket/shared/data/repositories/" //disk path where bitbucket holds repositories
//    public static String cachePath="/var/atlassian/application-data/bitbucket/shared/data/cache/"  //where scripts have reports files
    public static String totalReportFile=cachePath+"total.csv" //lates report path that will by download by rest
    public static List<String> rest_allowed_groups=["bitbucket_sysadmins","G_Atlassian-Admins","stash-users"] //groups of users allowed to use this functionality
    public static String csvDelimiter=";" //csv delimiter char
    public static String dateFormat="yyyy-MM-dd" //date format generated for reportNames and others ex: 2024-01-21
    public static String totalReportNamePrefix="bitbucket-summarize-report-" //total bitbucket summarize report name prefix ex: bitbucket-summarize-report-2024-01-20.csv
    public static String projectReportNamePrefix="project-summarize-report-" //project summarize report name prefix ex: project-summarize-report-Project KEY-2024-01-20.csv
    public static String repositoryReportNamePrefix="repository-summarize-report-" //repository summarize report name prefix ex: repository-summarize-report-Repo Key-2024-01-20.csv

    public static String projectFilesReportNamePrefix="project-files-report-" //project summarize report name prefix ex: project-files-report-Project KEY-2024-01-20.csv
    public static String repositoryFilesReportNamePrefix="repository-files-report-" //repository summarize report name prefix ex: repository-files-report-Repo Key-2024-01-20.csv
}