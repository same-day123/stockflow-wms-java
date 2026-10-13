package org.jeecg.modules.wms.shunfeng.other;

import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class VerifyCodeUtil {
    public VerifyCodeUtil() {
    }

    public static String loadFile(String fileName) {
        try {
            InputStream fis = new FileInputStream(fileName);
            byte[] bs = new byte[fis.available()];
            fis.read(bs);
            String res = new String(bs);
            fis.close();
            return res;
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static String md5EncryptAndBase64(String str) {
        return encodeBase64(md5Encrypt(str));
    }

    private static byte[] md5Encrypt(String encryptStr) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encryptStr.getBytes("utf8"));
            return md5.digest();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    private static String encodeBase64(byte[] b) {
        String str = (new Base64()).encodeAsString(b);
        return str;
    }
}
