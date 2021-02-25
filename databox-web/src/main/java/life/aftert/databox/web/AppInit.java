package life.aftert.databox.web;

import life.aftert.databox.core.usermgr.CoreUtil;
import life.aftert.databox.core.usermgr.model.SystemRole;
import life.aftert.databox.core.usermgr.model.UserInfo;
import life.aftert.databox.core.usermgr.service.IUserService;
import life.aftert.databox.server.filemgr.service.IDxStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppInit implements ApplicationRunner {

    @Autowired
    @Qualifier("dxStoreService")
    IDxStoreService dxStoreService;

    @Autowired
    @Qualifier("userServiceImpl")
    IUserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserInfo userInfo = userService.getUserInfoByName(CoreUtil.SYSTEM_USER);
        if (userInfo == null) {
            UserInfo userInfo1 = new UserInfo(CoreUtil.SYSTEM_USER, "huibox", SystemRole.SUPERADMIN,
                    "this is superadmin");
            userService.addUser(userInfo1);
        }
        dxStoreService.createSeqTable();
    }
}
