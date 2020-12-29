package com.rbkmoney.auto.approve.service.dominant;

import com.rbkmoney.auto.approve.exception.NotFoundException;
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
public class DominantService {

    private final RepositoryClientSrv.Iface dominantClient;

    public Category getCategory(Integer categoryId) {
        log.debug("Trying to get CategoryObject, categoryId={}", categoryId);
        CategoryRef categoryRef = new CategoryRef();
        categoryRef.setId(categoryId);
        VersionedObject versionedObject = getVersionedObjectFromReference(com.rbkmoney.damsel.domain.Reference.category(categoryRef), 0L);
        return versionedObject.getObject().getCategory().getData();
    }

    private VersionedObject getVersionedObjectFromReference(com.rbkmoney.damsel.domain.Reference reference, Long revisionVersion) {
        log.info("Trying to get VersionedObject, reference={}, revisionVersion={}", reference, revisionVersion);
        try {
            Reference referenceRevision;
            if (revisionVersion == null || revisionVersion == 0) {
                referenceRevision = Reference.head(new Head());
            } else {
                referenceRevision = Reference.version(revisionVersion);
            }
            VersionedObject versionedObject = checkoutObject(referenceRevision, reference);
            log.debug("VersionedObject {} has been found, reference={}", versionedObject, reference);
            return versionedObject;
        } catch (VersionNotFound | ObjectNotFound ex) {
            throw new NotFoundException(String.format("Version not found, reference=%s", reference), ex);
        } catch (TException ex) {
            throw new DominantException(String.format("Failed to get payment institution, reference=%s", reference), ex);
        }
    }

    private VersionedObject checkoutObject(Reference revisionReference, com.rbkmoney.damsel.domain.Reference reference) throws TException {
        log.debug("checkoutObject revisionReference={}, reference={}", revisionReference, reference);
        return dominantClient.checkoutObject(revisionReference, reference);
    }

}
