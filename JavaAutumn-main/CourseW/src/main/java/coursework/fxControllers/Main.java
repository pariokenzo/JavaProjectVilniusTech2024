package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibernateControllers.CustomHibernate;
import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//Norėdama pasiekti formos elementus iki forma yra sugeneruojama, turiu implementuoti Initializable interfeisą.
//Jums automatiškai siūlys initialize metodą įgyvendint, ten dedat kodą, kuris turi būt vykdomas pirmiausia
//Mūsų atveju gavosi, kad reikia ištraukt userius iš DB
public class Main implements Initializable {
    @FXML
    //Stenkitės nurodyti tikslius duomenų tipus, kurie bus saugomi ListView
    public ListView<User> userListField;
    @FXML
    public TextField loginField;
    @FXML
    public TextField nameField;
    @FXML
    public PasswordField pswField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField addressField;
    @FXML
    public DatePicker bDate;
    @FXML
    public TextField phoneNumField;
    @FXML
    public RadioButton adminChk;
    @FXML
    public RadioButton clientChk;
    //<editor-fold desc="Tableview attributes">
    @FXML
    public TableView<UserTableParameters> userTable;
    @FXML
    public TableColumn<UserTableParameters, Integer> colId;
    @FXML
    public TableColumn<UserTableParameters, String> colLogin;
    @FXML
    public TableColumn<UserTableParameters, String> colPsw;
    @FXML
    public TableColumn<UserTableParameters, String> colName;
    @FXML
    public TableColumn<UserTableParameters, String> colSurname;
    @FXML
    public TableColumn<UserTableParameters, String> colAddress;
    @FXML
    public TableColumn<UserTableParameters, String> colPhone;
    @FXML
    public TableColumn dummyCol;
    @FXML
    public TableColumn colDeleteCom;
    //</editor-fold>

    //<editor-fold desc="Main exchange tab fields">
    @FXML
    public ListView<Publication> availableBookList;
    @FXML
    public TextArea aboutBook;
    @FXML
    public TextArea ownerBio;
    @FXML
    public Label ownerInfo;
    @FXML
    public ListView<Comment> chatList;
    @FXML
    public TextArea messageArea;
    @FXML
    public Tab publicationManagementTab;
    @FXML
    public Tab userManagementTab;
    @FXML
    public Tab commentManagementTab;
    @FXML
    public Tab UserTab;
    @FXML
    public Tab clientBookManagementTab;
    @FXML
    public TabPane allTabs;
    @FXML
    public Button leaveReviewButton;
    @FXML
    public Tab bookExchangeTab;

    //<editor-fold desc="My Books Tab">
    @FXML
    public TableView<BookTableParameters> myBookList;
    @FXML
    public TableColumn<BookTableParameters, String> colBookTitle;
    @FXML
    public TableColumn<BookTableParameters, String> colRequestUser;
    @FXML
    public TableColumn colBookStatus; //Panasiai kaip dummyCol
    @FXML
    public TableColumn<BookTableParameters, String> colRequestDate;
    @FXML
    public TableColumn<BookTableParameters, Integer> collBookId;
    @FXML
    public TableColumn colHistory; //Panasiai kaip dummyCol
    public TableView<PublicationTableParameters> publicationTable;
    public TableColumn<BookTableParameters, Integer> colPubId;
    public TableColumn<BookTableParameters, String> colAuthor;
    public TableColumn<BookTableParameters, String> colTitle;

    public TableColumn<BookTableParameters, String> colStatus;
    public TableColumn<BookTableParameters, String> colOwner;
    public TableColumn colDelete;
    public TableColumn colChat;
    public TableColumn<CommentTableParameters, String> colTitleCom;
    public TableColumn<CommentTableParameters, String> colBody;
    public TableColumn<CommentTableParameters, String>  colCommentOwner;
    public TableColumn<CommentTableParameters, String>  colParentCom;
    public TableView<CommentTableParameters> commentTable;
    public TableColumn<CommentTableParameters, Integer> colComId;
    public TableColumn dummyColPub;
    private Integer activePublicationId = null;

    //</editor-fold>
    EntityManagerFactory entityManagerFactory;
    private CustomHibernate hibernate;
    private User currentUser;

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.hibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = user;
        fillUserList();



