package djvuconsole.simple.gwl.data

import kotlinx.coroutines.flow.Flow


class OfflineItemsRepository(private val itemDao: WordDAO) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Word>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<Word?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Word) = itemDao.insert(item)

    override suspend fun deleteItem(item: Word) = itemDao.delete(item)

    override suspend fun updateItem(item: Word) = itemDao.update(item)

    override suspend fun getTheme(theme: String): Flow<List<Word>> = itemDao.getTheme(theme)

    override suspend fun getThemes(): Flow<List<String>> = itemDao.getThemes()
}
