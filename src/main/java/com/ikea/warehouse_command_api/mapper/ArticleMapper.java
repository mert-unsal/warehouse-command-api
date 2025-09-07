package com.ikea.warehouse_command_api.mapper;

import com.ikea.warehouse_command_api.data.dto.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.dto.ArticleResponse;
import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ArticleDocument toDocument(ArticleCommandRequest req);

    @Mapping(target = "id", expression = "java(doc.id() != null ? doc.id().toHexString() : null)")
    ArticleResponse toResponse(ArticleDocument doc);

    default ObjectId toObjectId(String id) {
        return id == null ? null : new ObjectId(id);
    }
}
