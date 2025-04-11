package itstep.learning.androidpv211.chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import itstep.learning.androidpv211.R;
import itstep.learning.androidpv211.orm.ChatMessage;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {
    private ChatMessage chatMessage;
    private final TextView tvAuthor;
    private final TextView tvText;

    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        tvAuthor = itemView.findViewById( R.id.chat_msg_author );
        tvText = itemView.findViewById( R.id.chat_msg_text );
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        tvAuthor.setText( this.chatMessage.getAuthor() );
        tvText.setText( this.chatMessage.getText() );
    }
}
