package io.github.minkik715.mkchatting;

import java.text.SimpleDateFormat;
import java.util.Date;

public record ChatCommandDTO(
        String userId,
        String nickname,
        String room,
        ChatCommandType command,
        String msg,
        Date date,
        String messageType
) implements ChatMessageBaseDTO{

    public ChatCommandDTO(String user, String nickname, String room, ChatCommandType command, String msg) {
        this(user,nickname, room, command, msg, new Date(), "command");
    }

    public enum ChatCommandType {
        ENTER,
        EXIT,
        SEND
    }

    public void printChatMessage(String userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(date);

        boolean isMe = this.userId.equals(userId);
        String color = isMe ? "\u001B[34m" : "\u001B[0m"; // 파랑 or 기본색
        String reset = "\u001B[0m";

        System.out.printf("%s[%s][%s][%s] : %s%s\n", color, room, time, nickname, msg, reset);
    }
}
