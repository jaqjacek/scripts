package bar.reposize.model

import groovy.json.JsonBuilder

class FileSizeHelper {
    public Long size
    public String pathName
    public String branch
    public String repoName
    public String projectName

    FileSizeHelper(Long size, String pathName, String branch, String repoName, String projectName) {
        this.size = size
        this.pathName = pathName
        this.branch = branch
        this.repoName = repoName
        this.projectName = projectName
    }

    @Override
    public String toString() {
        return toJson()
    }

    public String toJson() {
        return new JsonBuilder(this).toPrettyString()
    }
}