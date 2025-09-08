package com.ikea.warehouse_command_api.util;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String INVALID_INVENTORY_DATA = "Invalid inventory data";
    public static final String INVALID_PRODUCTS_DATA = "Invalid products data";

    public static final String INVALID_JSON_FORMAT_PREFIX = "Invalid JSON format in uploaded file: ";
    public static final String CONTENT_TYPE_NOT_SUPPORTED = "Content type not supported. Please use multipart/form-data for file uploads.";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";

    public static final String PRODUCT_NOT_FOUND = "Product not found: ";
    public static final String VERSION_MISMATCH = "Version mismatch for Product with expected version_id ";
    public static final String REQUIRED_VERSION = " actual version_id is ";

}
