package net.mtuomiko.traffichistory;

/**
 * Dummy holder for incoming PubSub message. Not actually used since the function doesn't interpret the contents.
 */
public class PubsubMessage {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

