package net.sightwalk.Tasks;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateSightTask extends AuthenticatedAsyncNetworkTask {

    private JSONObject params;

    public CreateSightTask(LatLng location, String title, String type, String description, TaskInterface callback) throws JSONException {
        super(callback);

        params = new JSONObject();

        params.put("longitude", location.longitude);
        params.put("latitude", location.latitude);
        params.put("precision", 1);
        params.put("name", title);
        params.put("type", type);
        params.put("short_description", description.substring(0, description.length() > 100 ? 100 : description.length()));
        params.put("image_url", "https://pbs.twimg.com/profile_images/588458393444167680/jqP97Xwo.jpg");
        params.put("description", description);
        params.put("external_photo", "https://pbs.twimg.com/profile_images/588458393444167680/jqP97Xwo.jpg");
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