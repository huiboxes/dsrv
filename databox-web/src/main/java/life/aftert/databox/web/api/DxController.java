package life.aftert.databox.web.api;

import com.google.common.base.Splitter;
import life.aftert.databox.common.DxHeaders;
import life.aftert.databox.common.model.BucketModel;
import life.aftert.databox.common.model.DxObject;
import life.aftert.databox.common.model.DxObjectSummary;
import life.aftert.databox.common.model.ObjectListResult;
import life.aftert.databox.core.usermgr.model.SystemRole;
import life.aftert.databox.core.usermgr.model.UserInfo;
import life.aftert.databox.server.bucketmgr.service.IBucketService;
import life.aftert.databox.server.filemgr.service.IDxStoreService;
import life.aftert.databox.web.security.ContextUtil;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("dx/")
public class DxController extends BaseController {
    private static Logger logger = Logger.getLogger(DxController.class);

    @Autowired
    @Qualifier("bucketServiceImpl")
    IBucketService bucketService;

    @Autowired
    @Qualifier("dxStoreService")
    IDxStoreService dxStoreService;

    private static long MAX_FILE_IN_MEMORY = 2 * 1024 * 1024;

    private final int readBufferSize = 32 * 1024;

    private static String TMP_DIR = System.getProperty("user.dir") + File.separator + "tmp";

    public DxController() {
        File file = new File(TMP_DIR);
        file.mkdirs();
    }

