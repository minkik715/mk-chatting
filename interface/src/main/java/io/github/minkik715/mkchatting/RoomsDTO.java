package io.github.minkik715.mkchatting;

import java.util.List;

public record RoomsDTO(List<String> rooms, String messageType) implements ChatMessageBaseDTO {
    public RoomsDTO(List<String> rooms){
        this(rooms, "rooms");
    }
}
