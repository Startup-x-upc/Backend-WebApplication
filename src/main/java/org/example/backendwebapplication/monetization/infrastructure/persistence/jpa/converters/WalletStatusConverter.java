package org.example.backendwebapplication.monetization.infrastructure.persistence.jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.backendwebapplication.monetization.domain.model.valueobjects.WalletStatus;

@Converter(autoApply = true)
public class WalletStatusConverter implements AttributeConverter<WalletStatus, String> {

    @Override
    public String convertToDatabaseColumn(WalletStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public WalletStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : WalletStatus.valueOf(dbData);
    }
}