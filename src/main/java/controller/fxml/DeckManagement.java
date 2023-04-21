package controller.fxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controller.command.IButtonCommand;
import controller.command.MacroCommand;
import controller.command.scene.ChangeSceneCommand;
import controller.command.sound.PlayClipCommand;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import model.Deck;
import model.deckmanagement.RightCell;
import utilities.parser.CardParser;
import utilities.parser.DeckParser;
import model.deckmanagement.DeckCard;
import model.deckmanagement.DeckCell;
import model.deckmanagement.CenterCell;

public class DeckManagement {

    @FXML private Button backButton;
    @FXML private ListView<DeckCard> rightList, centerList;
    @FXML private ListView<Deck> leftList;
    @FXML private TextField deckNameTextField;
    
    @FXML
    void saveDeck(final ActionEvent event) throws IOException{
        PlayClipCommand playSound = new PlayClipCommand();
        Deck deck = new Deck();
        List<Deck> deckList, jsonDecks = new ArrayList<>();
        
        deck.setName(deckNameTextField.getText());
        jsonDecks = DeckParser.parseDecks();
        /* Check in the json if a deck with the same name already exist
         * in which case raise an alert 
         */
        for (Deck existingDeck : jsonDecks) {
            if (existingDeck.getName().equals(deck.getName())) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.initOwner(backButton.getScene().getWindow());
                alert.setTitle("Error");
                alert.setHeaderText("Deck already exists");
                alert.setContentText("A deck with the name '" + deck.getName() + "' already exists. Please choose a different name.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.show();
                return;
            }
        }
        /* Add chosen cards to the deck, the deck to his list
         * and to the json file
         */
        if (!centerList.getItems().isEmpty()){
            for (DeckCard card : centerList.getItems()){
                deck.addCard(card);
            }
            leftList.getItems().add(deck);
            deckList = leftList.getItems();
            DeckParser.writeDecks(deckList);
        }
        playSound.execute();
    }

    public void initialize() {
        List<DeckCard> cards = CardParser.parseCards();
        List<Deck> decks = DeckParser.parseDecks();
        ToggleGroup group = new ToggleGroup();
        List<IButtonCommand> backCommands = new ArrayList<>();

        

        // Setting up the custom 'ListCell' for the three listView
        rightList.setCellFactory(param -> new RightCell("Add", centerList));
        centerList.setCellFactory(param -> new CenterCell("Remove", centerList));
        leftList.setCellFactory(param -> new DeckCell(leftList, group));
        
        /* Add the data retrieved from json files to the appropriate listView,
         * if the files are empty an error is raised, which is why the if statement
         */
        if (cards != null) {
            rightList.getItems().addAll(cards);  
        } 
        if (decks != null) {
            leftList.getItems().addAll(decks);
        } 

        // Commands for the back-to-mainmenu button
        backCommands.add(new ChangeSceneCommand("MainMenu.fxml"));
        backCommands.add(new PlayClipCommand());
        MacroCommand backMacro = new MacroCommand(backCommands);
        backButton.setOnAction(event -> {
            backMacro.execute();
        });

    }  
}