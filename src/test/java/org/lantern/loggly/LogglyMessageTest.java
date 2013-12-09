package org.lantern.loggly;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class LogglyMessageTest {
    @Test
    public void testStacktraces() {
        LogglyMessage message = newMessage("bubba");
        assertTrue(message.getThrowableOrigin().contains(
                "org.lantern.loggly.LogglyMessageTest.catchException"));
        assertTrue(message.getStackTrace().contains(
                "org.lantern.loggly.LogglyMessageTest.catchException"));
        assertTrue(message.getStackTrace().contains(
                "org.lantern.loggly.LogglyMessageTest.throwException"));
    }

    private LogglyMessage newMessage(String message) {
        try {
            catchException();
            return null;
        } catch (Exception e) {
            return new LogglyMessage(message, e.getMessage(), new Date())
                    .setThrowable(e);
        }
    }

    private void catchException() {
        try {
            throwException();
        } catch (Exception e) {
            throw new RuntimeException("Caught exception for no reason", e);
        }
    }

    private void throwException() {
        throw new RuntimeException("Uggah!");
    }
}