package sensores.datossensores.itemsmenu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aware.Aware;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;
import sensores.datossensores.services.ServicioCPU;

public class SensorCPU extends Fragment implements View.OnClickListener {

    public SensorCPU(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.procesador, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Sensor de CPU");

        Intent aware = new Intent(getActivity(), Aware.class);
        getActivity().startService(aware);

        Button botonpantalla = (Button) rootView.findViewById(R.id.boton_cpu);
        botonpantalla.setOnClickListener(this);

        Button botoncancelar = (Button) rootView.findViewById(R.id.boton_cpu_cancelar);
        botoncancelar.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_cpu:
                Intent iniciarCPU = new Intent(getActivity(), ServicioCPU.class);
                getActivity().startService(iniciarCPU);
                Toast.makeText(getActivity(), "Servicio del sensor de la CPU creado. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boton_cpu_cancelar:
                Intent cancelarServicio = new Intent(getActivity(), ServicioCPU.class);
                getActivity().stopService(cancelarServicio);
                Toast.makeText(getActivity(), "¡Servicio del sensor dela CPU destruído!", Toast.LENGTH_SHORT).show();
                break;

        }

    }


}
