package com.rbkmoney.auto.approve.handler;

import com.rbkmoney.auto.approve.service.ClaimManagementService;
import com.rbkmoney.auto.approve.service.DominantService;
import com.rbkmoney.auto.approve.utils.ClaimUtils;
import com.rbkmoney.damsel.claim_management.*;
import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimCreatedHandler implements EventHandler<Event>{

    private final ClaimManagementService claimManagementServiceImpl;
    private final DominantService dominantServiceImpl;

    @Override
    public boolean isAccept(Event event) {
        return event.getChange().isSetCreated();
    }

    @Override
    @SneakyThrows
    public void handle(Event event) {
        ClaimCreated claimCreated = event.getChange().getCreated();
        String partyId = claimCreated.getPartyId();
        long claimId = claimCreated.getId();

        List<ShopParams> shopParamsList = ClaimUtils.extractShopParamsList(claimCreated);
        shopParamsList.forEach(shopParams -> {
            int optionalCategoryId = shopParams.getCategory().getId();
            Category category = dominantServiceImpl.getCategory(optionalCategoryId);
            if (CategoryType.test.equals(category.getType())) {
                claimManagementServiceImpl.accept(event.getUserInfo(), partyId, claimId, claimCreated.getRevision());
            }
        });
    }
}
