package com.credenceid.credentialstatuscheck.service;


import com.credenceid.credentialstatuscheck.client.StatusListClient;
import com.credenceid.credentialstatuscheck.dto.StatusVerificationResult;
import com.credenceid.credentialstatuscheck.exception.CredentialStatusNetworkException;
import com.credenceid.credentialstatuscheck.exception.CredentialStatusProcessingException;
import com.credenceid.credentialstatuscheck.util.Constants;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.credenceid.credentialstatuscheck.util.Utils.decodeStatusList;


public class StatusVerifierService {
    private static final Logger logger = LoggerFactory.getLogger(StatusVerifierService.class);

    private StatusVerifierService() {
    }

    /**
     * @param statusListIndex statusListIndex value of credential status
     * @return boolean whether the statusListIndex is less than zero
     */
    private static boolean validateStatusListIndex(final int statusListIndex) {
        return statusListIndex < 0;
    }

    /**
     * @param statusPurposeOfCredentialStatus  statusPurpose value of the credentialStatus
     * @param statusPurposeOfCredentialSubject statusPurpose value of the credentialSubject
     * @return boolean value of checking equality between the received string parameters.
     */
    private static boolean validateStatusPurpose(final String statusPurposeOfCredentialStatus, final String statusPurposeOfCredentialSubject) {
        return statusPurposeOfCredentialStatus.equalsIgnoreCase(statusPurposeOfCredentialSubject);
    }

    /**
     * Resolves bitstringStatusListEntry to return a List of {@link StatusVerificationResult}.
     *
     * @param listOfCredentialStatus list of CredentialStatus of the Verifiable Credential.
     * @return A List of {@link StatusVerificationResult}.
     * @throws CredentialStatusProcessingException If an error occurs during statusListIndex or statusPurpose verification.
     * @throws CredentialStatusNetworkException    If an error occurs during HTTP call
     */
    public static List<StatusVerificationResult> verifyStatus(final List<CredentialStatus> listOfCredentialStatus) throws CredentialStatusProcessingException, CredentialStatusNetworkException {
        List<StatusVerificationResult> statusVerificationResults = new ArrayList<>();
        for (CredentialStatus credentialStatus : listOfCredentialStatus) {
            Map<String, Object> credentialStatusMap = credentialStatus.getJsonObject();
            VerifiableCredential bitStringStatusListCredential;
            String statusPurpose = (String) credentialStatusMap.get("statusPurpose");
            int statusListIndex = Integer.parseInt((String) credentialStatusMap.get("statusListIndex"));
            String statusListCredential = (String) credentialStatusMap.get("statusListCredential");
            int statusSize = credentialStatusMap.get("statusSize") != null ? Integer.parseInt((String) credentialStatusMap.get("statusSize")) : 1;  //indicates the size of the status entry in bits
            if (validateStatusListIndex(statusListIndex)) {
                logger.error(Constants.STATUS_LIST_INDEX_ERROR_DETAIL);
                throw new CredentialStatusProcessingException(Constants.STATUS_LIST_INDEX_ERROR_TITLE, Constants.STATUS_LIST_INDEX_ERROR_DETAIL);
            }
            //fetch BitstringStatusListCredential.
            bitStringStatusListCredential = StatusListClient.fetchStatusListCredential(statusListCredential);
            //validation of statusPurpose of credentialStatus and credentialSubject
            if (!validateStatusPurpose(statusPurpose, (String) bitStringStatusListCredential.getCredentialSubject().getJsonObject().get("statusPurpose"))) {
                logger.error(Constants.STATUS_PURPOSE_COMPARISON_ERROR_TITLE);
                throw new CredentialStatusProcessingException(Constants.STATUS_PURPOSE_COMPARISON_ERROR_TITLE, Constants.STATUS_PURPOSE_COMPARISON_ERROR_DETAIL);
            }
            //encodedList
            String encodedList = (String) bitStringStatusListCredential.getCredentialSubject().getJsonObject().get("encodedList");
            boolean decodedIndexValue = decodeStatusList(encodedList, statusListIndex, statusSize);
            statusVerificationResults.add(new StatusVerificationResult(statusPurpose, decodedIndexValue));
        }
        return statusVerificationResults;
    }
}
