package net.sightwalk.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.security.Permission;
import java.util.ArrayList;

/**
 * Created by Frank on 12/26/2015.
 */
abstract public class PermissionActivity extends AppCompatActivity {
    private static final int PERMISSION_GPS_FINE = 1;
    private static final int PERMISSION_GPS_COARSE = 2;

    private ArrayList<PermissionInterface> transactions = new ArrayList<>();

    public boolean oneGranted(String... permissions) {
        for (String p : permissions) {
            if (isGranted(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean allGranted(String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p)) {
                return false;
            }
        }
        return true;
    }

    public boolean isGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean canAskGranting(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }

    private void askGrant(String[] permissions, PermissionInterface transaction) {
        transactions.add(transaction);
        ActivityCompat.requestPermissions(this, permissions, transactions.indexOf(transaction));
    }

    public void validateGranted(String permission, PermissionInterface transaction) {
        if (isGranted(permission)) {
            transaction.granted(permission);
        }

        if (canAskGranting(permission)) {
            // ask user to grant permission
            askGrant(new String[]{permission}, transaction);
        }

        // not (yet) granted...
        transaction.denied(permission);
    }

    public final void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        PermissionInterface transaction = transactions.get(requestCode);

        if (transaction instanceof PermissionInterface) {
            transactions.set(transactions.indexOf(transaction), null);

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                transaction.granted(permissions);
                return;
            }

            transaction.denied(permissions);
        }
    }

}
