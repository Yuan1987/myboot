package com.framework.config;

import java.net.InetAddress;
import java.util.concurrent.Executor;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 全文检索配置
 * @author yuan
 */
@Configuration
public class ElasticsearchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfig.class);
    /**
     * elk集群地址
     */
    @Value("${elasticsearch.ip}")
    private String hostName;
    /**
     * 端口
     */
    @Value("${elasticsearch.port}")
    private String port;
    /**
     * 集群名称
     */
    @Value("${elasticsearch.cluster-name}")
    private String clusterName;

    /**
     * 连接池
     */
    @Value("${elasticsearch.pool}")
    private String poolSize;

    @Bean
    public TransportClient init() {

        TransportClient transportClient = null;

        try {
            // 配置信息
            Settings esSetting = Settings.builder()
                    .put("cluster.name", clusterName)
                    .put("client.transport.sniff", true)//增加嗅探机制，找到ES集群
                    //.put("xpack.security.user", "elastic:changeme")
                    .put("thread_pool.search.size", Integer.parseInt(poolSize))//增加线程池个数，暂时设为5
                    .build();

            transportClient = new PreBuiltTransportClient(esSetting);
            String servers[] = hostName.split(",");
            for (String serverHost : servers) {
            	InetSocketTransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(InetAddress.getByName(serverHost), Integer.valueOf(port));
                transportClient.addTransportAddresses(inetSocketTransportAddress);			
			}
        } catch (Exception e) {
            LOGGER.error("elasticsearch TransportClient create error!!!", e);
        }
        return transportClient;
    }
    
    @Bean
    public Executor getAsyncExecutor() {
         ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setCorePoolSize(5);
            taskExecutor.setMaxPoolSize(10);
            taskExecutor.setQueueCapacity(25);
            taskExecutor.initialize();
            return taskExecutor;
    }

}