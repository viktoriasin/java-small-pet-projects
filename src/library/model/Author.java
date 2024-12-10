package library.model;

import java.time.LocalDate;

public record Author(
        long id, // unique author id
        String firstName,
        String lastName,
        LocalDate birthDate
) {
}

