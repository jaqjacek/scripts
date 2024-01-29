package bar.reposize.config
class RepoConfig {
//    public static String reposPath="/data/atlassian/application-data/bitbucket/shared/data/repositories/"
//    public static String cachePath="/data/atlassian/application-data/bitbucket/shared/data/cache_size/"
    public static String reposPath="/var/atlassian/application-data/bitbucket/shared/data/repositories/"
    public static String cachePath="/var/atlassian/application-data/bitbucket/shared/data/cache/"
    public static List<String> rest_allowed_groups=["bitbucket_sysadmins","G_Atlassian-Admins","stash-users"]
    public static String csvDelimiter=";"
}