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
import com.aware.plugin.ambient_noise.Provider;
import com.aware.plugin.ambient_noise.Settings;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;

public class SensorRuidoAmbiente extends Fragment implements View.OnClickListener{

    private Intent aware;
    private SendDataAmbientNoiseSensorJSON sendDataAmbientNoiseSensorJSON = null;
    Button botonruido;
    ProgressBar progressBar4;
    private Cursor noise_data;

    private static final String THINGSPEAK_API_KEY = "9MN1ZZDK0D75FW0X", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1", THINGSPEAK_FIELD2 = "field2", THINGSPEAK_FIELD3 = "field3";

    private double campo1, campo2;
    private int campo3;

    public SensorRuidoAmbiente(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.ruido, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Ruido Ambiente");

        aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Settings.STATUS_PLUGIN_AMBIENT_NOISE, true, "com.aware.plugin.ambient_noise");
        Aware.setSetting(getContext(), Settings.PLUGIN_AMBIENT_NOISE_SAMPLE_SIZE, 60, "com.aware.plugin.ambient_noise");

        Aware.startPlugin(getContext(), "com.aware.plugin.ambient_noise");

        botonruido = (Button) rootView.findViewById(R.id.boton_ruido);
        progressBar4 = (ProgressBar) rootView.findViewById(R.id.progressBar4);

        botonruido.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_ruido:

                noise_data = getActivity().getContentResolver().query(Provider.AmbientNoise_Data.CONTENT_URI,
                        null, null, null, Provider.AmbientNoise_Data.TIMESTAMP + " DESC LIMIT 1");

                if (noise_data != null && noise_data.getCount() > 0) {

                    botonruido.setClickable(false);
                    sendDataAmbientNoiseSensorJSON = new SendDataAmbientNoiseSensorJSON();
                    sendDataAmbientNoiseSensorJSON.execute(noise_data);

                }

                break;
        }

    }

    @Override
    public void onDestroy() {

        Aware.stopPlugin(getContext(), "com.aware.plugin.ambient_noise");

        if (noise_data != null && !noise_data.isClosed()) {
            noise_data.close();
        }

        super.onDestroy();

    }


    @Override
    public void onPause(){

        Aware.stopPlugin(getContext(), "com.aware.plugin.ambient_noise");

        if (noise_data != null && !noise_data.isClosed()) {
            noise_data.close();
        }

        super.onPause();

    }

    @Override
    public void onStop() {

        Aware.stopPlugin(getContext(), "com.aware.plugin.ambient_noise");

        if (noise_data != null && !noise_data.isClosed()) {
            noise_data.close();
        }

        super.onStop();
    }

    @Override
    public void onDestroyView(){

        Aware.stopPlugin(getContext(),"com.aware.plugin.ambient_noise");

        if (noise_data != null && !noise_data.isClosed()) {
            noise_data.close();
        }

        super.onDestroyView();
    }

    private class SendDataAmbientNoiseSensorJSON extends AsyncTask<Cursor, Integer, Void> {

        int progreso;

        @Override
        protected void onPostExecute(Void result){
            botonruido.setClickable(true);
            progressBar4.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Tarea finalizada", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute(){
            progressBar4.setMax(100);
            progressBar4.setProgress(0);
            progressBar4.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            progreso = values[0].intValue();
            progressBar4.setProgress(progreso);

        }


        @Override
        protected Void doInBackground(Cursor... params) {

            while (progreso < 100){

                progreso++;
                publishProgress(progreso);
                Http http4 = new Http(getActivity());

                for (Cursor data4 : params) {
                    if (data4 != null && data4.getCount() > 0 && data4.moveToFirst()) {

                        do {

                            campo1 = noise_data.getDouble(noise_data.getColumnIndex(Provider.AmbientNoise_Data.FREQUENCY));
                            campo2 = noise_data.getDouble(noise_data.getColumnIndex(Provider.AmbientNoise_Data.DECIBELS));
                            campo3 = noise_data.getInt(noise_data.getColumnIndex(Provider.AmbientNoise_Data.IS_SILENT));

                            Hashtable<String, String> postData4 = new Hashtable<>();

                            postData4.put("noise_data", DatabaseHelper.cursorToString(data4));
                            http4.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                    THINGSPEAK_FIELD1 + "=" + campo1 + "&" +
                                    THINGSPEAK_FIELD2 + "=" + campo2 + "&" +
                                    THINGSPEAK_FIELD3 + "=" + campo3, postData4, false);

                        } while (data4.moveToNext());

                    }

                }

            }

            return null;
        }
    }
}
