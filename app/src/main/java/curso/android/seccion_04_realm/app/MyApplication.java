package curso.android.seccion_04_realm.app;

import android.app.Application;

import java.util.concurrent.atomic.AtomicInteger;

import curso.android.seccion_04_realm.models.Board;
import curso.android.seccion_04_realm.models.Note;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    public static AtomicInteger BoardID = new AtomicInteger();
    public static AtomicInteger NoteID = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        BoardID = getTableId(realm, Board.class);
        NoteID = getTableId(realm, Note.class);
        realm.close();
        super.onCreate();
    }

    private void setUpRealmConfig() {
        Realm.init(getApplicationContext());

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
//Autoincrementar id
    private <T extends RealmObject> AtomicInteger getTableId(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        if (results.size() > 0) {
            return new AtomicInteger(results.max("id").intValue());
        } else return new AtomicInteger();
    }
}
