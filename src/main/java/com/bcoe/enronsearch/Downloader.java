package com.bcoe.enronsearch;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.github.axet.wget.WGet;

/*
Downloads the Enron dataset.
*/
public class Downloader 
{

  private static String enronURL = "http://www.cs.cmu.edu/~enron/enron_mail_20110402.tgz";
  private static String downloadPath = "./dataset/enron_mail.tgz";

  public void download() {
    try {
        URL url = new URL( enronURL );
        File target = new File( downloadPath );
        WGet w = new WGet(url, target);
        w.download();
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (RuntimeException allDownloadExceptions) {
        allDownloadExceptions.printStackTrace();
    }
  }
}
