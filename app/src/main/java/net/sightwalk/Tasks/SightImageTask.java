package net.sightwalk.Tasks;

import android.content.Context;

import net.sightwalk.Stores.SightImageStore;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Frank on 1/14/2016.
 */
public class SightImageTask extends FileUploadTask {

    private String identifier;
    private Context context;

    public SightImageTask(Context context, String identifier) {
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
