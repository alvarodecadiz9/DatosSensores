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
import com.aware.providers.Light_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;


public class ServicioLuz extends Service {

    private static final String THINGSPEAK_API_KEY = "LY6OKD4OKYOTZIQN", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1", THINGSPEAK_FIELD2 = "field2";

    private static AlarmManager alarmManager;
    private static Intent awareframework;
    private static PendingIntent repeatedTask;

    private SendDataLightSensorJSON sendDataLightSensorJSON = null;

    public ServicioLuz() {}

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
        Aware.setSetting(this, Aware_Preferences.STATUS_LIGHT, true);
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);

        Aware.startLight(this);

        Toast.makeText(ServicioLuz.this, "Siguiente monitorización del sensor de luz. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceToRepeat = new Intent(this, ServicioLuz.class);
        repeatedTask = PendingIntent.getService(this, 0, serviceToRepeat, 0);

        //Hacer que se ejecute cada minuto
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, repeatedTask);
    }

    /*
    *  Este método se ejecuta cada 60 segundos y una vez cuando el servicio es creado
    * */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendDataLightSensorJSON = new SendDataLightSensorJSON();
        sendDataLightSensorJSON.execute();

        return super.onStartCommand(intent, flags, startId);
    }


    private class SendDataLightSensorJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //Recuperando el último valor de la base de datos
            Cursor data1 = getContentResolver().query(Light_Provider.Light_Data.CONTENT_URI, null, null, null,
                    Light_Provider.Light_Data.TIMESTAMP + " DESC LIMIT 1");

            Log.d("ServicioLuz", "Light data:\n" + DatabaseUtils.dumpCursorToString(data1));

            Http http1 = new Http(getApplicationContext());

            if(data1 != null && data1.moveToNext()){

                double campo1 = data1.getDouble(data1.getColumnIndex(Light_Provider.Light_Data.LIGHT_LUX));
                int campo2 = data1.getInt(data1.getColumnIndex(Light_Provider.Light_Data.ACCURACY));

                Hashtable<String, String> postData1 = new Hashtable<>();
                postData1.put("data1", DatabaseHelper.cursorToString(data1));

                http1.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                        THINGSPEAK_FIELD1 + "=" + campo1 + "&" +
                        THINGSPEAK_FIELD2 + "=" + campo2, postData1, false);
                }

                if(data1 != null && !data1.isClosed()){
                    data1.close();
                }

            return null;
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Aware.setSetting(this, Aware_Preferences.STATUS_LIGHT, false);
        Aware.stopLight(this);
        stopService(awareframework);
        alarmManager.cancel(repeatedTask);
        sendDataLightSensorJSON.cancel(true);
        repeatedTask.cancel();
    }
}
