package com.frankgreen.task;

import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.ntag.NTagAuth;
import com.frankgreen.apdu.command.ntag.StartSession;
import com.frankgreen.apdu.command.ntag.StopSession;

/**
 * Created by kevin on 8/12/15.
 */
public class TaskWithPassword {
    private String name;
    private String password;
    private int slotNumber = 0;
    private NFCReader reader;
    private OnGetResultListener getResultListener;
    private TaskCallback callback;

    public TaskWithPassword(String name, NFCReader reader, int slotNumber, String password) {
        this.name = name;
        this.reader = reader;
        this.slotNumber = slotNumber;
        this.password = password;
    }

    interface TaskCallback {
        boolean run();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public NFCReader getReader() {
        return reader;
    }

    public void setReader(NFCReader reader) {
        this.reader = reader;
    }

    public OnGetResultListener getGetResultListener() {
        return getResultListener;
    }

    public void setGetResultListener(OnGetResultListener getResultListener) {
        this.getResultListener = getResultListener;
    }

    public TaskCallback getCallback() {
        return callback;
    }

    public void setCallback(TaskCallback callback) {
        this.callback = callback;
    }

    public boolean run() {
        Result result = null;
        if (this.callback != null && reader.getChipMeta().needAuthentication()) {
            InitNTAGParams initNTAGParams = new InitNTAGParams(slotNumber);
            initNTAGParams.setReader(reader);
            initNTAGParams.setPassword(password);
            initNTAGParams.setOnGetResultListener(getResultListener);
            StartSession startSession = new StartSession(initNTAGParams);
            NTagAuth nTagAuth = new NTagAuth(initNTAGParams);
            StopSession stopSession = new StopSession(initNTAGParams);

            try {
                startSession.run();
                nTagAuth.initPassword();
                if (nTagAuth.run()) {
                    return callback.run();
                } else {
                    result = new Result(getName(), new ReaderException("Invalid Password"));
                }
            } finally {
                stopSession.run();
            }
        }else{
            result = new Result(getName(), new ReaderException("Lost Password"));
        }
        if (result != null && getResultListener != null) {
            getResultListener.onResult(result);
        }
        return false;
    }
}
