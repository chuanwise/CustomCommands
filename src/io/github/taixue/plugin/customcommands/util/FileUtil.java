package io.github.taixue.plugin.customcommands.util;

import java.io.*;
import java.util.Objects;

public class FileUtil {

    /**
     * copy file from jar to disk
     * @param srcFilePath
     * @param fos
     * @return boolean
     */
    public static boolean fileCopy(String srcFilePath, File fos){
        boolean flag = false;
        try {
            try (BufferedInputStream fis = new BufferedInputStream(Objects.requireNonNull(FileUtil.class.getClassLoader().getResourceAsStream(srcFilePath)));
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fos))) {
                byte[] buf = new byte[1024];
                int c = 0;
                while ((c = fis.read(buf)) != -1) {
                    bufferedOutputStream.write(buf, 0, c);
                }
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException nullPointerException) {
            return false;
        }
        return flag;
    }
}
