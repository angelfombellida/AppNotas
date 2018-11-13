package curso.android.seccion_04_realm.models;

import java.util.Date;

import curso.android.seccion_04_realm.app.MyApplication;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Board extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String title;
    @Required
    private Date createdAt;

    private RealmList<Note> notes;

    public Board() {
    }

    public Board(String title) {
        this.title = title;
        this.id = MyApplication.BoardID.incrementAndGet();
        this.createdAt = new Date();
        notes = new RealmList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }
}
