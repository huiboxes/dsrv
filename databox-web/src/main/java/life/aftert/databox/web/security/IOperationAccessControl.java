package life.aftert.databox.web.security;

import life.aftert.databox.core.usermgr.model.SystemRole;
import life.aftert.databox.core.usermgr.model.UserInfo;

public interface IOperationAccessControl {

    UserInfo checkLogin(String userName, String password);

    boolean checkSystemRole(SystemRole systemRole1, SystemRole systemRole2);
    boolean checkSystemRole(SystemRole systemRole1, String userId);

    boolean checkTokenOwner(String userName, String token);

    boolean checkBucketOwner(String userName, String bucketName);

    boolean checkPermission(String token, String bucket);

}
