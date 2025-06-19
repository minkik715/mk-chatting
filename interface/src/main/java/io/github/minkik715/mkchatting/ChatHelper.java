package io.github.minkik715.mkchatting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;

public class ChatHelper {
    static ObjectMapper objectMapper = new ObjectMapper();
    public static boolean encrypt = true;
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "1234567890123456".getBytes(); // 16바이트 키 예시 (128비트)
    private static final SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);

    public static <T> void sendMessage(Channel channel, T message) {
        ByteBuf byteBuf = createByteBufMessage(message, encrypt);
        try {
            channel.writeAndFlush(byteBuf.retainedDuplicate());
        } finally {
            byteBuf.release();
        }
    }

    public static <T> void sendMessage(Collection<Channel> channels, T message) {
        ByteBuf byteBuf = createByteBufMessage(message, encrypt);
        try {
            for (Channel ch : channels) {
                ch.writeAndFlush(byteBuf.retainedDuplicate());
            }
        } finally {
            byteBuf.release();
        }
    }

    private static <T> ByteBuf createByteBufMessage(T message, boolean encrypt) {
        try {
            byte[] msgBytes = objectMapper.writeValueAsBytes(message);
            if (encrypt) {
                msgBytes = encrypt(msgBytes);
            }
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(msgBytes.length);
            buf.writeBytes(msgBytes);
            return buf;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt message", e);
        }
    }

    private static byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}
