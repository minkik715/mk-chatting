package io.github.minkik715.mkchatting.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.github.minkik715.mkchatting.ChatHelper;
import io.github.minkik715.mkchatting.ChatMessageBaseDTO;
import io.github.minkik715.mkchatting.RoomsDTO;
import io.netty.channel.*;

import java.io.BufferedReader;
import java.util.concurrent.CompletableFuture;

public class ChatClientHandler extends SimpleChannelInboundHandler<ChatMessageBaseDTO> {

    private ObjectMapper objectMapper;
    private String userId;
    private String nickname;
    private BufferedReader reader;
    private CompletableFuture<String> roomId;

    ChatClientHandler(String userId, String nickname, ObjectMapper objectMapper, CompletableFuture<String> roomId, BufferedReader reader) {
        this.objectMapper = objectMapper;
        this.nickname = nickname;
        this.userId = userId;
        this.roomId = roomId;
        this.reader = reader;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "channel Active");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessageBaseDTO msg) throws Exception {
        if (msg instanceof ChatCommandDTO message) {
            message.printChatMessage(userId);
        } else if (msg instanceof RoomsDTO rooms) {
            System.out.println("채팅방 목록:");
            for (int i = 0; i < rooms.rooms().size(); i++) {
                System.out.printf("[%d] %s%n", i, rooms.rooms().get(i));
            }

            System.out.print("입장할 방 번호를 입력하세요: ");
            int choice = Integer.parseInt(reader.readLine());
            String room = rooms.rooms().get(choice);
            ChatCommandDTO enter = new ChatCommandDTO(userId, nickname, room, ChatCommandDTO.ChatCommandType.ENTER, userId + "님이 입장하셨습니다.");
            ChatHelper.sendMessage(ctx.channel(), enter);
            this.roomId.complete(room);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
