package com.rbkmoney.auto.approve.configuration;

import com.rbkmoney.auto.approve.configuration.meta.UserIdentityEmailExtensionKit;
import com.rbkmoney.auto.approve.configuration.meta.UserIdentityIdExtensionKit;
import com.rbkmoney.auto.approve.configuration.meta.UserIdentityRealmExtensionKit;
import com.rbkmoney.auto.approve.configuration.meta.UserIdentityUsernameExtensionKit;
import com.rbkmoney.auto.approve.configuration.properties.ClaimManagementClientProperties;
import com.rbkmoney.damsel.claim_management.ClaimManagementSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableConfigurationProperties({ClaimManagementClientProperties.class})
public class ClaimManagementClientConfiguration {

    @Bean
    public ClaimManagementSrv.Iface storageSrv(ClaimManagementClientProperties properties) throws IOException {
        return new THSpawnClientBuilder()
                .withMetaExtensions(
                        Arrays.asList(
                                UserIdentityIdExtensionKit.INSTANCE,
                                UserIdentityEmailExtensionKit.INSTANCE,
                                UserIdentityUsernameExtensionKit.INSTANCE,
                                UserIdentityRealmExtensionKit.INSTANCE
                        )
                )
                .withAddress(properties.getUrl().getURI())
                .withNetworkTimeout(properties.getNetworkTimeout())
                .build(ClaimManagementSrv.Iface.class);
    }

}
