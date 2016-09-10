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
import sensores.datossensores.services.ServicioGoogle;

public class SensorGoogle extends Fragment implements View.OnClickListener {

    public SensorGoogle() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.google, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Plugin de Google");

        Button botongoogle = (Button) rootView.findViewById(R.id.boton_google);
        botongoogle.setOnClickListener(this);

        Button botoncancelar = (Button) rootView.findViewById(R.id.boton_google_cancelar);
        botoncancelar.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.boton_google:
                Intent iniciarPluginGoogle = new Intent(getActivity(), ServicioGoogle.class);
                getActivity().startService(iniciarPluginGoogle);
                Toast.makeText(getActivity(), "Servicio del plugin de google activity creado. Consulte el canal de ThingSpeak para ver los datos", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boton_google_cancelar:
                Intent cancelarServicio = new Intent(getActivity(), ServicioGoogle.class);
                getActivity().stopService(cancelarServicio);
                Toast.makeText(getActivity(), "¡Servicio del plugin de google activity destruído!", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
