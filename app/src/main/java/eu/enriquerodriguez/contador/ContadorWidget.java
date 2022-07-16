package eu.enriquerodriguez.contador;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContadorWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;

        for(int i=0;i<N;i++){

            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_contador);

            views.setOnClickPendingIntent(R.id.view, pendingIntent);

            new GetContador(appWidgetManager, appWidgetId, views).execute();

        }

    }

    class GetContador extends AsyncTask {

        AppWidgetManager appWidgetManager;
        int appWidgetId;
        RemoteViews views;

        String encontrado;

        public GetContador(AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
            this.views = views;
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

            views.setTextViewText(R.id.txtContador,  encontrado);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}
