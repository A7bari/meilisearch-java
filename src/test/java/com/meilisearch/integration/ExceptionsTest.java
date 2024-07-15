package com.meilisearch.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.meilisearch.integration.classes.AbstractIT;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.exceptions.APIError;
import com.meilisearch.sdk.exceptions.MeilisearchApiException;
import com.meilisearch.sdk.exceptions.MeilisearchCommunicationException;
import com.meilisearch.sdk.model.TaskError;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
public class ExceptionsTest extends AbstractIT {

    @BeforeEach
    public void initialize() {
        this.setUp();
    }

    @AfterAll
    static void cleanMeilisearch() {
        cleanup();
    }

    @Test
    public void testMeilisearchCommunicationException() throws Exception {
        String indexUid = "MeilisearchCommunicationException";
        Client wrongClient = new Client(new Config("http://wrongurl:1234", "masterKey"));

        assertThrows(MeilisearchCommunicationException.class, () -> wrongClient.getIndex(indexUid));
    }

    /** Test MeilisearchApiException serialization and getters */
    @Test
    public void testErrorSerializeAndGetters() {
        String message = "You must have an authorization token";
        String code = "missing_authorization_header";
        String type = "authentication_error";
        String link =
                "https://www.meilisearch.com/docs/reference/errors/error_codes#missing_authorization_header";
        try {
            throw new MeilisearchApiException(new APIError(message, code, type, link));
        } catch (MeilisearchApiException e) {
            assertThat(e.getMessage(), is(equalTo(message)));
            assertThat(e.getCode(), is(equalTo(code)));
            assertThat(e.getType(), is(equalTo(type)));
            assertThat(e.getLink(), is(equalTo(link)));
        }
    }

    /** Test Task Error Getters */
    @Test
    public void testTaskErrorGetters() {
        TaskError error = new TaskError();
        error.setCode("wrong field");
        assertThat(error.getCode(), is(equalTo("wrong field")));
    }

    /** Test MeilisearchApiException is thrown on Meilisearch bad request */
    @Test
    public void testMeilisearchApiExceptionBadRequest() throws Exception {
        String indexUid = "MeilisearchApiExceptionBadRequest";
        assertThrows(MeilisearchApiException.class, () -> client.getIndex(indexUid));
        try {
            client.getIndex(indexUid);
        } catch (MeilisearchApiException e) {
            assertThat(e.getCode(), is(equalTo("index_not_found")));
        }
    }
}
