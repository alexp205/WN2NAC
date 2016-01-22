package ch.skywatch.windoo.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class ReceiverMediaButton extends BroadcastReceiver {
    static final String TAG = "MediaButtonReceiver";

    ReceiverMediaButton() {
    }

    public void onReceive(Context context, Intent intent) {
        JDCWindooManager.getInstance().checkVolume(context);
    }
}
