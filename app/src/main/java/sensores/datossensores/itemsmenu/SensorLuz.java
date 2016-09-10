package sensores.datossensores.itemsmenu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;
import sensores.datossensores.services.ServicioLuz;

public class SensorLuz extends Fragment implements View.OnClickListener {

    public SensorLuz(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.luz, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Sensor de Luz");

       Button botonluz = (Button) rootView.findViewById(R.id.boton_luz);
        botonluz.setOnClickListener(this);

       Button botoncancelar = (Button) rootView.findViewById(R.id.boton_luz_cancelar);
        botoncancelar.setOnClickListener(this);

        return rootView;
    }


   @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_luz:
                Intent iniciarLuz = new Intent(getActivity(), ServicioLuz.class);
                getActivity().startService(iniciarLuz);
                Toast.makeText(getActivity(), "Servicio del sensor de luz creado. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boton_luz_cancelar:
                Intent cancelarLuz = new Intent(getActivity(), ServicioLuz.class);
                getActivity().stopService(cancelarLuz);
                Toast.makeText(getActivity(), "¡Servicio del sensor de luz destruído!", Toast.LENGTH_SHORT).show();
                break;

        }

    }


}
