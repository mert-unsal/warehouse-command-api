package com.ikea.warehouse_command_api.mapper;

import com.ikea.warehouse_command_api.data.document.ProductDocument;
import com.ikea.warehouse_command_api.data.dto.ProductCommandRequest;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ProductDocument toProductInsertDocument(ProductCommandRequest productCommandRequest);

    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    ProductResponse toResponse(ProductDocument productDocument);

    @Named("objectIdToString")
    default String objectIdToString(ObjectId objectId) {
        return Optional.ofNullable(objectId).map(ObjectId::toHexString).orElse(null);
    }

}
