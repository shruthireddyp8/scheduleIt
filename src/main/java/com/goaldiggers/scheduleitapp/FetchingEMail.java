package com.goaldiggers.scheduleitapp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class FetchingEMail {
	static int counter = 0;
    public static void fetch(String pop3Host, String storeType, String user,
            String password) {
        try {

            Properties properties = new Properties();
            

            String host = "pop.outlook.com";
            Session session = Session.getDefaultInstance(properties);
            Store store = session.getStore("pop3s");
            store.connect(host, 995, "", "");
            System.out.println(session.toString());

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0; i < messages.length; i++) {
            	PrintWriter writer = new PrintWriter(new File("D:\\MailMessages\\the-file-name-"+i+".txt"), "UTF-8");
                Message message = messages[i];
                System.out.println("---------------------------------");
                writePart(message,writer);
                writer.close();
            }

            // close the store and folder objects
            emailFolder.close(false);
            store.close();
            

        }
        catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String host = "pop.gmail.com";// change accordingly
        String mailStoreType = "pop3";
        String username = "abc@gmail.com";// change accordingly
        String password = "*****";// change accordingly

        // Call method fetch
        fetch(host, mailStoreType, username, password);

    }

    /*
     * This method checks for content-type based on which, it processes and
     * fetches the content of the message
     */
    public static void writePart(Part p,PrintWriter writer) throws Exception {
        if (p instanceof Message) {
            writeEnvelope((Message) p,writer);
        }

        System.out.println("----------------------------");
        System.out.println("CONTENT-TYPE: " + p.getContentType());

        // check if the content is plain text
        if (p.isMimeType("text/plain")) {
            System.out.println("This is plain text");
            System.out.println("---------------------------");
            String plainText = (String) p.getContent();
            String s[] = plainText.split("\\n");
            String tempPlainText = "";
            for(int i=0;i<=s.length-1;i++) {
            	s[i] = s[i].replace('*', '\t');
            	s[i] = s[i].replace(',', '\t');
            	if(!(s[i].contains("http") || s[i].contains("ttps")) && !(s[i].contains("Image") || s[i].contains("image")) && !(s[i].equals("\r"))) {
            		tempPlainText = tempPlainText+" "+s[i];
            	}
            }
            writer.println(tempPlainText);
            counter++;
        }
        // check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            System.out.println("This is a Multipart");
            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                writePart(mp.getBodyPart(i),writer);
            }
        }
        // check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
            System.out.println("This is a Nested Message");
            System.out.println("---------------------------");
            writePart((Part) p.getContent(),writer);
        }
        
    }

    /*
     * This method would print FROM,TO and SUBJECT of the message
     */
    public static void writeEnvelope(Message m,PrintWriter writer) throws Exception {
        System.out.println("This is the message envelope");
        System.out.println("---------------------------");
        Address[] a;

        // FROM
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++) {
            	writer.println("FROM: " + a[j].toString());
            }
        }

        // TO
        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < a.length; j++) {
                System.out.println("TO: " + a[j].toString());
            }
        }

        // SUBJECT
        if (m.getSubject() != null) {
            System.out.println("SUBJECT: " + m.getSubject());
        }

    }

}
