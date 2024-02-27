package djvuconsole.simple.gwl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import djvuconsole.simple.gwl.data.Word
import djvuconsole.simple.gwl.data.WordDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WordsViewDBModel(
    private val dao: WordDAO
): ViewModel() {
    fun insertWord(word: Word)
    {
        viewModelScope.launch {
            dao.insert(word)
        }
    }

    fun getAllWords(): Flow<List<Word>> {
        return dao.getAllItems()
    }

    fun getThemes(): Flow<List<String>> {
        return dao.getThemes()
    }

    fun getThemeWords(theme: String): Flow<List<Word>> {
        return dao.getTheme(theme)
    }
}