package us.verif.bot;

import java.io.*;
import java.util.Properties;

public class Config {

    public static void createConfigFile() {
        File f = new File("config.properties");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Properties prop = new Properties();
            OutputStream output = null;

            try {

                output = new FileOutputStream("config.properties");

                prop.setProperty("token", "here");
                prop.setProperty("guildId", "here");
                prop.setProperty("stripeWebhookUrl", "here");
                prop.setProperty("stripeWebhookPort", "here");

                prop.store(output, null);

            } catch (IOException io) {
                io.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public static String getToken() {

        Properties prop = new Properties();
        InputStream input = null;
        String token = null;

        try {

            input = new FileInputStream("config.properties");

            prop.load(input);

            token = prop.getProperty("token");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return token;
    }

    public static String getGuildId() {

        Properties prop = new Properties();
        InputStream input = null;
        String guildId = null;

        try {

            input = new FileInputStream("config.properties");

            prop.load(input);

            guildId = prop.getProperty("guildId");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return guildId;
    }
    public static String getStripeWebhookUrl() {

        Properties prop = new Properties();
        InputStream input = null;
        String guildId = null;

        try {

            input = new FileInputStream("config.properties");

            prop.load(input);

            guildId = prop.getProperty("stripeWebhookUrl");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return guildId;
    }
    public static String getStripeWebhookPort() {

        Properties prop = new Properties();
        InputStream input = null;
        String guildId = null;

        try {

            input = new FileInputStream("config.properties");

            prop.load(input);

            guildId = prop.getProperty("stripeWebhookPort");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return guildId;
    }
}
