package xyz.synse.economy.utils;

import java.io.File;
import java.io.IOException;

public class Maker {
    public static File mkDir(File file){
        if(!file.exists())
            file.mkdirs();

        return file;
    }

    public static File mkFile(File file){
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return file;
    }
}
