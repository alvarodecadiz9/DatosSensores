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
import com.aware.plugin.google.activity_recognition.Google_AR_Provider;
import com.aware.plugin.google.activity_recognition.Settings;
import com.aware.utils.DatabaseHelper;
import com.aware.utils.Http;

import java.util.Hashtable;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;

public class SensorPluginGoogle extends Fragment implements View.OnClickListener{

    private Intent aware;
    private SendDataGoogleSensorJSON sendDataGoogleSensorJSON = null;
    Button botongoogle;
    ProgressBar progressBar3;
    private Cursor google_data;

    private static final String THINGSPEAK_API_KEY = "NH8BDD7YQ804I6XY", THINGSPEAK_API_KEY_STRING = "api_key",
            THINGSPEAK_UPDATE_URL = "http://api.thingspeak.com/update?";

    private static final String THINGSPEAK_FIELD1 = "field1", THINGSPEAK_FIELD2 = "field2";

    private int campo1, campo2;


    public SensorPluginGoogle(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.google, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Plugin de Google");

        aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
        Aware.setSetting(getContext(), Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 200000);

        Aware.startPlugin(getContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION);

        botongoogle = (Button) rootView.findViewById(R.id.boton_google);
        progressBar3 = (ProgressBar) rootView.findViewById(R.id.progressBar3);

        botongoogle.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_google:

                google_data = getActivity().getContentResolver().query(Google_AR_Provider.Google_Activity_Recognition_Data.
                                CONTENT_URI, null, null, null, Google_AR_Provider.Google_Activity_Recognition_Data.TIMESTAMP +
                        " DESC LIMIT 1");

                if (google_data != null && google_data.getCount() > 0) {

                    botongoogle.setClickable(false);
                    sendDataGoogleSensorJSON = new SendDataGoogleSensorJSON();
                    sendDataGoogleSensorJSON.execute(google_data);

                }

                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Aware.stopPlugin(getContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION);

        if (google_data != null && !google_data.isClosed()) {
            google_data.close();
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        Aware.stopPlugin(getContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION);

        if (google_data != null && !google_data.isClosed()) {
            google_data.close();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Aware.stopPlugin(getContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION);

        if (google_data != null && !google_data.isClosed()) {
            google_data.close();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        Aware.stopPlugin(getContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION);

        if (google_data != null && !google_data.isClosed()) {
            google_data.close();
        }
    }

    private class SendDataGoogleSensorJSON extends AsyncTask<Cursor, Integer, Void> {

        int progreso;

        @Override
        protected void onPostExecute(Void result){
            botongoogle.setClickable(true);
            progressBar3.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Tarea finalizada", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute(){
            progressBar3.setMax(100);
            progressBar3.setProgress(0);
            progressBar3.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            progreso = values[0].intValue();
            progressBar3.setProgress(progreso);
        }


        @Override
        protected Void doInBackground(Cursor... params) {

            while (progreso < 100){

                progreso++;
                publishProgress(progreso);
                Http http3 = new Http(getActivity());


                for (Cursor data3 : params) {
                    if (data3 != null && data3.getCount() > 0 && data3.moveToFirst()) {

                        do {

                            campo1 = google_data.getInt(google_data.
                                    getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.ACTIVITY_TYPE));

                            campo2 = google_data.getInt(google_data.
                                    getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.CONFIDENCE));

                            Hashtable<String, String> postData3 = new Hashtable<>();

                            postData3.put("google_data", DatabaseHelper.cursorToString(data3));
                            http3.dataPOST(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" + THINGSPEAK_API_KEY + "&" +
                                    THINGSPEAK_FIELD1 + "=" + campo1 + "&" +
                                    THINGSPEAK_FIELD2 + "=" + campo2, postData3, false);

                        } while (data3.moveToNext());

                    }

                }

            }

            return null;
        }
    }
}
