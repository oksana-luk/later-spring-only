package ru.practicum.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

interface ItemNoteRepository extends JpaRepository<ItemNote, Long> {

    @Transactional(readOnly = true)
    List<ItemNote> findAllByItemUrlContainingAndItemUserId(String itemUrl, Long userId);

    @Query("select itn " +
            "from ItemNote as itn " +
            "left join itn.item as it " +
            "where it.user.id = ?1 and ?2 member of it.tags")
    List<ItemNote> findByTag(Long userId, String tag);

    Page<ItemNote> findAllByItemUserId(Long userId, Pageable page);
}
