package com.example.note.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.note.Model.Note;

import java.util.ArrayList;
import java.util.List;

public class MyDataBase extends SQLiteOpenHelper {
    private static final String DB_NAME="mydb";
    private static final int DB_VERSION=1;
    private static final String  TABLE_NAME="notetable";

    //soton haye jadval
    private static final String  KEY_ID="id";
    private static final String  TITLE="title";
    private static final String  DESCRIPTION="description";
    private static final String  DATE="date";

    private Context context;

    public MyDataBase(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"+ TITLE + " TEXT," + DESCRIPTION + " TEXT," + DATE + " TEXT" + ") " );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        //create table again
        onCreate(db);
    }

    public void addNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_ID,note.getId());
        values.put(TITLE,note.getTitle());
        values.put(DESCRIPTION,note.getDescription());
        values.put(DATE,note.getDate());

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public Note getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        TITLE, DESCRIPTION,DATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note();
        note.setId(Integer.parseInt(cursor.getString(0)));
        note.setTitle(cursor.getString(1));
        note.setDescription(cursor.getString(2));
        note.setDate(cursor.getString(3));

        db.close();
        return note;
    }


    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setDescription(cursor.getString(2));
                note.setDate(cursor.getString(3));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // return contact list
        return notes;
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,note.getId());
        values.put(TITLE,note.getTitle());
        values.put(DESCRIPTION,note.getDescription());
        values.put(DATE,note.getDate());

        // updating row
        db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(note.getId())});

    }

    public void NoteDelete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

}
