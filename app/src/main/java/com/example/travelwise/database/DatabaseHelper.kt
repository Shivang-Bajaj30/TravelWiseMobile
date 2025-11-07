package com.example.travelwise.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.travelwise.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "TravelWiseDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        
        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_FULL_NAME = "full_name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FULL_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * Insert a new user into the database
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    fun insertUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FULL_NAME, user.fullName)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_PASSWORD, user.password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    /**
     * Check if a user with the given email exists
     */
    fun userExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    /**
     * Authenticate user by email and password
     * @return User object if credentials are valid, null otherwise
     */
    fun authenticateUser(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_FULL_NAME, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_PASSWORD),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return user
    }

    /**
     * Get user by email
     */
    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_FULL_NAME, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_PASSWORD),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return user
    }

    /**
     * Get all users (for testing/debugging purposes)
     */
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_FULL_NAME, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_PASSWORD),
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            users.add(
                User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                )
            )
        }

        cursor.close()
        db.close()
        return users
    }

    /**
     * Print all users to Logcat (for debugging)
     */
    fun printAllUsers() {
        val users = getAllUsers()
        Log.d("DatabaseHelper", "========== USERS TABLE ==========")
        Log.d("DatabaseHelper", "Total users: ${users.size}")
        Log.d("DatabaseHelper", "-----------------------------------")
        if (users.isEmpty()) {
            Log.d("DatabaseHelper", "No users found in database")
        } else {
            users.forEach { user ->
                Log.d("DatabaseHelper", "ID: ${user.id}")
                Log.d("DatabaseHelper", "Name: ${user.fullName}")
                Log.d("DatabaseHelper", "Email: ${user.email}")
                Log.d("DatabaseHelper", "Phone: ${user.phone}")
                Log.d("DatabaseHelper", "Password: ${user.password}")
                Log.d("DatabaseHelper", "-----------------------------------")
            }
        }
        Log.d("DatabaseHelper", "===================================")
    }

    /**
     * Get database path (for debugging - to locate the database file)
     */
    fun getDatabasePath(): String {
        return readableDatabase.path
    }
}

