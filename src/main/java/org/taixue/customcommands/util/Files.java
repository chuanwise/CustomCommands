package org.taixue.customcommands.util;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.Objects;

public class Files {

    /**
     * 将一个 jar 内的文件复制到某地
     * @param srcFilePath   jar 内的文件名
     * @param fos           复制到的 File
     * @return boolean      true 当且仅当复制成功
     */
    public static boolean fileCopy(@NotNull String srcFilePath, @NotNull File fos){
        boolean flag = false;
        try {
            try (BufferedInputStream fis = new BufferedInputStream(Objects.requireNonNull(Files.class.getClassLoader().getResourceAsStream(srcFilePath)));
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
