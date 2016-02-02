package org.cook_team.wn2nac;

import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooEvent;

public class WindooEvent extends JDCWindooEvent {
    final Date time;
    public WindooEvent(JDCWindooEvent event) {
        this.type = event.getType();
        this.data = event.getData();
        this.time = new Date();
    }
}
