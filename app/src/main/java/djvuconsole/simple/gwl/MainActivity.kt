package djvuconsole.simple.gwl

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import djvuconsole.simple.gwl.data.InventoryDatabase
import djvuconsole.simple.gwl.data.Theme
import djvuconsole.simple.gwl.data.Word
import djvuconsole.simple.gwl.ui.WordsViewDBModel
import djvuconsole.simple.gwl.ui.theme.GWLTheme


// State of the List objects inside (!)
class ColorsViewModel: ViewModel() {
    private val _innerColors = ArrayList<Color>().toMutableStateList()

    val colorsInside: List<Color>
        get() = _innerColors

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

    fun addValues(words: List<Word>)
    {
        _innerWords.addAll(words)
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

    fun addValues(words: List<Word>)
    {
        _innerWords.addAll(words)
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

    fun count(): Int {
        return _innerWords.count()
    }

    fun addValues(words: List<Theme>)
    {
        _innerWords.addAll(words)
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

    fun getValues(): List<Theme> {
        return _innerWords
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

@Composable
fun WordUI(
    selectedTheme: String = "Theme 0",
    onSelect: (Word) -> Unit,
    word: WordViewModel = viewModel(),
    words: ThemeWordsSelectedViewModel = viewModel(),
    wellnessViewModel: ColorsViewModel = viewModel(),
    index: Int = 0,
    indexMax: Int = 0)
{
    if (wellnessViewModel.isEmpty())
        wellnessViewModel.setValues(List(words.count()) { Color.LightGray })

    var score by remember {
        mutableIntStateOf(0)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)) {
            Text(text = "Theme: $selectedTheme (${index + 1} / ${indexMax - 1})", textAlign = TextAlign.Center)
            Text(text = "Score: $score\n", textAlign = TextAlign.Center)
            Text(
                text = "${word.getValue().english} (${word.getValue().languageType})\n",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
            Text(
                text = if (word.getValue().russian.isNotEmpty()) "${word.getValue().russian} (${word.getValue().languageType})\n" else "",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )

            words.getValues().forEachIndexed { i, w ->
                WordElement(w, selectedWord = word.getValue(),
                    onSelect = {
                        wellnessViewModel.resetValues()
                        onSelect(w)
                        score += 2
                    },
                    onColor = {
                        wellnessViewModel.setValue(i, it)
                        score -= 1
                    },
                    color = wellnessViewModel.getValue(i)
                )
            }
        }
    }
}


@Composable
fun WordElement(word: Word, selectedWord: Word,
                onSelect: (Word) -> Unit,
                onColor: (Color) -> Unit,
                color: Color = Color.LightGray) {

    var expended by remember {
        mutableStateOf(false)
    }

    val isRight = word == selectedWord
    val extraPadding = if (expended) 48.dp else 0.dp
    val textDescription = if (!expended) word.greek else "${word.greek}\n${word.example}"

    Surface(color=MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row (modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding)
            ) {
                Button(
                    onClick = {
                        onColor(if (isRight) Color.Green else Color.Red)
                        if (isRight)
                            onSelect(word)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = textDescription)
                }

            }
            OutlinedButton(onClick = {
                expended = !expended
            }) {
                Text(text = "?",
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun OnboardingScreenElement(theme: Theme, onSelectTheme: (Theme) -> Unit)
{
    Surface(color=MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row (modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Button(
                    onClick = {
                        onSelectTheme(theme)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${theme.theme} (${theme.level})",
                        color = Color.Unspecified,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(onSelectTheme: (Theme) -> Unit,
                     themesViewModel: ThemesViewModel = viewModel(),
                     applicationContext: Context? = null,
                     databaseModel: WordsViewDBModel? = null)
{
    var loadData by remember { mutableStateOf(false)}

    val themes = databaseModel?.let { applicationContext?.let {
        gettingThemeData(
            databaseModel = databaseModel,
            applicationContext = applicationContext,
            loadData = loadData)
    } }
    themesViewModel.setValues(themes)

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                item {
                    Row (modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()) {
                        Text(
                            text = "Select theme"
                        )
                        Button(onClick = { loadData = true }, modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()) {
                            Text(text = "Load")
                        }
                    }
                }
                themes?.let {
                    items(it) { theme ->
                        OnboardingScreenElement(theme, onSelectTheme = onSelectTheme)
                    }
                }
            }
        }
    }
}

fun getRandomWordsCandidates(themesWords: List<Word>, selectedWord: Word, amount: Int = 3): List<Word>
{
    if (themesWords.isEmpty().or(selectedWord.english == ""))
        return emptyList()

    val inputCopy = buildList { addAll(themesWords) }
    val themesWordsAvailable = inputCopy.filter { it != selectedWord }
    val themesWordsAvailableShuffled = themesWordsAvailable.shuffled()
    val subset = themesWordsAvailableShuffled.subList(
        0,
        if (amount < themesWordsAvailableShuffled.count())
            amount
        else
            themesWordsAvailableShuffled.count() - 1
    )
    val subsetWithWord = subset.plus(selectedWord)
    return subsetWithWord.shuffled()
}

@Composable
fun loadThemesWords(databaseModel: WordsViewDBModel?, selectedTheme: String): List<Word>? {
    return databaseModel?.getThemeWords(selectedTheme)
//    }
}

@Composable
fun MyApp(databaseModel: WordsViewDBModel? = null,
          word: WordViewModel = viewModel(),
          words: ThemeWordsSelectedViewModel = viewModel(),
          themeWords: ThemeWordsViewModel = viewModel(),
          themes: ThemesViewModel = viewModel(),
          applicationContext: Context? = null)
{
    var shouldShowOnboarding by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("") }
    var selectedWordIndex by remember { mutableStateOf(0) }
    var selectedWordMaxIndex by remember { mutableStateOf(0) }

    if (themes.isEmpty())
        themes.setValues(
            databaseModel?.getThemesWithLevels() ?: emptyList()
        )

    if (shouldShowOnboarding) {
        OnboardingScreen(
            onSelectTheme = { theme: Theme ->
                run {
                    shouldShowOnboarding = false
                    selectedTheme = theme.theme

                    themeWords.setValues(
                        databaseModel?.getThemeWords(theme = theme.theme)
                    )

                    selectedWordMaxIndex = themeWords.count()

                    words.setValues(
                        getRandomWordsCandidates(
                            themesWords = themeWords.getValues(),
                            selectedWord = themeWords.getValue(selectedWordIndex)
                        )
                    )
                    word.setValue(themeWords.getValue(selectedWordIndex))

                }
            },
            themesViewModel = themes,
            applicationContext = applicationContext,
            databaseModel = databaseModel)
    } else {
        WordUI(
            selectedTheme = selectedTheme,
            word = word,
            words = words,
            index = selectedWordIndex,
            indexMax = selectedWordMaxIndex,
            onSelect = {
                run {
                    selectedWordIndex += 1
                    if (selectedWordIndex >= themeWords.getValues().count())
                        selectedWordIndex = 0

                    val selectedWord = themeWords.getValue(selectedWordIndex) ?: Word(english = "", greek = "")
                    word.setValue(selectedWord)

                    words.setValues(
                        getRandomWordsCandidates(
                            themesWords = themeWords.getValues(),
                            selectedWord = selectedWord
                        )
                    )
                }
            })
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
    GWLTheme {
        MyApp()
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WordsUIPreview() {
    GWLTheme {
        WordUI(
            selectedTheme = "Test theme",
            words = ThemeWordsSelectedViewModel(),
            onSelect = {}
        )
    }
}

@Composable
fun gettingThemeData(
    databaseModel: WordsViewDBModel,
    applicationContext: Context,
    loadData: Boolean = false
): List<Theme> {
    if (loadData) {
        // FIRST DATA PREPARATION
        val fileText = applicationContext.assets.open("data.csv").bufferedReader().use {
            it.readText()
        }

        // Using the lines() function to split the string
        val lines = fileText.lines()

        for(line in lines.subList(1, lines.count())) {
            // english|greek|lenguageType|gender|example|russian|theme|level|past_form|future_form
            val row = line.split("|")
            if (row.count() < 6) {
                continue
            }

            databaseModel.insertWord(
                Word(
                    english = row[0],
                    greek = row[1],
                    theme = row[6],
                    level = row[7]
                )
            )
        }
    }

    return databaseModel.getThemesWithLevels()
}

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = InventoryDatabase::class.java,
            name = "datamodel.db"
        ).allowMainThreadQueries().build()
    }
    private val databaseModel by viewModels<WordsViewDBModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WordsViewDBModel(db.wordDAO()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GWLTheme {
                // A surface container using the 'background' color from the theme
                MyApp(databaseModel = databaseModel, applicationContext = applicationContext)
            }
        }
    }
}
