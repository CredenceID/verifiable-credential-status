package com.credenceid.credentialstatuscheck.util;

import com.credenceid.credentialstatuscheck.exception.CredentialStatusProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    void testDecodeStatusList_validEncodedString() throws CredentialStatusProcessingException {
        String encoded = "uH4sIAAAAAAAAA-3OMQEAAAgDoEU3ugEWwENIQMI3cx0AAAAAAAAAAAAAAAAAAACgLGiNcIEAQAAA"; // Base64 encoding of "StatusListTest"
        boolean result = Utils.decodeStatusList(encoded, 4000, 1);
        assertTrue(result, "status at the index 4000 must be true");
    }

    @Test
    void testDecodeStatusList_validEncodedString_ResultFalse() throws CredentialStatusProcessingException {
        String encoded = "uH4sIAAAAAAAAA-3OMQEAAAgDoEU3ugEWwENIQMI3cx0AAAAAAAAAAAAAAAAAAACgLGiNcIEAQAAA"; // Base64 encoding of "StatusListTest"
        boolean result = Utils.decodeStatusList(encoded, 4001, 1);
        System.out.println("This is the result: " + result);
        assertFalse(result, "status at the index 4001 must be false");
    }

    @Test
    void testDecodeStatusList_emptyString() {
        CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                Utils.decodeStatusList("", 12, 1));

        assertEquals(Constants.ENCODED_LIST_ERROR_TITLE, exception.getTitle());
        assertEquals(Constants.ENCODED_LIST_IS_EMPTY_OR_NULL_ERROR_DETAIL, exception.getDetail());
    }

    @Test
    void testDecodeStatusList_nullString() {
        CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                Utils.decodeStatusList(null, 12, 1));

        assertEquals(Constants.ENCODED_LIST_ERROR_TITLE, exception.getTitle());
        assertEquals(Constants.ENCODED_LIST_IS_EMPTY_OR_NULL_ERROR_DETAIL, exception.getDetail());
    }

    @Test
    void testDecodeStatusList_invalidBase64StringNotStartsWith_u() {
        String invalidEncoded = "NotBase64!";
        assertThrows(CredentialStatusProcessingException.class, () ->
                Utils.decodeStatusList(invalidEncoded, 12, 1)
        );
        CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                Utils.decodeStatusList(invalidEncoded, 12, 1));
        assertEquals(Constants.ENCODED_LIST_ERROR_TITLE, exception.getTitle());
        assertEquals(Constants.ENCODED_LIST_STARTS_WITH_U_ERROR_DETAIL, exception.getDetail());
    }

    @Test
    void testDecodeStatusList_invalidBase64String() {
        String invalidEncoded = "uNotBase64!";
        CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                Utils.decodeStatusList(invalidEncoded, 12, 1));
        assertEquals(Constants.BASE64URL_ERROR_TITLE, exception.getTitle());
        assertEquals(Constants.BASE64_URL_ERROR_DETAIL, exception.getDetail());
    }

    @Test
    void testGetBitAtIndex_RANGE_ERROR() {
        String invalidEncoded = "H4sIAAAAAAAAA-3OMQEAAAgDoEU3ugEWwENIQMI3cx0AAAAAAAAAAAAAAAAAAACgLGiNcIEAQAAA";
        CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                Utils.getBitAtIndex(invalidEncoded, 100000, 1));
        assertEquals(Constants.RANGE_ERROR_TITLE, exception.getTitle());
        assertEquals(Constants.RANGE_ERROR_DETAIL, exception.getDetail());
    }
}
