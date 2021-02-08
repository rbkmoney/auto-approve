package com.rbkmoney.auto.approve.utils;

import com.rbkmoney.damsel.claim_management.*;

import java.util.List;
import java.util.stream.Collectors;

public class ClaimUtils {

    public static List<ShopParams> extractShopParamsList(ClaimCreated claim) {
        return claim.getChangeset().stream()
                .filter(unit -> unit.isSetPartyModification()
                        && unit.getPartyModification().isSetShopModification()
                        && unit.getPartyModification().getShopModification().getModification().isSetCreation())
                .map(unit -> unit.getPartyModification().getShopModification().getModification().getCreation())
                .collect(Collectors.toList());
    }

}
