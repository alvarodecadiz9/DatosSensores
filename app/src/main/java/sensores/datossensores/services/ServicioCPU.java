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

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Processor_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

public class ServicioCPU extends Service {

    private static final String THINGSPEAK_API_KEY = "LY6OKD4OKYOTZIQN", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD6 = "field6";

    private static AlarmManager alarmManager;
    private static Intent awareframework;
    private static PendingIntent repeatedTask;

    private SendDataCPUSensorJSON sendDataCPUSensorJSON = null;

    public ServicioCPU() {}

    @Override
    public IBinder onBind(Intent intent) { return  null; }

    @Override
    public void onCreate() {
        super.onCreate();

        //Iniciar Aware
        awareframework = new Intent(this, Aware.class);
        startService(awareframework);

        //Establecemos las opciones
        Aware.setSetting(this, Aware_Preferences.STATUS_PROCESSOR, true);
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_PROCESSOR, 20);

        Aware.startProcessor(this);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceToRepeat = new Intent(this, ServicioCPU.class);
        repeatedTask = PendingIntent.getService(this, 0, serviceToRepeat, 0);

        //Hacer que se ejecute cada minuto
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 60 * 1000, repeatedTask);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendDataCPUSensorJSON = new SendDataCPUSensorJSON();
        sendDataCPUSensorJSON.execute();

        return super.onStartCommand(intent, flags, startId);

    }

private class SendDataCPUSensorJSON extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {

        //Recuperando el último valor de la base de datos
        Cursor data3 = getContentResolver().query(Processor_Provider.Processor_Data.CONTENT_URI, null, null, null,
                Processor_Provider.Processor_Data.TIMESTAMP + " DESC LIMIT 1");

        Log.d("SensorCPU", "Sensor de la CPU:\n" + DatabaseUtils.dumpCursorToString(data3));

        Http http3 = new Http(getApplicationContext());

        if(data3 != null && data3.moveToNext()){

            double porcentaje = data3.getDouble(data3.getColumnIndex(Processor_Provider.Processor_Data.USER_LOAD));

            float campo6 = (float) Math.round(porcentaje * 100) / 100;

            Hashtable<String, String> postData3 = new Hashtable<>();
            postData3.put("data3", DatabaseHelper.cursorToString(data3));

            http3.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                        THINGSPEAK_FIELD6 + "=" + campo6, postData3, false);

        }

        if(data3 != null && !data3.isClosed()){
                data3.close();
        }

        return null;
    }

}

    @Override
    public void onDestroy() {

        super.onDestroy();
        Aware.setSetting(this, Aware_Preferences.STATUS_PROCESSOR, false);
        Aware.stopProcessor(this);
        stopService(awareframework);
        alarmManager.cancel(repeatedTask);
        sendDataCPUSensorJSON.cancel(true);
        repeatedTask.cancel();
    }

}
