package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dicegame.ui.AboutDialog
import com.example.dicegame.ui.StartScreen
import com.example.dicegame.ui.theme.DiceGameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceGameTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "StartScreen"){
                    composable("StartScreen") { StartScreen(navController) }
                    composable("MainGame") { ComDices(navController) }
                    composable("ShowDialog") { AboutDialog(showDialog = true, onDismiss = { true }) }
                }
                }
            }
        }
    }



@Composable
fun ComDices(navController: NavController,modifier: Modifier = Modifier) {
    var bottomResults = remember{ mutableStateListOf(1, 1, 1, 1, 1) } // User-controlled dice
    var topResults = remember { mutableStateListOf(1, 1, 1, 1, 1) } // Computer-controlled dice
    var humanScore by rememberSaveable { mutableStateOf(0) }
    var computerScore by rememberSaveable { mutableStateOf(0) }
    var humanTotal by rememberSaveable { mutableStateOf(0) }
    var computerTotal by rememberSaveable { mutableStateOf(0) }
    var turnText by rememberSaveable { mutableStateOf("Your Turn! Roll the dice!") }
    var rollCount by rememberSaveable { mutableStateOf(0) }
    var selectedDice = remember { mutableStateListOf(false, false, false, false, false) } // Track selected dice for user
    var humanWins by rememberSaveable { mutableStateOf(0) }
    var computerWins by rememberSaveable { mutableStateOf(0) }
    var winnerText by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var buttonText by rememberSaveable { mutableStateOf("Throw") }
    var targetScore by rememberSaveable { mutableStateOf(101) }
    var isGameStarted by rememberSaveable{ mutableStateOf(false) }
    var isComputerRolling by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()



    //when the game start displaying the set target field
    if (!isGameStarted){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Set target Score", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = targetScore.toString(),
                onValueChange = {newValue ->
                    targetScore = newValue.toIntOrNull() ?: 101
                },
                label = {Text(text = "Target Score")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    humanScore = 0
                    humanTotal = 0
                    computerScore =0
                    computerTotal = 0
                    rollCount = 0
                    bottomResults.replaceAll { 1 }
                    topResults.replaceAll { 1 }
                    selectedDice.clear()
                    selectedDice.addAll(List(5) { false })
                    turnText = "New Game! Your turn to Roll!"
                    isGameStarted = true }) {
                Text(text = "Start Game")

            }
        }
    } else {


        fun resetGame() {
            humanScore = 0
            humanTotal = 0
            computerScore = 0
            computerTotal = 0
            rollCount = 0
            bottomResults.replaceAll { 1 }
            topResults.replaceAll { 1 }
            selectedDice.clear()
            selectedDice.addAll(List(5) { false })
            turnText = "New Game! Your turn to Roll!"
            winnerText = ""
            isGameStarted = false


        }

        fun checkWinner() {
            if (humanTotal >= targetScore && computerTotal >= targetScore) {
                // Tiebreaker round (no rerolls allowed)
                turnText = "Tiebreaker Round! No Rerolls Allowed!"
                rollCount = 3  // Disable rerolls for both players
                return
            }

            if (humanTotal >= targetScore) {
                if (computerTotal < targetScore) {
                        humanWins++
                        winnerText = "You Win!"
                        showDialog = true

                    coroutineScope.launch {
                        delay(3000) // Allow the computer to finish rolling before UI updates
                    }
                    }
            } else if (computerTotal >= targetScore) {
                    if (humanTotal < targetScore) {
                        computerWins++
                        winnerText = "You Lose!"
                        showDialog = true

                        coroutineScope.launch {
                            delay(2000)
                    }
                }
            }
        }

        // Alert Dialog for Game Over
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false},
                title = { Text(
                    text = winnerText,
                    color = if (winnerText == "You Win!") Color.Green
                        else Color.Red)
                        },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        resetGame()
                    }) {
                        Text("Restart Game")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        navController.navigate("StartScreen")
                        // Handle navigation to main menu or exit
                    }) {
                        Text("Back to Main Menu")
                    }
                }
            )
        }

        fun scoreTurn() {
            if (showDialog) return // Stop rolling if the game is over

            isComputerRolling = true

            rollCount = if (turnText.contains("Tiebreaker")) 3 else 0 // Disable rerolls in sudden death
            humanScore = bottomResults.sum()
            humanTotal += humanScore

            turnText = "You scored! Computer's turn..."

            bottomResults.replaceAll { 1 }
            selectedDice.replaceAll { false } // Reset selection
            checkWinner()

            if (showDialog) {
                isComputerRolling = false

                return
            }// Stop computer's turn if game ended


            // Computer's turn logic
            coroutineScope.launch {
                delay(1500) // Wait before computer starts its turn

                rollCount = if (turnText.contains("Tiebreaker")) 3 else 0 // Disable rerolls in sudden death
                topResults.replaceAll { (1..6).random() } // Initial roll

                var rerolls = 0
                val maxRerolls = (0..2).random()

                while (rerolls < maxRerolls && !showDialog) { // Stop rerolling if game is over or in tiebreaker
                    delay(1000)
                    for (i in topResults.indices) {
                        if ((0..1).random() == 1) continue
//                        delay(500)
                        topResults[i] = (1..6).random()
                    }
                    rerolls++
                }

                computerScore = topResults.sum()
                computerTotal += computerScore
                checkWinner()
                isComputerRolling = false

                turnText = "Computer scored! Your turn to roll..."
            }

        }

