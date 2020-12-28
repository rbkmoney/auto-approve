package com.rbkmoney.auto.approve.utils;

import com.rbkmoney.damsel.claim_management.Claim;
import com.rbkmoney.damsel.claim_management.ContractModificationUnit;
import com.rbkmoney.damsel.claim_management.ContractorModificationUnit;
import com.rbkmoney.damsel.claim_management.ShopModificationUnit;

import java.util.List;
import java.util.stream.Collectors;

public class ClaimUtils {

    public static List<ShopModificationUnit> extractShopModification(Claim claim) {
        return claim.getChangeset().stream()
                .filter(unit -> unit.getModification().isSetPartyModification()
                        && unit.getModification().getPartyModification().isSetShopModification())
                .map(unit -> unit.getModification().getPartyModification().getShopModification())
                .collect(Collectors.toList());
    }

    public static List<ContractModificationUnit> extractContractModification(Claim claim) {
        return claim.getChangeset().stream()
                .filter(unit -> unit.getModification().isSetPartyModification()
                        && unit.getModification().getPartyModification().isSetContractModification())
                .map(unit -> unit.getModification().getPartyModification().getContractModification())
                .collect(Collectors.toList());
    }

    public static List<ContractorModificationUnit> extractContractorModification(Claim claim) {
        return claim.getChangeset().stream()
                .filter(unit -> unit.getModification().isSetPartyModification()
                        && unit.getModification().getPartyModification().isSetContractorModification())
                .map(unit -> unit.getModification().getPartyModification().getContractorModification())
                .collect(Collectors.toList());
    }

}
