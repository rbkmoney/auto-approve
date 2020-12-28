package com.rbkmoney.auto.approve.service;

import com.rbkmoney.auto.approve.configuration.meta.*;
import com.rbkmoney.auto.approve.exception.NotFoundException;
import com.rbkmoney.damsel.claim_management.Claim;
import com.rbkmoney.damsel.claim_management.ClaimManagementSrv;
import com.rbkmoney.damsel.claim_management.ClaimNotFound;
import com.rbkmoney.damsel.claim_management.UserInfo;
import com.rbkmoney.woody.api.flow.WFlow;
import com.rbkmoney.woody.api.trace.ContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimManagementService {

    private final ClaimManagementSrv.Iface claimManagementClient;

    public Claim get(UserInfo userInfo, String partyId, Long claimId) {
        if (userInfo == null) {
            throw new IllegalArgumentException("UserInfo must be set for requests to claim management");
        }
        log.info("Find a claim for partyId={}, claimId={}", partyId, claimId);
        try {
            Claim claim = new WFlow().createServiceFork(
                    () -> {
                        ContextUtils.setCustomMetadataValue(UserIdentityIdExtensionKit.KEY, userInfo.getId());
                        ContextUtils.setCustomMetadataValue(UserIdentityEmailExtensionKit.KEY, userInfo.getEmail());
                        ContextUtils.setCustomMetadataValue(UserIdentityUsernameExtensionKit.KEY, userInfo.getUsername());
                        ContextUtils.setCustomMetadataValue(UserIdentityRealmExtensionKit.KEY, getType(userInfo));
                        return claimManagementClient.getClaim(partyId, claimId);
                    }
            ).call();
            log.info("Received claim with partyId={}, docID={}", partyId, claimId);
            return claim;

        } catch (ClaimNotFound e) {
            throw new NotFoundException(String.format("Claim not found partyId=%s, docId=%s", partyId, claimId), e);
        } catch (TException ex) {
            throw new RuntimeException("Thrift exception while processed event", ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void accept(UserInfo userInfo, String partyId, Long claimId, Integer claimRevision) {
        if (userInfo == null) {
            throw new IllegalArgumentException("UserInfo must be set for requests to claim management");
        }
        log.info("Accept a claim for partyId={}, claimId={}", partyId, claimId);
        try {
            claimManagementClient.acceptClaim(partyId, claimId, claimRevision);
        } catch (TException ex) {
            throw new RuntimeException("Thrift exception while processed event", ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
