package ru.practicum.note;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.item.Item;

import java.time.Instant;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@Table(name = "item_notes")
public class ItemNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;

    @Column(name = "note_date")
    private Instant dateOfNote = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemNote itemNote = (ItemNote) o;
        return Objects.equals(id, itemNote.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
