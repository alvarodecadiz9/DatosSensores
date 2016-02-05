package sensores.datossensores.activities;



import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import sensores.datossensores.R;
import sensores.datossensores.itemsmenu.SensorBateria;
import sensores.datossensores.itemsmenu.SensorLuz;
import sensores.datossensores.itemsmenu.SensorPantalla;
import sensores.datossensores.itemsmenu.SensorPluginGoogle;
import sensores.datossensores.itemsmenu.SensorRuidoAmbiente;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView texto, texto2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texto = (TextView) findViewById(R.id.textView);

        texto2 = (TextView) findViewById(R.id.textView2);

        texto.setText(Html.fromHtml("<b>DatosSensores</b> es una aplicación que sirve" +
                " para obtener automáticamente datos de distintos sensores del móvil, utilizando para ello la " +
                        "librería <b>Aware Framework</b>."));

        texto2.setText(Html.fromHtml("Para enviar datos de los sensores diponibles en la app, pulse sobre el menú, " +
                "elija una opción y pulse el botón <b>Enviar datos</b>."));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        //Establecemos el icono del menú
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);

        //Establecemos que el ActionBar muestre el botón Home
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.navview);

        if(nvDrawer != null){

            setupDrawerContent(nvDrawer);

        }

        setupDrawerContent(nvDrawer);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void setupDrawerContent(NavigationView navigationView){

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sensor_luz:
                                menuItem.setChecked(true);
                                setFragment(0);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                texto.setText("");
                                texto2.setText("");
                                return true;

                            case R.id.sensor_bateria:
                                menuItem.setChecked(true);
                                setFragment(1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                texto.setText("");
                                texto2.setText("");
                                return true;

                            case R.id.sensor_google:
                                menuItem.setChecked(true);
                                setFragment(2);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                texto.setText("");
                                texto2.setText("");
                                return true;

                            case R.id.sensor_ruido:
                                menuItem.setChecked(true);
                                setFragment(3);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                texto.setText("");
                                texto2.setText("");
                                return true;

                            case R.id.sensor_pantalla:
                                menuItem.setChecked(true);
                                setFragment(4);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                texto.setText("");
                                texto2.setText("");
                                return true;
                        }

                        return true;
                    }
                });
    }


    public void setFragment(int position){
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch (position){
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SensorLuz sensorLuz = new SensorLuz();
                fragmentTransaction.replace(R.id.frame_principal, sensorLuz);
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SensorBateria sensorBateria = new SensorBateria();
                fragmentTransaction.replace(R.id.frame_principal, sensorBateria);
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SensorPluginGoogle sensorPluginGoogle = new SensorPluginGoogle();
                fragmentTransaction.replace(R.id.frame_principal, sensorPluginGoogle);
                fragmentTransaction.commit();
                break;

            case 3:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SensorRuidoAmbiente sensorRuidoAmbiente = new SensorRuidoAmbiente();
                fragmentTransaction.replace(R.id.frame_principal, sensorRuidoAmbiente);
                fragmentTransaction.commit();
                break;

            case 4:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SensorPantalla sensorPantalla = new SensorPantalla();
                fragmentTransaction.replace(R.id.frame_principal, sensorPantalla);
                fragmentTransaction.commit();
                break;
        }
    }





}
