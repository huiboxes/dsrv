package life.aftert.databox.server.bucketmgr.service;

import life.aftert.databox.common.model.BucketModel;
import life.aftert.databox.core.usermgr.model.UserInfo;

import java.util.List;

public interface IBucketService {

    boolean addBucket(UserInfo userInfo, String bucketName, String detail);

    boolean deleteBucket(String bucketName);

    boolean updateBucket(String bucketName, String detail);

    BucketModel getBucketById(String bucketId);

    BucketModel getBucketByName(String bucketName);

    List<BucketModel> getUserBuckets(String token);

}
