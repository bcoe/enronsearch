package com.bcoe.enronsearch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.*;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Unit test for Dataset iterator.
 */
public class MessageTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MessageTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MessageTest.class );
    }

    public void testExtractHeaders() throws IOException, MessagingException
    {
        FileInputStream fis = new FileInputStream( new File("./fixtures/msg001.eml") );
        Message message = new Message(new MimeMessage(null, fis));
        fis.close();
        assertEquals(message.getTo(), "ben@attachments.me");
    }

    public void testExtractBody() throws IOException, MessagingException
    {
        FileInputStream fis = new FileInputStream( new File("./fixtures/msg001.eml") );
        Message message = new Message(new MimeMessage(null, fis));
        fis.close();
        assertTrue( message.getBody().contains("State: OK") );
    }
}
