package life.aftert.databox.core.test;

import life.aftert.databox.core.authmgr.model.ServiceAuth;
import life.aftert.databox.core.authmgr.model.TokenInfo;
import life.aftert.databox.core.authmgr.serivce.IAuthService;
import life.aftert.databox.mybatis.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

public class AuthServiceTest extends BaseTest {
    @Autowired
    @Qualifier("authServiceImpl")
    IAuthService authService;

    @Test
    public void addToken() {
        TokenInfo tokenInfo = new TokenInfo("tokenTest");
        authService.addToken(tokenInfo);
    }

    @Test
    public void getTokenByUser(){
        List<TokenInfo> tokenInfos = authService.getTokenInfos("tokenTest");
        tokenInfos.forEach(tokenInfo -> {
            System.out.println(tokenInfo.getToken());
        });
    }

    @Test
    public void refreshToken() {
        List<TokenInfo> tokenInfos = authService.getTokenInfos("tokenTest");
        tokenInfos.forEach(tokenInfo -> {
            authService.refreshToken(tokenInfo.getToken());
        });
    }

    @Test
    public void deleteToken() {
        List<TokenInfo> tokenInfos = authService.getTokenInfos("tokenTest");
        if (tokenInfos.size() > 0) {
            authService.deleteToken(tokenInfos.get(0).getToken());
        }
    }


    @Test
    public void addAuth() {
        List<TokenInfo> tokenInfos = authService.getTokenInfos("tokenTest");
        if (tokenInfos.size() > 0) {
            ServiceAuth serviceAuth = new ServiceAuth();
            serviceAuth.setAuthTime(new Date());
            serviceAuth.setBucketName("testBucket");
            serviceAuth.setTargetToken(tokenInfos.get(0).getToken());
            authService.addAuth(serviceAuth);
        }
    }

    @Test
    public void deleteAuth() {
        List<TokenInfo> tokenInfos = authService.getTokenInfos("tokenTest");
        if (tokenInfos.size() > 0) {
            authService.deleteAuth("testBucket", tokenInfos.get(0).getToken());
        }
    }
}
