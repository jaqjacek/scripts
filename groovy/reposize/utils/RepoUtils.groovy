package reposize.utils

import reposize.config.RepoConfig
import reposize.model.FileSizeHelper
import reposize.model.RepoHelper
import com.atlassian.bitbucket.repository.Repository
import java.util.stream.Collectors

class RepoUtils {

    RepoUtils() {
    }

    public RepoHelper createRepoHelper(Repository repo) {
        RepoHelper rh = new RepoHelper(repo.name, repo.id.toString(), repo.slug, repo.project.key, repo.project.name, RepoConfig.reposPath + repo.id)
        this.fillRepoSize(rh)
        this.fillRepoLFSSize(rh)
        return rh
    }

    public void fillRepoSize(RepoHelper repo) {
        File f = new File(repo.repoPath)
        Long size = folderSize(f)
        repo.setSize(String.valueOf(size))
    }

    public void fillRepoLFSSize(RepoHelper repo) {

        String cmd = """git --git-dir=${repo.repoPath} log -p  """
        def p = cmd.execute()
        List<String> lines = p.inputStream.readLines()

        Long lfsSize = 0
        for (int i = 0; i < lines.size(); i++) {
            if (lines[i].contains("oid sha256")) {
                lfsSize += getSizeFromLine(lines[i + 1])
            }
        }
        repo.setLFSSize(lfsSize.toString())
    }

    public void getSingleLFSFileSize(RepoHelper repo, File) {

        String cmd = """git --git-dir=${repo.repoPath} log -p  """
        def p = cmd.execute()
        List<String> lines = p.inputStream.readLines()

        Long lfsSize = 0
        for (int i = 0; i < lines.size(); i++) {
            if (lines[i].contains("oid sha256")) {
                lfsSize += getSizeFromLine(lines[i + 1])
            }
        }
        repo.setLFSSize(lfsSize.toString())
    }

    Long getSizeFromLine(String rawLine) {
        if (rawLine.startsWith("+size")) {
            return Long.parseLong(rawLine.split(" ")[1])
        }
        return 0L;
    }

    public void setLFSFileSize(RepoHelper rh, FileSizeHelper fsh) {
        String rawCMD = """git --git-dir=${rh.repoPath} log -p  ${fsh.branch} -- ${fsh.pathName}  """
        String result = rawCMD.execute().text
        List<String> lines = result.split("\n").reverse().toList()
        RepoUtils ru = new RepoUtils()
        if (lines.size() > 0) {
            Long newSize = ru.getSizeFromLine(lines[0])
            if (newSize > 0) {
                fsh.size = newSize
            }
        }
    }

    public List<String> getBranches(RepoHelper repo) {
        String cmd = """git --git-dir=${repo.repoPath} branch """
        def p = cmd.execute()
        List<String> lines = p.inputStream.readLines()

        List<String> branchesNames = []
        for (int i = 0; i < lines.size(); i++) {
            if (lines[i].contains("error")) continue
            branchesNames.add(lines[i].replace("*", "").trim())
        }
        return branchesNames
    }

    public String getLatestCommitForBranch(RepoHelper repo, String branchName) {
        String cmd = """git --git-dir=${repo.repoPath} rev-list --max-count=1 ${branchName} """
        def p = cmd.execute()
        List<String> lines = p.inputStream.readLines()
        return lines[0]
    }

    public List<FileSizeHelper> getFilesListWithPath(RepoHelper rh, String commitsha, String branchName = "") {
        String cmd = """git --git-dir=${rh.repoPath} ls-tree -r -l ${commitsha}"""
        def p = cmd.execute()
        List<String> lines = p.inputStream.readLines()

        List<FileSizeHelper> files = []
        for (int i = 0; i < lines.size(); i++) {

            List<String> parts = lines[i].split("\\s") as ArrayList
            parts = parts.stream().filter({ it -> !it.isEmpty() }).collect(Collectors.toList())

            Long size = Long.parseLong(parts[3])
            //fastes way to check if file is on lfs is to check it size if its 132 then its a symlink to lfs
            Long lfsFile = size == 132 || size == 133 ? 1 : 0;

            String path = lines[i].split(parts[3] + "\t")[1]
            FileSizeHelper fsh = new FileSizeHelper(size, path, branchName, rh.repoName, rh.projectName, lfsFile)
            if (lfsFile) {
                setLFSFileSize(rh, fsh)
            }
            files.add(fsh)
        }
        return files
    }


    public List<FileSizeHelper> getFileListForRepo(RepoHelper rh) {
        List<FileSizeHelper> fs = []
        List<String> branches = getBranches(rh)
        for (String branchName : branches) {
            String commitSHA = getLatestCommitForBranch(rh, branchName)
            fs.addAll(getFilesListWithPath(rh, commitSHA, branchName))
        }
        return fs
    }

    public static String createCache() {
        File f = new File(RepoConfig.cachePath)
        if (!f.exists()) {
            f.mkdirs().wait(30)
        }

        if (!f.exists()) {
            return "can't create cache{ath ${RepoConfig.cachePath}"
        }
        return "ok"
    }

    public Long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public void saveReportFile(String dataToSave, String fileName) {
        File report = new File(RepoConfig.cachePath + fileName)
        report.write(dataToSave)
    }

    public boolean reportFileExists() {
        File f = new File(RepoConfig.cachePath)
        if (!f.exists()) return false;
        f = new File(RepoConfig.totalReportFile)
        if (!f.exists()) return false;


        return true;
    }

}