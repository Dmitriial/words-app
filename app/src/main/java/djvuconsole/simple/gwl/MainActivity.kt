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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import djvuconsole.simple.gwl.data.Word
import djvuconsole.simple.gwl.ui.WordsViewDBModel
import djvuconsole.simple.gwl.ui.theme.GWLTheme
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


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
    private val _innerWords = ArrayList<Word>().toMutableStateList()

    fun count(): Int {
        return _innerWords.count()
    }

    fun addValues(words: List<Word>)
    {
        _innerWords.addAll(words)
    }

    fun setValues(words: List<Word>)
    {
        _innerWords.clear()
        _innerWords.addAll(words)
    }

    fun getValue(index: Int): Word {
        if (_innerWords.count() <= index)
            return Word(greek = "", english = "")

        return _innerWords[index]
    }

    fun getValues(): List<Word> {
        return _innerWords.toMutableStateList()
    }
}


class ThemesViewModel: ViewModel() {
    private val _innerWords = ArrayList<String>().toMutableStateList()

    fun count(): Int {
        return _innerWords.count()
    }

    fun addValues(words: List<String>)
    {
        _innerWords.addAll(words)
    }

    fun setValues(words: List<String>)
    {
        _innerWords.clear()
        _innerWords.addAll(words)
    }

    fun getValue(index: Int): String {
        if (_innerWords.count() <= index)
            return ""

        return _innerWords[index]
    }

    fun getValues(): List<String> {
        return _innerWords.toMutableStateList()
    }
}


