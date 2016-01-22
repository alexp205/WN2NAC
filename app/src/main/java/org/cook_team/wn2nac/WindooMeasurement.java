package org.cook_team.wn2nac;

import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooMeasurement;

public class WindooMeasurement extends JDCWindooMeasurement {
    private Date sentAt;
    private int seq;

    public int getSeq() {
        return seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }
    public Date getSentAt() {
        return sentAt;
    }
    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
