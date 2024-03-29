package com.rbkmoney.auto.approve.configuration;

import com.rbkmoney.damsel.domain_config.RepositoryClientSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class RepositoryClientConfiguration {

    @Bean
    public RepositoryClientSrv.Iface dominantClient(
            @Value("${dominant.client.url}") Resource resource,
            @Value("${dominant.client.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(RepositoryClientSrv.Iface.class);
    }
}
