package io.github.archipelagominecraft.core.APClient;

public class APContext {
    private static final APContext INSTANCE = new APContext();

    public static APContext getContext() {
        return INSTANCE;
    }

    private APClient client;

    public APClient getClient() {
        return client;
    }

    public void setClient(APClient client) {
        if(client == this.client) {
            return;
        }
        this.client = client;
    }
}
