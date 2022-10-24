package org.example;

import java.net.URI;
import java.io.StringReader;
import java.net.URISyntaxException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Main {
    private static final boolean DEBUG = true;

    /**
     * args[0] = User Id
     * args[1] = Token
     *
     * @param args
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        final GgClient clientEndPoint;
        clientEndPoint = new GgClient(
                /* @TODO wss */
                new URI("ws://chat-1.goodgame.ru/chat2/")
        );
        clientEndPoint.addMessageHandler(new GgClient.MessageHandler() {
            public void handleMessage(String message) {
                if (DEBUG) {
                    System.out.println("<- " + message);
                }

                JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
            }
        });

        clientEndPoint.sendMessage(getMessageGg(
                "auth",
                Json.createObjectBuilder()
                        .add("user_id", args[0])
                        .add("token", args[1])
        ));

        clientEndPoint.sendMessage(getMessageGg(
                "join",
                Json.createObjectBuilder()
                        .add("channel_id", 115023)
                        .add("hidden", 0)
        ));

        /* @TODO Timer or Async.setSendTimeout or TyrusRemoteEndpoint.sendPing */
        while (true) {
            clientEndPoint.sendMessage(getMessageGg("ping", Json.createObjectBuilder()));
            Thread.sleep(15000);
        }
    }

    private static String getMessageGg(String type, JsonObjectBuilder data) {
        return Json.createObjectBuilder()
                .add("type", type)
                .add("data", data)
                .build()
                .toString();
    }
}