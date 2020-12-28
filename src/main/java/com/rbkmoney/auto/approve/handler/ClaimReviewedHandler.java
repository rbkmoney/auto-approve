package com.rbkmoney.auto.approve.handler;

import com.rbkmoney.auto.approve.service.ClaimManagementService;
import com.rbkmoney.auto.approve.utils.ClaimUtils;
import com.rbkmoney.damsel.claim_management.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimReviewedHandler implements EventHandler<Event>{

    private final ClaimManagementService claimManagementService;

    @Override
    public boolean isAccept(Event event) {
        return event.getChange().isSetStatusChanged() &&
                event.getChange().getStatusChanged().isSetStatus() &&
                event.getChange().getStatusChanged().getStatus().isSetReview();
    }

    @Override
    @SneakyThrows
    public void handle(Event event) {
        ClaimStatusChanged statusChanged = event.getChange().getStatusChanged();
        String partyId = statusChanged.getPartyId();
        long claimId = statusChanged.getId();

        Claim claim = claimManagementService.get(event.getUserInfo(), partyId, claimId);
        List<ShopModificationUnit> shopModification = ClaimUtils.extractShopModification(claim);
        List<ContractModificationUnit> contractModification = ClaimUtils.extractContractModification(claim);
        List<ContractorModificationUnit> contractorModification = ClaimUtils.extractContractorModification(claim);

        if(!shopModification.isEmpty() && !contractModification.isEmpty() && !contractorModification.isEmpty()) {
            claimManagementService.accept(event.getUserInfo(), partyId, claimId, claim.getRevision());
        }
    }
}
