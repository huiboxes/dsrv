package life.aftert.databox.web;

import life.aftert.databox.core.DxConfiguration;
import life.aftert.databox.server.filemgr.service.DxStoreServiceImpl;
import life.aftert.databox.server.filemgr.service.HdfsServiceImpl;
import life.aftert.databox.server.filemgr.service.IDxStoreService;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.io.IOException;

@Configuration
public class DxServerBeanConfiguration {

    /**
     * 创建 hbase 连接
     *
     * @return conn
     * @throws IOException ioe.
     */
    @Bean
    public Connection getConnection() throws IOException {
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        DxConfiguration confUtil = DxConfiguration.getConfiguration();

        config.set("hbase.zookeeper.quorum", confUtil.getString("hbase.zookeeper.quorum"));
        config.set("hbase.zookeeper.property.clientPort",
                confUtil.getString("hbase.zookeeper.port"));
        config.set(HConstants.HBASE_RPC_TIMEOUT_KEY, "3600000");

        return ConnectionFactory.createConnection(config);
    }

    @Bean(name = "dxStoreService")
    public IDxStoreService getHosStore(@Autowired Connection connection) throws Exception {
        DxConfiguration confUtil = DxConfiguration.getConfiguration();
        String zkHosts = confUtil.getString("hbase.zookeeper.quorum");
        DxStoreServiceImpl store = new DxStoreServiceImpl(connection, new HdfsServiceImpl(),
                zkHosts);
        return store;
    }

    @Bean
    public CookieSerializer httpSessionIdResolver(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setSameSite(null);
        return cookieSerializer;
    }

}
