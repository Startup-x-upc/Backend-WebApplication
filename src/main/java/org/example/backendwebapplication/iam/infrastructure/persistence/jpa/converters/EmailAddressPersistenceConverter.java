package org.example.backendwebapplication.iam.infrastructure.persistence.jpa.converters;

import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link EmailAddress} value objects between the domain model
 * and the persistence column (plain String).
 */
@Converter(autoApply = false)
public class EmailAddressPersistenceConverter implements AttributeConverter<EmailAddress, String> {

    @Override
    public String convertToDatabaseColumn(EmailAddress emailAddress) {
        return emailAddress == null ? null : emailAddress.address();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new EmailAddress(dbData);
    }
}
