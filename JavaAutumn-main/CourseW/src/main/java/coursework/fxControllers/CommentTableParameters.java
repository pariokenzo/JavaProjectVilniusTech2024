package coursework.fxControllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CommentTableParameters {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty body = new SimpleStringProperty();
    private SimpleStringProperty commentOwner = new SimpleStringProperty();
    private SimpleIntegerProperty parentCom = new SimpleIntegerProperty();

    // Getter and Setter for 'id'
    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // Getter and Setter for 'title'
    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    // Getter and Setter for 'body'
    public String getBody() {
        return body.get();
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }

    public void setBody(String body) {
        this.body.set(body);
    }

    // Getter and Setter for 'commentOwner'
    public String getCommentOwner() {
        return commentOwner.get();
    }

    public SimpleStringProperty commentOwnerProperty() {
        return commentOwner;
    }

    public void setCommentOwner(String commentOwner) {
        this.commentOwner.set(commentOwner);
    }

    public int getParentCom() {
        return parentCom.get();
    }
    public SimpleIntegerProperty parentComProperty() {
        return parentCom;
    }


    public void setParentComment(int parentCom) { // Renamed and expects an integer now
        this.parentCom.set(parentCom);
    }
}
