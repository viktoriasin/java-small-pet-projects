package library.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record Book(
        String isbn, // unique book identifier
        String title,
        LocalDate releaseDate,
        List<Author> authors,
        Set<String> categories,
        int pages,
        BigDecimal price
) {
}
