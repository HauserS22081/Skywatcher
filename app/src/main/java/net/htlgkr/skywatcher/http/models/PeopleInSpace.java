package net.htlgkr.skywatcher.http.models;

public class PeopleInSpace {
    //"people": [
    //    {
    //      "craft": "ISS",
    //      "name": "Oleg Kononenko"
    //    },


    private String craft;
    private String name;

    public PeopleInSpace(String craft, String name) {
        this.craft = craft;
        this.name = name;
    }

    public String getCraft() {
        return craft;
    }

    public void setCraft(String craft) {
        this.craft = craft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
