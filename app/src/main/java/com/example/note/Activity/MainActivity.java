package com.example.note.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.note.Adapter.MAdapter;
import com.example.note.Model.Note;
import com.example.note.R;
import com.example.note.database.MyDataBase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MAdapter mAdapter;
    private List<Note> notes;
    private String title;
    private String description;
    private String date;
    private final static int REQUEST_CODE1 = 101;
    private final static int REQUEST_CODE2 = 102;
    private int note_index;
    private ActionMode actionMode;
    private MyActionModeCallBack actionModeCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes = new ArrayList<>();
        MyDataBase db = new MyDataBase(getApplicationContext());
        notes.addAll(db.getAllNotes());

        setUp();


        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab_main_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(EditActivity.TITLE, "");
                intent.putExtra(EditActivity.DESCRIPTION, "");
                startActivityForResult(intent, REQUEST_CODE1);
            }
        });

        actionModeCallBack = new MyActionModeCallBack();

    }


    private void onDATABASEAdd(Note note) {
        MyDataBase db = new MyDataBase(getApplicationContext());
        db.addNote(note);
    }

    private void onDATABASEUpdate(Note note) {
        MyDataBase db = new MyDataBase(getApplicationContext());
        db.updateNote(note);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE1 && resultCode == RESULT_OK && data != null) {
            title = data.getStringExtra(EditActivity.TITLE);
            description = data.getStringExtra(EditActivity.DESCRIPTION);
            date = data.getStringExtra(EditActivity.DATE);
            if (title.length() != 0 || description.length() != 0) {
                Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_LONG).show();
                onDATABASEAdd(generate_note());
                setUp();
            }
        }
        if (requestCode == REQUEST_CODE2 && resultCode == RESULT_OK && data != null) {
            Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_LONG).show();
            title = data.getStringExtra(EditActivity.TITLE);
            description = data.getStringExtra(EditActivity.DESCRIPTION);
            date = data.getStringExtra(EditActivity.DATE);
            onDATABASEUpdate(change_note());
            System.out.println(notes.get(note_index).getId());
            setUp();
        }
    }

    private Note change_note() {
        notes.get(note_index).setTitle(title);
        notes.get(note_index).setDescription(description);
        notes.get(note_index).setDate(date);
        return notes.get(note_index);
    }


    private void setUp() {
        recyclerView = findViewById(R.id.rc_main);

        mAdapter = new MAdapter(this, notes, new MAdapter.MViewHolder.onNoteclickListener() {
            @Override
            public void onNoteclicked(Note note) {
                if (mAdapter.getSelectedCount() == 0) {
                    onMyItemClicked(note);
                } else {
                    onMyItemLongClicked(note);
                }
            }

            @Override
            public void onLongclicked(Note note) {
                onMyItemLongClicked(note);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mAdapter);

    }

    private void onMyItemLongClicked(Note note) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallBack);
        }
        mAdapter.toggleSelection(notes.indexOf(note));

        int count = mAdapter.getSelectedCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();//refresh actionmode
        }


    }

    private void onMyItemClicked(Note note) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.TITLE, note.getTitle());
        intent.putExtra(EditActivity.DESCRIPTION, note.getDescription());
        note_index = notes.indexOf(note);
        startActivityForResult(intent, REQUEST_CODE2);

    }


    private Note generate_note() {
        Note n = new Note();
        int id;
        if (notes.size() == 0)
            id = 0;
        else
            id = notes.get(notes.size() - 1).getId() + 1;
        n.setId(id);
        n.setTitle(title);
        n.setDescription(description);
        n.setDate(date);
        notes.add(n);
        return n;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_no_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SearchView searchView = findViewById(R.id.noactionSearch_main_menu);
        searchView.setQueryHint("search...");
        searchView.setIconifiedByDefault(false);

        final List<String> title_list = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            title_list.add(notes.get(i).getTitle());
        }
        final List<String> des_list = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            des_list.add(notes.get(i).getDescription());
        }

        if (item.getItemId() == R.id.noactionSearch_main_menu) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (title_list.contains(query) || des_list.contains(query)) {
                    mAdapter.getFilter().filter(query);
                } else {
                    Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
            return true;
    }

        return super.onOptionsItemSelected(item);
}

class MyActionModeCallBack implements androidx.appcompat.view.ActionMode.Callback {
    @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_main_action, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.btn_actionMenu_delete) {

                List<Integer> selected_item_position = mAdapter.getSelectedItem();
                MyDataBase db = new MyDataBase(getApplicationContext());
                for (int i = selected_item_position.size() - 1; i >= 0; i--) {
                    db.NoteDelete(notes.get(selected_item_position.get(i)).getId());
                    mAdapter.removeItem(selected_item_position.get(i));
                }
                mAdapter.notifyDataSetChanged();


                mode.finish();//finish action mode
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(androidx.appcompat.view.ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }

    }
}
