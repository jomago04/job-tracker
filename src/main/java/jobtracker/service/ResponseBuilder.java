package jobtracker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility class for building JSON responses for the REST API.
 * Provides consistent JSON serialization across all endpoints.
 *
 * Uses Gson for JSON conversion with nice formatting for readability.
 */
public class ResponseBuilder {
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    /**
     * Convert an object to JSON string.
     * Handles nulls gracefully and includes null fields in output.
     *
     * @param object The object to serialize (can be null)
     * @return JSON string representation, or "null" if object is null
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Create a success response object with data.
     * Useful for GET endpoints that return results.
     *
     * @param data The data to include in response
     * @return JSON string
     */
    public static String success(Object data) {
        return toJson(data);
    }

    /**
     * Create an error response object.
     * Includes error message and optional error code.
     *
     * @param message Human-readable error message
     * @return JSON string with error details
     */
    public static String error(String message) {
        return toJson(new ErrorResponse(message, null));
    }

    /**
     * Create an error response object with error code.
     *
     * @param message Human-readable error message
     * @param errorCode Application-specific error code
     * @return JSON string with error details
     */
    public static String error(String message, String errorCode) {
        return toJson(new ErrorResponse(message, errorCode));
    }

    /**
     * Simple error response structure for JSON serialization.
     */
    static class ErrorResponse {
        public String error;
        public String code;

        ErrorResponse(String error, String code) {
            this.error = error;
            this.code = code;
        }
    }
}
