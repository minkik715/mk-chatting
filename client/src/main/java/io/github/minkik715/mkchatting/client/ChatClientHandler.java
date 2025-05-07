package io.github.minkik715.mkchatting.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

public class ChatClientHandler extends SimpleChannelInboundHandler<ChatCommandDTO> {

    private ObjectMapper objectMapper;
    private String userId;

    ChatClientHandler(String userId, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.userId = userId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] msg = objectMapper.writeValueAsBytes(new ChatCommandDTO(userId, "1", ChatCommandDTO.ChatCommandType.ENTER, userId + "님이 입장하셨습니다."));
        ByteBuf buf = ctx.alloc().buffer(msg.length);
        buf.writeBytes(msg);
        ChannelFuture f = ctx.writeAndFlush(buf);
        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println(channelFuture.isSuccess() + " " + channelFuture.channel().remoteAddress() + "channel Active");
            }
        });
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatCommandDTO msg) throws Exception {
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
