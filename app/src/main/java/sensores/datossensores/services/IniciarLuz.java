package sensores.datossensores.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Light_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;


public class IniciarLuz extends Service {

    public Cursor light_data;
    SendDataLightSensorJSON sendDataLightSensorJSON;
    private Intent aware;

    private static final String THINGSPEAK_API_KEY = "HZ1SHW04MG9RJ6VI", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1", THINGSPEAK_FIELD2 = "field3";

    private double campo1;
    private int campo2;

    public IniciarLuz() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        aware = new Intent(this, Aware.class);
        startService(aware);

        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_LIGHT, 2000000);

        Aware.startSensor(getApplicationContext(), Aware_Preferences.STATUS_LIGHT);

        Toast.makeText(IniciarLuz.this, "Servicio creado", Toast.LENGTH_SHORT).show();

        sendDataLightSensorJSON = new SendDataLightSensorJSON();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        light_data = getContentResolver().query(Light_Provider.Light_Data.CONTENT_URI, null, null, null,
                Light_Provider.Light_Data.TIMESTAMP + " DESC LIMIT 1");

        if(light_data != null && light_data.moveToFirst()) {

           // sendDataLightSensorJSON = new SendDataLightSensorJSON();
            sendDataLightSensorJSON.execute(light_data);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (light_data != null && !light_data.isClosed()) {

            light_data.close();
        }

        Aware.stopSensor(getApplicationContext(), Aware_Preferences.STATUS_LIGHT);
        Toast.makeText(this, "¡Servicio destruído!", Toast.LENGTH_SHORT).show();
        sendDataLightSensorJSON.cancel(true);

    }

    private class SendDataLightSensorJSON extends AsyncTask<Cursor, Void, Void> {

        private boolean cent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cent = true;
        }

        @Override
        protected Void doInBackground(Cursor... params) {

            Http http1 = new Http(getApplicationContext());

            for (Cursor data1 : params) {
                if (data1 != null && data1.getCount() > 0) {

                    do {

                        campo1 = light_data.getDouble(light_data.getColumnIndex(Light_Provider.Light_Data.LIGHT_LUX));
                        campo2 = light_data.getInt(light_data.getColumnIndex(Light_Provider.Light_Data.ACCURACY));

                        Hashtable<String, String> postData1 = new Hashtable<>();
                        postData1.put("light_data", DatabaseHelper.cursorToString(data1));

                        http1.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                THINGSPEAK_FIELD1 + "=" + campo1 + "&" +
                                THINGSPEAK_FIELD2 + "=" + campo2, postData1, false);

                    } while (data1.moveToNext());

                }

            }


            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cent = false;
        }
    }
}
