package com.rbkmoney.auto.approve.handler;

import com.rbkmoney.auto.approve.AutoApproveApplication;
import com.rbkmoney.auto.approve.service.ClaimManagementService;
import com.rbkmoney.auto.approve.service.DominantService;
import com.rbkmoney.damsel.claim_management.*;
import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryRef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoApproveApplication.class)
public class ClaimReviewedHandlerTest {

    @Autowired
    private ClaimReviewedHandler claimReviewedHandler;

    @MockBean
    private ClaimManagementService claimManagementService;

    @MockBean
    private DominantService dominantService;

    @Before
    public void setUp() {
        given(claimManagementService.get(any(), any(), any())).willReturn(
                new Claim().setChangeset(List.of(
                        new ModificationUnit().setModification(Modification.party_modification(
                                PartyModification.shop_modification(new ShopModificationUnit()
                                        .setModification(ShopModification.category_modification(new CategoryRef()))))),
                        new ModificationUnit().setModification(Modification.party_modification(
                                PartyModification.contract_modification(new ContractModificationUnit()))),
                        new ModificationUnit().setModification(Modification.party_modification(
                                PartyModification.contractor_modification(new ContractorModificationUnit())))
                )));
        given(dominantService.getCategory(any())).willReturn(new Category());
    }

    @Test
    public void testHandle() {
        claimReviewedHandler.handle(new Event()
                .setChange(Change.status_changed(
                        new ClaimStatusChanged()
                                .setId(111)
                                .setPartyId("party_id")
                                .setStatus(ClaimStatus.review(new ClaimReview())))));
        verify(claimManagementService, Mockito.times(1)).get(any(), any(), any());
        verify(claimManagementService, Mockito.times(1)).accept(any(), any(), any(), any());
        verify(dominantService, Mockito.times(1)).getCategory(any());
    }
}