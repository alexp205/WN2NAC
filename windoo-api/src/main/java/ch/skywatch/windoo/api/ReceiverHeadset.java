package ch.skywatch.windoo.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class ReceiverHeadset extends BroadcastReceiver {
    static final String TAG = "HeadsetReceiver";

    ReceiverHeadset() {
    }

    public void onReceive(Context context, Intent intent) {
        boolean connected = true;
        if (!(intent.hasExtra("state") && intent.hasExtra("microphone") && intent.getIntExtra("state", 0) == 1 && intent.getIntExtra("microphone", 0) == 1)) {
            connected = false;
        }
        JDCWindooManager.getInstance().checkDongleConnection(context, connected);
    }
}
