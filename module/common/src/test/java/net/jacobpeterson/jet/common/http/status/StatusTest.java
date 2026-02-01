package net.jacobpeterson.jet.common.http.status;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class StatusTest {

    @Test
    public void isInformational() {
        assertTrue(Status.CONTINUE_100.isInformational());
        assertFalse(Status.OK_200.isInformational());
    }

    @Test
    public void isSuccessful() {
        assertTrue(Status.OK_200.isSuccessful());
        assertFalse(Status.MULTIPLE_CHOICES_300.isSuccessful());
    }

    @Test
    public void isRedirection() {
        assertTrue(Status.MULTIPLE_CHOICES_300.isRedirection());
        assertFalse(Status.BAD_REQUEST_400.isRedirection());
    }

    @Test
    public void isClientError() {
        assertTrue(Status.BAD_REQUEST_400.isClientError());
        assertFalse(Status.INTERNAL_SERVER_ERROR_500.isClientError());
    }

    @Test
    public void isServerError() {
        assertTrue(Status.INTERNAL_SERVER_ERROR_500.isServerError());
        assertFalse(Status.CONTINUE_100.isServerError());
    }

    @Test
    public void isError() {
        assertTrue(Status.BAD_REQUEST_400.isError());
        assertTrue(Status.INTERNAL_SERVER_ERROR_500.isError());
        assertFalse(Status.CONTINUE_100.isError());
        assertFalse(Status.OK_200.isError());
    }

    @Test
    public void _toString() {
        assertEquals("200 OK", Status.OK_200.toString());
    }

    @Test
    public void forCode() {
        assertEquals(Status.OK_200, Status.forCode(200));
        assertNull(Status.forCode(999));
    }

    @Test
    public void forDescription() {
        assertEquals(Status.OK_200, Status.forDescription("OK"));
        assertNull(Status.forDescription("a"));
    }

    @Test
    public void forString() {
        assertEquals(Status.OK_200, Status.forString("200"));
        assertEquals(Status.OK_200, Status.forString("OK"));
        assertEquals(Status.OK_200, Status.forString("200 OK"));
        assertEquals(Status.INTERNAL_SERVER_ERROR_500, Status.forString("500"));
        assertEquals(Status.INTERNAL_SERVER_ERROR_500, Status.forString("INTERNAL SERVER ERROR"));
        assertEquals(Status.INTERNAL_SERVER_ERROR_500, Status.forString("internal server error"));
        assertEquals(Status.INTERNAL_SERVER_ERROR_500, Status.forString("500 Internal Server Error"));
        assertNull(Status.forString("a"));
        assertNull(Status.forString("a b"));
        assertNull(Status.forString("a b c"));
        assertNull(Status.forString("0 a"));
        assertNull(Status.forString("0 a b"));
        assertNull(Status.forString("a 0"));
    }

    @Test
    public void staticIsInformational() {
        assertTrue(Status.isInformational(100));
        assertTrue(Status.isInformational(199));
        assertFalse(Status.isInformational(99));
        assertFalse(Status.isInformational(200));
    }

    @Test
    public void staticIsSuccessful() {
        assertTrue(Status.isSuccessful(200));
        assertTrue(Status.isSuccessful(299));
        assertFalse(Status.isSuccessful(199));
        assertFalse(Status.isSuccessful(300));
    }

    @Test
    public void staticIsRedirection() {
        assertTrue(Status.isRedirection(300));
        assertTrue(Status.isRedirection(399));
        assertFalse(Status.isRedirection(299));
        assertFalse(Status.isRedirection(400));
    }

    @Test
    public void staticIsClientError() {
        assertTrue(Status.isClientError(400));
        assertTrue(Status.isClientError(499));
        assertFalse(Status.isClientError(399));
        assertFalse(Status.isClientError(500));
    }

    @Test
    public void staticIsServerError() {
        assertTrue(Status.isServerError(500));
        assertTrue(Status.isServerError(599));
        assertFalse(Status.isServerError(499));
        assertFalse(Status.isServerError(600));
    }

    @Test
    public void staticIsError() {
        assertTrue(Status.isError(400));
        assertTrue(Status.isError(500));
        assertFalse(Status.isError(100));
        assertFalse(Status.isError(200));
    }
}
