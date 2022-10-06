package com.oktested.core.network;

public class ErrorUtils {

    /*
     * public static Error parseError(Response<?> response) {
     * Converter<ResponseBody, Error> converter = RetrofitClient.getClient("")
     * .responseBodyConverter(Error.class, new Annotation[0]); Error error; try
     * { error = converter.convert(response.errorBody()); } catch (IOException
     * e) { return new Error(); } return error; }
     */

    public static class ApiErrorException extends Throwable {

        public ApiErrorException(String message) {
            super(message);
        }
    }
}