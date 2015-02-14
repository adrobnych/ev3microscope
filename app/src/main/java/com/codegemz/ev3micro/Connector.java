package com.codegemz.ev3micro;

import java.io.DataOutputStream;
import java.io.IOException;
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
	
    private DataOutputStream outStream;

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
        if (outStream == null){
            boolean connected = false;
            BluetoothDevice nxt = this.bluetoothAdapter.getRemoteDevice(this.address);

            try {
                this.bluetoothSocket = nxt.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                this.bluetoothSocket.connect();
                connected = true;
                outStream = new DataOutputStream(this.bluetoothSocket.getOutputStream());
            } catch (IOException e) {
                connected = false;

            }

            return connected;
        }
        else
            return true;

    }


    public void writeMessage(String btCommand) {

            btMessage(btCommand);
    }

	public void btMessage(String btCommand) {

		if(this.bluetoothSocket!= null) {
			try{
                byte[] bt_message = null;
                if(btCommand.equals("motor_A_UP")) {
                    bt_message = new byte[]{(byte) 0x0C, (byte) 0x00, (byte) 0x80, (byte) 0x00,
                            (byte) 0x80,
                            (byte) 0x00,
                            (byte) 0x00,
                            (byte) 0xA4, // set power command
                            (byte) 0x00, // layer ?
                            (byte) 0x01, // motor - A =1   B=2
                            (byte) ((EV3App) ctx.getApplicationContext()).getLevelA(),   // power
                            //   0 -> 32  33 <- 63    64 and 65 - max


                            (byte) 0xA6,  //start motor command
                            (byte) 0,     // layer
                            (byte) 0x01  // motor - A =1   B=2
                    };
                }
                if(btCommand.equals("motor_A_stop")) {
                    bt_message = new byte[]{(byte) 0x09, (byte) 0x00, (byte) 0x80, (byte) 0x00,
                            (byte) 0x80,
                            (byte) 0x00,
                            (byte) 0x00,
                            /*(byte) 0xA4, // set power command
                            (byte) 0x00, // layer ?
                            (byte) 0x01, // motor - A =1   B=2
                            (byte) ((EV3App) ctx.getApplicationContext()).getLevelA(),   // power
                            //   0 -> 32  33 <- 63    64 and 65 - max


                            (byte) 0xA6,  //start motor command
                            (byte) 0,     // layer
                            (byte) 0x01  // motor - A =1   B=2*/
                            (byte) 0xA3,
                            (byte) 0x00,
                            (byte) 0x01,
                            (byte) 0x01
                    };
                }
                if(btCommand.equals("motor_B_UP")) {
                    bt_message = new byte[]{(byte) 0x0C, (byte) 0x00, (byte) 0x80, (byte) 0x00,
                            (byte) 0x80,
                            (byte) 0x00,
                            (byte) 0x00,
                            (byte) 0xA4, // set power command
                            (byte) 0x00, // layer ?
                            (byte) 0x02, // motor - A =1   B=2
                            (byte) ((EV3App) ctx.getApplicationContext()).getLevelA(),   // power
                            //   0 -> 32  33 <- 63    64 and 65 - max


                            (byte) 0xA6,  //start motor command
                            (byte) 0,     // layer
                            (byte) 0x02  // motor - A =1   B=2
                    };
                }
                if(btCommand.equals("motor_B_stop")) {
                    bt_message = new byte[]{(byte) 0x09, (byte) 0x00, (byte) 0x80, (byte) 0x00,
                            (byte) 0x80,
                            (byte) 0x00,
                            (byte) 0x00,
                            /*(byte) 0xA4, // set power command
                            (byte) 0x00, // layer ?
                            (byte) 0x01, // motor - A =1   B=2
                            (byte) ((EV3App) ctx.getApplicationContext()).getLevelA(),   // power
                            //   0 -> 32  33 <- 63    64 and 65 - max


                            (byte) 0xA6,  //start motor command
                            (byte) 0,     // layer
                            (byte) 0x01  // motor - A =1   B=2*/
                            (byte) 0xA3,
                            (byte) 0x00,
                            (byte) 0x02,
                            (byte) 0x01
                    };
                }
                outStream.write(bt_message, 0, bt_message.length);
                Log.d(Connector.TAG, "Successfully written message" + new String(bt_message));

			}
			catch (Exception e) {

				Log.d(Connector.TAG, "Couldn't write message: " + e);

			}  


		}
		else {
			Log.d(Connector.TAG, "Couldn't write message");
		}
	}



    public void btLevel() {

        if(this.bluetoothSocket!= null) {
            try{
                //https://wiki.qut.edu.au/display/cyphy/Mailbox+and+Messages
                //http://www.robotappstore.com/Knowledge-Base/How-to-send-commands-to-Lego-NXT/37.html

                byte[]   bt_message = new byte[]{(byte) 0x0C, (byte) 0x00, (byte) 0x80, (byte) 0x00,
                        (byte) 0x80,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0xA4, // set power command
                        (byte) 0x00, // layer ?
                        (byte) 0x02, // motor - A =1   B=2
                        (byte) ((EV3App)ctx.getApplicationContext()).getLevelA(),   // power
                                       //   0 -> 32  33 <- 63    64 and 65 - max


                        (byte) 0xA6,  //start motor command
                        (byte) 0,     // layer
                        (byte) 0x02  // motor - A =1   B=2
                };
                outStream.write(bt_message, 0, bt_message.length);
                Log.d(Connector.TAG, "Successfully written message" + new String(bt_message));

               /* byte[] l_message =  composeBTArray("level1");
                outStream.write(l_message, 0, l_message.length);
                Log.d(Connector.TAG, "Successfully written message" + new String(l_message));

                l_message = composeBTArray(prepareDeigits()[0]);
                outStream.write(l_message, 0, l_message.length);
                Log.d(Connector.TAG, "Successfully written message" + new String(l_message));


                l_message =  composeBTArray("level2");
                outStream.write(l_message, 0, l_message.length);
                Log.d(Connector.TAG, "Successfully written message" + new String(l_message));

                l_message =  composeBTArray(prepareDeigits()[1]);
                outStream.write(l_message, 0, l_message.length);
                Log.d(Connector.TAG, "Successfully written message" + new String(l_message));*/

                //outStream.flush();

            }
            catch (Exception e) {

                Log.d(Connector.TAG, "Couldn't write message: " + e);

            }


        }
        else {
            Log.d(Connector.TAG, "Couldn't write message");
        }
    }

    public void close() {

        try {
            //outStream.flush();
            if(outStream != null)
                outStream.close();
        } catch (IOException e) {
            Log.d(Connector.TAG, "Couldn't close BT connector: " + e);
        }
    }

    private byte[] composeBTArray(String payLoad){
        byte[]   bt_message = new byte[]{(byte) 18, (byte) 0, (byte) 1, (byte) 0, (byte) 129,
                (byte) 158, (byte) 4, (byte) 97, (byte) 98, (byte) 99, (byte) 0, (byte) 7, (byte) 0,
                (byte) 0, (byte) 0, (byte) 0,
                (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        for(int i=0; i<payLoad.length(); i++)
            bt_message[i+13] = (byte) payLoad.charAt(i);
        return bt_message;

    }
}
