package life.aftert.databox.server.bucketmgr.test;

import life.aftert.databox.common.model.BucketModel;
import life.aftert.databox.core.authmgr.model.ServiceAuth;
import life.aftert.databox.core.authmgr.serivce.IAuthService;
import life.aftert.databox.core.usermgr.model.SystemRole;
import life.aftert.databox.core.usermgr.model.UserInfo;
import life.aftert.databox.core.usermgr.service.IUserService;
import life.aftert.databox.mybatis.test.BaseTest;
import life.aftert.databox.server.bucketmgr.dao.BucketMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BucketMapperTest extends BaseTest {

    @Autowired
    BucketMapper bucketMapper;
    @Autowired
    @Qualifier("authServiceImpl")
    IAuthService authService;
    @Autowired
    @Qualifier("userServiceImpl")
    IUserService userService;

    @Test
    public void addBucket() {
        BucketModel bucketModel = new BucketModel("test1", "test", "");
        bucketMapper.addBucket(bucketModel);
        UserInfo userInfo = new UserInfo("test", "test", SystemRole.ADMIN, "");
        userService.addUser(userInfo);
        ServiceAuth serviceAuth = new ServiceAuth();
        serviceAuth.setTargetToken(userInfo.getUserId());
        serviceAuth.setBucketName(bucketModel.getBucketName());
        authService.addAuth(serviceAuth);
        BucketModel bucketModel2 = new BucketModel("test2", "test2", "");
        bucketMapper.addBucket(bucketModel2);
    }

}
