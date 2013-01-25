package org.fuzzyrobot.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import org.fuzzyrobot.omnibus.core.Bus;
import org.fuzzyrobot.omnibus.core.BusInterface;
import org.fuzzyrobot.omnibus.core.Subscriber;

public class SimpleActivity extends Activity {

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

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Thing thing = new Thing(s.toString());
                bus.publish(thing);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.detach(this);
    }
}
