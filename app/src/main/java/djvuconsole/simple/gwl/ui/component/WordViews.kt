package djvuconsole.simple.gwl.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import djvuconsole.simple.gwl.MainTopAppBar
import djvuconsole.simple.gwl.R
import djvuconsole.simple.gwl.data.Word
import djvuconsole.simple.gwl.ui.navigation.NavigationDestination
import djvuconsole.simple.gwl.ui.theme.GWLTheme


object WordsDestination: NavigationDestination {
    override val route = "words"
    override val titleRes = R.string.app_name
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
    val textDescription = if (!expended) word.greek else "${word.greek}\nexample:${word.example}\nenglish: ${word.english}\nrussian: ${word.russian}"

    Surface(color= MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row (modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(
    modifier: Modifier = Modifier,
    selectedTheme: String = "Theme 0",
    onSelect: (Word) -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
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

    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopAppBar(
                title = stringResource(WordsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)) {
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


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WordsUIPreview() {
    GWLTheme {
        WordScreen(
            selectedTheme = "Test theme",
            words = ThemeWordsSelectedViewModel(),
            onSelect = {},
            onNavigateUp = {}
        )
    }
}