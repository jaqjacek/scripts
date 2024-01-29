package reposize.model

import groovy.json.JsonBuilder

class RepoHelper {
    public String repoPath;
    public String repoName;
    public String repoID
    public String repoUrl
    public String repoSize
    public String lfsSize
    public String totalSize
    public String projectKey
    public String projectName

    RepoHelper(String repoName, String repoID, String repoUrl, String projectKey, String projectName, String repoPath) {
        this.repoName = repoName
        this.repoID = repoID
        this.repoUrl = repoUrl
        this.projectKey = projectKey
        this.projectName = projectName
        this.repoPath = repoPath
        totalSize = '0'
        repoSize = '0'
        lfsSize = '0'

    }

    RepoHelper() {

    }

    public setSize(String size) {
        this.repoSize = size
        calculateTotal()
    }

    public setLFSSize(String size) {
        this.lfsSize = size
        calculateTotal()
    }

    private calculateTotal() {
        totalSize = String.valueOf(Long.parseLong(repoSize) + Long.parseLong(lfsSize))
    }

    @Override
    public String toString() {
        return toJson()
    }

    public String toJson() {
        return new JsonBuilder(this).toPrettyString()
    }
}