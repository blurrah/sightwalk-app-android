package net.sightwalk.Tasks;

import org.json.JSONObject;

/**
 * Created by Frank on 12/27/2015.
 */
public interface TaskInterface {
    void onSuccess(JSONObject data);

    void onFailure(int errorCode);
}
