package eu.enriquerodriguez.contador;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ctx = this;

        TextView txtContador = (TextView)findViewById(R.id.txtContador);

        GetContador getContador = new GetContador(this, txtContador);
        getContador.execute();

        Button btnComprarAcciones = (Button)findViewById(R.id.btnComprarAcciones);
        btnComprarAcciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://realmurcia.es/hazlosuyo/"));
                startActivity(browserIntent);
            }
        });

        Button btnAddWidget = (Button)findViewById(R.id.btnAddWidget);
        btnAddWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppWidgetManager appWidgetManager = getSystemService(AppWidgetManager.class);
                ComponentName myProvider = new ComponentName(ctx, ContadorWidget.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(appWidgetManager.isRequestPinAppWidgetSupported()){
                        appWidgetManager.requestPinAppWidget(myProvider, null, null);

                    }
                }else{
                    new AlertDialog.Builder(ctx)
                            .setTitle("Inserta el widget desde tu pantalla principal")
                            .setMessage("Si quieres añadir el widget del contador, accede a tu pantalla principal y seleccion el widget de Contador Real Murcia")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView txtContador = (TextView)findViewById(R.id.txtContador);

        GetContador getContador = new GetContador(this, txtContador);
        getContador.execute();
    }

    class GetContador extends AsyncTask{

        String encontrado;
        Activity ctx;

        TextView txtContador;

        public GetContador(Activity ctx, TextView txtContador) {
            this.ctx = ctx;
            this.txtContador = txtContador;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtContador.setText("Cargando...");
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            encontrado = new String();

            String RE_NAME = "<h2>(.*?)</h2>";

            try {
                URL url = new URL("https://realmurcia.es/hazlosuyo/index.php");
                BufferedReader red;
                try {
                    red = new BufferedReader(new InputStreamReader(url.openStream()));
                    String cadena;

                    while ((cadena = red.readLine()) != null) {

                        Pattern pat_name = Pattern.compile(RE_NAME);
                        Matcher mat_name = pat_name.matcher(cadena);

                        if (mat_name.find()) {
                            encontrado = mat_name.group(1);
                            break;
                        }

                    }
                    red.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            txtContador.setText(encontrado);

        }
    }
}
