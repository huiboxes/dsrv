package life.aftert.databox.core.authmgr.serivce;

import life.aftert.databox.core.authmgr.model.ServiceAuth;
import life.aftert.databox.core.authmgr.model.TokenInfo;

import java.util.List;

public interface IAuthService {

    boolean addAuth(ServiceAuth auth);

    boolean deleteAuth(String bucketName, String token);

    boolean deleteAuthByBucket(String bucketName);

    boolean deleteAuthByToken(String token);

    ServiceAuth getServiceAuth(String bucketName, String token);


    boolean addToken(TokenInfo tokenInfo);

    boolean deleteToken(String token);

    boolean checkToken(String token);

    boolean updateToken(String token, int expireTime, boolean isActive);

    boolean refreshToken(String token);

    TokenInfo getTokenInfo(String token);

    List<TokenInfo> getTokenInfos(String creator);

}
