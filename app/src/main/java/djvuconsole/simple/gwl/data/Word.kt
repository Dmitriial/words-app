package djvuconsole.simple.gwl.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


enum class LType
{
    N, // noun | Существительное
    P, // pronoun | Местоимение
    V, // verb | Глагол
    A, // adjective | Прилагательное
    ADV, // adverb | Наречие
    NU, // numerals | Числительные
    UNKNOWN
}


enum class Gender
{
    M, // masculine
    F, // feminine
    N, // neuter
    M_N, // neuter + masculine for GK
    F_N, // neuter + feminine for GK
    N_N, // neuter + neuter for GK
    UNKNOWN
}

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val theme: String = "",
    val greek: String,
    val english: String,
    val languageType: LType = LType.UNKNOWN,

    val gender: Gender = Gender.UNKNOWN,

    val russian: String = "",
    val example: String = "",

    val level: String = "",

    val futureForm: String = "",
    val pastFrom: String = ""
)


@Dao
interface WordDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Word)

    @Update
    suspend fun update(item: Word)

    @Delete
    suspend fun delete(item: Word)

    @Query("SELECT * from words WHERE id = :id")
    fun getItem(id: Int): Flow<Word>

    @Query("SELECT * from words ORDER BY greek ASC")
    fun getAllItems(): Flow<List<Word>>

    @Query("SELECT * from words WHERE theme = :theme")
    fun getTheme(theme: String): Flow<List<Word>>

    @Query("SELECT DISTINCT theme from words")
    fun getThemes(): Flow<List<String>>
}

