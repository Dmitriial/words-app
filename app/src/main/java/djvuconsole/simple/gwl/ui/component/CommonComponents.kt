package djvuconsole.simple.gwl.ui.component

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import djvuconsole.simple.gwl.data.Theme
import djvuconsole.simple.gwl.data.Word


// State of the List objects inside (!)
class ColorsViewModel: ViewModel() {
    private val _innerColors = ArrayList<Color>().toMutableStateList()

    fun setValue(index: Int, value: Color) {
        if (_innerColors.count() <= index)
            return

        _innerColors[index] = value
    }

    fun resetValues()
    {
        _innerColors.replaceAll {
            Color.LightGray
        }
    }

    fun isEmpty(): Boolean {
        return _innerColors.isEmpty()
    }

    fun setValues(colors: List<Color>)
    {
        _innerColors.addAll(colors)
    }

    fun getValue(index: Int): Color {
        if (_innerColors.count() <= index)
            return Color.LightGray

        return _innerColors[index]
    }
}

class ThemeWordsViewModel: ViewModel() {
    private val _innerWords = ArrayList<Word>()

    fun count(): Int {
        return _innerWords.count()
    }

    fun setValues(words: List<Word>?)
    {
        _innerWords.clear()
        if (words != null) {
            _innerWords.addAll(words)
            _innerWords.shuffle()
        }
    }

    fun getValue(index: Int): Word {
        if (_innerWords.count() <= index)
            return Word(greek = "", english = "")

        return _innerWords[index]
    }

    fun getValues(): List<Word> {
        return buildList { addAll(_innerWords) }
    }
}


class ThemeWordsSelectedViewModel: ViewModel() {
    private val _innerWords = ArrayList<Word>()

    fun count(): Int {
        return _innerWords.count()
    }

    fun setValues(words: List<Word>?)
    {
        _innerWords.clear()
        if (words != null)
            _innerWords.addAll(words)
    }

    fun getValue(index: Int): Word {
        if (_innerWords.count() <= index)
            return Word(greek = "", english = "")

        return _innerWords[index]
    }

    fun getValues(): List<Word> {
        return buildList { addAll(_innerWords) }
    }
}

class ThemesViewModel: ViewModel() {
    private val _innerWords = ArrayList<Theme>()

    fun isEmpty(): Boolean {
        return _innerWords.isEmpty()
    }

    fun setValues(words: List<Theme>?)
    {
        _innerWords.clear()

        if (words != null) {
            _innerWords.addAll(words)
        }
    }

    fun getValue(index: Int): Theme {
        if (_innerWords.count() <= index)
            return Theme()

        return _innerWords[index]
    }
}


class WordViewModel: ViewModel() {
    private var _innerWord = Word(english = "", greek = "")

    fun getValue(): Word {
        return _innerWord
    }

    fun setValue(word: Word) {
        _innerWord = word
    }
}
