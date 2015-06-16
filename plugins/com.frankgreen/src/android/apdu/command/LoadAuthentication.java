package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;

/**
 * Created by kevin on 5/27/15.
 */
public class LoadAuthentication extends Base {
    private static final String TAG = "LoadAuthentication";
    private String password;

    public LoadAuthentication(NFCReader nfcReader) {
        super(nfcReader);
    }

    public LoadAuthentication(NFCReader nfcReader, String password) {
        super(nfcReader);
        this.password = password;
    }

    public OnGetResultListener listener;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean run(int slotNumber) {
//        FF 82 00 00 06 FF FF FF FF FF FF
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x82, (byte) 0x0, (byte) 0x0, (byte) 0x06,
                (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0
        };
        byte[] pwd = Util.toNFCByte(this.password, 6);
        System.arraycopy(pwd, 0, sendBuffer, 5, 6);
        byte[] receiveBuffer = new byte[16];
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = getNfcReader().getReader();
        Result result;
        try {
            int byteCount = reader.transmit(slotNumber, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("LoadAuthentication", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("LoadAuthentication", e);
            e.printStackTrace();
        }
        if (this.listener != null) {
            this.listener.onResult(result);
        }
        return result.isSuccess();
    }

    public void setOnGetResultListener(OnGetResultListener listener) {
        this.listener = listener;
    }
}