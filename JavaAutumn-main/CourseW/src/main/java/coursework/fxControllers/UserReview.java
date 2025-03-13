package coursework.fxControllers;

import coursework.hibernateControllers.CustomHibernate;
import coursework.model.Client;
import coursework.model.Comment;
import coursework.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class UserReview {
    @FXML
    public TreeView<Comment> userReview;
    @FXML
    public TextArea commentBody;
    @FXML
    public TextField commentTitle;
    @FXML
    public Button updateButton;
    @FXML
    public ContextMenu commentContextMenu;
    @FXML
    public MenuItem deleteItem;

    private CustomHibernate hibernate;
    private User currentUser;
    private Client targetClient;

    public void setData(EntityManagerFactory entityManagerFactory, User user, Client client) {
        this.hibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = user;
        this.targetClient = client;
        fillTree();

        if (currentUser instanceof Client) {
            //updateButton.setDisable(true);
            commentContextMenu.hide();
            //deleteItem.setDisable(true);
        }
    }

    private void fillTree() {
        userReview.setRoot(new TreeItem<>());
        userReview.setShowRoot(false);
        userReview.getRoot().setExpanded(true);
        Client clientFromDb = hibernate.getEntityById(Client.class, targetClient.getId());
        clientFromDb.getCommentList().stream()
                .filter(c -> c.getChat() == null)
                .forEach(c -> addTreeItem(c, userReview.getRoot()));
    }
    public void addTreeItem(Comment comment, TreeItem<Comment> parentComment) {
        TreeItem<Comment> treeItem = new TreeItem<>(comment);
        parentComment.getChildren().add(treeItem);
        comment.getReplies().forEach(sub -> addTreeItem(sub, treeItem));
    }

    public void insertComment() {
        if (currentUser instanceof Client client) {
            Comment selectedComment = userReview.getSelectionModel().getSelectedItem() != null ? userReview.getSelectionModel().getSelectedItem().getValue() : null;
            Comment comment;
            if (selectedComment != null) {
                comment = new Comment(commentTitle.getText(), commentBody.getText(), selectedComment, client);
                selectedComment.getReplies().add(comment);
                hibernate.update(selectedComment);
            } else {
                comment = new Comment(commentTitle.getText(), commentBody.getText(), targetClient, client);
            }
            hibernate.create(comment);
            fillTree();
        }
    }
    public void loadComment() {
        TreeItem<Comment> selectedItem = userReview.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Comment selectedComment = selectedItem.getValue();
            commentTitle.setText(selectedComment.getTitle());
            commentBody.setText(selectedComment.getBody());
        } else {
            commentTitle.clear();
            commentBody.clear();
        }
    }
    public void updateComment() {
        Comment selectedComment = userReview.getSelectionModel().getSelectedItem().getValue();
        if (selectedComment.getCommentOwner().getId() == currentUser.getId()) {
            selectedComment.setTitle(commentTitle.getText());
            selectedComment.setBody(commentBody.getText());
            hibernate.update(selectedComment);
            fillTree();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You can only update your own comments.");
            alert.showAndWait();
        }
    }

    public void deleteComment() {
        TreeItem<Comment> selectedItem = userReview.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Comment selectedComment = selectedItem.getValue();
            if (selectedComment.getCommentOwner().getId() == currentUser.getId()) {
                selectedItem.getParent().getChildren().remove(selectedItem);
                hibernate.deleteCommentFromTable(selectedComment.getId());
                fillTree();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("You can only delete your own comments.");
                alert.showAndWait();
            }
        }
    }


}