package com.rbkmoney.auto.approve.service.impl;

import com.rbkmoney.auto.approve.exception.NotFoundException;
import com.rbkmoney.auto.approve.service.DominantService;
import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain_config.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DominantServiceImpl implements DominantService {

    private final RepositoryClientSrv.Iface dominantClient;

    @Override
    public Category getCategory(Integer categoryId) {
        log.info("Trying to get CategoryObject, categoryId={}", categoryId);
        var reference = com.rbkmoney.damsel.domain.Reference.category(new CategoryRef().setId(categoryId));
        Reference referenceRevision = Reference.head(new Head());
        VersionedObject versionedObject;
        try {
            versionedObject = dominantClient.checkoutObject(referenceRevision, reference);
        } catch (VersionNotFound | ObjectNotFound e) {
            throw new NotFoundException(String.format("Version or object not found, categoryId=%d", categoryId), e);
        } catch (TException e) {
            throw new RuntimeException("Thrift exception while processed event", e);
        }
        log.info("Received CategoryObject, categoryId={}", categoryId);
        return versionedObject.getObject().getCategory().getData();
    }
}
