package djvuconsole.simple.gwl

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import djvuconsole.simple.gwl.data.Theme
import djvuconsole.simple.gwl.data.Word
import djvuconsole.simple.gwl.ui.WordsViewDBModel
import djvuconsole.simple.gwl.ui.component.OnboardingDestination
import djvuconsole.simple.gwl.ui.component.OnboardingScreen
import djvuconsole.simple.gwl.ui.component.ThemeWordsSelectedViewModel
import djvuconsole.simple.gwl.ui.component.ThemeWordsViewModel
import djvuconsole.simple.gwl.ui.component.ThemesViewModel
import djvuconsole.simple.gwl.ui.component.WordScreen
import djvuconsole.simple.gwl.ui.component.WordViewModel
import djvuconsole.simple.gwl.ui.component.WordsDestination
import djvuconsole.simple.gwl.ui.component.getRandomWordsCandidates


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        })
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    databaseModel: WordsViewDBModel? = null,
    applicationContext: Context? = null,
    word: WordViewModel = viewModel(),
    words: ThemeWordsSelectedViewModel = viewModel(),
    themeWords: ThemeWordsViewModel = viewModel(),
    themes: ThemesViewModel = viewModel()
) {

    var shouldShowOnboarding by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("") }
    var selectedWordIndex by remember { mutableIntStateOf(0) }
    var selectedWordMaxIndex by remember { mutableIntStateOf(0) }

    if (themes.isEmpty())
        themes.setValues(
            databaseModel?.getThemesWithLevels() ?: emptyList()
        )

    NavHost(
        navController = navController, startDestination = OnboardingDestination.route, modifier = modifier
    ) {
        composable(route = OnboardingDestination.route) {
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
                        // navigate to the next section
                        navController.navigate(WordsDestination.route)
                    }
                },
                themesViewModel = themes,
                applicationContext = applicationContext,
                databaseModel = databaseModel
            )
        }
        composable(route = WordsDestination.route) {
            WordScreen(
                selectedTheme = selectedTheme,
                word = word,
                words = words,
                index = selectedWordIndex,
                indexMax = selectedWordMaxIndex,
                onNavigateUp = { navController.navigateUp() },
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
                }
            )
        }
    }
}

@Composable
fun MainApp(navController: NavHostController = rememberNavController(),
            databaseModel: WordsViewDBModel? = null,
            applicationContext: Context? = null ) {
    MainNavHost(
        navController = navController,
        databaseModel = databaseModel,
        applicationContext = applicationContext
    )
}