package org.cook_team.wn2nac;

public class WindooUser {
    private int userID = -1;
    private String name = "";
    private String email = "";

    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setUserID(int userID) { this.userID = userID; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
