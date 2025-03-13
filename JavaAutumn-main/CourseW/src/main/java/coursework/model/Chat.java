package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Book book;
    @ManyToOne
    private Manga manga;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Comment> messages;

    public Chat(Book book, List<Comment> messages) {
        this.book = book;
        this.messages = messages;
    }

    public Chat(Manga manga, List<Comment> messages) {
        this.manga = manga;
        this.messages = messages;
    }
}