    @RequestMapping(value = "bucket", method = RequestMethod.POST)
    public Object createBucket(@RequestParam("bucket") String bucketName,
                               @RequestParam(name = "detail", required = false, defaultValue = "") String detail) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!currentUser.getSystemRole().equals(SystemRole.VISITER)) {
            bucketService.addBucket(currentUser, bucketName, detail);
            try {
                dxStoreService.createBucketStore(bucketName);
            } catch (IOException ioe) {
                bucketService.deleteBucket(bucketName);
                return  "create bucket error";
            }
            return "success";
        }
        return "PERMISSION DENIED";
    }


    @RequestMapping(value = "deleteBucket", method = RequestMethod.POST)
    public Object deleteBucket(@RequestParam("bucket") String bucket) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (operationAccessControl.checkBucketOwner(currentUser.getUserName(), bucket)) {
            try {
                dxStoreService.deleteBucketStore(bucket);
            } catch (IOException ioe) {
                return "delete bucket error";
            }
            bucketService.deleteBucket(bucket);
            return "success";
        }
        return "PERMISSION DENIED";
    }

    @RequestMapping(value = "bucket", method = RequestMethod.PUT)
    public Object updateBucket(
            @RequestParam(name = "bucket") String bucket,
            @RequestParam(name = "detail") String detail) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        BucketModel bucketModel = bucketService.getBucketByName(bucket);
        if (operationAccessControl
                .checkBucketOwner(currentUser.getUserName(), bucketModel.getBucketName())) {
            bucketService.updateBucket(bucket, detail);
            return "success";
        }
        return "PERMISSION DENIED";
    }

    @RequestMapping(value = "bucket", method = RequestMethod.GET)
    public Object getBucket(@RequestParam(name = "bucket") String bucket) {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        BucketModel bucketModel = bucketService.getBucketByName(bucket);
        if (operationAccessControl
                .checkPermission(currentUser.getUserId(), bucketModel.getBucketName())) {
            return bucketModel;
        }
        return "PERMISSION DENIED";

    }

    @RequestMapping(value = "bucket/list", method = RequestMethod.GET)
    public Object getBucket() {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        List<BucketModel> bucketModels = bucketService.getUserBuckets(currentUser.getUserId());
        return bucketModels;
    }

    @RequestMapping(value = "object", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public Object putObject(@RequestParam("bucket") String bucket,
                            @RequestParam("key") String key,
                            @RequestParam(value = "mediaType", required = false) String mediaType,
                            @RequestParam(value = "content", required = false) MultipartFile content,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return "Permission denied";
        }
        if (!key.startsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("object key must start with /");
        }

        Enumeration<String> headNames = request.getHeaderNames();
        Map<String, String> attrs = new HashMap<>();
        String contentEncoding = request.getHeader("content-encoding");
        if (contentEncoding != null) {
            attrs.put("content-encoding", contentEncoding);
        }
        while (headNames.hasMoreElements()) {
            String header = headNames.nextElement();
            if (header.startsWith(DxHeaders.COMMON_ATTR_PREFIX)) {
                attrs.put(header.replace(DxHeaders.COMMON_ATTR_PREFIX, ""), request.getHeader(header));
            }
        }
        ByteBuffer buffer = null;
        File distFile = null;
        try {
            // put dir object
            if (key.endsWith("/")) {
                if (content != null) {
                    response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    content.getInputStream().close();
                    return null;
                }
                dxStoreService.put(bucket, key, null, 0, mediaType, attrs);
                response.setStatus(HttpStatus.SC_OK);
                return "success";
            }
            if (content == null || content.getSize() == 0) {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.getWriter().write("object content could not be empty");
                return "object content could not be empty";
            }

            if (content != null) {
                if (content.getSize() > MAX_FILE_IN_MEMORY) {
                    distFile = new File(TMP_DIR + File.separator + UUID.randomUUID().toString());
                    content.transferTo(distFile);
                    content.getInputStream().close();
                    buffer = new FileInputStream(distFile).getChannel()
                            .map(FileChannel.MapMode.READ_ONLY, 0, content.getSize());
                } else {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    org.apache.commons.io.IOUtils.copy(content.getInputStream(), outputStream);
                    buffer = ByteBuffer.wrap(outputStream.toByteArray());
                    content.getInputStream().close();
                }
            }
            dxStoreService.put(bucket, key, buffer, content.getSize(), mediaType, attrs);
            return "success";
        } catch (IOException ioe) {
            logger.error(ioe);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("server error");
            return "server error";
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
            if (content != null) {
                try {
                    content.getInputStream().close();
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
            if (distFile != null) {
                distFile.delete();
            }
        }
    }

    @RequestMapping(value = "object/list", method = RequestMethod.GET)
    public ObjectListResult listObject(@RequestParam("bucket") String bucket,
                                       @RequestParam("startKey") String startKey,
                                       @RequestParam("endKey") String endKey,
                                       HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (startKey.compareTo(endKey) > 0) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            return null;
        }
        ObjectListResult result = new ObjectListResult();
        List<DxObjectSummary> summaryList = dxStoreService.list(bucket, startKey, endKey);
        result.setBucket(bucket);
        if (summaryList.size() > 0) {
            result.setMaxKey(summaryList.get(summaryList.size() - 1).getKey());
            result.setMinKey(summaryList.get(0).getKey());
        }
        result.setObjectCount(summaryList.size());
        result.setObjectList(summaryList);
        return result;
    }

    @RequestMapping(value = "object/info", method = RequestMethod.GET)
    public DxObjectSummary getSummary(String bucket, String key, HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        DxObjectSummary summary = dxStoreService.getSummary(bucket, key);
        if (summary == null) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
        return summary;
    }

    @RequestMapping(value = "object/list/prefix", method = RequestMethod.GET)
    public ObjectListResult listObjectByPrefix(@RequestParam("bucket") String bucket,
                                               @RequestParam("dir") String dir,
                                               @RequestParam("prefix") String prefix,
                                               @RequestParam(value = "startKey", required = false, defaultValue = "") String start,
                                               HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (!dir.startsWith("/") || !dir.endsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("dir must start with / and end with /");
            return null;
        }
        if ("".equals(start) || start.equals("/")) {
            start = null;
        }
        if (start != null) {
            List<String> segs = StreamSupport.stream(Splitter
                    .on("/")
                    .trimResults()
                    .omitEmptyStrings().split(start).spliterator(), false).collect(Collectors.toList());
            start = segs.get(segs.size() - 1);
        }
        ObjectListResult result = this.dxStoreService.listByPrefix(bucket, dir, prefix, start, 100);
        return result;
    }

    @RequestMapping(value = "object/list/dir", method = RequestMethod.GET)
    public ObjectListResult listObjectByDir(@RequestParam("bucket") String bucket,
                                            @RequestParam("dir") String dir,
                                            @RequestParam(value = "startKey", required = false, defaultValue = "") String start,
                                            HttpServletResponse response)
            throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (!dir.startsWith("/") || !dir.endsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("dir must start with / and end with /");
            return null;
        }
        if ("".equals(start) || start.equals("/")) {
            start = null;
        }
        if (start != null) {
            List<String> segs = StreamSupport.stream(Splitter
                    .on("/")
                    .trimResults()
                    .omitEmptyStrings().split(start).spliterator(), false).collect(Collectors.toList());
            start = segs.get(segs.size() - 1);
        }

        ObjectListResult result = this.dxStoreService.listDir(bucket, dir, start, 100);
        return result;
    }


