package com.contramund.inputtypesserver.primitives;

public record ChatMessage(
     String senderName,
     ChatMessageType msgType,
     String payload,
     String date
) {
}
