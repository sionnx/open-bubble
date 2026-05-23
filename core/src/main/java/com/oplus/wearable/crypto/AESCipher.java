package com.oplus.wearable.crypto;

import logcat.AndroidLogcatLogger;
import logcat.LogPriority;

/**
 * JNI 封装，包名/类名与 {@code libencrypt.so} 导出符号一致，保证 Consult 握手与固件兼容。
 */
public final class AESCipher {
    private static final String TAG = "AESCipher";
    private static final AndroidLogcatLogger LOGGER = new AndroidLogcatLogger();

    static {
        System.loadLibrary("encrypt");
    }

    private final Object lock = new Object();

    private static native int decrypt(byte[] data, byte[] key, byte[] out, int length);

    private static native int decrypt256(byte[] data, byte[] key, byte[] out, byte[] iv, int length);

    private static native int encrypt(byte[] data, byte[] key, byte[] out, int length);

    private static native int encrypt256(byte[] data, byte[] key, byte[] out, byte[] iv, int length);

    private static native int generatorKey(long time1, long time2, byte[] out);

    public byte[] deriveIdentityKey(long localRandom, long remoteRandom) {
        synchronized (lock) {
            byte[] key = new byte[16];
            if (generatorKey(localRandom, remoteRandom, key) == 0) {
                return key;
            }
            LOGGER.log(LogPriority.ERROR, TAG, "deriveIdentityKey failed");
            return null;
        }
    }

    public byte[] encryptAes128(byte[] data, byte[] key) {
        if (key == null || data == null || data.length == 0) {
            throw new IllegalArgumentException("Missing argument");
        }
        byte[] out = data.length % 16 == 0
                ? new byte[data.length + 1]
                : new byte[(((data.length / 16) + 1) * 16) + 1];
        synchronized (lock) {
            if (encrypt(data, key, out, data.length) > 0) {
                return out;
            }
        }
        LOGGER.log(LogPriority.ERROR, TAG, "encryptAes128 failed");
        return null;
    }

    public byte[] decryptAes128(byte[] cipher, byte[] key) {
        if (key == null || cipher == null || cipher.length == 0) {
            throw new IllegalArgumentException("Missing argument");
        }
        int length = cipher.length;
        byte[] buf = new byte[length];
        synchronized (lock) {
            int decrypted = decrypt(cipher, key, buf, length);
            if (decrypted <= 0 || decrypted > length) {
                LOGGER.log(LogPriority.ERROR, TAG, "decryptAes128 failed: decrypted=" + decrypted + ", length=" + length);
                return null;
            }
            byte[] result = new byte[decrypted];
            System.arraycopy(buf, 0, result, 0, decrypted);
            return result;
        }
    }

    public byte[] encryptAes256(byte[] data, byte[] key, byte[] iv) {
        if (key == null || data == null || data.length == 0) {
            throw new IllegalArgumentException("Missing argument");
        }
        int blocks = data.length / 16;
        int remainder = data.length % 16;
        byte[] payload;
        byte[] framed;
        if (remainder == 0) {
            framed = new byte[data.length + 1];
            payload = new byte[data.length];
            framed[0] = 0;
        } else {
            int padded = (blocks + 1) * 16;
            framed = new byte[padded + 1];
            framed[0] = (byte) (16 - remainder);
            payload = new byte[padded];
        }
        System.arraycopy(data, 0, payload, 0, data.length);
        synchronized (lock) {
            if (encrypt256(payload, key, framed, iv, payload.length) <= 0) {
                LOGGER.log(LogPriority.ERROR, TAG, "encryptAes256 failed");
                return null;
            }
            System.arraycopy(payload, 0, framed, 1, payload.length);
            return framed;
        }
    }

    public byte[] decryptAes256(byte[] cipher, byte[] key, byte[] iv) {
        if (key == null || cipher == null || cipher.length == 0) {
            throw new IllegalArgumentException("Missing argument");
        }
        int padFlag = cipher[0];
        int length = cipher.length - 1;
        byte[] body = new byte[length];
        System.arraycopy(cipher, 1, body, 0, length);
        int strip = padFlag == 16 ? 0 : padFlag;
        synchronized (lock) {
            int decrypted = decrypt256(body, key, body, iv, length) - strip;
            if (decrypted <= 0 || decrypted > length) {
                LOGGER.log(LogPriority.ERROR, TAG, "decryptAes256 failed: decrypted=" + decrypted + ", length=" + length + ", strip=" + strip);
                return null;
            }
            byte[] result = new byte[decrypted];
            System.arraycopy(body, 0, result, 0, decrypted);
            return result;
        }
    }
}
