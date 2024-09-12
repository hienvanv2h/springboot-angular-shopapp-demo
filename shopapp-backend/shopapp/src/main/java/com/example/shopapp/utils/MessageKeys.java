package com.example.shopapp.utils;

public class MessageKeys {

    // User
    public static final String LOGIN_SUCCESS = "user.login.login_success";
    public static final String LOGIN_FAILED = "user.login.login_failed";
    public static final String INVALID_CREDENTIALS = "user.login.invalid_credentials";

    public static final String PASSWORD_MISMATCH = "user.register.password_mismatch";
    public static final String REGISTER_SUCCESS = "user.register.register_success";
    public static final String PHONE_EXISTED = "user.register.phone_existed";
    public static final String ROLE_NOT_ALLOWED = "user.register.role_not_allowed";
    public static final String ROLE_NOT_FOUND = "user.register.role_not_found";
    public static final String USER_NOT_FOUND = "user.not_found";
    public static final String USER_NOT_FOUND_DEFAULT = "user.not_found_default";
    public static final String USER_IS_LOCKED = "user.is_locked";

    // Token
    public static final String INVALID_TOKEN_FORMAT = "token.invalid_format";


    // Category
    public static final String CATEGORY_NOT_FOUND = "category.not_found";
    public static final String CATEGORY_CREATE_SUCCESS = "category.create_success";
    public static final String CATEGORY_DELETE_SUCCESS = "category.delete_success";

    // Order
    public static final String ORDER_NOT_FOUND = "order.not_found";
    public static final String ORDER_DELETE_SUCCESS = "order.delete_success";
    public static final String ORDER_SHIPPING_DATE_INVALID = "order.invalid_shipping_date";

    // Order Detail
    public static final String ORDER_DETAIL_NOT_FOUND= "order_detail.not_found";
    public static final String ORDER_DETAIL_DELETE_SUCCESS= "order_detail.delete_success";

    // Product
    public static final String PRODUCT_NOT_FOUND= "product.not_found";
    public static final String PRODUCT_CREATE_SUCCESS= "product.create_success";
    public static final String PRODUCT_DELETE_SUCCESS= "product.delete_success";
    public static final String PRODUCT_IMAGE_UPLOAD_SUCCESS= "product.image.upload_success";
    public static final String PRODUCT_IMAGE_MAXIMUM_QUANTITY = "product.image.validation.maximum_quantity";
    public static final String PRODUCT_IMAGE_MAXIMUM_SIZE= "product.image.validation.maximum_size";
    public static final String PRODUCT_IMAGE_INVALID_FILETYPE= "product.image.validation.invalid_filetype";

}
