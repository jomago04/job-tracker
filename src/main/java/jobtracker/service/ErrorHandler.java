package jobtracker.service;

import spark.Response;

/**
 * Utility class for handling errors and setting appropriate HTTP responses.
 * Centralizes error handling logic for consistency across all REST endpoints.
 */
public class ErrorHandler {

    /**
     * Handle a "not found" error (resource doesn't exist).
     * Sets HTTP status to 404 and returns error JSON.
     *
     * @param res Spark Response object
     * @param message Description of what was not found
     * @return JSON error response
     *
     * Example: resource with ID "abc123" not found
     */
    public static String notFound(Response res, String message) {
        res.status(404);
        return ResponseBuilder.error(message, "NOT_FOUND");
    }

    /**
     * Handle a "bad request" error (validation failed).
     * Sets HTTP status to 400 and returns error JSON.
     *
     * @param res Spark Response object
     * @param message Description of validation error
     * @return JSON error response
     *
     * Example: missing required field "email"
     */
    public static String badRequest(Response res, String message) {
        res.status(400);
        return ResponseBuilder.error(message, "BAD_REQUEST");
    }

    /**
     * Handle a "conflict" error (duplicate resource or business logic violation).
     * Sets HTTP status to 409 and returns error JSON.
     *
     * @param res Spark Response object
     * @param message Description of conflict
     * @return JSON error response
     *
     * Example: email already exists in system
     */
    public static String conflict(Response res, String message) {
        res.status(409);
        return ResponseBuilder.error(message, "CONFLICT");
    }

    /**
     * Handle an internal server error (unexpected exception).
     * Sets HTTP status to 500 and returns error JSON.
     *
     * @param res Spark Response object
     * @param message Description of error
     * @return JSON error response
     *
     * Example: database connection failed
     */
    public static String internalError(Response res, String message) {
        res.status(500);
        return ResponseBuilder.error(message, "INTERNAL_ERROR");
    }

    /**
     * Handle an internal server error with exception details.
     * Logs stack trace and returns generic error message (don't expose internal details).
     *
     * @param res Spark Response object
     * @param e The exception that occurred
     * @return JSON error response with generic message
     */
    public static String internalError(Response res, Exception e) {
        e.printStackTrace();  // Log for debugging
        res.status(500);
        return ResponseBuilder.error("An unexpected error occurred", "INTERNAL_ERROR");
    }

    /**
     * Set HTTP status to 500 with error message.
     * Use this when the error is definitely internal/server-side.
     *
     * @param res Spark Response object
     * @param message Error message
     * @return JSON formatted error
     */
    public static String error(Response res, int statusCode, String message) {
        res.status(statusCode);
        return ResponseBuilder.error(message);
    }
}
