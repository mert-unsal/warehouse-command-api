package com.ikea.warehouse_command_api.mapper;

import com.ikea.warehouse_command_api.data.document.ArticleDocument;
import com.ikea.warehouse_command_api.data.request.ArticleCommandRequest;
import com.ikea.warehouse_command_api.data.response.ArticleResponse;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ArticleDocument toDocument(ArticleCommandRequest articleCommandRequest);

    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    ArticleResponse toResponse(ArticleDocument articleDocument);

    @Named("objectIdToString")
    default String objectIdToString(ObjectId objectId) {
        return Optional.ofNullable(objectId).map(ObjectId::toHexString).orElse(null);
    }

}
