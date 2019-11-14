package com.goaldiggers.scheduleitapp;

import java.util.Arrays;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class OutlookMail {

    public static void main(String[] args) throws MessagingException {

        Properties properties = new Properties();

        String host = "pop.outlook.com";
        Session session = Session.getDefaultInstance(properties);
        Store store = session.getStore("pop3s");
        store.connect(host, 995, "scheduleit05@outlook.com", "Villanova$1");
        System.out.println(session.toString());
        Folder inbox = store.getFolder("INBOX");

        inbox.open(Folder.READ_WRITE);

        int count = 0;
        // Fetch unseen messages from inbox folder
        Message[] messages = inbox
                .search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        // Sort messages from recent to oldest
        Arrays.sort(messages, (m1, m2) -> {
            try {
                return m2.getSentDate().compareTo(m1.getSentDate());
            }
            catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        for (Message message : messages) {
            count++;

            System.out.println("sendDate: " + message.getSentDate()
                    + " subject:" + message.getSubject() + " body: "
                    + message.getDescription());
        }

        inbox.close(true);

        store.close();

    }

}
