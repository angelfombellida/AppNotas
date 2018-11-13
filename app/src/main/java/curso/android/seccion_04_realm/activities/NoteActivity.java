package curso.android.seccion_04_realm.activities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import curso.android.seccion_04_realm.R;
import curso.android.seccion_04_realm.adapters.NoteAdapter;
import curso.android.seccion_04_realm.models.Board;
import curso.android.seccion_04_realm.models.Note;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board>{

    private Realm realm;
    private ListView listView;
    private RealmList<Note> notes;
    private NoteAdapter adapter;
    private int boardId;
    private FloatingActionButton fab;
    private Board board;
    private String BoardTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        Bundle b = getIntent().getExtras();
        if(b!=null) boardId = b.getInt("id");
        board = realm.where(Board.class).equalTo("id",boardId).findFirst();
        notes = board.getNotes();
        board.addChangeListener(this);

        BoardTitle = board.getTitle();
        this.setTitle(BoardTitle);

        listView = findViewById(R.id.listViewNote);
        fab = findViewById(R.id.fabAddNote);
        adapter = new NoteAdapter(notes,R.layout.list_view_note_item,this);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForCreatingNote("New note", "Type your new note");
            }
        });

        registerForContextMenu(listView);
    }


    //**AlertDialogs
    private void showAlertForCreatingNote(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title!=null) builder.setTitle(title);
        if(message!=null) builder.setMessage(message);


        View inflatedView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
        builder.setView(inflatedView);

        final EditText input = inflatedView.findViewById(R.id.editTextName);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String note = input.getText().toString().trim();
                if (note.length() > 0)
                   createNewNote(note);
                else
                    Toast.makeText(getApplicationContext(), "Type something!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void showAlertDialogDeleteAllBoards(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        //Si no inflamos una vista y le pasamos un layout, crea uno "vacío" por defecto

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllNotes();
            }
        });

        builder.create().show();
    }


    private void showAlertForEditingNote(String title, String message, final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View inflatedView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
        builder.setView(inflatedView);

        final EditText input = inflatedView.findViewById(R.id.editTextName);
        input.setText(note.getDescription());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String desc = input.getText().toString().trim();
                if (desc.length() == 0)
                    Toast.makeText(getApplicationContext(), "A new description is required", Toast.LENGTH_SHORT).show();
                else if (desc.equals(note.getDescription()))
                    Toast.makeText(getApplicationContext(), "The new description must be different", Toast.LENGTH_SHORT).show();
                else editNote(desc, note);
            }
        });

        builder.create().show();
    }


    //  **CRUD**
    private void createNewNote(String note) {
        realm.beginTransaction();
        Note newNote = new Note(note);
        board.getNotes().add(newNote);
        realm.copyToRealmOrUpdate(newNote); //copyToRealm crashea la app
        realm.commitTransaction();
    }

    private void deleteNote (Note note){
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }

    private void deleteAllNotes() {
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm();
        realm.commitTransaction();
    }

    private void editNote(String desc, Note note) {
        realm.beginTransaction();
        note.setDescription(desc);
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    //**Menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_board, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_deleteAllItems:
                showAlertDialogDeleteAllBoards("Delete all notes", "Are you sure you want to delete all notes from \""+BoardTitle+"\"?");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_boards, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.item_delete:
                deleteNote(notes.get(info.position));
                return true;
            case R.id.item_edit:
                showAlertForEditingNote("Edit note", "Type the new description",notes.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //  **Events**
    @Override
    public void onChange(Board board) {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
