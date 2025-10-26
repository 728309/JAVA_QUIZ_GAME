Java Fundamentals Final Project

JavaFX quiz application that demonstrates core Java OOP 
(abstraction, encapsulation, polymorphism), collections, JSON I/O, scene navigation, theming, and robust error handling.

NOTE-1: You have to log in, for you to play the quiz. Login info you can get in the user.json file

NOTE-2: At some points you have to stretch the windows to see in which question you are and how many points you have.
        pictures you can see in the sizing.pdf

NOTE-3: To run the game simply go to App and click run.

## Features
- **Login** with simple user store (`users.json`)
- **Load quizzes from JSON** (SurveyJS-style) via file chooser
- **Multiple-choice & boolean** questions in one unified model
- **Per-question timer** + neon segmented timer bar
- **Scoring & points** (time-based bonus), **results history** saved to JSON
- **Theme switcher** (Cyan/Pink TRON accents)
- Clear **alerts**, input validation, and error handling



## Tech Stack
- **Java 21**, **JavaFX 21** (FXML, controllers, CSS)
- **org.json** for JSON parsing
- **Maven** build
- **No external DB**; JSON files for persistence



## Project Structure 
```
src/main/java/com/example/javafx_project/

 App.java
 Launcher.java

  controllers/
    MenuController.java
    LoginController.java
    GameController.java
    ResultController.java
    
  helpers/
    GameManager.java       // Singleton session state (quiz, player, progress)
    Navigator.java         // FXML navigation with theme carry-over
    Theme.java             // Accent color setter
    MsgHelper.java               // Alert helper (non-blocking-friendly usage)
    PathHelper.java             // Central FXML/CSS paths

  model/
    Question.java          // interface
    QuestionType.java      // enum: MULTIPLE, BOOLEAN
    CombiQuestion.java     // single class for multiple-choice & boolean
    Quiz.java
    Player.java
    Result.java

  service/
    GameLoader.java        // JSON -> Quiz & Questions (robust parsing)
    ResultService.java     // save/read results to ~/.quiz-results/<quiz>-results.json
    UserService.java       // read users.json from resources
    TimeService.java       // countdown timer wrapper

resources/com.example.javafx_project/
  Menu.fxml
  Login.fxml
  Game.fxml
  Results.fxml

  .../css/
      css/fashion.css

  .../data/
      data/users.json
```


## JSON Quiz 

- TRON-Question
- quiz-surveyjs-id
- users (login info)


## Running
```bash
# From project root
mvn clean javafx:run
```


## Design Overview (OOP)
- **Abstraction & Polymorphism:** `Question` interface with a unified `CombiQuestion` implementation that can represent both MULTIPLE and BOOLEAN types. The UI renders based on `getType()` & `getChoices()`.

- **Encapsulation:** fields are private + getters; validation is enforced in constructors/factories.

- **Separation of concerns:** Models (data), Services (I/O, timer), Helpers (state, nav, theming), Controllers (UI).

- **Singleton (careful use):** `GameManager` centralizes the current `Quiz`, `Player`, index, score, and points.


## Robustness
- **GameLoader:** validates JSON structure (`pages`, `elements`, `choices`, `correctAnswer`) and throws meaningful errors if malformed.

- **ResultService:** ensures the save directory exists and tolerates older files (points fallback).

- **Controller guards:** disable Start until login + quiz loaded; catch IO/JSON errors and show alerts.


## UI/UX
- **FXML** views + **CSS** theme (`fashion.css`) for a TRON  like look.

- **Segmented timer** (`.seg.on`) to visually count down.

- **Theme switcher** in the menu toggles accent color (cyan/pink).

## Rubric Mapping (summary)
- **Conventions:** naming & structure are consistent (fixed `QuestionType.MULTIPLE` typo).

- **Concise code:** controllers have short, readable methods; utilities isolated.

- **OOP:** interface + polymorphic model; encapsulation via private fields; singleton used deliberately.

- **Robustness:** validation, try/catch, and safe UI interactions (no blocking dialogs during animation).

- **Formatting/Language:** clean, professional code; no profanity.

## Teacher Note
This project was developed by the student with guidance from AI (ChatGPT) 
for debugging, design feedback, and documentation. All implementation choices
and final code were authored and verified by the student to demonstrate understanding of
Java fundamentals.
