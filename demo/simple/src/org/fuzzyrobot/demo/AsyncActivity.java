package org.fuzzyrobot.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import org.fuzzyrobot.omnibus.api.Bus;
import org.fuzzyrobot.omnibus.core.BusApp;
import org.fuzzyrobot.omnibus.core.BusInterface;
import org.fuzzyrobot.omnibus.core.Subscriber;

public class AsyncActivity extends Activity {

    private BusInterface bus;
    private EditText input;
    private TextView output;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        input = (EditText) findViewById(R.id.input);
        output = (TextView) findViewById(R.id.output);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus = Bus.attach(this);

        bus.subscribe(Thing.class, new Subscriber<Thing>() {
            public void receive(Thing value) {
                output.setText(value.getText());
            }
        });

        BusApp.getInstance(this).provide(Thing.class, new ThingProvider());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.detach(this);
    }

}
