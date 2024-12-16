package com.credenceid.credentialstatuscheck.service;

import com.credenceid.credentialstatuscheck.client.StatusListClient;
import com.credenceid.credentialstatuscheck.dto.StatusVerificationResult;
import com.credenceid.credentialstatuscheck.exception.CredentialStatusNetworkException;
import com.credenceid.credentialstatuscheck.exception.CredentialStatusProcessingException;
import com.credenceid.credentialstatuscheck.util.Constants;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusVerifierServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CredentialStatus credentialStatus;

    @Mock
    private Map<String, Object> jsonObjectMock;

    @BeforeEach
    void init() {
        when(credentialStatus.getJsonObject()).thenReturn(jsonObjectMock);
        when(jsonObjectMock.get("statusListCredential")).thenReturn("https://dhs-svip.github.io/ns/uscis/status/3");
    }


    @Test
    @DisplayName("testVerifyStatus_RevocationTrue will return the revocation status as True")
    void testVerifyStatus_RevocationTrue() throws IOException, CredentialStatusProcessingException, CredentialStatusNetworkException {
        String mockResource = "test_data/BitstringStatusListCredential.json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(mockResource), "Resource not found: " + mockResource).getFile());
        String mockStatusJSON = Files.readString(file.toPath());
        when(jsonObjectMock.get("statusPurpose")).thenReturn("revocation");
        when(jsonObjectMock.get("statusListIndex")).thenReturn("4000");
        when(jsonObjectMock.get("statusSize")).thenReturn("1");
        List<CredentialStatus> listOfCredentialStatus = List.of(credentialStatus);
        VerifiableCredential bitStringStatusListCredential = objectMapper.readValue(mockStatusJSON, VerifiableCredential.class);

        try (var mockClient = Mockito.mockStatic(StatusListClient.class)) {
            mockClient.when(() -> StatusListClient.fetchStatusListCredential(any()))
                    .thenReturn(bitStringStatusListCredential);
            List<StatusVerificationResult> results = StatusVerifierService.verifyStatus(listOfCredentialStatus);
            assertNotNull(results);
            assertFalse(results.isEmpty());
            assertTrue(results.getFirst().status());
            assertEquals("revocation", results.getFirst().statusPurpose());
        }
    }

    @Test
    void testVerifyStatus_StatusPurposeCompareFailure() throws IOException {
        String mockResource = "test_data/InvalidBitstringStatusListCredential.json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(mockResource), "Resource not found: " + mockResource).getFile());
        String mockStatusJSON = Files.readString(file.toPath());
        when(jsonObjectMock.get("statusListIndex")).thenReturn("1");
        when(jsonObjectMock.get("statusSize")).thenReturn("1");
        when(jsonObjectMock.get("statusPurpose")).thenReturn("revocation");
        VerifiableCredential bitStringStatusListCredential = objectMapper.readValue(mockStatusJSON, VerifiableCredential.class);
        List<CredentialStatus> listOfCredentialStatus = List.of(credentialStatus);
        try (var mockClient = Mockito.mockStatic(StatusListClient.class)) {
            mockClient.when(() -> StatusListClient.fetchStatusListCredential(any())).thenReturn(bitStringStatusListCredential);
            CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                    StatusVerifierService.verifyStatus(listOfCredentialStatus)
            );

            assertEquals(Constants.STATUS_PURPOSE_COMPARISON_ERROR_TITLE, exception.getTitle());
            assertEquals(Constants.STATUS_PURPOSE_COMPARISON_ERROR_DETAIL, exception.getDetail());
        }
    }

    @Test
    void testVerifyStatus_withInvalidStatusListIndex() {
        when(jsonObjectMock.get("statusListIndex")).thenReturn("-1");
        when(jsonObjectMock.get("statusPurpose")).thenReturn("revocation");
        List<CredentialStatus> listOfCredentialStatus = List.of(credentialStatus);
        CredentialStatusProcessingException exception = assertThrows(CredentialStatusProcessingException.class, () ->
                StatusVerifierService.verifyStatus(listOfCredentialStatus)
        );

        assertEquals(Constants.STATUS_LIST_INDEX_ERROR_TITLE, exception.getTitle());
        assertEquals(Constants.STATUS_LIST_INDEX_ERROR_DETAIL, exception.getDetail());
    }
}
