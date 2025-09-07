package com.ikea.warehouse_command_api.mapper;

import com.ikea.warehouse_command_api.data.dto.ProductCommandRequest;
import com.ikea.warehouse_command_api.data.dto.ProductResponse;
import com.ikea.warehouse_command_api.data.document.ProductDocument;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    ProductDocument toDocument(ProductCommandRequest req);

    @Mapping(target = "id", expression = "java(doc.id() != null ? doc.id().toHexString() : null)")
    ProductResponse toResponse(ProductDocument doc);

    default ObjectId toObjectId(String id) {
        return id == null ? null : new ObjectId(id);
    }
}
