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
import com.aware.providers.Light_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;

public class SensorLuz extends Fragment implements View.OnClickListener {

    private Intent aware;
    private SendDataLightSensorJSON sendDataLightSensorJSON = null;
    Button botonluz;
    ProgressBar progressBar1;
    private Cursor light_data;

    private static final String THINGSPEAK_API_KEY = "HZ1SHW04MG9RJ6VI", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1", THINGSPEAK_FIELD2 = "field3";

    private double campo1;
    private int campo2;

    public SensorLuz(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.luz, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Sensor de Luz");

        aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Aware_Preferences.STATUS_LIGHT, true);
        Aware.setSetting(getContext(), Aware_Preferences.FREQUENCY_LIGHT, 2000000);

        Aware.startSensor(getContext(), Aware_Preferences.STATUS_LIGHT);


        botonluz = (Button) rootView.findViewById(R.id.boton_luz);
        progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        botonluz.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_luz:
                light_data = getActivity().getContentResolver().query(Light_Provider.Light_Data.CONTENT_URI, null, null, null,
                        Light_Provider.Light_Data.TIMESTAMP + " DESC LIMIT 1");

                if(light_data != null && light_data.getCount() > 0) {

                    botonluz.setClickable(false);
                    sendDataLightSensorJSON = new SendDataLightSensorJSON();
                    sendDataLightSensorJSON.execute(light_data);

                }

                break;

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aware.stopSensor(getContext(), Aware_Preferences.STATUS_LIGHT);

         if (light_data != null && !light_data.isClosed()) {
                light_data.close();
         }

    }


    private class SendDataLightSensorJSON extends AsyncTask<Cursor, Integer, Void> {

        int progreso;

        @Override
        protected void onPostExecute(Void result){
            botonluz.setClickable(true);
            progressBar1.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Tarea finalizada", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute(){
            progressBar1.setMax(100);
            progressBar1.setProgress(0);
            progressBar1.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            progreso = values[0].intValue();
            progressBar1.setProgress(progreso);
        }


        @Override
        protected Void doInBackground(Cursor... params) {

            while (progreso < 100){

                progreso++;
                publishProgress(progreso);
                Http http1 = new Http(getActivity());

                for (Cursor data1 : params) {
                    if (data1 != null && data1.getCount() > 0 && data1.moveToFirst()) {

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

            }

            return null;
        }
    }

}
