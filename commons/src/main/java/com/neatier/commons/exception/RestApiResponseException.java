/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.exception;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import com.fernandocejas.arrow.optional.Optional;
import com.google.gson.JsonSyntaxException;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.commons.helpers.LongTaskOnIOScheduler;
import com.neatier.commons.helpers.LongTaskScheduler;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rx.Observer;

/**
 * An {@link ErrorBundleException} subclass for presenting an error message when a Rest Api related
 * error occurs.
 *
 * @author László Gálosi
 * @since 31/07/15
 */
public class RestApiResponseException extends ErrorBundleException {

    /**
     * Status code response info argument key.
     */
    public static final String RESP_STATUS = "Resp-Status";

    /**
     * {@link ErrorKind} response info argument key.
     */
    public static final String RESP_KIND = "Resp-ErrorKind";

    /**
     * Error reason response info argument key.
     */
    public static final String RESP_REASON = "Resp-Reason";

    /**
     * Response info argument key containing the json serialized response string from the Rest Api
     * if available.
     */
    public static final String RESP_BODY = "Resp-Body";

    /**
     * Response info argument key containing the request url for which the error response resulted.
     */
    public static final String RESP_URL_FROM = "Resp-Url-From";

    /**
     * Response info argument key containing the error stack trace.
     */
    public static final String STACK_TRACE = "StackTrace";

    /**
     * Response info argument key containing the response url of the RestApi error.
     */
    public static final String RESP_SENT = "Resp-Sent";

    /**
     * A json serialized response body representing an unspecified error with the given
     * String format parameter argument.
     */
    public static final String EMPTY_RESPONSE_BODY =
          "{\"error\":\"empty response body\",\"error_description\":\"%s\"}";
    public static final String TIMEOUT_ERROR = "timeout";

    /**
     * Response info argument key containing the raw response from the Rest Api if available.
     */
    public static final String RAW_ERROR_RESPONSE = "raw";

    /**
     * A json serialized string with unknown Rest API error.
     */
    static final String UNKNOWN_ERROR_RESPONSE =
          "{\"error\":\"unknown_error\",\"error_description\":\"Unknown error from the server\"}";
    /**
     * Response info containing key-value pairs of error information.
     */
    protected KeyValuePairs<String, Object> mResponseInfo;

    /**
     * {@link JsonSerializer} used for response body deserialization.
     */
    private JsonSerializer<ErrorResponse> mJsonSerializer;

    /**
     * The error response object.
     */
    private ErrorResponse mErrorResponse;

    /**
     * The response in raw error format.
     */
    private String mRawErrorResponse;

    /**
     * {@link LongTaskScheduler} for response body asynchronous serialization.
     */
    private LongTaskScheduler mSerializeOn;

    /**
     * The status code of the Rest API error.
     */
    private int mStatusCode;

    /**
     * The remote address of the Rest Api request of which the response arrived.
     */
    private String mRemoteAddress;

    /**
     * Constructor with the given response info key-value pairs.
     */
    public RestApiResponseException(final KeyValuePairs<String, Object> params) {
        this(params, null);
    }

