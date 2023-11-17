package cn.ms.car.common.utils;

import cn.ms.car.common.utils.sign.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

abstract public class IOUtils {

    public static String inputStream2Base64(InputStream in) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            org.apache.commons.io.IOUtils.copy(in, out);
            byte[] bytes = out.toByteArray();
            return Base64.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }
}
