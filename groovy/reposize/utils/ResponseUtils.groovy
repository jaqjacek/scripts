package reposize.utils

import reposize.config.RepoConfig
import reposize.model.FileSizeHelper
import reposize.model.RepoHelper

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

class ResponseUtils {

    public static Response getResponseByParams(MultivaluedMap queryParams, RepoHelper rh, String fileName) {
        if (queryParams.containsKey("asFile")) {
            File tmpFile = File.createTempFile(fileName, ".json")
            FileOutputStream fos = new FileOutputStream(tmpFile)
            fos.write(rh.toJson().bytes)
            Response r = Response.ok(tmpFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(tmpFile)
                    .header("Content-Disposition", "attachment; filename=\"${fileName}.json\"")
                    .build()
            return r;
        }
        if (queryParams.containsKey("asCSVFile")) {
            File tmpFile = File.createTempFile(fileName, ".csv")
            FileOutputStream fos = new FileOutputStream(tmpFile)
            fos.write(RepoCSVUtils.repoToCSV(rh, true).bytes)
            Response r = Response.ok(tmpFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(tmpFile)
                    .header("Content-Disposition", "attachment; filename=\"${fileName}.csv\"")
                    .build()
            return r;
        }
        return Response.ok(rh).build()
    }

    public static Response getResponseByParams(MultivaluedMap queryParams, List<RepoHelper> rhs, String fileName) {
        if (queryParams.containsKey("asFile")) {
            File tmpFile = File.createTempFile(fileName, ".json")
            FileOutputStream fos = new FileOutputStream(tmpFile)
            fos.write(rhs.toString().bytes)
            Response r = Response.ok(tmpFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(tmpFile)
                    .header("Content-Disposition", "attachment; filename=\"${fileName}.json\"")
                    .build()

            return r;
        }
        if (queryParams.containsKey("asCSVFile")) {
            File tmpFile = File.createTempFile(fileName, ".csv")
            FileOutputStream fos = new FileOutputStream(tmpFile)
            fos.write(RepoCSVUtils.repoToCSV(rhs, true).bytes)
            Response r = Response.ok(tmpFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(tmpFile)
                    .header("Content-Disposition", "attachment; filename=\"${fileName}.csv\"")
                    .build()

            return r;
        }
        return Response.ok(rhs).build()
    }

    public static Response getFilesResponseByParams(MultivaluedMap queryParams, List<FileSizeHelper> fshs, String fileName) {
        if (queryParams.containsKey("asFile")) {
            File tmpFile = File.createTempFile(fileName, ".json")
            FileOutputStream fos = new FileOutputStream(tmpFile)
            fos.write(fshs.toString().bytes)
            Response r = Response.ok(tmpFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(tmpFile)
                    .header("Content-Disposition", "attachment; filename=\"${fileName}.json\"")
                    .build()

            return r;
        }
        if (queryParams.containsKey("asCSVFile")) {
            String outputString = FileCSVUtils.fileSizeToCSV(fshs, true).toString()
            File tmpFile = File.createTempFile(fileName, ".csv")
            FileOutputStream fos = new FileOutputStream(tmpFile)
            fos.write(outputString.bytes)
            Response r = Response.ok(tmpFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(tmpFile)
                    .header("Content-Disposition", "attachment; filename=\"${fileName}.csv\"")
                    .build()

            return r;
        }
        return Response.ok(fshs).build()
    }

    public static Response getTotalReport(String reportName) {
        RepoUtils ru = new RepoUtils();
        if (ru.reportFileExists()) {
            File f = new File(RepoConfig.totalReportFile)
            Response r = Response.ok(f, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .entity(f)
                    .header("Content-Disposition", "attachment; filename=\"${reportName}.csv\"")
                    .build()

            return r;
        }

        return Response.ok('''{"error":"No report File generated"}''').build()
    }


}