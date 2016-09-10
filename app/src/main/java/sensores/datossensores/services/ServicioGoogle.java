package sensores.datossensores.services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.plugin.google.activity_recognition.Google_AR_Provider;
import com.aware.plugin.google.activity_recognition.Settings;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

public class ServicioGoogle extends Service {

    private static final String THINGSPEAK_API_KEY = "LY6OKD4OKYOTZIQN", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD7 = "field7", THINGSPEAK_FIELD8 = "field8";

    private static AlarmManager alarmManager;
    private static Intent awareframework;
    private static PendingIntent repeatedTask;

    private SendDataGoogleSensorJSON sendDataGoogleSensorJSON = null;

    public ServicioGoogle() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Iniciar Aware
        awareframework = new Intent(this, Aware.class);
        startService(awareframework);

        //Establecemos las opciones
        Aware.setSetting(this, Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
        Aware.setSetting(this, Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 200000);

        Aware.startPlugin(this, "com.aware.plugin.google.activity_recognition");

        Toast.makeText(ServicioGoogle.this, "Siguiente monitorización del plugin de Google Activity. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceToRepeat = new Intent(this, ServicioGoogle.class);
        repeatedTask = PendingIntent.getService(this, 0, serviceToRepeat, 0);

        //Hacer que se ejecute cada minuto
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, repeatedTask);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendDataGoogleSensorJSON = new SendDataGoogleSensorJSON();
        sendDataGoogleSensorJSON.execute();

        return super.onStartCommand(intent, flags, startId);

    }

    private class SendDataGoogleSensorJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //Recuperando el último valor de la base de datos
            Cursor data4 = getContentResolver().query(Google_AR_Provider.Google_Activity_Recognition_Data.CONTENT_URI, null, null, null,
                    Google_AR_Provider.Google_Activity_Recognition_Data.TIMESTAMP + " DESC LIMIT 1");

            Log.d("ServicioGoogle", "Google data:\n" + DatabaseUtils.dumpCursorToString(data4));

            Http http4 = new Http(getApplicationContext());

            if(data4 != null && data4.moveToNext()){

                    int campo7 = data4.getInt(data4.getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.ACTIVITY_TYPE));
                    int campo8 = data4.getInt(data4.getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.CONFIDENCE));

                    Hashtable<String, String> postData4 = new Hashtable<>();
                    postData4.put("data4", DatabaseHelper.cursorToString(data4));

                    http4.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                            THINGSPEAK_FIELD7 + "=" + campo7 + "&" +
                            THINGSPEAK_FIELD8 + "=" + campo8, postData4, false);

                }

                if(data4 != null && !data4.isClosed()){
                    data4.close();
                }

            return null;
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Aware.setSetting(this, Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, false);
        Aware.stopPlugin(this, "com.aware.plugin.google.activity_recognition");
        stopService(awareframework);
        alarmManager.cancel(repeatedTask);
        sendDataGoogleSensorJSON.cancel(true);
        repeatedTask.cancel();
    }

}

