package com.example.dictionary_exercise


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dictionary.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "words"
        private const val COLUMN_WORD = "word"
        private const val COLUMN_DEFINITION = "definition"

        private const val CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_WORD TEXT PRIMARY KEY, " +
                    "$COLUMN_DEFINITION TEXT NOT NULL);"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
        // Add some initial words and definitions
        addWord(db, "Monkey", "Giant lemurs, now extinct, were as large as adult gorillas.")
        addWord(db, "banana", "A long curved fruit with yellow skin and soft flesh.")
        addWord(db, "orange", "A round citrus fruit with a tough bright reddish-yellow rind.")
        addWord(db, "grape", "A small, round, typically sweet fruit growing in bunches on a vine.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun addWord(db: SQLiteDatabase, word: String, definition: String) {
        val values = ContentValues()
        values.put(COLUMN_WORD, word)
        values.put(COLUMN_DEFINITION, definition)
        db.insert(TABLE_NAME, null, values)
    }

    fun getDefinition(word: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_DEFINITION),
            "$COLUMN_WORD=?",
            arrayOf(word),
            null, null, null
        )
        var definition: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                definition = it.getString(0)
            }
        }
        db.close()
        return definition
    }

    fun getSubstringMatches(text: String): List<String> {
        val db = readableDatabase
        val suggestions = mutableListOf<String>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_WORD),
            "$COLUMN_WORD LIKE ?",
            arrayOf("%$text%"),
            null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                suggestions.add(it.getString(0))
            }
        }
        db.close()
        return suggestions
    }
}