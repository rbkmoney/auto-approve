package com.rbkmoney.auto.approve.handler;

import com.rbkmoney.auto.approve.AutoApproveApplication;
import com.rbkmoney.auto.approve.service.ClaimManagementService;
import com.rbkmoney.auto.approve.service.DominantService;
import com.rbkmoney.damsel.claim_management.*;
import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.domain.ShopLocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoApproveApplication.class)
public class ClaimCreatedHandlerTest {

    @Autowired
    private ClaimCreatedHandler claimCreatedHandler;

    @MockBean
    private ClaimManagementService claimManagementService;

    @MockBean
    private DominantService dominantService;

    @Before
    public void setUp() {
        given(dominantService.getCategory(any())).willReturn(new Category());
    }

    @Test
    public void testHandle() {
        claimCreatedHandler.handle(new Event()
                .setChange(Change.created(new ClaimCreated()
                        .setId(111)
                        .setPartyId("party_id")
                        .setRevision(11)
                        .setChangeset(List.of(Modification.party_modification(PartyModification.shop_modification(
                                new ShopModificationUnit().setModification(ShopModification.creation(
                                        new ShopParams().setCategory(new CategoryRef(1)))))))))));
        verify(claimManagementService, Mockito.times(1)).accept(any(), any(), any(), any());
        verify(dominantService, Mockito.times(1)).getCategory(any());
    }
}