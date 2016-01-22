package org.cook_team.wn2nac;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import ch.skywatch.windoo.api.JDCWindooEvent;

public class WindooEvent extends JDCWindooEvent {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Date time;

    public WindooEvent(JDCWindooEvent event) {
        this.type = event.getType();
        this.data = event.getData();
        this.time = GregorianCalendar.getInstance().getTime();
    }

    public String message() {
        StringBuilder b = new StringBuilder();
        b.append("[" + dateFormat.format(time) + "]\n");
        b.append("(" + String.valueOf(type) + ") ");

        if (type == JDCWindooEvent.JDCWindooAvailable)
            b.append("JDCWindooAvailable" + "\n");
        else if (type == JDCWindooEvent.JDCWindooNotAvailable)
            b.append("JDCWindooNotAvailable");
        else if (type == JDCWindooEvent.JDCWindooCalibrated)
            b.append("JDCWindooCalibrated" + "\n");
        else if (type == JDCWindooEvent.JDCWindooVolumeNotAtItsMaximum)
            b.append("JDCWindooVolumeNotAtItsMaximum" + "\n");
        else if (type == JDCWindooEvent.JDCWindooPublishSuccess) {
            b.append("JDCWindooPublishSuccess" + "\n");
            b.append("JDCWindooPublishSuccess : " + data + "\n");
        } else if (type == JDCWindooEvent.JDCWindooPublishException) {
            b.append("JDCWindooPublishException" + "\n");
            b.append("JDCWindooPublishException : " + data + "\n");
        } else if (type == JDCWindooEvent.JDCWindooNewWindValue) {
            b.append("JDCWindooNewWindValue" + "\n");
            b.append("Wind received : " + data + "\n");
        } else if (type == JDCWindooEvent.JDCWindooNewTemperatureValue) {
            b.append("JDCWindooNewTemperatureValue" + "\n");
            b.append("Temperature received : " + data + "\n");
        } else if (type == JDCWindooEvent.JDCWindooNewHumidityValue) {
            b.append("JDCWindooNewHumidityValue" + "\n");
            b.append("Humidity received : " + data + "\n");
        } else if (type == JDCWindooEvent.JDCWindooNewPressureValue) {
            b.append("JDCWindooNewPressureValue" + "\n");
            b.append("Pressure received : " + data + "\n");
        }
        return b.toString();
    }
}
