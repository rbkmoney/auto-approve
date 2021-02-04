package com.rbkmoney.auto.approve.service;

import com.rbkmoney.damsel.claim_management.Claim;
import com.rbkmoney.damsel.claim_management.UserInfo;

public interface ClaimManagementService {
    Claim get(UserInfo userInfo, String partyId, Long claimId);
    void accept(UserInfo userInfo, String partyId, Long claimId, Integer claimRevision);
}
