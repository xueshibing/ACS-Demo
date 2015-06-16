package com.frankgreen;

/**
 * Created by kevin on 5/20/15.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
//import com.frankgreen.apdu.OnGetResultListener;
//import com.frankgreen.apdu.Result;
//import com.frankgreen.apdu.command.UID;
//import com.frankgreen.task.ReadTask;
//import com.frankgreen.task.UIDTask;
//import com.frankgreen.task.WriteTask;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACRNFCReaderPhoneGapPlugin extends CordovaPlugin {

    private static final String TAG = "ACR";
    private static final String LISTEN = "listen";
    private static final String READ_DATA = "readData";
    private static final String WRITE_DATA = "writeData";
    private static final String AUTHENTICATE_WITH_KEY_A = "authenticateWithKeyA";
    private static final String AUTHENTICATE_WITH_KEY_B = "authenticateWithKeyB";
    private static final String WRITE_AUTHENTICATE = "writeAuthenticate";


    private CordovaWebView webView;

    private NFCReader nfcReader;


    private UsbManager usbManager;
//    private UsbDevice usbDevice;


    //    private Reader reader;
    PendingIntent mPermissionIntent;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    nfcReader.attach(intent);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                synchronized (this) {
                    nfcReader.detach(intent);
                }
            }
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        this.webView = webView;
        super.initialize(cordova, webView);

        Log.d(TAG, "initializing...");
        usbManager = (UsbManager) cordova.getActivity().getSystemService(Context.USB_SERVICE);
        nfcReader = new NFCReader(usbManager);
        nfcReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNumber, int previousState, int currentState) {
//                if (!nfcReader.isProcessing()) {
                Log.d(TAG, "slotNumber " + slotNumber);
                Log.d(TAG, "previousState " + previousState);
                Log.d(TAG, "currentState " + currentState);
                Log.d(TAG, "");
                if (slotNumber == 0 && currentState == Reader.CARD_PRESENT) {
                    Log.d(TAG, "Ready to read!!!!");
                    nfcReader.reset(slotNumber);
                } else {// if (currentState == Reader.CARD_ABSENT && previousState == Reader.CARD_PRESENT) {
                    Log.d(TAG, "Card Lost");
                    webView.sendJavascript("ACR.onCardAbsent();");
                }
//                }
            }
        });

        nfcReader.setOnStatusChangeListener(new NFCReader.StatusChangeListener() {

                                                @Override
                                                public void onReady(Reader reader) {
                                                    Log.d(TAG, "onReady");
                                                    webView.sendJavascript("ACR.onReady('" + reader.getReaderName() + "');");
                                                }

                                                @Override
                                                public void onAttach(UsbDevice device) {
                                                    Log.d(TAG, "onAttach");
                                                    webView.sendJavascript("ACR.onAttach('" + device.getDeviceName() + "');");
                                                }

                                                @Override
                                                public void onDetach(UsbDevice device) {
                                                    Log.d(TAG, "onDetach");
                                                    webView.sendJavascript("ACR.onDetach('" + device.getDeviceName() + "');");
                                                }
                                            }
        );
        // Register receiver for USB permission

        mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        nfcReader.setPermissionIntent(mPermissionIntent);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }


    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, "execute " + action);

        // TODO call error callback if there is no reader
        if (action.equalsIgnoreCase(LISTEN)) {
            listen(callbackContext);
        } else if (action.equalsIgnoreCase(READ_DATA)) {
            readData(callbackContext, data);
        } else if (action.equalsIgnoreCase(WRITE_DATA)) {
            writeData(callbackContext, data);
        } else if (action.equalsIgnoreCase(AUTHENTICATE_WITH_KEY_A)) {
            authenticateWithKeyA(callbackContext, data);
        } else if (action.equalsIgnoreCase(AUTHENTICATE_WITH_KEY_B)) {
            authenticateWithKeyB(callbackContext, data);
        } else if (action.equalsIgnoreCase(WRITE_AUTHENTICATE)) {
            writeAuthenticate(callbackContext, data);
        } else {
            return false;
        }
        return true;
    }

    private void writeAuthenticate(final CallbackContext callbackContext, JSONArray data) {
        try {
            nfcReader.writeAuthenticate(0, data.getInt(0),data.getString(1),data.getString(2),new OnGetResultListener() {
                @Override
                public void onResult(Result result) {
                    Log.w(TAG, "==========writeAuthenticate==========");
                    Log.w(TAG,result.getCodeString());
                    if (result.isSuccess() && result.getData() != null) {
                        for (byte b : result.getData()) {
                            Log.w(TAG, "byte " + b);
                        }
                    }
                    Log.w(TAG, "====================");
                    PluginResult pluginResult = new PluginResult(
                            result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                            Util.resultToJSON(result));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void authenticateWithKeyB(final CallbackContext callbackContext, JSONArray data) {
        try {
            nfcReader.authenticateWithKeyB(0, data.getInt(0),data.getString(1), new OnGetResultListener() {
                @Override
                public void onResult(Result result) {
                    Log.w(TAG, "==========authenticateWithKeyB==========");
                    Log.w(TAG,result.getCodeString());
                    if (result.isSuccess() && result.getData() != null) {
                        for (byte b : result.getData()) {
                            Log.w(TAG, "byte " + b);
                        }
                    }
                    Log.w(TAG, "====================");
                    PluginResult pluginResult = new PluginResult(
                            result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                            Util.resultToJSON(result));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void authenticateWithKeyA(final CallbackContext callbackContext, JSONArray data) {
        try {
            nfcReader.authenticateWithKeyA(0, data.getInt(0),data.getString(1),new OnGetResultListener() {
                @Override
                public void onResult(Result result) {
                    Log.w(TAG, "==========authenticateWithKeyA==========");
                    Log.w(TAG,result.getCodeString());
                    if (result.isSuccess() && result.getData() != null) {
                        for (byte b : result.getData()) {
                            Log.w(TAG, "byte " + b);
                        }
                    }
                    Log.w(TAG, "====================");
                    PluginResult pluginResult = new PluginResult(
                            result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                            Util.resultToJSON(result));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readData(final CallbackContext callbackContext, JSONArray data) {

        try {
            nfcReader.readData(0, data.getInt(0),new OnGetResultListener() {
                @Override
                public void onResult(Result result) {
                    Log.w(TAG, "==========ReadData==========");
                    Log.w(TAG,result.getCodeString());
                    if (result.isSuccess() && result.getData() != null) {
                        for (byte b : result.getData()) {
                            Log.w(TAG, "byte " + b);
                        }
                    }
                    Log.w(TAG, "====================");
                    PluginResult pluginResult = new PluginResult(
                            result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                            Util.resultToJSON(result));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeData(final CallbackContext callbackContext, JSONArray data) {
        try {
            nfcReader.writeData(0, data.getInt(0), data.getString(1),new OnGetResultListener() {
                @Override
                public void onResult(Result result) {
                    Log.w(TAG, "==========Write==========");
                    Log.w(TAG,result.getCodeString());
//                        if (result.isSuccess() && result.getData() != null) {
//                            for (byte b : result.getData()) {
//                                Log.w(TAG, "byte " + b);
//                            }
//                        }
                    Log.w(TAG, "====================");
                    PluginResult pluginResult = new PluginResult(
                            result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                            Util.resultToJSON(result));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listen(final CallbackContext callbackContext) {

        nfcReader.listen(
                new OnGetResultListener() {
                    @Override
                    public void onResult(Result result) {
                        Log.w(TAG, "==========UID==========");
                        Log.w(TAG,result.getCodeString());
                        if (result.getData() != null) {
                            for (byte b : result.getData()) {
                                Log.w(TAG, "byte " + b);
                            }
                        }
                        Log.w(TAG, "====================");
                        PluginResult pluginResult = new PluginResult(
                                result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                                Util.resultToJSON(result));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }
                }
        );
    }

    private Activity getActivity() {
        return this.cordova.getActivity();
    }

    private Intent getIntent() {
        return getActivity().getIntent();
    }
}