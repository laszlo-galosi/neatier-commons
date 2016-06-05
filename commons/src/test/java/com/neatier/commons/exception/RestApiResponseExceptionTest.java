/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.exception;

import com.neatier.commons.CommonsTestCase;
import com.neatier.commons.helpers.KeyValuePairs;

import org.junit.Test;

import static com.neatier.commons.exception.RestApiResponseException.RESP_BODY;
import static com.neatier.commons.exception.RestApiResponseException.RESP_REASON;
import static com.neatier.commons.exception.RestApiResponseException.RESP_STATUS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by László Gálosi on 13/08/15
 */
public class RestApiResponseExceptionTest extends CommonsTestCase {

    private static final String FAKE_REASON = "Bad request.";
    private static final int FAKE_STATUS = 400;
    private static final String FAKE_ERROR_DESC = "The user name or password is incorrect.";
    private static final String FAKE_ERROR = "invalid_grant";
    private static final String FAKE_ERROR_RESPONSE =
            "{\"error\":\"" + FAKE_ERROR + "\",\"error_description\":\"" + FAKE_ERROR_DESC + "\"}";

    @Test
    public void test_GetErrorResponseHappyCase() throws Exception {
        RestApiResponseException restException =
                new RestApiResponseException(getFakeResponseInfo());
        RestApiResponseException.ErrorResponse errorResponse = restException.getErrorResponse();
        assertThat(restException.getMessage(), is(FAKE_REASON));
        assertThat(restException.getStatusCode(), is(FAKE_STATUS));
        assertThat(errorResponse.error, is(FAKE_ERROR));
        assertThat(errorResponse.errorDescription, is(FAKE_ERROR_DESC));
    }

    @Test
    public void test_initErrorResponseHappyCase()
            throws Exception {
        RestApiResponseException restException =
                new RestApiResponseException(getFakeResponseInfo());
        restException.initErrorResponse();
        RestApiResponseException.ErrorResponse errorResponse = restException.getErrorResponse();
        assertThat(restException.getMessage(), is(FAKE_REASON));
        assertThat(restException.getStatusCode(), is(FAKE_STATUS));
        assertThat(errorResponse.error, is(FAKE_ERROR));
        assertThat(errorResponse.errorDescription, is(FAKE_ERROR_DESC));
    }

    private KeyValuePairs<String, Object> getFakeResponseInfo() {
        return new KeyValuePairs<String, Object>()
                .put(RESP_STATUS, FAKE_STATUS)
                .put(RESP_REASON, FAKE_REASON)
                .put(RESP_BODY, FAKE_ERROR_RESPONSE);
    }
}
