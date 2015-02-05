package com.example.ev3reader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class Connector {
	public static final String TAG = "Connector";

	public static final boolean BT_ON = true;
	public static final boolean BT_OFF = false;

	public BluetoothAdapter bluetoothAdapter;
	public BluetoothSocket bluetoothSocket;
	public String address;
	
	public byte[] last_message;

	Context ctx;

	public Connector(Context ctx, String address, BluetoothAdapter bluetoothAdapter) {
		this.address = address;
		this.bluetoothAdapter = bluetoothAdapter;

		this.ctx = ctx;
	}

	public void setBluetooth(boolean state) {
		if(state == Connector.BT_ON) {
			// Check if bluetooth is off
			if(this.bluetoothAdapter.isEnabled() == false)
			{
				this.bluetoothAdapter.enable();

			}

		}
		// Check if bluetooth is enabled
		else if(state == Connector.BT_OFF) {
			// Check if bluetooth is enabled
			if(this.bluetoothAdapter.isEnabled() == true)
			{
				this.bluetoothAdapter.disable();

			}

		}

	}

	public boolean connect() {

		boolean connected = false;
		BluetoothDevice nxt = this.bluetoothAdapter.getRemoteDevice(this.address);

		try {
			this.bluetoothSocket = nxt.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			this.bluetoothSocket.connect();
			connected = true;

		} 
		catch (IOException e) {
			connected = false;

		}

		return connected;

	}
	

	public String readMessage() {
		String message = "error, empty string";

		if(this.bluetoothSocket!= null) {
			try {
				byte[] buffer = new byte[500];
				DataInputStream input = new DataInputStream(this.bluetoothSocket.getInputStream());
				int length = input.read(buffer);
				last_message = buffer;
				message = "" + (new String(buffer)) + ": length: " + length + "\n";
				for(int i=0; i< length; i++)
					message = message + "|" + ((int)buffer[i]&0xff);
				Log.d(Connector.TAG, "Successfully read message");
				//Toast.makeText(ctx, "Successfully read message!", Toast.LENGTH_LONG).show();
				input.close();
			} 
			catch (IOException e) {
				message = e.toString();
				Log.d(Connector.TAG, "Couldn't read message");
				//Toast.makeText(ctx, "Couldn't read message!", Toast.LENGTH_LONG).show();

			}  
		}
		else {
			message = "bluetoothSocket == null";
			Log.d(Connector.TAG, "Couldn't read message");
			//Toast.makeText(ctx, "Couldn't read message", Toast.LENGTH_LONG).show();

		}

		return message;

	}

	public void writeMessage() {

		if(this.bluetoothSocket!= null) {
			try{
				DataOutputStream outStream = new DataOutputStream(this.bluetoothSocket.getOutputStream());
				//https://wiki.qut.edu.au/display/cyphy/Mailbox+and+Messages
                byte[] l_message = {(byte)18, (byte)0, (byte)1,    (byte)0,  (byte)129,  (byte)158,    (byte)4,   (byte)97,   (byte)98,   (byte)99,    (byte)0,    (byte)("hello!".length() + 1),    (byte)0,  (byte)'h',  (byte)101,  (byte)108,  (byte)108,  (byte)111,   (byte)33,    (byte)0};
                Log.d(Connector.TAG, "++++++++++++++++++++++++++++++++ message to write: " + l_message);
				outStream.write(l_message, 0, l_message.length);
                byte[] l_message1 = {(byte)18, (byte)0, (byte)1,    (byte)0,  (byte)129,  (byte)158,    (byte)4,   (byte)97,   (byte)98,   (byte)99,    (byte)0,    (byte)("hello!".length() + 1),    (byte)0,  (byte)'g',  (byte)101,  (byte)108,  (byte)108,  (byte)111,   (byte)33,    (byte)0};
                Log.d(Connector.TAG, "++++++++++++++++++++++++++++++++ message to write: " + l_message);
                outStream.write(l_message1, 0, l_message1.length);
                byte[] l_message2 = {(byte)18, (byte)0, (byte)1,    (byte)0,  (byte)129,  (byte)158,    (byte)4,   (byte)97,   (byte)98,   (byte)99,    (byte)0,    (byte)("hello!".length() + 1),    (byte)0,  (byte)'z',  (byte)101,  (byte)108,  (byte)108,  (byte)111,   (byte)33,    (byte)0};
                Log.d(Connector.TAG, "++++++++++++++++++++++++++++++++ message to write: " + l_message);
                outStream.write(l_message2, 0, l_message2.length);
				outStream.flush();
				outStream.close();
				Log.d(Connector.TAG, "Successfully written message" + l_message);
			}
			catch (Exception e) {

				Log.d(Connector.TAG, "Couldn't write message: " + e);

			}  


		}
		else {
			Log.d(Connector.TAG, "Couldn't write message");
		}
	}

}
