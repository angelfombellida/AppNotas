package curso.android.seccion_04_realm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import curso.android.seccion_04_realm.R;
import curso.android.seccion_04_realm.adapters.BoardAdaptader;
import curso.android.seccion_04_realm.models.Board;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class BoardActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Board>>, AdapterView.OnItemClickListener {

    private FloatingActionButton fab;
    private Realm realm;
    private BoardAdaptader adaptader;
    private ListView listView;
    private RealmResults<Board> boards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        realm = Realm.getDefaultInstance(); //Cargar configuracion por defecto

        listView = findViewById(R.id.listViewBoard);

        boards = realm.where(Board.class).findAll();
        adaptader = new BoardAdaptader(this, R.layout.list_view_board_item, boards);
        listView.setAdapter(adaptader);
        boards.addChangeListener(this);

        listView.setOnItemClickListener(this);

        fab = findViewById(R.id.fabAddBoard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForCreatingBoard("Create table", "Type the new table name");
            }
        });

        registerForContextMenu(listView);
    }



    //**CRUD**
    public void createNewBoard(String name) {
        realm.beginTransaction();
        Board board = new Board(name);
        realm.copyToRealm(board);
        realm.commitTransaction();
    }

    public void deleteAllBoards() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }


    private void deleteBoard(Board board) {
        realm.beginTransaction();
        board.deleteFromRealm();
        realm.commitTransaction();
    }


    private void editBoard(String name, Board board) {
        realm.beginTransaction();
        board.setTitle(name);
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }



    //**AlertDialogs**
    private void showAlertForCreatingBoard(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View inflatedView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
        builder.setView(inflatedView);

        final EditText input = inflatedView.findViewById(R.id.editTextName);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input.getText().toString().trim();
                if (name.length() > 0)
                    createNewBoard(name);
                else
                    Toast.makeText(getApplicationContext(), "Type a name!", Toast.LENGTH_SHORT).show();
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
                deleteAllBoards();
            }
        });

        builder.create().show();
    }

    private void showAlertForEditingBoard(String title, String message, final Board board) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View inflatedView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
        builder.setView(inflatedView);

        final EditText input = inflatedView.findViewById(R.id.editTextName);
        input.setText(board.getTitle());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input.getText().toString().trim();
                if (name.length() == 0)
                    Toast.makeText(getApplicationContext(), "A new name is required", Toast.LENGTH_SHORT).show();
                else if (name.equals(board.getTitle()))
                    Toast.makeText(getApplicationContext(), "The new name must be different", Toast.LENGTH_SHORT).show();
                else editBoard(name, board);
            }
        });

        builder.create().show();
    }



    //  **Events**
    @Override
    public void onChange(RealmResults<Board> boards) {
        adaptader.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("id", boards.get(position).getId());
        startActivity(intent);
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
                showAlertDialogDeleteAllBoards("Delete all boards", "Are you sure you want to delete all boards?");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(boards.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_boards, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.item_delete:
                deleteBoard(boards.get(info.position));
                return true;
            case R.id.item_edit:
                showAlertForEditingBoard("Edit board", "Type the new title",boards.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

}