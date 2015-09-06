package com.frankgreen.task;

import android.os.AsyncTask;
import android.util.Log;
import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.*;
import org.apache.cordova.PluginResult;

/**
 * Created by kevin on 6/2/15.
 */
public class ReadTask extends AsyncTask<ReadParams, Void, Boolean> {
    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(ReadParams... paramses) {
        ReadParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }

        final ReadBinaryBlock read = new ReadBinaryBlock(params);

        if (params.getReader().getChipMeta().needAuthentication()) {
            TaskWithPassword task = new TaskWithPassword("ReadBinaryBlock",
                    params.getReader(),
                    params.getSlotNumber(),
                    params.getPassword()
            );
            task.setGetResultListener(params.getOnGetResultListener());
            task.setCallback(new TaskWithPassword.TaskCallback() {
                @Override
                public boolean run() {
                    return read.run();
                }
            });

            task.run();
        } else {
            return read.run();
        }
        return false;
    }
}
