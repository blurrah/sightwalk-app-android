package net.sightwalk.Tasks;

import net.sightwalk.Models.Sight;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateSightTask extends AuthenticatedAsyncNetworkTask {

    private JSONObject params;

    public CreateSightTask(Sight sight, TaskInterface callback) throws JSONException {
        super(callback);
        params = sight.toJSON();
    }

    @Override
    protected String getPath() {
        return "sight/";
    }

    @Override
    protected String getMethod() {
        return AsyncNetworkTask.METHOD_POST;
    }

    @Override
    protected JSONObject getParams() {
        return params;
    }
}