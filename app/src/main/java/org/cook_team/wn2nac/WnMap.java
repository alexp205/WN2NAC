package org.cook_team.wn2nac;

import com.google.android.gms.maps.model.CameraPosition;

import de.greenrobot.event.EventBus;

public class WnMap {

    private static final EventBus bus = EventBus.getDefault();

    public static CameraPosition mCam;
    public static boolean init = false;
    public static boolean measureFragmentVisible = false;
    public static int currentStep = 2;
    public static WindooMeasurement goTo;

    public static class GotoEvent {}
    public static class OpenEvent {}
}
