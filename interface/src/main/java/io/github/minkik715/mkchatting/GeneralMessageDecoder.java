package io.github.minkik715.mkchatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import static io.github.minkik715.mkchatting.ChatHelper.encrypt;

public class GeneralMessageDecoder<T> extends MessageToMessageDecoder<ByteBuf> {

    private final ObjectMapper objectMapper;
    private final Class<T> clazz;
    private final boolean decryptEnabled;

    public GeneralMessageDecoder(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
        this.decryptEnabled = encrypt;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        if (decryptEnabled) {
            bytes = ChatHelper.decrypt(bytes);
        }

        T dto = objectMapper.readValue(bytes, clazz);
        out.add(dto);
    }
}
