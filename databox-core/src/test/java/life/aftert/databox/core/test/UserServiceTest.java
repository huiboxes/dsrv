package life.aftert.databox.core.test;

import life.aftert.databox.core.usermgr.model.SystemRole;
import life.aftert.databox.core.usermgr.model.UserInfo;
import life.aftert.databox.core.usermgr.service.IUserService;
import life.aftert.databox.mybatis.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class UserServiceTest extends BaseTest {

    @Autowired
    @Qualifier("userServiceImpl")
    IUserService userService;

    @Test
    public void addUser() {
        UserInfo userInfo = new UserInfo("test", "123456", SystemRole.ADMIN, "test");
        userService.addUser(userInfo);
    }

    @Test
    public void getUser() {
        UserInfo userInfo = userService.getUserInfoByName("test");
        System.out.println( userInfo.getUserId() + "|" + userInfo.getUserName() + "|" + userInfo.getPassword());
    }

    @Test
    public void deleteUser() {
        UserInfo userInfo = userService.getUserInfoByName("test");
        userService.deleteUser(userInfo.getUserId());
    }

}
