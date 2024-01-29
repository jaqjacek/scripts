package reposize.config
/*
Configuration for whole process of generating information for whole repository size scripts
 */
class RepoConfig {
//    public static String reposPath="/data/atlassian/application-data/bitbucket/shared/data/repositories/"
//    public static String cachePath="/data/atlassian/application-data/bitbucket/shared/data/cache_size/"
    public static String reposPath="/var/atlassian/application-data/bitbucket/shared/data/repositories/" //disk path where bitbucket holds repositories
    public static String cachePath="/var/atlassian/application-data/bitbucket/shared/data/cache/"  //where scripts have reports files
    public static String totalReportFile="/var/atlassian/application-data/bitbucket/shared/data/cache/total.csv" //lates report path that will by download by rest
    public static List<String> rest_allowed_groups=["bitbucket_sysadmins","G_Atlassian-Admins","stash-users"] //groups of users allowed to use this functionality
    public static String csvDelimiter=";" //csv delimiter char
}