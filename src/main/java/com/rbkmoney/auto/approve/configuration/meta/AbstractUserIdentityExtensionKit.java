package com.rbkmoney.auto.approve.configuration.meta;

import com.rbkmoney.woody.api.trace.Metadata;
import com.rbkmoney.woody.api.trace.context.metadata.MetadataConversionException;
import com.rbkmoney.woody.api.trace.context.metadata.MetadataConverter;
import com.rbkmoney.woody.api.trace.context.metadata.MetadataExtension;
import com.rbkmoney.woody.api.trace.context.metadata.MetadataExtensionKitImpl;
import lombok.Getter;

@Getter
public abstract class AbstractUserIdentityExtensionKit extends MetadataExtensionKitImpl<String> {

    private final String key;

    public AbstractUserIdentityExtensionKit(String keyVal) {
        super(
                new MetadataExtension<>() {

                    @Override
                    public String getValue(Metadata metadata) {
                        return metadata.getValue(keyVal);
                    }

                    @Override
                    public void setValue(String val, Metadata metadata) {
                        metadata.putValue(keyVal, val);
                    }
                },
                new MetadataConverter<>() {

                    @Override
                    public String convertToObject(String key, String value) throws MetadataConversionException {
                        return value;
                    }

                    @Override
                    public String convertToString(String key, String value) throws MetadataConversionException {
                        return value;
                    }

                    @Override
                    public boolean apply(String key) {
                        return key.equalsIgnoreCase(keyVal);
                    }
                }
        );
        this.key = keyVal;
    }
}
