package com.monetware.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /**
     * Saves the string into the file
     * @param string The string to be saved
     * @param file The file the string to be saved in
     */
    public static void saveStringToFile(String string, File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(string.getBytes());
        } catch (IOException exception) {
            exception.printStackTrace(System.out);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace(System.out);
            }
        }
    }

}
