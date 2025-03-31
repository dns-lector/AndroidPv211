package itstep.learning.androidpv211;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RatesActivity extends AppCompatActivity {
    private static final String nbuRatesUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private TextView tvContainer;
    private ExecutorService pool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rates);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvContainer = findViewById( R.id.rates_tv_container );
        pool = Executors.newFixedThreadPool( 3 );
        pool.submit( this::loadRates );
        // new Thread( this::loadRates ).start();
    }

    private void loadRates() {
        try {
            URL url = new URL( nbuRatesUrl );
            InputStream urlStream = url.openStream();   // GET-request
            ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int len;
            while( ( len = urlStream.read( buffer ) ) > 0 ) {
                byteBuilder.write( buffer, 0, len );
            }
            String charsetName = StandardCharsets.UTF_8.name();
            String data = byteBuilder.toString( charsetName );
            urlStream.close();
            runOnUiThread( () -> tvContainer.setText( data ) );
        }
        catch( MalformedURLException ex ) {
            Log.d( "loadRates", "MalformedURLException " + ex.getMessage() );
        }
        catch( IOException ex ) {
            Log.d( "loadRates", "IOException " + ex.getMessage() );
        }
        /*
        android.os.NetworkOnMainThreadException - всі запити до мережі мають бути в
        окремих потоках.

        java.lang.SecurityException: Permission denied (missing INTERNET permission?)
        Багато дій в Андроїд вимагає дозволів, зокрема Інтернет. Дозволи декларуються
        у маніфесті <uses-permission android:name="android.permission.INTERNET"/>

        android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
        Для роботи з елементами UI необхідно повернути управління до потоку UI: runOnUiThread();
         */
    }

    @Override
    protected void onDestroy() {
        pool.shutdownNow();
        super.onDestroy();
    }
}
/*
Д.З. Завершення калькулятора. Продовження.
 */