        //Priklausomai nuo prisijungusio, apribojam matomuma
        enableVisibility();
    }

    public void fillUserList() {
        userListField.getItems().clear();
        List<User> userList = hibernate.getAllRecords(User.class);
        userListField.getItems().addAll(userList);
    }

    private void enableVisibility() {
        if (currentUser instanceof Client) {
            allTabs.getTabs().remove(publicationManagementTab);
            allTabs.getTabs().remove(UserTab);
            allTabs.getTabs().remove(userManagementTab);
            allTabs.getTabs().remove(commentManagementTab);
        } else {
            allTabs.getTabs().remove(clientBookManagementTab);
            allTabs.getTabs().remove(bookExchangeTab);
            leaveReviewButton.setDisable(false);
        }
    }
    public void switchToMyBooksTab() {
        allTabs.getSelectionModel().select(clientBookManagementTab);
    }

    public void createNewUser() {
        if (clientChk.isSelected()) {
            Client client = new Client(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), addressField.getText(), bDate.getValue());
            hibernate.create(client);
        } else {
            Admin admin = new Admin(loginField.getText(), pswField.getText(), nameField.getText(), surnameField.getText(), phoneNumField.getText());
            hibernate.create(admin);
        }
        fillUserList();
    }

    public void disableFields() {
        if (clientChk.isSelected()) {
            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
        } else {
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
        }
    }


    //Sioje vietoje initialize mums reikia, kad nustatytume tam tikras reiksmes, kai dar nereikia duomenu bazes
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //<editor-fold desc="User Table initialize">
        userTable.setEditable(true);

        //Atvaizdavimui
        colPubId.setCellValueFactory(new PropertyValueFactory<>("pubId"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPsw.setCellValueFactory(new PropertyValueFactory<>("password"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        colCommentOwner.setCellValueFactory(new PropertyValueFactory<>("commentOwner"));
        colParentCom.setCellValueFactory(new PropertyValueFactory<>("parentCom"));
        colTitleCom.setCellValueFactory(new PropertyValueFactory<>("title"));
        colBody.setCellValueFactory(new PropertyValueFactory<>("body"));
        colComId.setCellValueFactory(new PropertyValueFactory<>("id"));

        //Jei noriu padaryt redaguojamas celes
        colLogin.setCellFactory(TextFieldTableCell.forTableColumn());
        colLogin.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setLogin(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setLogin(event.getNewValue());
            hibernate.update(user);
        });

        //Jei turesite lentele, kurioje saugosime ir Admin ir Customer, tiems stulpeliams, kur yra specifiniai pagal klases
        //Reikia pasitikrinti koks ten tas User
        colAddress.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setAddress(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Client client) {
                client.setAddress(event.getNewValue());
                hibernate.update(user);
            }
        });

        colPhone.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhoneNum(event.getNewValue());
            User user = hibernate.getEntityById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            if (user instanceof Admin admin) {
                admin.setPhoneNum(event.getNewValue());
                hibernate.update(admin);
            }
        });
        // Callback for delete button in "My Books" tab


        //Cia dabar bus knopke

        Callback<TableColumn<UserTableParameters, Void>, TableCell<UserTableParameters, Void>> callback = param -> {
            final TableCell<UserTableParameters, Void> cell = new TableCell<>() {
                private final Button deleteUserButton = new Button("Delete");

                {
                    deleteUserButton.setOnAction(event -> {
                        UserTableParameters row = getTableView().getItems().get(getIndex());
                        hibernate.delete(User.class, row.getId());
                        fillUserTable();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteUserButton);
                    }
                }
            };
            return cell;
        };
        dummyCol.setCellFactory(callback);

        Callback<TableColumn<CommentTableParameters, Void>, TableCell<CommentTableParameters, Void>> commentDeleteCallback = param -> {
            final TableCell<CommentTableParameters, Void> cell = new TableCell<>() {
                private final Button deleteCommentButton = new Button("Delete Comment");

                {
                    deleteCommentButton.setOnAction(event -> {
                        CommentTableParameters row = getTableView().getItems().get(getIndex());
                        System.out.println("Deleting comment with ID: " + row.getId());
                        hibernate.deleteCommentFromTable(row.getId());
                        fillCommentTable();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteCommentButton);
                    }
                }
            };
            return cell;
        };

        colDeleteCom.setCellFactory(commentDeleteCallback);

        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> deleteButtonCallback = param -> {
            return new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        BookTableParameters row = getTableView().getItems().get(getIndex());
                        System.out.println("Deleting book with ID: " + row.getId());
                         hibernate.deletePublication(row.getId());
                        fillPublicationTable();
                    });

                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
        };

        colDelete.setCellFactory(deleteButtonCallback);
        Callback<TableColumn<PublicationTableParameters, Void>, TableCell<PublicationTableParameters, Void>> deleteButtonPub = param -> {
            return new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        PublicationTableParameters row = getTableView().getItems().get(getIndex());
                        System.out.println("Deleting publication with ID: " + row.getId());
                        hibernate.deletePublication(row.getId());
                        fillPublicationTable();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
        };

        dummyColPub.setCellFactory(deleteButtonPub);
        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> chatButtonCallback = param -> {
            return new TableCell<>() {
                private final Button viewChatButton = new Button("View Chat");

                {
                    viewChatButton.setOnAction(event -> {
                        // Get the selected book from the row
                        BookTableParameters bookRow = getTableView().getItems().get(getIndex());
                        // Call the method to switch to the chat
                        viewChatForBook(bookRow.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewChatButton);
                    }
                }
            };
        };

        colChat.setCellFactory(chatButtonCallback);
        //</editor-fold>
        //<editor-fold desc="Client book management window">
        availableBookList.setEditable(true);

        collBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("publicationTitle"));
        colRequestUser.setCellValueFactory(new PropertyValueFactory<>("publicationUser"));
        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> callbackBookStatus = param -> {
            final TableCell<BookTableParameters, Void> cell = new TableCell<>() {

                private final ChoiceBox<PublicationStatus> bookStatus = new ChoiceBox<>();

                {
                    bookStatus.getItems().addAll(PublicationStatus.values());
                    bookStatus.setOnAction(event -> {
                        BookTableParameters rowData = getTableRow().getItem();
                        if (rowData != null) {
                            rowData.setPublicationStatus(bookStatus.getValue());

                            Publication publication = hibernate.getEntityById(Publication.class, rowData.getId());
                            publication.setPublicationStatus(bookStatus.getValue());
                            hibernate.update(publication);

                            insertPublicationRecord(publication);
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        BookTableParameters rowData = getTableRow().getItem();
                        bookStatus.setValue(rowData.getPublicationStatus());
                        setGraphic(bookStatus);
                    }
                }
            };
            return cell;
        };

        colBookStatus.setCellFactory(callbackBookStatus);

        //Cia dabar bus knopke
        Callback<TableColumn<BookTableParameters, Void>, TableCell<BookTableParameters, Void>> historyButton = param -> new TableCell<>() {
            private final Button viewHistoryBtn = new Button("View history");

            {
                viewHistoryBtn.setOnAction(event -> {
                    BookTableParameters row = getTableView().getItems().get(getIndex());
                    try {
                        loadHistory(row.getId(), currentUser);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewHistoryBtn);
                }
            }
        };

        colHistory.setCellFactory(historyButton);

        //</editor-fold>


    }


    public void loadUserData() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();

        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        nameField.setText(userInfoFromDb.getName());
        surnameField.setText(userInfoFromDb.getSurname());

        if (userInfoFromDb instanceof Client) {
            Client client = (Client) userInfoFromDb;
            addressField.setText(client.getAddress());
        } else {
            Admin admin = (Admin) userInfoFromDb;
            phoneNumField.setText(admin.getPhoneNum());
        }

    }

    public void updateUser() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        userInfoFromDb.setName(nameField.getText());
        userInfoFromDb.setSurname(surnameField.getText());
        userInfoFromDb.setLogin(loginField.getText());
        userInfoFromDb.setPassword(pswField.getText());

        hibernate.update(userInfoFromDb);
        fillUserList();
    }

    public void deleteUser() {

        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        hibernate.delete(User.class, selectedUser.getId());
        fillUserList();
    }

    public void loadProductForm() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("productWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Book Exchange Test");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

    }

    private void fillUserTable() {
        userTable.getItems().clear();
        List<User> users = hibernate.getAllRecords(User.class);
        for (User u : users) {
            UserTableParameters userTableParameters = new UserTableParameters();
            userTableParameters.setId(u.getId());
            userTableParameters.setLogin(u.getLogin());
            userTableParameters.setPassword(u.getPassword());
            userTableParameters.setName(u.getName());
            userTable.getItems().add(userTableParameters);
        }
    }
    private void fillCommentTable() {
        commentTable.getItems().clear();
        List<Comment> comments = hibernate.getAllRecords(Comment.class);
        for (Comment c : comments) {
            if (c.getChat() == null) {
                CommentTableParameters commentTableParameters = new CommentTableParameters();
                commentTableParameters.setId(c.getId());
                commentTableParameters.setTitle(c.getTitle());
                commentTableParameters.setBody(c.getBody());
                commentTableParameters.setCommentOwner(String.valueOf(c.getCommentOwner().getId()));
                if (c.getParentComment() != null) {
                    commentTableParameters.setParentComment(c.getParentComment().getId());
                } else {
                    commentTableParameters.setParentComment(-1);
                }
                commentTable.getItems().add(commentTableParameters);
            }
        }
    }
    public void fillPublicationTable() {
        publicationTable.getItems().clear();
        List<Publication> publications = hibernate.getAllRecords(Publication.class);
        for (Publication p : publications) {
            PublicationTableParameters publicationTableParameters = new PublicationTableParameters();

            publicationTableParameters.setId(p.getId());
            publicationTableParameters.setAuthor(p.getAuthor());
            publicationTableParameters.setTitle(p.getTitle());
            publicationTableParameters.setOwner(p.getOwner() != null ? p.getOwner().getName() : "Unknown");

            publicationTable.getItems().add(publicationTableParameters);
        }
    }


    public void loadData() {
        //Kai spaudziam ant tab, tik tam tab pildom duomenis
        if (userManagementTab.isSelected()) {
            fillUserTable();
        }
        else if (bookExchangeTab.isSelected()) {
            availableBookList.getItems().clear();
            availableBookList.getItems().addAll(hibernate.getAvailablePublications(currentUser));
        } else if (clientBookManagementTab.isSelected()) {
            fillBookList();
        } else if (publicationManagementTab.isSelected()) {
            fillPublicationTable();
        }
        else if (commentManagementTab.isSelected()) {
            fillCommentTable();
        }

    }



    public void loadReviewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("userReview.fxml"));
        Parent parent = fxmlLoader.load();
        UserReview userReview = fxmlLoader.getController();
        userReview.setData(entityManagerFactory, currentUser, availableBookList.getSelectionModel().getSelectedItem().getOwner());
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }




    //<editor-fold desc="Book Exchange Tab Management">
    public void loadPublicationInfo() {
        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());

        if (publicationFromDb instanceof Book book)
            aboutBook.setText(
                    "Title :" + book.getTitle() + "\n" +
                    "Title :" + book.getTitle() + "\n" +
                            "Year:" + book.getPublicationYear());
        loadChatMessages(hibernate.getChatByPublication(publicationFromDb));
        ownerInfo.setText(publicationFromDb.getOwner().getName());
        ownerBio.setText(publicationFromDb.getOwner().getClientBio());
    }


    public void chatWithOwner() {
        Publication selectedBook = availableBookList.getSelectionModel().getSelectedItem();
        if (selectedBook == null && activePublicationId != null) {
            // Fetch the book using the active publication ID
            selectedBook = hibernate.getEntityById(Publication.class, activePublicationId);
        }

        Chat chat = hibernate.getChatByPublication(selectedBook);
        if (chat == null) {
            chat = new Chat((Book) selectedBook, new ArrayList<>());
            hibernate.create(chat);
        }

        // Load and display chat messages
        loadPublicationInfo();
        activePublicationId = null;
    }

    private void showAlert(String s) {
    }

    private void loadChatMessages(Chat chat) {
        chatList.getItems().clear();

        List<Comment> messages = hibernate.getChatMessages(chat);

        if (messages != null) {
            chatList.getItems().addAll(messages);
        }

        chatList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Comment message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    String sender;
                    if (message.getCommentOwner().getId() == (currentUser.getId())) {
                        sender = "Owner"; // Current user
                    }
                     else {
                        sender = "Buyer";
                    }
                    setText(sender + ": " + message.getBody());
                }
            }});


    }
    public void loadChatForOwner(int id){
        loadChatMessages(hibernate.getChatByPublication(hibernate.getEntityById(Publication.class, id)));
    }
    private void viewChatForBook(int bookId) {
        activePublicationId = bookId;

        allTabs.getSelectionModel().select(bookExchangeTab);

        for (Publication book : availableBookList.getItems()) {
            if (book.getId() == bookId) {
                availableBookList.getSelectionModel().select(book);
                break;
            }
        }

        loadChatForOwner(bookId);
        chatWithOwner();
    }
    public void deleteChat(Publication publication) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            List<Chat> chats = new ArrayList<>();
            if (publication instanceof Book) {
                chats = em.createQuery("SELECT c FROM Chat c WHERE c.book = :book", Chat.class)
                        .setParameter("book", (Book) publication)
                        .getResultList();
            } else if (publication instanceof Manga) {
                chats = em.createQuery("SELECT c FROM Chat c WHERE c.manga = :manga", Chat.class)
                        .setParameter("manga", (Manga) publication)
                        .getResultList();
            }

            for (Chat chat : chats) {
                em.remove(chat);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    public void sendMessage() {
        String messageContent = messageArea.getText().trim();

        if (messageContent.isEmpty()) {
            showAlert("Message cannot be empty.");
            return;
        }

        Publication selectedPublication = availableBookList.getSelectionModel().getSelectedItem();
        if (selectedPublication == null && activePublicationId != null) {
            selectedPublication = hibernate.getEntityById(Publication.class, activePublicationId);
        }

        if (selectedPublication == null) {
            showAlert("Please select a publication or ensure a valid publication is active.");
            return;
        }
        Client publicationOwner = selectedPublication.getOwner();
        if (publicationOwner == null) {
            showAlert("The selected publication doesn't have an owner.");
            return;
        }

        // Retrieve or create the chat
        Chat chat = hibernate.getChatByPublication(selectedPublication);
        if (chat == null) {
            if (selectedPublication instanceof Book) {
                chat = new Chat((Book) selectedPublication, new ArrayList<>());
                ((Book) selectedPublication).getChatList().add(chat);
            } else if (selectedPublication instanceof Manga) {
                chat = new Chat((Manga) selectedPublication, new ArrayList<>());
                ((Manga) selectedPublication).getChatList().add(chat);
            }
            hibernate.create(chat);
        }

        Comment newMessage = new Comment("Chat", messageContent, (Client) currentUser, publicationOwner);
        newMessage.setChat(chat);

        if (newMessage.getReplies() == null) {
            newMessage.setReplies(new ArrayList<>());
        }

        hibernate.create(newMessage);

        chat.getMessages().add(newMessage);
        hibernate.update(chat);
        messageArea.clear();
        loadChatMessages(chat);
    }
    public void reserveBook() {

        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());
        publicationFromDb.setPublicationStatus(PublicationStatus.REQUESTED);
        publicationFromDb.setClient((Client) currentUser);
        hibernate.update(publicationFromDb);

        PeriodicRecord periodicRecord = new PeriodicRecord((Client) currentUser, publicationFromDb, LocalDate.now(), PublicationStatus.REQUESTED);
        hibernate.create(periodicRecord);

    }
    //</editor-fold>


    //<editor-fold desc="MyBooks Tab management">
    private void fillBookList() {
        myBookList.getItems().clear();
        List<Publication> publications = hibernate.getOwnPublications(currentUser);
        for (Publication p : publications) {
            BookTableParameters bookTableParameters = new BookTableParameters();
            bookTableParameters.setId(p.getId());
            bookTableParameters.setPublicationTitle(p.getTitle());
            if (p.getClient() != null) {
                bookTableParameters.setPublicationUser(p.getClient().getName() + " " + p.getClient().getSurname());
            }
            bookTableParameters.setPublicationStatus(p.getPublicationStatus());

            myBookList.getItems().add(bookTableParameters);
        }
    }


    private void insertPublicationRecord(Publication publication) {
        PeriodicRecord periodicRecord = new PeriodicRecord(publication.getClient(), publication, LocalDate.now(), publication.getPublicationStatus());
        hibernate.create(periodicRecord);
    }

    //UZDUOTIS. Logika mano pasiskolintu knygu atvaizdavimui


    //Logika istoriniu knygos duomenu perziurai

    private void loadHistory(int id, User currentUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("history.fxml"));
        Parent parent = fxmlLoader.load();
        History history = fxmlLoader.getController();
        history.setData(entityManagerFactory, currentUser, id);
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Test");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void updatePublication(ActionEvent actionEvent) {
        BookTableParameters selectedPublicationParams = myBookList.getSelectionModel().getSelectedItem();
        if (selectedPublicationParams == null) {
            showAlert("Please select a publication to update.");
            return;
        }

        Publication selectedPublication = hibernate.getEntityById(Publication.class, selectedPublicationParams.getId());

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("productWindow.fxml"));
            Parent parent = fxmlLoader.load();

            ProductWindow productController = fxmlLoader.getController();
            productController.setCurrentUser(currentUser);
            productController.setPublication(selectedPublication);
            productController.setMainController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewPublication() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("productWindow.fxml"));
            Parent parent = fxmlLoader.load();

            ProductWindow productController = fxmlLoader.getController();
            productController.setCurrentUser(currentUser);
            productController.setMainController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




    //</editor-fold>


