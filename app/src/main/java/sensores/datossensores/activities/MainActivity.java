package sensores.datossensores.activities;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import sensores.datossensores.R;
import sensores.datossensores.itemsmenu.SensorBateria;
import sensores.datossensores.itemsmenu.SensorGoogle;
import sensores.datossensores.itemsmenu.SensorLuz;
import sensores.datossensores.itemsmenu.SensorCPU;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView texto, texto2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texto = (TextView) findViewById(R.id.textView);
        texto2 = (TextView) findViewById(R.id.textView2);

        Toolbar appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer); //Establecemos el icono del menú
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Establecemos que el ActionBar muestre el botón Home
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navview);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                boolean fragmentTransaction = false;
                Fragment fragment = null;

                switch (menuItem.getItemId()){
                    case R.id.sensor_luz:
                        fragment = new SensorLuz();
                        fragmentTransaction = true;
                        texto.setText("");
                        texto2.setText("");
                        break;

                    case R.id.sensor_bateria:
                        fragment = new SensorBateria();
                        fragmentTransaction = true;
                        texto.setText("");
                        texto2.setText("");
                        break;

                    case R.id.sensor_procesador:
                        fragment = new SensorCPU();
                        fragmentTransaction = true;
                        texto.setText("");
                        texto2.setText("");
                        break;


                    case R.id.sensor_google:
                        fragment = new SensorGoogle();
                        fragmentTransaction = true;
                        texto.setText("");
                        texto2.setText("");
                        break;
                }

                if(fragmentTransaction){
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    menuItem.setChecked(true);
                    getSupportActionBar().setTitle(menuItem.getTitle());
                }

                drawerLayout.closeDrawers();

                return true;
            }
        });


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

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