    /**
     * Constructor with the given response info key-value pairs and a {@link Throwable cause}-
     */
    public RestApiResponseException(final KeyValuePairs<String, Object> params,
          final Throwable cause) {
        super((String) params.getOrDefault(RESP_REASON, "Server response: Unknown error:"), cause);
        this.mResponseInfo = params;
        this.mSerializeOn = new LongTaskOnIOScheduler();
        this.mJsonSerializer = new JsonSerializer<>();
        this.mStatusCode = (int) params.getOrDefault(RESP_STATUS, -1);
        this.mRemoteAddress = (String) params.get(RESP_URL_FROM);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("RestApiResponseException{");
        sb.append("ResponseInfo=").append(mResponseInfo);
        sb.append(", tatusCode=").append(mStatusCode);
        sb.append(", errorKind=").append(getKind());
        sb.append(", url=").append(mRemoteAddress);
        sb.append(", ErrorResponse=").append(mErrorResponse);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns the error kind of the Rest Api error.
     */
    public ErrorKind getKind() {
        int errorKindId =
              (int) mResponseInfo.getOrDefaultChecked(RESP_KIND, ErrorKind.UNEXPECTED);
        return ErrorKind.find(errorKindId).get();
    }

    /**
     * Initialize the {@link ErrorResponse} object by serializing the response body if available or
     * the {@link #UNKNOWN_ERROR_RESPONSE} will be the response body.
     */
    public void initErrorResponse() {
        try {
            this.mErrorResponse = mJsonSerializer.deserialize(
                  (String) mResponseInfo.getOrDefault(RESP_BODY, UNKNOWN_ERROR_RESPONSE),
                  ErrorResponse.class);
        } catch (JsonSyntaxException e) {
            mRawErrorResponse =
                  ((String) mResponseInfo.getOrDefault(RESP_BODY, UNKNOWN_ERROR_RESPONSE))
                        .replaceAll("\\n", "");
            mErrorResponse = new ErrorResponse(RAW_ERROR_RESPONSE, mRawErrorResponse);
        }
    }

    /**
     * Returns true if the request should be repeated if {@link #getKind()} kind of error happens.
     */
    public boolean shouldRetry() {
        int errorKindId =
              (int) mResponseInfo.getOrDefaultChecked(RESP_KIND, ErrorKind.UNEXPECTED);
        return errorKindId == ErrorKind.NETWORK || errorKindId == ErrorKind.SERVER;
    }

    /**
     * Returns the status code of this Rest Api error from the response info key-value pairs.
     */
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * Returns the reason of this Rest Api error from the response info key-value pairs.
     */
    public @Nullable String getReason() {
        return (String) mResponseInfo.get(RESP_REASON);
    }

    /**
     * Returns the response body as a json serialized string from the response info key-value pairs.
     */
    public @Nullable String getResponseBody() {
        return (String) mResponseInfo.get(RESP_BODY);
    }

    /**
     * Returns the response info of this RestApi error as key-value pairs.
     */
    public KeyValuePairs<String, Object> getResponseInfo() {
        return mResponseInfo;
    }

    /**
     * Creates and returns the error response object by de-serializing the response body.
     */
    public ErrorResponse getErrorResponse() {
        if (mErrorResponse == null) {
            mResponseInfo.getJsonSerializedAsync(RESP_BODY, ErrorResponse.class, mJsonSerializer)
                         .subscribeOn(mSerializeOn.notifyMeOn())
                         .observeOn(mSerializeOn.notifyMeOn())
                         .subscribe(new Observer<ErrorResponse>() {
                             @Override public void onCompleted() {

                             }

                             @Override public void onError(final Throwable e) {
                                 mErrorResponse = new ErrorResponse(
                                       RAW_ERROR_RESPONSE,
                                       (String) mResponseInfo.getOrDefault(RESP_BODY, ""));
                                 mRawErrorResponse =
                                       (String) mResponseInfo.getOrDefault(RESP_BODY, "");
                             }

                             @Override public void onNext(final ErrorResponse er) {
                                 mErrorResponse = er;
                             }
                         });
        }
        return mErrorResponse;
    }

    /**
     * Returns the remote address of the Rest Api request of which the error response resulted.
     */
    public String getRemoteAddress() {
        return mRemoteAddress;
    }

    /**
     * Error response class encapsulating an short error code, an error message and and a verbose
     * longer error description strings.
     */
    public static class ErrorResponse {
        public String error;
        public String errorDescription;
        public String message;

        public ErrorResponse(final String error, final String errorDescription) {
            this.error = error;
            this.errorDescription = errorDescription;
        }

        @Override public String toString() {
            final StringBuilder sb = new StringBuilder("ErrorResponse{");
            sb.append("error='").append(error).append('\'');
            sb.append(", errorDescription='").append(errorDescription).append('\'');
            sb.append(", message='").append(message).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Enum class for classification of a Rest Api error.
     */

    public static class ErrorKind {

        /**
         * Network connection related kind of error.
         */
        public static final int NETWORK = 0;

        /**
         * User authentication related kind of error.
         */
        public static final int AUTHENTICATION = 1;

        /**
         * A Rest Api request error related kind of error. Use this when some request
         * parameters missing or invalid.
         */
        public static final int CLIENT = 2;

        /**
         * A server accessing related eror kind.
         */
        public static final int SERVER = 3;

        /**
         * An unexpected kind of error.
         */
        public static final int UNEXPECTED = 4;

        /**
         * A request related kind of error. Use this, when the reason and kind of error is known
         * from the Rest Api response.
         */
        public static final int REQUEST = 5;

        /**
         * Sparse array containing all the available error kind objects with its id-s
         */
        private static SparseArray<ErrorKind> values = new SparseArray<>(6);

        static {
            values.put(NETWORK, new ErrorKind(NETWORK, "Network", "Network connection error"));
            values.put(AUTHENTICATION,
                       new ErrorKind(AUTHENTICATION, "Auth", "Server authentication error"));
            values.put(CLIENT, new ErrorKind(CLIENT, "Client", "Client error"));
            values.put(SERVER, new ErrorKind(SERVER, "Server", "Server error"));
            values.put(UNEXPECTED,
                       new ErrorKind(UNEXPECTED, "Unexpected", "Unexpected fatal error"));
            values.put(REQUEST,
                       new ErrorKind(REQUEST, "Request", "Server responded with an error."));
        }

        /**
         * The error kind id of this error kind object.
         */
        public @ErrorKindId int id;

        /**
         * The error kind name.
         */
        public String name;

        /**
         * The error kind description.
         */
        public String description;

        /**
         * Static instance getter method for {@link #REQUEST} error kind.
         */
        public static ErrorKind request() {
            return find(REQUEST).get();
        }

        /**
         * Returns an error kind object identified by the given id, as an optional if found.
         */
        public static Optional<ErrorKind> find(int id) {
            return Optional.fromNullable(values.get(id));
        }

        /**
         * Returns an error kind object instance by the given HTTP status code
         */
        public static ErrorKind getByCode(int code) {
            @ErrorKindId int errorKindId;
            if (code == 401) {
                errorKindId = AUTHENTICATION;
            } else if (code >= 400 && code < 500) {
                errorKindId = CLIENT;
            } else if (code >= 500 && code < 600) {
                errorKindId = SERVER;
            } else {
                errorKindId = UNEXPECTED;
            }
            return ErrorKind.find(errorKindId).get();
        }

        public ErrorKind(final int id, final String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        @Override public String toString() {
            return name;
        }

        /**
         * {@link IntDef} annotation for identifying unique error kinds.
         */
        @IntDef({ NETWORK, AUTHENTICATION, CLIENT, SERVER, UNEXPECTED, REQUEST })
        @Retention(RetentionPolicy.SOURCE)
        public @interface ErrorKindId {
        }
    }
}
