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
import com.aware.plugin.google.activity_recognition.Google_AR_Provider;
import com.aware.plugin.google.activity_recognition.Settings;
import com.aware.providers.Light_Provider;
import com.aware.providers.Screen_Provider;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;

public class SensorPantalla extends Fragment implements View.OnClickListener{

    private Intent aware;
    private SendDataScreenSensorJSON sendDataScreenSensorJSON = null;
    Button botonpantalla;
    ProgressBar progressBar;
    private Cursor screen_data;

    private static final String THINGSPEAK_API_KEY = "CRNFMJRDKW7WUZCU";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";
    private static final String THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1";

    private int campo1;

    public SensorPantalla(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.pantalla, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Pantalla");

        aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Aware_Preferences.STATUS_SCREEN, true);;

        Aware.startSensor(getContext(), Aware_Preferences.STATUS_SCREEN);

        botonpantalla = (Button) rootView.findViewById(R.id.boton_pantalla);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar5);

        botonpantalla.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_pantalla:
                screen_data = getActivity().getContentResolver().query(Screen_Provider.Screen_Data.CONTENT_URI, null, null, null,
                        Screen_Provider.Screen_Data.TIMESTAMP + " DESC LIMIT 10");

                if(screen_data != null && screen_data.getCount() > 0){
                    campo1 = screen_data.getInt(screen_data.getColumnIndex(Screen_Provider.Screen_Data.SCREEN_STATUS));
                }

                botonpantalla.setClickable(false);
                DatabaseHelper.cursorToString(screen_data);

                sendDataScreenSensorJSON = new SendDataScreenSensorJSON();
                sendDataScreenSensorJSON.execute(screen_data);

                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Aware.stopSensor(getContext(), Aware_Preferences.STATUS_SCREEN);

        if (screen_data != null && !screen_data.isClosed()) {
            screen_data.close();
        }

    }


    private class SendDataScreenSensorJSON extends AsyncTask<Cursor, Integer, Void> {

        int progreso;

        @Override
        protected void onPostExecute(Void result){
            botonpantalla.setClickable(true);
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

                for (Cursor data4 : params) {
                    if (data4 != null && data4.getCount() > 0) {
                        Http http4 = new Http(getActivity());
                        Hashtable<String, String> postData4 = new Hashtable<>();

                        postData4.put("screen_data", DatabaseHelper.cursorToString(data4));
                        http4.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                THINGSPEAK_FIELD1 + "=" + campo1, postData4, false);
                    }

                }

            }

            return null;
        }
    }
}