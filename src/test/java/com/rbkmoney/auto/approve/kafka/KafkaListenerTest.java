package com.rbkmoney.auto.approve.kafka;

import com.rbkmoney.auto.approve.handler.ClaimCreatedHandler;
import com.rbkmoney.damsel.claim_management.*;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.domain.ShopLocation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
public class KafkaListenerTest extends AbstractKafkaTest {

    @Value("${kafka.topic}")
    public String topic;

    private KafkaTemplate<String, Event> kafkaTemplate = new KafkaTemplate<>(createProducerFactory());

    @MockBean
    private ClaimCreatedHandler reviewedHandler;

    @Before
    public void setUp(){
        given(reviewedHandler.isAccept(any())).willCallRealMethod();
    }

    @Test
    public void testReviewedHandler() throws InterruptedException, ExecutionException {
        Thread.sleep(2000L);
        kafkaTemplate.send(topic, "kek", buildEvent()).get();
        Mockito.verify(reviewedHandler, Mockito.timeout(3000L).times(1)).handle(any());
    }

    private Event buildEvent() {
        return new Event()
                .setOccuredAt("date_time")
                .setUserInfo(new UserInfo()
                        .setId("id")
                        .setEmail("kek@kek.ru")
                        .setUsername("kekke")
                        .setType(UserType.internal_user(new InternalUser())))
                .setChange(Change.created(new ClaimCreated()
                        .setPartyId("party_id")
                        .setId(12456)
                        .setCreatedAt("kekkekekekekek")
                        .setRevision(11)
                        .setChangeset(List.of(Modification.party_modification(PartyModification.shop_modification(
                                new ShopModificationUnit()
                                        .setId("kek")
                                        .setModification(ShopModification.creation(
                                                new ShopParams()
                                                        .setLocation(ShopLocation.url("rrr"))
                                                        .setContractId("ee")
                                                        .setDetails(new ShopDetails("ff"))
                                                        .setPayoutToolId("1111")
                                                        .setCategory(new CategoryRef(1))))))))));
    }
}
