package io.github.minkik715.mkchatting;

public record ChatCommandDTO(
        String user,
        String room,
        ChatCommandType command,
        String msg
) {
    public enum ChatCommandType {
        ENTER,
        EXIT,
        SEND
    }
}
