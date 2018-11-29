package com.example.z003b2z.twodew.db

import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity

abstract class GenericRepository {
  abstract fun insertCustomItem(item: GenericSettingsEntity): Long

  abstract fun delete(item: GenericSettingsEntity): Int

  abstract suspend fun selectAllWho(): List<GenericSettingsEntity>

  abstract suspend fun selectAllWhat(): List<GenericSettingsEntity>

  abstract suspend fun selectAllWhen(): List<GenericSettingsEntity>
}