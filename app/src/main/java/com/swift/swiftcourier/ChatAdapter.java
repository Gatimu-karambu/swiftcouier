package com.swift.swiftcourier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private String currentUserId;

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(currentUserId)) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).bind(message);
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        SenderViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message_sender);
        }

        void bind(Message message) {
            textMessage.setText(message.getMessageText());
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message_receiver);
        }

        void bind(Message message) {
            textMessage.setText(message.getMessageText());
        }
    }

    public void addMessage(Message message) {
        if (!messageExists(message)) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }

    private boolean messageExists(Message newMessage) {
        for (Message message : messages) {
            if (message.getMessageText().equals(newMessage.getMessageText()) &&
                    message.getSender().equals(newMessage.getSender()) &&
                    message.getTimestamp().equals(newMessage.getTimestamp())) {
                return true;
            }
        }
        return false;
    }
}