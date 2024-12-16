package com.credenceid.credentialstatuscheck.util;

/**
 * A utility class that contains constant error messages used throughout the application.
 * These constants are used to provide standardized error descriptions for various scenarios
 * such as network issues, validation failures, and range errors.
 * <p>
 * The class is designed to be non-instantiable, with a private constructor to prevent
 * object creation.
 * </p>
 */
public class Constants {
    public static final String STATUS_LIST_NETWORK_ERROR_TITLE = "STATUS_LIST_NETWORK_ERROR";
    public static final String STATUS_LIST_NETWORK_ERROR_DETAIL =
            "Error occurred during http call to statusListCredential endpoint";
    public static final String STATUS_PURPOSE_COMPARISON_ERROR_TITLE =
            "STATUS_PURPOSE_COMPARISON_ERROR";
    public static final String STATUS_PURPOSE_COMPARISON_ERROR_DETAIL =
            "statusPurpose value of the credentialStatus and statusPurpose value of the credentialSubject are not equal";
    public static final String STATUS_LIST_INDEX_ERROR_TITLE =
            "STATUS_LIST_INDEX_ERROR";
    public static final String STATUS_LIST_INDEX_ERROR_DETAIL =
            "statusListIndex must be greater than or equal to zero";
    public static final String RANGE_ERROR_TITLE = "RANGE ERROR";
    public static final String RANGE_ERROR_DETAIL =
            "A provided value is outside of the expected range of an associated value, such as a given index value for an array being larger than the current size of the array.";
    public static final String ENCODED_LIST_IS_EMPTY_OR_NULL_ERROR_DETAIL = "Encoded string cannot be null or empty";
    public static final String ENCODED_LIST_ERROR_TITLE = "ENCODED_LIST_ERROR";
    public static final String ENCODED_LIST_STARTS_WITH_U_ERROR_DETAIL = "The received encoded list doesn't start with 'u'. the encoded list must start with the letter 'u'";
    public static final String BASE64URL_ERROR_TITLE = "BASE64URL_ERROR";
    public static final String BASE64_URL_ERROR_DETAIL = "The provided string is not a valid Base64URL-encoded string";

    private Constants() {
    }
}
