package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.Params;

/**
 * Created by kevin on 5/27/15.
 */
public abstract class Base<T extends Params> {
    private T params;

    public Base(T params) {
        this.params = params;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public abstract boolean run() throws ReaderException;


}
