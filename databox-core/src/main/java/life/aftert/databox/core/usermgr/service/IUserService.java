package life.aftert.databox.core.usermgr.service;

import life.aftert.databox.core.usermgr.model.UserInfo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IUserService {

    boolean addUser(UserInfo userInfo);

    boolean updateUserInfo(String userId, String password, String detail);

    boolean deleteUser(String userId);

    UserInfo getUserInfo(String userId);

    UserInfo checkPassword(String userName, String password);

    UserInfo getUserInfoByName(String userName);

    List getUserlist();

}