//    @RequestMapping(value = "object", method = RequestMethod.DELETE)
//    public Object deleteObject(@RequestParam("bucket") String bucket,
//                               @RequestParam("key") String key) throws Exception {
//        UserInfo currentUser = ContextUtil.getCurrentUser();
//        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
//            return  "PERMISSION DENIED";
//        }
//        this.dxStoreService.deleteObject(bucket, key);
//        return "success";
//    }

    @RequestMapping(value = "deleteDir", method = RequestMethod.POST)
    public Object deleteDir(@RequestParam("bucket") String bucket,
                               @RequestParam("key") String key) throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            return  "PERMISSION DENIED";
        }
        this.dxStoreService.deleteObject(bucket, key);
        return "success";
    }

    @RequestMapping(value = "deleteFile", method = RequestMethod.POST)
    public Object deleteFile(@RequestParam("bucket") String bucket,
                               @RequestParam("key") String key) throws Exception {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            return  "PERMISSION DENIED";
        }
        this.dxStoreService.deleteObject(bucket, key);
        return "success";
    }

    @RequestMapping(value = "object/content", method = RequestMethod.GET)
    public void getObject(@RequestParam("bucket") String bucket,
                          @RequestParam("key") String key, HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        UserInfo currentUser = ContextUtil.getCurrentUser();
        if (!operationAccessControl.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return;
        }
        DxObject object = this.dxStoreService.getObject(bucket, key);
        if (object == null) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return;
        }
        response.setHeader(DxHeaders.COMMON_OBJ_BUCKET, bucket);
        response.setHeader(DxHeaders.COMMON_OBJ_KEY, key);
        response.setHeader(DxHeaders.RESPONSE_OBJ_LENGTH, "" + object.getMetaData().getLength());
        String iflastModify = request.getHeader("If-Modified-Since");
        String lastModify = object.getMetaData().getLastModifyTime() + "";
        response.setHeader("Last-Modified", lastModify);
        String contentEncoding = object.getMetaData().getContentEncoding();
        if (contentEncoding != null) {
            response.setHeader("content-encoding", contentEncoding);
        }
        if (iflastModify != null && iflastModify.equals(lastModify)) {
            response.setStatus(HttpStatus.SC_NOT_MODIFIED);
            return;
        }
        response.setHeader(DxHeaders.COMMON_OBJ_BUCKET, object.getMetaData().getBucket());
        response.setContentType(object.getMetaData().getMediaType());
        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = object.getContent();
        try {
            byte[] buffer = new byte[readBufferSize];
            int len = -1;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            response.flushBuffer();
        } finally {
            inputStream.close();
            outputStream.close();
        }

    }
}
