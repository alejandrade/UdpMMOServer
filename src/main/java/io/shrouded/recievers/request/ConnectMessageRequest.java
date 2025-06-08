package io.shrouded.recievers.request;

public record ConnectMessageRequest(String jwt, String playerName) implements PayloadMessageRequest { }
