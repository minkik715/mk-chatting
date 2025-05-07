package io.github.minkik715.mkchatting;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
        property = "messageType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatCommandDTO.class, name = "command"),
        @JsonSubTypes.Type(value = RoomsDTO.class, name = "rooms")
})
public interface ChatMessageBaseDTO { }
