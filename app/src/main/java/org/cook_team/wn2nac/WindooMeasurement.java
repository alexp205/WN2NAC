package org.cook_team.wn2nac;

import java.util.Date;

import ch.skywatch.windoo.api.JDCWindooMeasurement;

public class WindooMeasurement extends JDCWindooMeasurement {

    private int seq;
    private Date sentAt;
    private int UserID;
    private int WindooID;

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
    public int getUserID() { return UserID; }
    public void setUserID(int UserID) {
        this.UserID = UserID;
    }
    public int getWindooID() { return WindooID; }
    public void setWindooID(int WindooID) {
        this.WindooID = WindooID;
    }
}
