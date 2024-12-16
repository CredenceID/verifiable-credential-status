package com.credenceid.credentialstatuscheck.exception;

/**
 * Exception thrown when there is an error related to the server processing status list.
 */
public class CredentialStatusProcessingException extends Exception {
    private final String title;
    private final String detail;

    public CredentialStatusProcessingException(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}