//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = winnerText,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Red
//            )
//        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Games Won - You: $humanWins | Computer: $computerWins",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp, top = 5.dp)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Turn Message
            Text(
                text = turnText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Top row (Computer's dice)
            DiceRow(topResults, selectedDice) {
            }

            // Display Scores
            Text(
                text = "Your Score: $humanTotal | Computer's Score: $computerTotal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)
            )

            // Display Roll Count
            Text(text = "Rolls: $rollCount/3", fontSize = 16.sp, fontWeight = FontWeight.Medium)

            // Bottom row (User's dice) with selection feature
            Row(horizontalArrangement = Arrangement.Center) {
                bottomResults.forEachIndexed { index, result ->
                    DiceImage(
                        result = result,
                        isSelected = selectedDice[index],
                        onClick = { selectedDice[index] = !selectedDice[index] },

                        )
                }
            }

            // Buttons Row
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(10.dp)) {
                Button(
                    onClick = {
                        if (showDialog || rollCount == 3) return@Button // Prevent rolling after game over or in tiebreaker

                        if (rollCount < 3) { // Allow max 3 rerolls
                            rollCount++
                            if (rollCount == 1) {
                                bottomResults.replaceAll { (1..6).random() }
                                buttonText = "Reroll"
                            } else {
                                bottomResults.forEachIndexed { index, _ ->
                                    if (!selectedDice[index]) {
                                        bottomResults[index] = (1..6).random()
                                    }
                                }
                            }
                        }

                    },
                    enabled = (rollCount < 3 && !showDialog) && !isComputerRolling, // Disable reroll button when game is over or in tiebreaker
                    modifier = Modifier.padding(end = 10.dp),
                ) {
                    Text(text = buttonText)
                }

                Button(
                    onClick = {
                        scoreTurn() // End turn and score
                        buttonText = "Throw"
                    },
                    enabled = !isComputerRolling,
                    modifier = Modifier.padding(start = 10.dp)

                ) {
                    Text(text = "Score")
                }

            }

        }
    }
}



// Reusable DiceRow Composable
@Composable
fun DiceRow(diceResults: List<Int>, selectedDice: List<Boolean>, onDiceClick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center) {
        diceResults.forEachIndexed { index, result ->
            DiceImage(result, selectedDice[index]) { onDiceClick(index) }
        }
    }
}

// Reusable DiceImage Composable
@Composable
fun DiceImage(result: Int, isSelected: Boolean, onClick: () -> Unit) {
    Image(
        painter = painterResource(
            when (result) {
                1 -> R.drawable.dice1
                2 -> R.drawable.dice2
                3 -> R.drawable.dice3
                4 -> R.drawable.dice4
                5 -> R.drawable.dice5
                else -> R.drawable.dice6
            }
        ),
        contentDescription = null,
        modifier = Modifier
            .size(70.dp)
            .padding(4.dp)
            .clickable { onClick() }
            .border(2.dp, if (isSelected) Color.Red else Color.Transparent)
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DiceGameTheme {
        ComDices(rememberNavController())
    }
}