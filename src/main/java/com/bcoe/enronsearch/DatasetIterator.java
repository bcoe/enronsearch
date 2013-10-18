package com.bcoe.enronsearch;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.*;

import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.utils.*;
import org.apache.commons.compress.archivers.tar.*;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
Iterates over enron_mail.tar and emits
message objects for indexing.
*/
public class DatasetIterator 
{

  private static String gzipInputPath = "./dataset/enron_mail.tgz";
  private static String tarOutputPath = "./dataset/enron_mail.tar";
  private TarArchiveInputStream tarInputStream;

  public DatasetIterator() {
    try {
      unGzip();
      initializeTarInputStream();
    } catch ( FileNotFoundException e ) {
      System.out.println("Could not load corpus from archive: " + e);
    } catch ( IOException e ) {
      System.out.println("Exception occurred loading archive: " + e);
    } catch ( ArchiveException e ) {
      System.out.println("Archive appears to be corrupt: " + e);
    }
  }

  private void initializeTarInputStream() throws FileNotFoundException, ArchiveException  {
    File in = new File(tarOutputPath);

    if (!in.exists()) {
      System.out.println(tarOutputPath + " not found. download dataset first with --download.");
      throw new FileNotFoundException();
    }

    InputStream is = new FileInputStream(tarOutputPath); 
    tarInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
  }

  public Message nextMessage() {
    try {
      TarArchiveEntry entry = null;

      // Fetch the first non-folder entry.
      while ((entry = (TarArchiveEntry)tarInputStream.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          break;
        }
      }

      // we've reached the end of the archive.
      if (entry == null) {
        return null;
      }

      return new Message(new MimeMessage(null, tarInputStream));

    } catch (IOException e) {
      System.out.println("Failed to read message from tar: " + e);
    } catch (MessagingException e) {
      System.out.println("Failed to load MIME Message: " + e);
    }

    return null;
  }

  private static void unGzip() throws FileNotFoundException, IOException {
    System.out.println("unzipping dataset.");

    final File outputFile = new File(tarOutputPath);
    final File inputFile = new File(gzipInputPath);

    if (outputFile.exists()) {
      System.out.println("dataset already unzipped.");
      return;
    }

    if (!inputFile.exists()) {
      System.out.println(gzipInputPath + " not found. download dataset first with --download.");
      throw new FileNotFoundException();
    }

    final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
    final FileOutputStream out = new FileOutputStream(outputFile);

    IOUtils.copy(in, out);

    in.close();
    out.close();

    System.out.println("dataset unzipped.");
  }
}
