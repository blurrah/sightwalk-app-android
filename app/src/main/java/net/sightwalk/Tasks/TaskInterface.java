package net.sightwalk.Tasks;

import org.json.JSONObject;

public interface TaskInterface {
    void onSuccess(JSONObject data);
    void onFailure(int errorCode);
}