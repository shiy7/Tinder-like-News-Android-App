package com.laioffer.tinnews.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.laioffer.tinnews.model.Article;

// Entities: Current entities/tables Database contains
// Version Code: It specifies a current version. It is used for data migration.
@Database(entities = {Article.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    // to define how to access the database
    public abstract RoomDao dao();

    // could add migration strategy
    // Migration Strategy: How we could migrate the database from previous versions if something has changed.
}
