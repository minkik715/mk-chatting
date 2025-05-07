package io.github.minkik715.mkchatting;

public record ChatCommandDTO(
        String user,
        String room,
        ChatCommandType command,
        String msg,
        String messageType
) implements ChatMessageBaseDTO{

    public ChatCommandDTO(String user, String room, ChatCommandType command, String msg) {
        this(user, room, command, msg, "command");
    }

    public enum ChatCommandType {
        ENTER,
        EXIT,
        SEND
    }
}
