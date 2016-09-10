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
import com.aware.Aware_Preferences;
import com.aware.providers.Battery_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

public class ServicioBateria extends Service {

    private static final String THINGSPEAK_API_KEY = "LY6OKD4OKYOTZIQN", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD3 = "field3", THINGSPEAK_FIELD4 = "field4", THINGSPEAK_FIELD5 = "field5";

    private static AlarmManager alarmManager;
    private static Intent awareframework;
    private static PendingIntent repeatedTask;

    private SendDataBatterySensorJSON sendDataBatterySensorJSON = null;

    public ServicioBateria() {}

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
        Aware.setSetting(this, Aware_Preferences.STATUS_BATTERY, true);

        Aware.startBattery(this);

        Toast.makeText(ServicioBateria.this, "Siguiente monitorización de la batería. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceToRepeat = new Intent(this, ServicioBateria.class);
        repeatedTask = PendingIntent.getService(this, 0, serviceToRepeat, 0);

        //Hacer que se ejecute cada minuto
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, repeatedTask);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendDataBatterySensorJSON = new SendDataBatterySensorJSON();
        sendDataBatterySensorJSON.execute();

        return super.onStartCommand(intent, flags, startId);

    }

    private class SendDataBatterySensorJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //Recuperando el último valor de la base de datos
            Cursor data2 = getContentResolver().query(Battery_Provider.Battery_Data.CONTENT_URI, null, null, null,
                    Battery_Provider.Battery_Data.TIMESTAMP + " DESC LIMIT 1");

            Log.d("ServicioBateria", "Battery data:\n" + DatabaseUtils.dumpCursorToString(data2));

            //Estableciendo conexión HTTP
            Http http2 = new Http(getApplicationContext());

            if(http2 != null){

                getContentResolver().delete(Battery_Provider.Battery_Data.CONTENT_URI, null, null);
            }

            if(data2 != null && data2.moveToNext()) {

                    int campo3 = data2.getInt(data2.getColumnIndex(Battery_Provider.Battery_Data.LEVEL));
                    int campo4 = data2.getInt(data2.getColumnIndex(Battery_Provider.Battery_Data.VOLTAGE));
                    int campo5 = data2.getInt(data2.getColumnIndex(Battery_Provider.Battery_Data.TEMPERATURE));

                    Hashtable<String, String> postData2 = new Hashtable<>();
                    postData2.put("data2", DatabaseHelper.cursorToString(data2));

                    http2.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                            THINGSPEAK_FIELD3 + "=" + campo3 + "&" +
                            THINGSPEAK_FIELD4 + "=" + campo4 + "&" +
                            THINGSPEAK_FIELD5 + "=" + campo5, postData2, false);

                }

                if(data2 != null && !data2.isClosed()){
                    data2.close();
                }

            return null;
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Aware.setSetting(this, Aware_Preferences.STATUS_BATTERY, false);
        Aware.stopBattery(this);
        stopService(awareframework);
        alarmManager.cancel(repeatedTask);
        sendDataBatterySensorJSON.cancel(true);
        repeatedTask.cancel();
    }

}
