package com.warba.banking.customer.mapper;


import com.warba.banking.customer.model.mapper.AddressMapper;
import com.warba.banking.customer.model.request.CreateAddressRequest;
import com.warba.banking.customer.repository.entity.Address;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class AddressMapperTest {

    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    @Test
    void testToAddress() {
        // Given a CreateAddressRequest
        CreateAddressRequest request = new CreateAddressRequest("Home", "Country", "City", "Street", 10);

        // When mapping to Address
        Address address = addressMapper.toAddress(request);

        // Then the fields should map correctly.
        assertNotNull(address);
        assertEquals("Home", address.getLabel());
        assertEquals("Country", address.getCountry());
        assertEquals("City", address.getCity());
        assertEquals("Street", address.getStreet());
        assertEquals(10, address.getBuildingNumber());
        // The id is ignored so it should remain null.
        assertNull(address.getId());
    }
}
