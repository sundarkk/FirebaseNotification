package com.nescantinfo.fcb.fcbnotify;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String tokenname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            Preference notificationPref = (SwitchPreference)findPreference("notifications_new_message");
            notificationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isNotificationOn = (Boolean) newValue;
                    if(preference.getKey().equals("notifications_new_message") && isNotificationOn){
                        // call the push registration for this user
                        checkPlayServices();
                        FirebaseMessaging.getInstance().subscribeToTopic("news");
                        String token = FirebaseInstanceId.getInstance().getToken();
                        tokenname= FirebaseInstanceId.getInstance().getToken();
                        Log.d(TAG, "Token value " + token);

                    }else{
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                    }
                    return true;
                }
            });
            //Toast.makeText(MainActivity.this, tokenname.toString(), Toast.LENGTH_SHORT).show();
        }
        private boolean checkPlayServices() {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Log.i(TAG, "This device is not supported.");
                    getActivity().finish();
                }
                return false;
            }
            return true;
        }
    }
}
