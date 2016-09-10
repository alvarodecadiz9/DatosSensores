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
import sensores.datossensores.services.ServicioBateria;

public class SensorBateria extends Fragment implements View.OnClickListener {

    public SensorBateria() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.bateria, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Batería");

       Button botonbateria = (Button) rootView.findViewById(R.id.boton_bateria);
        botonbateria.setOnClickListener(this);

        Button botoncancelar = (Button) rootView.findViewById(R.id.boton_bateria_cancelar);
        botoncancelar.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.boton_bateria:
                Intent iniciarBateria = new Intent(getActivity(), ServicioBateria.class);
                getActivity().startService(iniciarBateria);
                Toast.makeText(getActivity(), "Servicio de la batería creado. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boton_bateria_cancelar:
                Intent cancelarServicio = new Intent(getActivity(), ServicioBateria.class);
                getActivity().stopService(cancelarServicio);
                Toast.makeText(getActivity(), "¡Servicio de la batería destruído!", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}

