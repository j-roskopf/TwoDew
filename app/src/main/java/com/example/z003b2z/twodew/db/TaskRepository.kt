package com.example.z003b2z.twodew.db

import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity
import com.example.z003b2z.twodew.db.entity.Type
import org.koin.standalone.KoinComponent

class TaskRepository(private val taskDatabase: TaskDatabase): GenericRepository() {
  override suspend fun selectAllWhat(): List<GenericSettingsEntity> {
    return taskDatabase.settingsDao().selectAllWhat(Type.WHAT).toList()
  }

  override suspend fun selectAllWhen(): List<GenericSettingsEntity> {
    return taskDatabase.settingsDao().selectAllWhat(Type.WHEN).toList()
  }

  override suspend fun selectAllWho(): List<GenericSettingsEntity> {
    return taskDatabase.settingsDao().selectAllWhat(Type.WHO).toList()
  }

  override fun insertCustomItem(item: GenericSettingsEntity): Long {
    return taskDatabase.settingsDao().insertSettingsItem(item)
  }

  override fun delete(item: GenericSettingsEntity): Int {
    return taskDatabase.settingsDao().deleteSettingsItem(item)
  }

}