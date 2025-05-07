package io.github.minkik715.mkchatting.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatCommandDTO> {

    ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> roomUserChannels;

    ChatServerHandler(ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> roomUserChannels, ObjectMapper objectMapper) {
        this.roomUserChannels = roomUserChannels;
        this.objectMapper = objectMapper;
    }
    private ObjectMapper objectMapper;

    private String userId;
    private String roomId;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatCommandDTO msg) throws Exception {
        System.out.println(msg.toString());
        if(msg.command() == ChatCommandDTO.ChatCommandType.ENTER){
            roomUserChannels.computeIfAbsent(msg.room(), k -> new ConcurrentHashMap<>())
                    .put(msg.user(), ctx.channel());
            userId = msg.user();
            roomId = msg.room();
            sentMessage(msg);
        }else if(msg.command() == ChatCommandDTO.ChatCommandType.EXIT){
            removeUser(ctx);
        }else{
            sentMessage(msg);
        }
    }

    private void sentMessage(ChatCommandDTO message) {
        ConcurrentHashMap<String, Channel> room = roomUserChannels.get(message.room());
        if(room != null){
            byte[] msg = null;
            try {
                msg = objectMapper.writeValueAsBytes(message);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            byte[] finalMsg = msg;
            System.out.println(room.values().size());
            room.values().forEach((channel) ->{
                ByteBuf buf = channel.alloc().buffer(finalMsg.length);
                buf.writeBytes(finalMsg);
                channel.writeAndFlush(buf);
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        removeUser(ctx);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        removeUser(ctx);
        ctx.close();
    }

    private void removeUser(ChannelHandlerContext ctx) {
        ConcurrentHashMap<String, Channel> room = roomUserChannels.get(roomId);
        if(room != null){
            room.remove(userId);
            ctx.channel().close();
        }
    }
}
