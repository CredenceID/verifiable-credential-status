package com.credenceid.credentialstatuscheck.dto;

/**
 * A record representing the result of a status verification.
 *
 * @param statusPurpose The purpose of the status verification.
 * @param status        The status result (true if valid, false otherwise).
 */
public record StatusVerificationResult(String statusPurpose, boolean status) {
}
