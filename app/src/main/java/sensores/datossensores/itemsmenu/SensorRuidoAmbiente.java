package sensores.datossensores.itemsmenu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sensores.datossensores.R;
import sensores.datossensores.activities.MainActivity;

public class SensorRuidoAmbiente extends Fragment{

    public SensorRuidoAmbiente(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.ruido, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Ruido Ambiente");

        return rootView;
    }
}
