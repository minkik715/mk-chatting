package io.github.minkik715.mkchatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class GeneralMessageDecoder<T> extends MessageToMessageDecoder<ByteBuf> {

    ObjectMapper objectMapper;
    private Class<T> clazz;

    public GeneralMessageDecoder(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        T dto = objectMapper.readValue(bytes, clazz);
        list.add(dto);
    }
}
