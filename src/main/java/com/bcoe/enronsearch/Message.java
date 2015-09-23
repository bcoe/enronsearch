package com.bcoe.enronsearch;

import java.util.Enumeration;
import java.io.IOException;

import javax.mail.*;
import javax.mail.internet.*;

/*
Munge MIME message data into
an easier to work with object
for indexing.
*/
public class Message
{

  private String id;
  private String to;
  private String from;
  private String subject;
  private String body = "";
  private String cc;
  private String bcc;
  
  public String getCc() {
	return cc;
}

public String getBcc() {
	return bcc;
}

public String getDateString() {
	return dateString;
}

private String dateString;

  public Message(MimeMessage rawMessage) {
    extractHeaders(rawMessage);
    extractBody(rawMessage);
  }

  private void extractHeaders(MimeMessage rawMessage) {
    try {

      for (Enumeration<Header> e = rawMessage.getAllHeaders(); e.hasMoreElements();) {
        Header h = e.nextElement();

        switch (h.getName()) {
          case "From":
            from = h.getValue(); 
            break;
          case "To":
            to = h.getValue(); 
            break;
          case "Subject":
            subject = h.getValue(); 
            break;
          case "Message-ID":
            id = h.getValue(); 
            break;
          case "Cc":
        	cc = h.getValue();
        	break;
          case "Bcc":
        	bcc = h.getValue();
        	break;
          case "Date":
        	dateString = h.getValue();
        	break;
        }
      }

      extractBody(rawMessage);

    } catch (MessagingException e) {
      System.out.println("failed to extract message headers: " + e);
    }
  }

  private void extractBody(MimeMessage rawMessage) {
    try {
      
      Object contentObject = rawMessage.getContent();

      if(contentObject instanceof Multipart) {

        BodyPart clearTextPart = null;
        Multipart content = (Multipart)contentObject;

        for(int i = 0; i < content.getCount(); i++) {
          BodyPart part =  content.getBodyPart(i);

          if(part.isMimeType("text/plain")) {
            body = (String) part.getContent();
            break;
          }

        }
      } else if (contentObject instanceof String) {
        body = (String) contentObject;
      }
    } catch (MessagingException e) {
      System.out.println("Failed to parse body from MIME message: " + e);
    } catch (IOException e) {
      System.out.println("Failed to read MIME part: " + e);
    }
  }

  public String getId() {
    return id;
  }

  public String getTo() {
    return to;
  }

  public String getFrom() {
    return from;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public String toString() {
    return "to: " + getTo() + "\nfrom: " + getFrom() + "\nsubject: " + getSubject() + "\n\n" + body + "\n\n";
  }
}
