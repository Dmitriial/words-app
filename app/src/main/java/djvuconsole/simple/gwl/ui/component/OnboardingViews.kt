package djvuconsole.simple.gwl.ui.component

import android.content.Context
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import djvuconsole.simple.gwl.R
import djvuconsole.simple.gwl.data.Theme
import djvuconsole.simple.gwl.data.Word
import djvuconsole.simple.gwl.ui.WordsViewDBModel
import djvuconsole.simple.gwl.ui.navigation.NavigationDestination


object OnboardingDestination: NavigationDestination {
    override val route = "start"
    override val titleRes = R.string.app_name
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

@Composable
fun OnboardingScreenElement(theme: Theme, onSelectTheme: (Theme) -> Unit)
{
    Surface(color= MaterialTheme.colorScheme.primary,
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
    var loadData by remember { mutableStateOf(false) }

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