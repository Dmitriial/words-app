package djvuconsole.simple.gwl.data


class OfflineItemsRepository(private val itemDao: WordDAO) : ItemsRepository {
    override fun getAllItemsStream(): List<Word> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Word = itemDao.getItem(id)

    override suspend fun insertItem(item: Word) = itemDao.insert(item)

    override suspend fun deleteItem(item: Word) = itemDao.delete(item)

    override suspend fun updateItem(item: Word) = itemDao.update(item)

    override suspend fun getTheme(theme: String): List<Word> = itemDao.getTheme(theme)

    override suspend fun getThemes(): List<String> = itemDao.getThemes()

    override suspend fun getThemesWithLevel(): List<Theme> = itemDao.getThemesWithLevel()
}
