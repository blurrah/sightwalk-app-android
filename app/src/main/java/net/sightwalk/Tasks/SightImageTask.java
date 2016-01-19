package net.sightwalk.Tasks;

import android.content.Context;

public class SightImageTask extends FileUploadTask {

    private String identifier;
    private Context context;

    public SightImageTask(Context context, String identifier, TaskInterface callback) {
        super(callback);

        this.context = context;
        this.identifier = identifier;
    }

    @Override
    protected String getPath() {
        return "sight/" + identifier + "/photo";
    }

    @Override
    protected String getTitle() {
        return "sightImage";
    }

    @Override
    protected String getDescription() {
        return "Sight photo";
    }
}