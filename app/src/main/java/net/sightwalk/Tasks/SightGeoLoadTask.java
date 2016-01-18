package net.sightwalk.Tasks;

import com.google.android.gms.maps.model.LatLng;

public class SightGeoLoadTask extends AuthenticatedAsyncNetworkTask {

    private LatLng location;
    private int distance;

    public SightGeoLoadTask(LatLng location, int distance, TaskInterface callback) {
        super(callback);

        this.location = location;
        this.distance = distance;
    }

    @Override
    protected String getPath() {
        return "sight/" + Double.toString(location.latitude) + "/" + Double.toString(location.longitude) + "/" + distance;
    }

    @Override
    protected String getMethod() {
        return AsyncNetworkTask.METHOD_GET;
    }
}