@Composable
fun WordUI(theme: String = "Theme 0", word: Word,
           onSelect: (Word) -> Unit,
           words: ThemeWordsViewModel = viewModel(),
           wellnessViewModel: ColorsViewModel = viewModel())
{
    if (wellnessViewModel.isEmpty())
        wellnessViewModel.setValues(List(words.count()) { Color.LightGray })

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)) {
            Text(text = "Theme: $theme\n", textAlign = TextAlign.Center)
            Text(
                text = "${word.english} (${word.languageType})\n",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
            Text(
                text = if (word.russian.isNotEmpty()) "${word.russian} (${word.languageType})\n" else "",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )

            words.getValues().forEachIndexed { i, w ->
                WordElement(w, selectedWord = word,
                    onSelect = {
                        wellnessViewModel.resetValues()
                        onSelect(it)
                    },
                    onColor = {
                        wellnessViewModel.setValue(i, it)
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
fun OnboardingScreenElement(theme: String, onSelectTheme: (String) -> Unit)
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
                        text = theme,
                        color = Color.Unspecified,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(onSelectTheme: (String) -> Unit,
                     themes: List<String>)
{
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                item {
                    Text(
                        text = "Select the theme for training"
                    )
                }
                items(themes) {theme ->
                    OnboardingScreenElement(theme, onSelectTheme = onSelectTheme)
                }
            }
        }
    }
}

fun getRandomWordsCandidates(themesWords: List<Word>, selectedWord: Word, amount: Int = 3): List<Word>
{
    val themesWordsAvailable = themesWords.filter { it != selectedWord }
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

//fun loadAllWords(): Map<String, List<Word>> {
//    val themes = listOf("HELLO", "Something else")
//    val inputStream = ClassLoader::class.java.classLoader?.getResourceAsStream("assets/data.csv")
//    val inputString = inputStream?.bufferedReader().use{it?.readText()}
//
//    // todo: loads themes with words
//    val wordsAll = listOf(
//        Word(
//            greek = "δουλειά",
//            english = "job",
//            gender = Gender.F,
//            languageType = LType.N
//        ),
//        Word(
//            greek = "καλλιτέχνης",
//            english = "artist",
//            gender = Gender.M,
//            languageType = LType.N
//        ),
//        Word(
//            greek = "γιατρός",
//            english = "doctor",
//            gender = Gender.M,
//            languageType = LType.N
//        ),
//        Word(
//            greek = "οδηγός",
//            english = "driver",
//            gender = Gender.M,
//            languageType = LType.N
//        ),
//        Word(
//            greek = "μηχανικός",
//            english = "engineer",
//            gender = Gender.M,
//            languageType = LType.N
//        ),
//        Word(
//            greek = "εξερευνητής",
//            english = "explorer",
//            gender = Gender.M,
//            languageType = LType.N
//        )
//    )
//    return mapOf(themes[0] to wordsAll)
//}

@Composable
fun MyApp(databaseModel: WordsViewDBModel? = null,
          themeWords: ThemeWordsViewModel = viewModel(),
          themeWordsSelected: ThemeWordsViewModel = viewModel(),
          themesViewModel: ThemesViewModel = viewModel(),
          applicationContext: Context? = null)
{
    val coroutineScope = rememberCoroutineScope()
    val themes: MutableList<String> = mutableListOf()

    databaseModel?.let { applicationContext?.let {
        gettingThemeData(
            databaseModel = databaseModel,
            themeModel = themesViewModel,
            applicationContext = applicationContext)
    } }

    // selects the theme status (!)
    var shouldShowOnboarding by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf(themes[0]) }

    var selectedWordIndex by remember { mutableIntStateOf(0) }
    var selectedWord by remember { mutableStateOf(Word(greek = "", english = "")) }

    if (shouldShowOnboarding) {
        OnboardingScreen(
            onSelectTheme = { theme: String ->
                run {
                    shouldShowOnboarding = false
                    selectedTheme = theme

                    coroutineScope.launch {
                        val words = databaseModel?.getThemeWords(selectedTheme)
                        words?.onEach {
                            themeWords.setValues(it)
                        }
                    }

                    selectedWord = themeWords.getValue(0)
                    themeWordsSelected.setValues(
                        getRandomWordsCandidates(
                            themesWords = themeWords.getValues(),
                            selectedWord = selectedWord
                        )
                    )
                }
            },
            themes=themes)
    } else {
        WordUI(
            theme = selectedTheme,
            word = selectedWord,
            words = themeWordsSelected,
            onSelect = {
                run {
                    selectedWordIndex += 1
                    if (selectedWordIndex >= selectedTheme.length)
                        selectedWordIndex = 0

                    selectedWord = themeWords.getValue(selectedWordIndex)

                    themeWordsSelected.setValues(
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
            theme = "Test theme",
            word = Word(
                greek = "test greek",
                english = "test english",
            ),
            words = ThemeWordsViewModel(),
            onSelect = {}
        )
    }
}

@Composable
fun gettingThemeData(
    databaseModel: WordsViewDBModel,
    themeModel: ThemesViewModel,
    applicationContext: Context
): List<String> {
    var themesFlow = databaseModel.getThemes().collectAsState(initial = emptyList())
    val allWords = databaseModel.getAllWords().collectAsState(initial = emptyList())

    if (themesFlow.value.isNotEmpty().and(allWords.value.isNotEmpty())) {
        return themesFlow.value
    }

    if (themesFlow.value.isEmpty()) {
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

            // todo: processed more fields
            databaseModel.insertWord(
                Word(
                    english = row[0],
                    greek = row[1],
                    theme = row[6],
                    level = row[7]
                )
            )
        }
        themesFlow = databaseModel.getThemes().collectAsState(initial = emptyList())
    }
    themeModel.setValues(themesFlow.value)
    val allWordsAfter = databaseModel.getAllWords().collectAsState(initial = emptyList())

    return databaseModel.getThemes().collectAsState(initial = emptyList()).value
}

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = InventoryDatabase::class.java,
            name = "datamodel.db"
        ).build()
    }
    private val wordsModel by viewModels<WordsViewDBModel>(
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

//        val themesList = mutableListOf<String>()
//
//        runBlocking {
//            val themesFlow = wordsModel.getThemes().collectAsState(initial = emptyList())
//
//            // fill data from Flow object
//            themesFlow.onEach {
//                themesList.addAll(
//                    it.toMutableStateList()
//                )
//            }
//
//            if (themesList.isEmpty()) {
//                // FIRST DATA PREPARATION
//                val fileText = applicationContext.assets.open("data.csv").bufferedReader().use {
//                    it.readText()
//                }
//
//                // Using the lines() function to split the string
//                val lines = fileText.lines()
//                val wordsSrc = LinkedList<Word>()
//
//                for(line in lines.subList(1, lines.count())) {
//                    // english|greek|lenguageType|gender|example|russian|theme|level|past_form|future_form
//                    val row = line.split("|")
//                    if (row.count() < 6) {
//                        continue
//                    }
//
//                    // todo: processed more fields
//                    wordsModel.insertWord(
//                        Word(
//                            english = row[0],
//                            greek = row[1],
//                            theme = row[6],
//                            level = row[7]
//                        )
//                    )
//                }
//            }
//
//            val flow = wordsModel.getThemes()
//            flow.onEach {
//                themesList.addAll(it)
//            }.collect()
//        }

        setContent {
            GWLTheme {
                // A surface container using the 'background' color from the theme
                MyApp(databaseModel = wordsModel, applicationContext = applicationContext)
            }
        }
    }
}
