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

public class SensorLuz extends Fragment implements View.OnClickListener{

    private Intent aware;
    private SendDataLightSensorJSON sendDataLightSensorJSON = null;
    Button botonluz;
    ProgressBar progressBar;
    private Cursor light_data;

    private static final String THINGSPEAK_API_KEY = "HZ1SHW04MG9RJ6VI";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";
    private static final String THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD2 = "field2";
    private static final String THINGSPEAK_FIELD3 = "field3";

    private Double campo2;
    private int campo3;


    public SensorLuz(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.luz, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Sensor de Luz");

        aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Aware_Preferences.STATUS_LIGHT, true);
        //Aware.setSetting(getContext(), Aware_Preferences.FREQUENCY_LIGHT, 2000000);

        Aware.startSensor(getContext(), Aware_Preferences.STATUS_LIGHT);


        botonluz = (Button) rootView.findViewById(R.id.boton_luz);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        botonluz.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_luz:
                light_data = getActivity().getContentResolver().query(Light_Provider.Light_Data.CONTENT_URI, null, null, null,
                        Light_Provider.Light_Data.TIMESTAMP + " DESC LIMIT 10");

                if(light_data != null && light_data.getCount() > 0){

                        botonluz.setClickable(false);
                        campo2 = light_data.getDouble(light_data.getColumnIndex(Light_Provider.Light_Data.LIGHT_LUX));
                        campo3 = light_data.getInt(light_data.getColumnIndex(Light_Provider.Light_Data.ACCURACY));

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

                for (Cursor data1 : params) {
                    if (data1 != null && data1.getCount() > 0) {
                        Http http1 = new Http(getActivity());
                        Hashtable<String, String> postData1 = new Hashtable<>();

                        postData1.put("light_data", DatabaseHelper.cursorToString(data1));
                        http1.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                THINGSPEAK_FIELD2 + "=" + campo2 + "&" +
                                THINGSPEAK_FIELD3 + "=" + campo3, postData1, false);
                    }

                }

            }

            return null;
        }
    }

}
