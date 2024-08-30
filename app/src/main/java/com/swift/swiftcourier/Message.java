package com.swift.swiftcourier;

public class Message {
    private String sender;
    private String receiver;
    private String messageText;
    private String timestamp;

    public Message(String sender, String receiver, String messageText, String timestamp) {

        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
