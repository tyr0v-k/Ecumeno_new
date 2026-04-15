package com.uvpv521.calendar.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.uvpv521.calendar.data.entities.Book
import com.uvpv521.calendar.data.entities.Category
import com.uvpv521.calendar.data.entities.Prayer
import com.uvpv521.calendar.data.entities.Verse
import java.io.FileOutputStream
import java.util.Locale
import kotlin.io.copyTo
import kotlin.io.use

class DatabaseHelper(private val context: Context, private val dbName: String) : SQLiteOpenHelper(context, dbName, null, 1) {

    init { copyDatabaseIfNeeded() }

    private fun copyDatabaseIfNeeded() {
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            val localeName = if (Locale.getDefault().language == "ru") "_ru." else "_en."
            val dbAssetName = dbName.substringBeforeLast(".") + localeName + dbName.substringAfterLast(".")
            context.assets.open(dbAssetName).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun getBooks(): List<Book> {
        val books = mutableListOf<Book>()
        readableDatabase.rawQuery("SELECT * FROM books ORDER BY book_number", null).use { cursor ->
            while (cursor.moveToNext()) {
                books.add(Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4)))
            }
        }
        return books
    }

    fun getChapters(bookNumber: Int): List<Int> {
        val chapters = mutableListOf<Int>()
        readableDatabase.rawQuery("SELECT DISTINCT chapter FROM verses WHERE book_number = ? ORDER BY chapter",
            arrayOf(bookNumber.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                chapters.add(cursor.getInt(0))
            }
        }
        return chapters
    }

    fun getVerses(bookNumber: Int, chapter: Int): List<Verse> {
        val verses = mutableListOf<Verse>()
        readableDatabase.rawQuery("SELECT * FROM verses WHERE book_number = ? AND chapter = ? ORDER BY verse",
            arrayOf(bookNumber.toString(), chapter.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                verses.add(Verse(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3)))
            }
        }
        return verses
    }

    fun getCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        readableDatabase.rawQuery("SELECT * FROM categories ORDER BY category_number", null).use { cursor ->
            while (cursor.moveToNext()) {
                categories.add(Category(cursor.getInt(0), cursor.getString(1)))
            }
        }
        return categories
    }

    fun getPrayers(categoryNumber: Int): List<Prayer> {
        val prayers = mutableListOf<Prayer>()
        readableDatabase.rawQuery("SELECT * FROM prayers WHERE category_number = ? ORDER BY prayer_position",
            arrayOf(categoryNumber.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                prayers.add(Prayer(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)))
            }
        }
        return prayers
    }
}