package djvuconsole.simple.gwl.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Word] from a given data source.
 */
interface ItemsRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllItemsStream(): List<Word>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Word?

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Word)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Word)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Word)

    suspend fun getTheme(theme: String): List<Word>

    suspend fun getThemes(): List<String>

    suspend fun getThemesWithLevel(): List<Theme>
}
