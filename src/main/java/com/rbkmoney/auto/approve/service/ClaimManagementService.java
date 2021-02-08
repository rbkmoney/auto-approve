package com.rbkmoney.auto.approve.service;

import com.rbkmoney.damsel.claim_management.UserInfo;

public interface ClaimManagementService {
    void accept(UserInfo userInfo, String partyId, Long claimId, Integer claimRevision);
}
