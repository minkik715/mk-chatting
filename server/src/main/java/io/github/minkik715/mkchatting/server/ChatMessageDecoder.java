package io.github.minkik715.mkchatting.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ChatMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    ObjectMapper objectMapper;

    ChatMessageDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        ChatCommandDTO dto = objectMapper.readValue(bytes, ChatCommandDTO.class);
        list.add(dto);
    }
}
