package com.rbkmoney.auto.approve.deserializer;

import com.rbkmoney.damsel.claim_management.Event;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;

public class EventDeserializer extends AbstractThriftDeserializer<Event> {

    public Event deserialize(String s, byte[] bytes) {
        return super.deserialize(bytes, new Event());
    }

}