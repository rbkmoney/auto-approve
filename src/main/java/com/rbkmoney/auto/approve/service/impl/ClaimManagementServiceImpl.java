package com.rbkmoney.auto.approve.service.impl;

import com.rbkmoney.auto.approve.configuration.meta.*;
import com.rbkmoney.auto.approve.exception.NotFoundException;
import com.rbkmoney.auto.approve.service.ClaimManagementService;
import com.rbkmoney.damsel.claim_management.*;
import com.rbkmoney.woody.api.flow.WFlow;
import com.rbkmoney.woody.api.trace.ContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimManagementServiceImpl implements ClaimManagementService {

    private final ClaimManagementSrv.Iface claimManagementClient;

    @Override
    public Claim get(UserInfo userInfo, String partyId, Long claimId) {
        if (userInfo == null) {
            throw new IllegalArgumentException("UserInfo must be set for requests to claim management");
        }
        log.info("Find a claim for partyId={}, claimId={}", partyId, claimId);
        Claim claim = null;
        try {
            claim = new WFlow().createServiceFork(
                    () -> {
                        ContextUtils.setCustomMetadataValue(UserIdentityIdExtensionKit.KEY, userInfo.getId());
                        ContextUtils.setCustomMetadataValue(UserIdentityEmailExtensionKit.KEY, userInfo.getEmail());
                        ContextUtils.setCustomMetadataValue(UserIdentityUsernameExtensionKit.KEY, userInfo.getUsername());
                        ContextUtils.setCustomMetadataValue(UserIdentityRealmExtensionKit.KEY, getType(userInfo));
                        return claimManagementClient.getClaim(partyId, claimId);
                    }
            ).call();
        } catch (ClaimNotFound e) {
            throw new NotFoundException(String.format("Claim not found, partyId=%s, claimId=%d", partyId, claimId), e);
        } catch (TException e) {
            throw new RuntimeException("Thrift exception while processed event", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Received claim with partyId={}, claimId={}", partyId, claimId);
        return claim;

    }

    @Override
    public void accept(UserInfo userInfo, String partyId, Long claimId, Integer claimRevision) {
        log.info("Accept a claim for partyId={}, claimId={}", partyId, claimId);
        new WFlow().createServiceFork(() -> {
                    ContextUtils.setCustomMetadataValue(UserIdentityIdExtensionKit.KEY, userInfo.getId());
                    ContextUtils.setCustomMetadataValue(UserIdentityEmailExtensionKit.KEY, userInfo.getEmail());
                    ContextUtils.setCustomMetadataValue(UserIdentityUsernameExtensionKit.KEY, userInfo.getUsername());
                    ContextUtils.setCustomMetadataValue(UserIdentityRealmExtensionKit.KEY, getType(userInfo));
                    try {
                        claimManagementClient.acceptClaim(partyId, claimId, claimRevision);
                    } catch (ClaimNotFound e) {
                        throw new NotFoundException(String.format("Claim not found for partyId=%s, claimId=%d, claimRevision=%d", partyId, claimId, claimRevision), e);
                    } catch (InvalidClaimRevision | InvalidClaimStatus e) {
                        throw new RuntimeException(String.format("Invalid claim state for partyId=%s, claimId=%d, claimRevision=%d", partyId, claimId, claimRevision), e);
                    } catch (TException e) {
                        throw new RuntimeException("Thrift exception while processed event", e);
                    }
                }
        ).run();
        log.info("Claim has been accepted for partyId={}, claimId={}", partyId, claimId);
    }

    private UserTypeEnum getType(UserInfo userInfo) {
        if (userInfo.getType().isSetInternalUser()) {
            return UserTypeEnum.internal;
        } else if (userInfo.getType().isSetExternalUser()) {
            return UserTypeEnum.external;
        } else {
            throw new IllegalArgumentException("Unknown userInfo type: " + userInfo.getType());
        }
    }
}
