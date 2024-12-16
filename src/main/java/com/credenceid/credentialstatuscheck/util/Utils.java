package com.credenceid.credentialstatuscheck.util;

import com.credenceid.credentialstatuscheck.exception.CredentialStatusProcessingException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;


/**
 * Utility class that provides methods for decoding Base64URL-encoded strings, decompressing GZIP data,
 * and extracting specific bits at a given index from a decoded status list.
 * <p>
 * This class is designed to handle the decoding, decompression, and bitextraction logic
 * used in status list processing. It is not intended to be instantiated.
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    // Private constructor to prevent instantiation
    private Utils() {
    }

    /**
     * Decodes a Base64URL-encoded and GZIP-compressed string, validates it, and extracts the bit at a specified index.
     * It also handles decompression of the data.
     *
     * @param encodedListStr The Base64URL-encoded and compressed string prefixed with 'u'.
     *                       The 'u' prefix is removed before processing.
     * @param index          The index of the status to extract.
     * @param statusSize     The size of each status in bits.
     * @return boolean indicating whether the bit at the specified index is set (true) or not (false).
     * @throws CredentialStatusProcessingException If the encoded string is null or empty and is improperly formatted.
     */
    public static boolean decodeStatusList(String encodedListStr, int index, int statusSize) throws CredentialStatusProcessingException {
        if (encodedListStr == null || encodedListStr.isEmpty()) {
            logger.error("Encoded list is null or empty");
            throw new CredentialStatusProcessingException(Constants.ENCODED_LIST_ERROR_TITLE, Constants.ENCODED_LIST_IS_EMPTY_OR_NULL_ERROR_DETAIL);
        }

        if (!encodedListStr.startsWith("u")) {
            logger.error("Encoded list does not start with 'u': {}", encodedListStr);
            throw new CredentialStatusProcessingException(Constants.ENCODED_LIST_ERROR_TITLE, Constants.ENCODED_LIST_STARTS_WITH_U_ERROR_DETAIL);
        }
        String encodedList = encodedListStr.substring(1);

        // Validate if the string is Base64URL
        if (!isValidBase64Url(encodedList)) {
            logger.error("The provided string is not a valid Base64URL-encoded string: {}", encodedList);
            throw new CredentialStatusProcessingException(Constants.BASE64URL_ERROR_TITLE, Constants.BASE64_URL_ERROR_DETAIL);
        }

        return getBitAtIndex(encodedList, index, statusSize);
    }


    /**
     * Extracts a specific bit from a Base64URL-encoded and GZIP-compressed string at a given index.
     * This method decodes the string, decompresses it, and returns whether the bit at the specified index is set (true) or not (false).
     *
     * @param encodedString   The Base64URL-encoded and compressed string.
     * @param credentialIndex The index of the credential to retrieve the bit for.
     * @param statusSize      The size of each status in bits.
     * @return boolean indicating whether the bit at the specified index is set (true) or not (false).
     * @throws CredentialStatusProcessingException If the index is out of bounds of the decompressed data.
     */
    public static boolean getBitAtIndex(String encodedString, int credentialIndex, int statusSize) throws CredentialStatusProcessingException {
        //Decode the base64url encoded string
        byte[] decodedBytes = decodeBase64Url(encodedString);
        //Decompress the decodedBytes[]
        byte[] decompressedBytes = decompressGzip(decodedBytes);
        int index = credentialIndex * statusSize;
        if (index >= decompressedBytes.length) {
            logger.error(Constants.RANGE_ERROR_DETAIL);
            throw new CredentialStatusProcessingException(Constants.RANGE_ERROR_TITLE, Constants.RANGE_ERROR_DETAIL);
        }
        // Step 3: Access the bit at the specified index
        int byteIndex = index / 8;          // Find the byte index
        int bitPosition = index % 8;        // Find the bit within the byte
        byte byteValue = decompressedBytes[byteIndex];
        // Calculate the mask for the bit we are interested in
        int bitMask = 1 << (7 - bitPosition);  // Left-to-right indexing (MSB is 0th bit)
        // Check if the bit is set (non-zero value)
        return (byteValue & bitMask) != 0;
    }

    /**
     * Decodes a Base64URL string into a byte array.
     *
     * @param base64Url The Base64URL-encoded string.
     * @return A byte array representing the decoded data.
     * @throws IllegalArgumentException If the provided string is not a valid Base64URL string.
     */
    public static byte[] decodeBase64Url(String base64Url) {
        String standardBase64 = base64Url.replace('-', '+').replace('_', '/');
        Base64 decoder = new Base64(true);
        return decoder.decode(standardBase64);
    }

    /**
     * Decompresses a GZIP-compressed byte array.
     *
     * @param compressedData The GZIP-compressed byte array.
     * @return A byte array containing the decompressed data.
     */
    public static byte[] decompressGzip(byte[] compressedData) throws CredentialStatusProcessingException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            try (ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream()) {
                while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                    decompressedStream.write(buffer, 0, bytesRead);
                }
                return decompressedStream.toByteArray();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new CredentialStatusProcessingException("IO_EXCEPTION", e.getMessage());
        }
    }


    /**
     * Validates if the given string is a valid Base64URL-encoded string.
     * Base64URL encoding uses a URL-safe alphabet where '+' and '/' are replaced with '-' and '_', respectively,
     * and padding ('=') is not used.
     *
     * @param str The string to validate.
     * @return true if the string matches the Base64URL pattern, false otherwise.
     */
    private static boolean isValidBase64Url(String str) {
        // Base64URL strings should not have padding '=' and use '-' and '_' instead of '+' and '/'
        String base64UrlPattern = "^[A-Za-z0-9_-]+$";
        return str.matches(base64UrlPattern);
    }
}