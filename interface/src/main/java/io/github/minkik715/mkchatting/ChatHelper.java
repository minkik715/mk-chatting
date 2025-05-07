package io.github.minkik715.mkchatting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;

import java.util.Collection;


public class ChatHelper {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> void sendMessage(Channel channel, T message) {
        ByteBuf byteBuf = createByteBufMessage(message);
        try {
            channel.writeAndFlush(byteBuf.retainedDuplicate());
        } finally {
            byteBuf.release();
        }
    }

    public static <T> void sendMessage(Collection<Channel> channels, T message) {
        ByteBuf byteBuf = createByteBufMessage(message);
        try {
            for (Channel ch : channels) {
                ch.writeAndFlush(byteBuf.retainedDuplicate());
            }
        } finally {
            byteBuf.release();
        }
    }

    private static <T> ByteBuf createByteBufMessage(T message) {
        try {
            byte[] msgBytes = objectMapper.writeValueAsBytes(message);
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(msgBytes.length);
            buf.writeBytes(msgBytes);
            return buf;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }
}

