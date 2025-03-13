package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibernateControllers.GenericHibernate;
import coursework.model.*;
import coursework.model.enums.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class ProductWindow {

    @FXML
    public ComboBox<Format> formatComboBox;
    @FXML
    public ComboBox<Demographic> demographicComboBox;
    @FXML
    public ComboBox<Genre> genreComboBox;
    @FXML
    public ComboBox<Language> languageComboBox;
    @FXML
    public ComboBox<PublicationStatus> publicationStatusComboBox;
    @FXML
    public TextField titleField;
    @FXML
    public TextField authorField;
    @FXML
    public ListView<Book> productListField;
    public CheckBox isColor;
    public ComboBox<Language> languageComboBoxManga;
    public TextField illustratorField;
    public Spinner<Integer> volumeNumber;
    public ListView<Manga> mangaList;
    public TextField mangaTitleField;
    public TextField mangaAuthorField;
    public TextField originalLanguageText;
    public ComboBox<Demographic> demographicComboBoxManga;
    public ListView<Book> bookList;
    public Button updateBookButton;
    public Button deleteBookButton;
    public Button deleteMangaButton;
    public Button updateMangaButton;
    public Button updateMangaClient;
    public Button updateButtonClient;
    public Tab bookTab;
    public Tab mangaTab;
    private User currentUser;
    private Publication currentPublication;
    private Main mainController;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    @FXML
    public void initialize() {
        setData();
        if (currentPublication != null) {
            setPublication(currentPublication);
        }
    }

    public void setMainController(Main mainController) {
        this.mainController = mainController;
    }

    private void enableVisibility() {
        if (currentUser instanceof Client) {
            bookList.setVisible(false);
            mangaList.setVisible(false);
            deleteBookButton.setVisible(false);
            deleteMangaButton.setVisible(false);
            updateBookButton.setVisible(false);
            updateMangaButton.setVisible(false);
        }
        else if (currentUser instanceof Admin)
        {
            updateMangaClient.setVisible(false);
            updateButtonClient.setVisible(false);
        }
    }

    public void setData() {
        genreComboBox.getItems().setAll(Genre.values());
        demographicComboBox.getItems().setAll(Demographic.values());
        formatComboBox.getItems().setAll(Format.values());
        languageComboBox.getItems().setAll(Language.values());
        publicationStatusComboBox.getItems().setAll(PublicationStatus.values());
        volumeNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5000, 1));
        demographicComboBoxManga.getItems().setAll(Demographic.values());
        fillProductList();
        fillMangaList();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        enableVisibility();
    }

    public void setPublication(Publication publication) {
        this.currentPublication = publication;
        if (publication instanceof Book) {
            Book book = (Book) publication;
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            genreComboBox.setValue(book.getGenre());
            languageComboBox.setValue(book.getLanguage());
            demographicComboBox.setValue(book.getDemographic());
            formatComboBox.setValue(book.getFormat());
            publicationStatusComboBox.setValue(book.getPublicationStatus());
            bookTab.setDisable(false);
            mangaTab.setDisable(true);
            // Select the book tab
            bookTab.getTabPane().getSelectionModel().select(bookTab);
        } else if (publication instanceof Manga) {
            Manga manga = (Manga) publication;
            mangaTitleField.setText(manga.getTitle());
            mangaAuthorField.setText(manga.getAuthor());
            illustratorField.setText(manga.getIllustrator());
            originalLanguageText.setText(manga.getOriginalLanguage());
            volumeNumber.getValueFactory().setValue(manga.getVolumeNumber());
            demographicComboBoxManga.setValue(manga.getDemographic());
            isColor.setSelected(manga.isColor());
            bookTab.setDisable(true);
            mangaTab.setDisable(false);
            // Select the manga tab
            mangaTab.getTabPane().getSelectionModel().select(mangaTab);
        }
    }

    public void createBook() {
        try {
            Book book = new Book(
                    titleField.getText(),
                    authorField.getText(),
                    genreComboBox.getValue(),
                    languageComboBox.getValue(),
                    demographicComboBox.getValue(),
                    formatComboBox.getValue(),
                    publicationStatusComboBox.getValue()
            );
            book.setOwner((Client) currentUser);
            book.setPublicationStatus(PublicationStatus.AVAILABLE);

            hibernate.create(book);
            fillProductList();  // Refresh the list to include the new book
            showAlert("Book created successfully!");
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

            // Switch to the "My Books" tab in the Main controller
            FXMLLoader loader = new FXMLLoader(StartGUI.class.getResource("main.fxml"));
            Parent root = loader.load();
            Main mainController = loader.getController();
            mainController.switchToMyBooksTab();
        } catch (Exception e) {
            showAlert("Error creating book: " + e.getMessage());
        }
    }

    public void deleteBook() {
        Book selectedBook = productListField.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Please select a book to delete.");
            return;
        }

        try {
            hibernate.delete(Book.class, selectedBook.getId());
            fillProductList();
            showAlert("Book deleted successfully!");
        } catch (Exception e) {
            showAlert("Error deleting book: " + e.getMessage());
        }
    }

    public void updateBook() {
        Book selectedBook = productListField.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Please select a book to update.");
            return;
        }
        selectedBook.setTitle(titleField.getText());
        selectedBook.setAuthor(authorField.getText());
        selectedBook.setGenre(genreComboBox.getValue());
        selectedBook.setLanguage(languageComboBox.getValue());
        selectedBook.setFormat(formatComboBox.getValue());
        selectedBook.setDemographic(demographicComboBox.getValue());
        selectedBook.setPublicationStatus(publicationStatusComboBox.getValue());
        hibernate.update(selectedBook);
        fillProductList();
        showAlert("Book updated successfully!");
    }

    public void fillProductList() {
        bookList.getItems().clear();
        List<Book> books = hibernate.getAllRecords(Book.class);
        bookList.getItems().addAll(books);
    }

    public void fillMangaList() {
        mangaList.getItems().clear();
        List<Manga> manga = hibernate.getAllRecords(Manga.class);
        mangaList.getItems().addAll(manga);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateManga(ActionEvent actionEvent) {
        Manga selectedBook = mangaList.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Please select a manga to update.");
            return;
        }
        selectedBook.setTitle(mangaTitleField.getText());
        selectedBook.setAuthor(mangaAuthorField.getText());
        selectedBook.setDemographic(demographicComboBoxManga.getValue());
        selectedBook.setOriginalLanguage(originalLanguageText.getText());
        selectedBook.setColor(isColor.isSelected());
        selectedBook.setIllustrator(illustratorField.getText());
        selectedBook.setVolumeNumber(volumeNumber.getValue());
        hibernate.update(selectedBook);
        fillMangaList();
        showAlert("Manga updated successfully!");
    }

    public void deleteManga(ActionEvent actionEvent) {
        Manga selectedBook = mangaList.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Please select a manga to delete.");
            return;
        }

        try {
            hibernate.delete(Manga.class, selectedBook.getId());
            fillMangaList();
            showAlert("Manga deleted successfully!");
        } catch (Exception e) {
            showAlert("Error deleting manga: " + e.getMessage());
        }
    }

    public void createManga(ActionEvent actionEvent) {
        try {
            Manga manga = new Manga(
                    mangaTitleField.getText(),
                    mangaAuthorField.getText(),
                    illustratorField.getText(),
                    originalLanguageText.getText(),
                    volumeNumber.getValue(),
                    demographicComboBoxManga.getValue(),
                    isColor.isSelected()
            );
            manga.setOwner((Client) currentUser);
            manga.setPublicationStatus(PublicationStatus.AVAILABLE);
            hibernate.create(manga);
            fillMangaList();
            showAlert("Manga created successfully!");
            mainController.fillPublicationTable();
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Error creating manga: " + e.getMessage());
        }
    }

    public void updateBookClient(ActionEvent actionEvent) {
        if (currentPublication == null || !(currentPublication instanceof Book)) {
            showAlert("No book selected to update.");
            return;
        }

        Book book = (Book) currentPublication;
        book.setTitle(titleField.getText());
        book.setAuthor(authorField.getText());
        book.setGenre(genreComboBox.getValue());
        book.setLanguage(languageComboBox.getValue());
        book.setFormat(formatComboBox.getValue());
        book.setDemographic(demographicComboBox.getValue());
        book.setPublicationStatus(publicationStatusComboBox.getValue());
        hibernate.update(book);

        showAlert("Book updated successfully!");
        mainController.publicationTable.getItems().clear();
        mainController.fillPublicationTable();
        Stage stage = (Stage) updateBookButton.getScene().getWindow();
        stage.close();
    }

    public void updateMangaClient(ActionEvent actionEvent) {
        if (currentPublication == null || !(currentPublication instanceof Manga)) {
            showAlert("No manga selected to update.");
            return;
        }

        Manga manga = (Manga) currentPublication;
        manga.setTitle(mangaTitleField.getText());
        manga.setAuthor(mangaAuthorField.getText());
        manga.setIllustrator(illustratorField.getText());
        manga.setOriginalLanguage(originalLanguageText.getText());
        manga.setVolumeNumber(volumeNumber.getValue());
        manga.setDemographic(demographicComboBoxManga.getValue());
        manga.setColor(isColor.isSelected());
        hibernate.update(manga);

        showAlert("Manga updated successfully!");
        mainController.publicationTable.getItems().clear();
        mainController.fillPublicationTable();
        Stage stage = (Stage) updateMangaButton.getScene().getWindow();
        stage.close();
    }
}