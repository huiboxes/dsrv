package life.aftert.databox.core.usermgr.dao;

import life.aftert.databox.core.usermgr.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

@Mapper
public interface UserInfoMapper {
    void addUser(@Param("userInfo") UserInfo userInfo);

    int deleteUser(@Param("userId") String userId);

    int updateUserInfo(@Param("userId") String userId,
                       @Param("password") String password,
                       @Param("detail") String detail);

    @ResultMap("UserInfoResultMap")
    UserInfo getUserInfo(@Param("userId") String userId);

    UserInfo checkPassword(@Param("userName") String userName,
                           @Param("password") String password);

    @ResultMap("UserInfoResultMap")
    UserInfo getUserInfoByName(@Param("userName") String userName);
}