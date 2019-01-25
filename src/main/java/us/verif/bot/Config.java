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
        }
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("config.properties");

            prop.setProperty("token", "NTIzMjQ4MTM0NDc0OTU2ODEx.DyGEwA.NcJlNbzvHFKGP4hbyVNbTQlk9_k");

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
}
