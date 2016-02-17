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
    ProgressBar progressBar5;
    private Cursor screen_data;

    private static final String THINGSPEAK_API_KEY = "CRNFMJRDKW7WUZCU", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

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
        progressBar5 = (ProgressBar) rootView.findViewById(R.id.progressBar5);

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
                    botonpantalla.setClickable(false);
                    campo1 = screen_data.getInt(screen_data.getColumnIndex(Screen_Provider.Screen_Data.SCREEN_STATUS));


                }

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
            progressBar5.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Tarea finalizada", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute(){
            progressBar5.setMax(100);
            progressBar5.setProgress(0);
            progressBar5.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            progreso = values[0].intValue();
            progressBar5.setProgress(progreso);
        }


        @Override
        protected Void doInBackground(Cursor... params) {

            while (progreso < 100){

                progreso++;
                publishProgress(progreso);

                for (Cursor data5 : params) {
                    if (data5 != null && data5.getCount() > 0) {
                        Http http5 = new Http(getActivity());
                        Hashtable<String, String> postData5 = new Hashtable<>();

                        postData5.put("screen_data", DatabaseHelper.cursorToString(data5));
                        http5.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                THINGSPEAK_FIELD1 + "=" + campo1, postData5, false);
                    }

                }

            }

            return null;
        }
    }
}
