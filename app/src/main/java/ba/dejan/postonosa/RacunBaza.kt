package ba.dejan.postonosa

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase


@Entity(tableName = "racuni_table")
data class Racun(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipUsluge: String,
    val iznos: Double,
    val provizija: Double,
    val vrijeme: Long = System.currentTimeMillis()
)
@Dao
interface RacunDao {
    @Insert
    fun dodajRacun(racun: Racun)
    @Query("SELECT * FROM racuni_table WHERE vrijeme >= :vrijemePocetka ORDER BY vrijeme DESC")
    fun dohvatiRacuneKorisnika(vrijemePocetka: Long): List<Racun>
    @Query("SELECT SUM(iznos + provizija) FROM racuni_table WHERE vrijeme >= :vrijemePocetka")
    fun ukupnoZaKorisnika(vrijemePocetka: Long): Double?
    @Query("SELECT * FROM racuni_table ORDER BY vrijeme DESC")
    fun dohvatiSve(): List<Racun>
    @Query("SELECT SUM(iznos + provizija) FROM racuni_table")
    fun ukupnoZaRazduzenje(): Double?
    @Query("DELETE FROM racuni_table")
    fun obrisiSve()
    @Delete
    fun obrisiPojedinacni(racun: Racun)
}
@Database(entities = [Racun::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun racunDao(): RacunDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "postonosa_db"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}