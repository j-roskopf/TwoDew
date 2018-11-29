package com.example.z003b2z.twodew.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.db.dao.JobDao
import com.example.z003b2z.twodew.db.dao.SettingsDao
import com.example.z003b2z.twodew.db.dao.TaskDao
import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity
import com.example.z003b2z.twodew.db.entity.JobEntity
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.db.entity.Type
import com.example.z003b2z.twodew.di.tasks.TaskItemProvider
import com.example.z003b2z.twodew.di.tasks.WhenItemProvider
import com.example.z003b2z.twodew.di.tasks.WhoItemProvider
import io.reactivex.subjects.BehaviorSubject
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import java.util.concurrent.Executors

@Database(entities = [Task::class, JobEntity::class, GenericSettingsEntity::class], version = 1, exportSchema = false)
@TypeConverters(TaskTypeConverter::class)
abstract class TaskDatabase : RoomDatabase(), KoinComponent {

  private val taskItemProvider: TaskItemProvider by inject()
  private val whoItemProvider: WhoItemProvider by inject()
  private val whenItemProvider: WhenItemProvider by inject()

  abstract fun taskDao(): TaskDao
  abstract fun jobDao(): JobDao
  abstract fun settingsDao(): SettingsDao

  fun provideStartingData(): ArrayList<GenericSettingsEntity> {
    val toReturn = ArrayList<GenericSettingsEntity>()

    taskItemProvider.provideListOfTaskItems().forEach {
      toReturn.add(GenericSettingsEntity(0, it.text, Type.WHAT))
    }

    whoItemProvider.provideListOfWhoItems().forEach {
      toReturn.add(GenericSettingsEntity(0, it.text, Type.WHO))
    }

    whenItemProvider.provideListOfWhenItems().forEach {
      toReturn.add(GenericSettingsEntity(0, it.text, Type.WHEN))
    }

    return toReturn
  }

  companion object {

    @Volatile
    private var INSTANCE: TaskDatabase? = null

    val dbReadySubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun getInstance(context: Context): TaskDatabase {
      return if (INSTANCE != null) {
        INSTANCE!!
      } else {
        synchronized(this) {
          INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }
      }
    }

    private fun buildDatabase(context: Context): TaskDatabase {
      val db = Room.databaseBuilder(
        context.applicationContext,
        TaskDatabase::class.java, "task-db"
      ).fallbackToDestructiveMigration()

      dbReadySubject.onNext(true)

      return db.addCallback(object : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
          super.onCreate(db)
          ioThread {
            getInstance(context).settingsDao().insertAllData(getInstance(context).provideStartingData())
            dbReadySubject.onNext(true)
          }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
          super.onOpen(db)
          Timber.d("test")
        }
      }).build()
    }
  }
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f: () -> Unit) {
  IO_EXECUTOR.execute(f)
}

class TaskTypeConverter {
  @TypeConverter
  fun fromString(value: String?): Type? {
    return value?.let { Type.valueOf(it) }
  }

  @TypeConverter
  fun typeToString(type: Type?): String? {
    return type.toString()
  }
}