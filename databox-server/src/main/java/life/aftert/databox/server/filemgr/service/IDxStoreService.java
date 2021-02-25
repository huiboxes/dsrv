package life.aftert.databox.server.filemgr.service;

import life.aftert.databox.common.model.DxObject;
import life.aftert.databox.common.model.DxObjectSummary;
import life.aftert.databox.common.model.ObjectListResult;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public interface IDxStoreService {

    void createSeqTable() throws IOException;

    void put(String bucket, String key, ByteBuffer content, long length, String mediaType,
             Map<String, String> properties) throws Exception;

    DxObjectSummary getSummary(String bucket, String key) throws IOException;

    List<DxObjectSummary> list(String bucket, String startKey, String endKey)
            throws IOException;

    ObjectListResult listDir(String bucket, String dir,
                                    String start, int maxCount) throws IOException;

    ObjectListResult listByPrefix(String bucket, String dir, String keyPrefix, String start,
                                         int maxCount) throws IOException;

    DxObject getObject(String bucket, String key) throws IOException;

    void deleteObject(String bucket, String key) throws Exception;

    void deleteBucketStore(String bucket) throws IOException;

    void createBucketStore(String bucket) throws IOException;


}
