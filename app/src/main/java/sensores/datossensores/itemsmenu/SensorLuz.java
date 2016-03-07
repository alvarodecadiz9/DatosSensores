package sensores.datossensores.itemsmenu;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;
import sensores.datossensores.services.IniciarLuz;

public class SensorLuz extends Fragment implements View.OnClickListener {

    private Intent aware;
    Button botonluz, botoncancelar;
    private Cursor light_data;

    public SensorLuz(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.luz, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Sensor de Luz");

       /* aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Aware.setSetting(getContext(), Aware_Preferences.STATUS_LIGHT, true);
        Aware.setSetting(getContext(), Aware_Preferences.FREQUENCY_LIGHT, 2000000);

        Aware.startSensor(getContext(), Aware_Preferences.STATUS_LIGHT);*/


        botonluz = (Button) rootView.findViewById(R.id.boton_luz);
        botoncancelar = (Button) rootView.findViewById(R.id.boton_luz_cancelar);

        botonluz.setOnClickListener(this);
        botoncancelar.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_luz:

                getActivity().startService(new Intent(getActivity(), IniciarLuz.class));
                break;

            case R.id.boton_luz_cancelar:
                getActivity().stopService(new Intent(getActivity(), IniciarLuz.class));
                break;

        }

    }

    public void onDestroy() {
        super.onDestroy();
        Aware.stopSensor(getContext(), Aware_Preferences.STATUS_LIGHT);
    }


}
