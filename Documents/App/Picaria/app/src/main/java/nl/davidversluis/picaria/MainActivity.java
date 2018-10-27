package nl.davidversluis.picaria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    //All linked to buttons
    public void gotoabout(View view) {
        Intent intent = new Intent(this, Aboutscreen.class);
        startActivity(intent);
    }
    public void gotosimpleboard(View view) {
        Intent intent = new Intent(this, Simplescreen.class);
        startActivity(intent);
    }
    public void gotohowto(View view) {
        Intent intent = new Intent(this, Howto.class);
        startActivity(intent);
    }
}
