package com.example.z003b2z.twodew.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class GenericSettingsEntity(
  @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int,
  @ColumnInfo(name = "text") var text: String,
  @ColumnInfo(name = "type") var type: Type
)

enum class Type {
  WHO,
  WHAT,
  WHEN
}