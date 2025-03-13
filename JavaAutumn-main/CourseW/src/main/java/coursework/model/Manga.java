package coursework.model;

import coursework.model.enums.Demographic;
import coursework.model.enums.Language;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manga extends Publication{

    private String illustrator;
    private String originalLanguage;
    private int volumeNumber;
    @Enumerated
    private Demographic demographic;
    private boolean isColor;
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chatList;

    public Manga(String title, String author, String illustrator, String originalLanguage, int volumeNumber, Demographic demographic, boolean isColor) {
        super(title, author);
        this.illustrator = illustrator;
        this.originalLanguage = originalLanguage;
        this.volumeNumber = volumeNumber;
        this.demographic = demographic;
        this.isColor = isColor;
    }



    @Override
    public String toString() {
        return title + " by " + author  + " (" + demographic + ")" + " (" + originalLanguage + ")"  +" (" + volumeNumber + ")" + " (" + isColor + ")";
    }

}
