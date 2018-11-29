package com.example.z003b2z.twodew.db.dao

import androidx.room.*
import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity
import com.example.z003b2z.twodew.db.entity.Type
import org.koin.standalone.KoinComponent

@Dao
interface SettingsDao: KoinComponent {

  @Insert
  fun insertSettingsItem(item: GenericSettingsEntity): Long

  @Delete
  fun deleteSettingsItem(item: GenericSettingsEntity): Int

  @Query("SELECT * FROM settings WHERE type == :whoType")
  fun selectAllWho(whoType: Type): Array<GenericSettingsEntity>

  @Query("SELECT * FROM settings WHERE type == :whatType")
  fun selectAllWhat(whatType: Type): Array<GenericSettingsEntity>

  @Query("SELECT * FROM settings WHERE type == :whenType")
  fun selectAllWhen(whenType: Type): Array<GenericSettingsEntity>

  @Insert
  fun insertAllData(provideStartingData: ArrayList<GenericSettingsEntity>)
}