package com.codegemz.ev3micro;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codegemz.ev3micro.Connector;
import com.codegemz.ev3micro.R;

import java.util.Set;

public class MainActivity extends ActionBarActivity {
	private Connector connector;
	private TextView statusTV;
	private BluetoothAdapter bluetoothAdapter;
    private String btAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		statusTV = (TextView) findViewById(R.id.statusLabel);
		
		//create BT adapter
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                if(device.getName().equals("EV3")) {
                    btAddress = device.getAddress();
                    Log.d(Connector.TAG, "" + device.getAddress() + "  " + device.getName());
                    break;
                }
            }
        }
        if(btAddress == null)
            showBTAlarm();
		
		// Register for broadcasts on BluetoothAdapter statechmange
	    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
	    this.registerReceiver(mReceiver, filter);
		
		// Establish a bluetooth connection to the NXT
		this.connector = new Connector(getApplicationContext(), btAddress,
				bluetoothAdapter);
		
		if(this.bluetoothAdapter.isEnabled() == false)
			this.connector.setBluetooth(Connector.BT_ON);
		else{
			setStatusText("Bluetooth on");
            Log.d(Connector.TAG, "Bluetooth turned on");
		}

        Button buttonMBackA = (Button) findViewById(R.id.buttonMBackA);
        buttonMBackA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    writeMessage("mbackA");
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    writeMessage("stop_A");
                    return true;
                }

                return false;
            }
        });

        Button buttonMotorA = (Button) findViewById(R.id.buttonMotorA);
        buttonMotorA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    writeMessage("motorA");
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    writeMessage("stop_A");
                    return true;
                }

                return false;
            }
        });

        Button buttonMotorB = (Button) findViewById(R.id.buttonMotorB);
        buttonMotorB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    writeMessage("motorB");
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    writeMessage("stop_B");
                    return true;
                }

                return false;
            }
        });

        Button buttonTest = (Button) findViewById(R.id.buttonTest);
        buttonTest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    writeMessage("level");
                    return true;
                }

                

                return false;
            }
        });

        SeekBar sbA = (SeekBar) findViewById(R.id.seekBarA);
        sbA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int current_level;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               current_level = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((EV3App)getApplication()).setLevelA(current_level);
                setStatusText("new level: " + current_level);
                writeMessage("level");
            }
        });

	}

    private void showBTAlarm() {
        new AlertDialog.Builder(this)
                .setTitle("No EV3 devices found!")
                .setMessage("Please pair with one and only one EV3 device using Bluetooth settings on this device")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.connector.setBluetooth(Connector.BT_OFF);
        this.connector.close();
    }
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    // Unregister broadcast listeners
	    this.unregisterReceiver(mReceiver);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();

	        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
	            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
	                                                 BluetoothAdapter.ERROR);
	            switch (state) {
	            case BluetoothAdapter.STATE_OFF:
	                setStatusText("Bluetooth off");
	                Log.d(Connector.TAG, "Bluetooth turned off");
	                Toast.makeText(getApplicationContext(), "Bluetooth turned off!", Toast.LENGTH_LONG).show();
	                break;
	            case BluetoothAdapter.STATE_TURNING_OFF:
	                setStatusText("Turning Bluetooth off...");
	                break;
	            case BluetoothAdapter.STATE_ON:
	                setStatusText("Bluetooth on");
	                Log.d(Connector.TAG, "Bluetooth turned on");
	                Toast.makeText(getApplicationContext(), "Bluetooth turned on!", Toast.LENGTH_LONG).show();
	                break;
	            case BluetoothAdapter.STATE_TURNING_ON:
	                setStatusText("Turning Bluetooth on...");
	                break;
	            }
	        }
	    }
	};
	
	private void setStatusText(String text){
		statusTV.setText("Status: " + text);
	}
	



	private void writeMessage(String btCommand) {
        if(this.connector.connect())
            (new WriteMessageTask(btCommand)).execute("");
        else{
            setStatusText("BT connection failed");
            Log.d(Connector.TAG, "BT connection failed");
        }

	}
	
	 private class WriteMessageTask extends AsyncTask<String, Void, String> {

            private String btCommmand;

            WriteMessageTask(String btCommand){
                this.btCommmand = btCommand;
            }

	        @Override
	        protected String doInBackground(String... params) {
	        	connector.writeMessage(btCommmand);
	            return "Pushed message to robot";
	        }

	        @Override
	        protected void onPostExecute(String result) {
	        	TextView tv = (TextView) findViewById(R.id.label1);
				tv.setText("received:" + result);
	        	//Toast.makeText(getApplicationContext(), "Successfully pushed message!", Toast.LENGTH_LONG).show();
	        }

	        @Override
	        protected void onPreExecute() {}

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
}
