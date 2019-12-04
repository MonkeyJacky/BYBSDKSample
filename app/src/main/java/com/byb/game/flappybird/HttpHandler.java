package com.byb.game.flappybird;

import org.json.JSONObject;

public interface HttpHandler {
    void handleAuth(JSONObject data);

    void handlePayOrder(JSONObject data);
}
