package ru.kazachkov.florist.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.realm.Realm;
import ru.kazachkov.florist.BuildConfig;
import ru.kazachkov.florist.interfaces.App;

@ReportsCrashes(formUri = "http://soft-techno.com/public/report.php",
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        mode = ReportingInteractionMode.SILENT,
        buildConfigClass = BuildConfig.class)
public class FloristApp extends MultiDexApplication implements App {


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Realm.init(getApplicationContext());

        ACRA.init(this);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                .build());

    }

}
