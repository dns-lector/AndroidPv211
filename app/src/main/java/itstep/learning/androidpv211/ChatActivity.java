package itstep.learning.androidpv211;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import itstep.learning.androidpv211.orm.ChatMessage;
import itstep.learning.androidpv211.orm.NbuRate;

public class ChatActivity extends AppCompatActivity {
    private static final String chatUrl = "https://chat.momentfor.fun/";
    private ExecutorService pool;
    private final List<ChatMessage> messages = new ArrayList<>();
    private EditText etAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeBars = insets.getInsets( WindowInsetsCompat.Type.ime() );
            v.setPadding( systemBars.left, systemBars.top, systemBars.right,
                    Math.max( systemBars.bottom, imeBars.bottom )
            );
            return insets;
        });
        pool = Executors.newFixedThreadPool( 3 );
        CompletableFuture
                .supplyAsync( () -> Services.fetchUrl( chatUrl ) )
                .thenApply( this::parseChatResponse )
                .thenAccept( this::processChatResponse );
        etAuthor = findViewById( R.id.chat_et_author );
    }

    private void processChatResponse( List<ChatMessage> parsedMessages ) {
        for( ChatMessage m : parsedMessages ) {
            if( messages.stream().noneMatch( cm -> cm.getId().equals( m.getId() ) ) ) {
                messages.add( m );
            }
        }
        runOnUiThread( () ->
            etAuthor.setText( messages.size() + "" ) );
    }

    private List<ChatMessage> parseChatResponse( String body ) {
        List<ChatMessage> res = new ArrayList<>();
        try {
            JSONObject root = new JSONObject( body );
            // TODO: check root.status
            JSONArray arr = root.getJSONArray( "data" );
            int len = arr.length() ;
            for( int i = 0; i < len; i++ ) {
                res.add( ChatMessage.fromJsonObject( arr.getJSONObject(i) ) ) ;
            }
        }
        catch( JSONException ex ) {
            Log.d( "parseChatResponse", "JSONException " + ex.getMessage() );
        }
        return res;
    }

    @Override
    protected void onDestroy() {
        pool.shutdownNow();
        super.onDestroy();
    }

}
/*
Д.З. При прийомі відповіді сервера повідомлень (у методі
parseChatResponse) забезпечити перевірку статусу відповіді
(поля root.status). Його значення має бути 1 (числова
одиниця). Якщо значення інше, то слід вивести лог з
відповідним зауваженням, що запит завершився з статусом Х
та ігнорувати подальшу обробку тіла відповіді.
 */