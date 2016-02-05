package sensores.datossensores.itemsmenu;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Battery_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;

public class SensorBateria extends Fragment implements View.OnClickListener{

    private Intent aware;
    private SendDataBatterySensorJSON sendDataBatterySensorJSON = null;
    Button botonbateria;
    ProgressBar progressBar;
    private Cursor battery_data;

    private static final String THINGSPEAK_API_KEY = "YXKCZC3MC3FO0S9OI";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";
    private static final String THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1";
    private static final String THINGSPEAK_FIELD2 = "field2";
    private static final String THINGSPEAK_FIELD3 = "field3";

    private int campo1, campo2, campo3;

    public SensorBateria(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.bateria, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Batería");

        aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Aware_Preferences.STATUS_BATTERY, true);

        Aware.startSensor(getContext(), Aware_Preferences.STATUS_BATTERY);

        botonbateria = (Button) rootView.findViewById(R.id.boton_bateria);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        botonbateria.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_bateria:
                battery_data = getActivity().getContentResolver().query(Battery_Provider.Battery_Data.CONTENT_URI, null, null, null,
                        Battery_Provider.Battery_Data.TIMESTAMP + " DESC LIMIT 10");

                if(battery_data != null && battery_data.getCount() > 0){
                    campo1 = battery_data.getInt(battery_data.getColumnIndex(Battery_Provider.Battery_Data.SCALE));
                    campo2 = battery_data.getInt(battery_data.getColumnIndex(Battery_Provider.Battery_Data.VOLTAGE));
                    campo3 = battery_data.getInt(battery_data.getColumnIndex(Battery_Provider.Battery_Data.TEMPERATURE));

                }

                botonbateria.setClickable(false);

                sendDataBatterySensorJSON = new SendDataBatterySensorJSON();
                sendDataBatterySensorJSON.execute(battery_data);

                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aware.stopSensor(getContext(), Aware_Preferences.STATUS_BATTERY);

        if (battery_data != null && !battery_data.isClosed()) {
            battery_data.close();
        }
    }


    private class SendDataBatterySensorJSON extends AsyncTask<Cursor, Integer, Void> {

        int progreso;

        @Override
        protected void onPostExecute(Void result){
            botonbateria.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Tarea finalizada", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute(){
            progressBar.setMax(100);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            progreso = values[0].intValue();
            progressBar.setProgress(progreso);

            if(progreso < 100 && progressBar.getVisibility() == View.GONE){
                progressBar.setVisibility(View.VISIBLE);
            }

            if(progreso == 100){
                progressBar.setVisibility(View.GONE);
            }

        }


        @Override
        protected Void doInBackground(Cursor... params) {

            while (progreso < 100){

                progreso++;
                publishProgress(progreso);

                for (Cursor data2 : params) {
                    if (data2 != null && data2.getCount() > 0) {
                        Http http2 = new Http(getActivity());
                        Hashtable<String, String> postData2 = new Hashtable<>();

                        postData2.put("battery_data", DatabaseHelper.cursorToString(data2));
                        http2.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                THINGSPEAK_FIELD1 + "=" + campo1 + "&" +
                                THINGSPEAK_FIELD2 + "=" + campo2 + "&" +
                                THINGSPEAK_FIELD3 + "=" + campo3, postData2, false);
                    }

                }

            }

            return null;
        }
    }
}
