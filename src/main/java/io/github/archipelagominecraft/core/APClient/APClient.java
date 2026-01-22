package io.github.archipelagominecraft.core.APClient;

import io.github.archipelagominecraft.core.ArchipelagoMinecraftClientCore;
import io.github.archipelagomw.Client;
import io.github.archipelagomw.flags.ItemsHandling;

import java.net.URISyntaxException;

public class APClient extends Client {
    private final APContext ctx;

    public APClient(APContext ctx) {
        super();
        this.ctx = ctx;
        // Change this name to pull from a config file or some equivalent later
        this.setGame("Minecraft");
    }

    public static int tryConnection(APContext context, String address, int port, String slot, String password) {
        APClient apClient = context.getClient();
        if (apClient != null) {
            apClient.close();
        }
        apClient = new APClient(context);
        context.setClient(apClient);
        apClient.setPassword(password);
        apClient.setName(slot);
        apClient.setItemsHandlingFlags(ItemsHandling.SEND_ITEMS + ItemsHandling.SEND_OWN_ITEMS + ItemsHandling.SEND_STARTING_INVENTORY);

        try {
            String server = address + ":" + port;
            apClient.connect(server);
        } catch (URISyntaxException e) {
            //Replace with a useful chat error instead later
            ArchipelagoMinecraftClientCore.LOGGER.error(e.toString());
        }
        return 0;
    }


    @Override
    public void onError(Exception ex) {

    }

    @Override
    public void onClose(String Reason, int attemptingReconnect) {

    }
}
