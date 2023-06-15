package com.example.droidmodels.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.IllegalArgumentException
import java.util.Optional
import kotlin.reflect.KClass


class SQLDatabase(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?,
    databaseName: String,
    databaseVersion: Int
) : SQLiteOpenHelper(context, databaseName, factory, databaseVersion) {

     private val models = mutableMapOf<String, String>()
    private val hasSetDB = false

    override fun onCreate(db: SQLiteDatabase) {
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")
        Log.e("TABLE CREATE", "START")

        for (value in models.values){
            db.execSQL(value)
            Log.e("TABLE CREATE", value)
        }
        Log.e("TABLE CREATE", "DONE")

    }

    private fun createTableCommand(model: SQLModel<*>): String {

        val columns = model.getColumns().map { column ->

            val columnContent = mutableListOf(column.name)

            columnContent.add(column.getSQLType())

            if (column.primaryKey) {
                columnContent.add("PRIMARY KEY AUTOINCREMENT")
            }
            if (column.unique) {
                columnContent.add("UNIQUE")
            }

            columnContent.joinToString(" ")
        }
        return ("CREATE TABLE IF NOT EXISTS ${model.getName()} ( ${columns.joinToString(", ")} )")
    }

    fun <T: Document>addModel(model: SQLModel<T>){
        if(!hasSetDB) SQLModel.db = this
        model.postInit()
        val command = createTableCommand(model)
        models[model.getName()] = command
//
//        this.readableDatabase.execSQL(command)
//        this.writableDatabase.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        for (name in models.keys){
            db.execSQL("DROP TABLE IF EXISTS $name")
        }
        onCreate(db)
    }
}



abstract class SQLModel<T : Document>(private val name: String, private val kClass: KClass<T>, private val idKey: String="id"): Model<T> {
    abstract val id: SQLPrimaryColumn<T, *>
    var fieldToColumnMapping: MutableMap<String, String>  = mutableMapOf()

    fun postInit(){
        getColumns().forEach {
            fieldToColumnMapping[it.fieldName] = it.name
        }
    }

    fun getColumns(): List<SQLColumn<T, *>>{
        val list: MutableList<SQLColumn<T, *>> = mutableListOf(id)
        val tFields = kClass.getFields().filter {
            try {
                it.first != id.name && it.second.get<Any>(this) is SQLColumn<*, *>
            } catch (e: IllegalArgumentException){
                false
            }
        }.map { it.second.get<SQLColumn<T, *>>(this)  }.toList()
        list.addAll(tFields)
        return list
    }

    override fun getName(): String {
        return name
    }

     companion object Meta {
         lateinit var db: SQLDatabase
    }

    @Throws(TypeCastException::class)
    override fun get(id: String): Optional<T> {

        val readableDatabase = db.readableDatabase

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $name WHERE ${this.id.name} = ?", arrayOf(id)
        )
        if (!cursor.moveToFirst()) {
            cursor.close()
            return Optional.empty()
        }

        val map: MutableMap<String, Any> = mutableMapOf()
        kClass.getFields().forEach { (name, type) ->

            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex > -1){
                val value = cursor.get(columnIndex, type.getType())
                if (value != null){
                    map[name] = value
                }
            }
        }
        cursor.close()
        return Optional.of(this.fromMap(map))
    }

    override fun<K: Model<T>> K.update(id: String, block: K.() -> Map<Column<T, *>, Any>): Boolean {

        val values = ContentValues()

        val updates = block()
        updates.forEach {(column, value) ->
            values.put(column.name, value)
        }

        Log.e("UPDATE QUERY", "name$name, values=[$values] ${this@SQLModel.id.name} = $id")

        val count = db.writableDatabase.update(name, values, "${this@SQLModel.id.name} = ?", arrayOf(id))
        db.writableDatabase.close()
        Log.e("UPDATE QUERY RESULT", "name$name, values=[$values] ${this@SQLModel.id.name} = $id => [count:${count} == update_count:${updates.size}")

        return count == updates.size
    }

    override fun delete(id: Long): Boolean {
        return delete(listOf(id))
    }

    override fun delete(ids: List<Long>): Boolean {

        val count = db.writableDatabase.delete(name, "${this.id.name} = ?", ids.map { "$it" }.toTypedArray())
        db.writableDatabase.close()

        Log.e("RAPPING", "count => [$count], id => $id")

        return count == ids.size
    }

    override fun add(item: T): Boolean {

        val values = ContentValues()

        for (memberName in item.getMemberNames()){
            item.getMember<Any>(memberName)?.let {
                fieldToColumnMapping[memberName]?.let { columnName ->
                    values.put(columnName, it)
                }
            }
        }

        val rowId = db.writableDatabase.insert(name, null, values)
        db.writableDatabase.close()

        return rowId != (-1).toLong()
    }

    private fun query(q: String): Optional<List<T>> {
        Log.e("DBQUERY", q)
        val cursor = db.readableDatabase.rawQuery(q, null)
        Log.e("DBQUERY MADE!!", q)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return Optional.empty()
        }
        Log.e("CURSOR", "$cursor")
        Log.e("CURSOR", "${cursor.columnCount} ${cursor.count}")

        val items = mutableListOf<T>()

        do {
            val idColumnIndex = cursor.getColumnIndex(id.name)
            val id = cursor.getInt(idColumnIndex)
            val map: MutableMap<String, Any> = mutableMapOf()
            kClass.getFields().forEach { (name, type) ->

                val columnIndex = cursor.getColumnIndex(name)
                if (columnIndex > -1){
                    val value = cursor.get(columnIndex, type.getType())
                    if (value!=null){
                        map[name] = value
                    }
                        Log.e("VALUE CUSROR", "[$value] retrieved for $name")
                }
            }
            map[this.idKey] = id
            Log.e("MAPP", "\n\n${map}\n\n")

            items.add(fromMap(map))
        } while (cursor.moveToNext())
        cursor.close()
        return Optional.of(items)
    }

    override fun<K: Model<T>> K.query(block: K.() -> Query<T>): Optional<List<T>> {

        val q = block()
        val toSelect = q.getFieldsToSelect()?.let { "(${it.joinToString(", ")})" }?: "*"

        return query("Select $toSelect FROM $name $q")
    }

    override fun getAll(): Optional<List<T>> {
        return query("SELECT * FROM $name")
    }
}

fun Cursor.get(columnIndex: Int, type: KClass<*>): Any? {
    return when(type){
        Byte::class -> getInt(columnIndex).toByte()
        ByteArray::class -> getBlob(columnIndex)
        Double::class -> getDouble(columnIndex)
        Int::class -> getInt(columnIndex)
        Long::class -> getLong(columnIndex)
        Boolean::class -> getInt(columnIndex) > 0
        Short::class -> getShort(columnIndex)
        Float::class -> getFloat(columnIndex)
        String::class -> getString(columnIndex)
        else -> null // throw TypeCastException("invalid type for $type")
    }
}

fun ContentValues.put(name: String, value: Any){
    when (value) {
        is Boolean -> put(name, value)
        is Byte -> put(name, value)
        is ByteArray -> put(name, value)
        is Double -> put(name, value)
        is Float -> put(name, value)
        is Int -> put(name, value)
        is Long -> put(name, value)
        is Short -> put(name, value)
        is String -> put(name, value)
        else -> Unit
//        else -> throw Exception("Invalid type ${value::class} for SQL query")
    }
}

