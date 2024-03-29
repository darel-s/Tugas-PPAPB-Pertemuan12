package com.example.tugaspertemuan12

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NoteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "notes_app"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PLAYER = "playerName"
        private const val COLUMN_CLUB = "clubName"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTabQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_PLAYER TEXT, $COLUMN_CLUB TEXT)"
        db?.execSQL(createTabQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTabQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTabQuery)
        onCreate(db)
    }


    fun insertNote(note: Note): Boolean {
        if (note.playerName.isNotEmpty() && note.clubName.isNotEmpty()) {
            val values = ContentValues().apply {
                put(COLUMN_PLAYER, note.playerName)
                put(COLUMN_CLUB, note.clubName)
            }
            writableDatabase.use { db ->
                db.insert(TABLE_NAME, null, values)
            }
            return true
        } else {
            return false
        }
    }

    fun getAllNotes(): List<Note> {
        val noteList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB))

            val note = Note(id, title, content)
            noteList.add(note)
        }
        cursor.close()
        db.close()
        return noteList
    }

    fun updateNote(note: Note): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_PLAYER, note.playerName)
            put(COLUMN_CLUB, note.clubName)
        }
        val result = writableDatabase.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(note.id.toString()))
        return result > 0
    }

    fun getNoteByID(noteId: Int): Note{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLUB))

        cursor.close()
        db.close()
        return Note (id, title, content)
    }

    fun deleteNote(noteId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}