package io.github.minkik715.mkchatting.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.github.minkik715.mkchatting.ChatHelper;
import io.github.minkik715.mkchatting.ChatMessageBaseDTO;
import io.github.minkik715.mkchatting.RoomsDTO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatMessageBaseDTO> {

    ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> roomUserChannels;

    ChatServerHandler(ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> roomUserChannels, ObjectMapper objectMapper) {
        this.roomUserChannels = roomUserChannels;
        this.objectMapper = objectMapper;
    }

    private ObjectMapper objectMapper;

    private String userId;
    private String roomId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChatHelper.sendMessage(ctx.channel(), new RoomsDTO(new ArrayList<>(roomUserChannels.keySet())));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessageBaseDTO msg) throws Exception {
        ChatCommandDTO message = (ChatCommandDTO) msg;
        if (message.command() == ChatCommandDTO.ChatCommandType.ENTER) {
            roomUserChannels.computeIfAbsent(message.room(), k -> new ConcurrentHashMap<>())
                    .put(message.userId(), ctx.channel());
            userId = message.userId();
            roomId = message.room();
        } else if (message.command() == ChatCommandDTO.ChatCommandType.EXIT) {
            removeUser(ctx);
        }
        sendMessageToRoom(roomId, message);
    }

    private void sendMessageToRoom(String roomId, ChatCommandDTO message) {
        ConcurrentHashMap<String, Channel> room = roomUserChannels.get(roomId);
        if (room != null) {
            ChatHelper.sendMessage(room.values(), message);
        }
    }

    private void sendMessageToUser(ChannelHandlerContext ctx, ChatCommandDTO message) {
        ChatHelper.sendMessage(ctx.channel(), message);
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
        if (room != null) {
            room.remove(userId);
            ctx.channel().close();
        }
    }
}
