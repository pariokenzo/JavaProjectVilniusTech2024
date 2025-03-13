package coursework.fxControllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PublicationTableParameters {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty author = new SimpleStringProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty publicationStatus = new SimpleStringProperty();
    private SimpleStringProperty owner = new SimpleStringProperty();

    // ID
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    // Author
    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    // Title
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    // Publication Status
    public String getPublicationStatus() {
        return publicationStatus.get();
    }

    public void setPublicationStatus(String publicationStatus) {
        this.publicationStatus.set(publicationStatus);
    }

    public SimpleStringProperty publicationStatusProperty() {
        return publicationStatus;
    }

    // Owner
    public String getOwner() {
        return owner.get();
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public SimpleStringProperty ownerProperty() {
        return owner;
    }
}