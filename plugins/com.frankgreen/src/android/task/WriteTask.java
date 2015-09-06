package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.command.UpdateBinaryBlock;

/**
 * Created by kevin on 6/2/15.
 */
public class WriteTask extends AsyncTask<WriteParams, Void, Boolean> {
    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(WriteParams... paramses) {
        WriteParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        final UpdateBinaryBlock update = new UpdateBinaryBlock(params);

        if (params.getReader().getChipMeta().needAuthentication()) {
            TaskWithPassword task = new TaskWithPassword("UpdateBinaryBlock",
                    params.getReader(),
                    params.getSlotNumber(),
                    params.getPassword()
            );
            task.setGetResultListener(params.getOnGetResultListener());
            task.setCallback(new TaskWithPassword.TaskCallback() {
                @Override
                public boolean run() {
                    return update.run();
                }
            });
            task.run();
        } else {
            return update.run();
        }
        return false;
    }
}
