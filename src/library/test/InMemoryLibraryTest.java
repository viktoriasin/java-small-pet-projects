package library.test;


import library.model.Author;
import library.model.Book;
import library.InMemoryLibrary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryLibraryTest {
    private InMemoryLibrary library;

    private Author author1;
    private Author author2;
    private Author author3;
    @BeforeEach
    void setUp() {
        library = new InMemoryLibrary(10);

        // Добавляем авторов
        author1 = library.addAuthor("John", "Doe", LocalDate.of(1980, 1, 1));
        author2 = library.addAuthor("Jane", "Smith", LocalDate.of(1990, 2, 2));
        author3 = library.addAuthor("Joshua", "Bloch", LocalDate.of(1961, 8, 28));

        // Добавляем книги
        Book book = library.addBook("978-3-16-148410-0", "The Great Adventure",
                LocalDate.of(2021, 1, 1),
                Set.of("Adventure"),
                List.of(author1.id()),
                300,
                BigDecimal.valueOf(19.99));

        library.addBook("978-1-23-456789-7", "Adventures in Wonderland",
                LocalDate.of(2022, 2, 2),
                Set.of("Fantasy"),
                List.of(author2.id()),
                400,
                BigDecimal.valueOf(25.99));

        library.addBook("978-0-00-111111-1", "Cooking Adventures",
                LocalDate.of(2023, 3, 3),
                Set.of("Cooking"),
                List.of(author1.id()),
                200,
                BigDecimal.valueOf(15.99));

        library.addBook(
                "978-1-23456-789-0",
                "Java Basics",
                LocalDate.of(2020, 5, 10),
                Set.of("Programming", "Java"),
                List.of(author3.id()),
                300,
                new BigDecimal("29.99")
        );

        library.addBook(
                "978-1-23456-789-1",
                "Advanced Java",
                LocalDate.of(2022, 7, 15),
                Set.of("Programming", "Advanced"),
                List.of(author3.id()),
                450,
                new BigDecimal("39.99")
        );

        library.addBook(
                "978-1-23456-789-2",
                "Spring Boot Guide",
                LocalDate.of(2021, 3, 5),
                Set.of("Spring", "Framework"),
                List.of(author2.id()),
                500,
                new BigDecimal("34.99")
        );

        library.addBook(
                "978-1-23456-789-3",
                "Effective Java",
                LocalDate.of(2019, 1, 1),
                Set.of("Best Practices", "Java"),
                List.of(author3.id()),
                400,
                new BigDecimal("44.99")
        );
    }

    @Test
    void testFindBookByIsbn_BookFound() {
        String isbn = "978-3-16-148410-0";
        Optional<Book> result = library.findBookByIsbn(isbn);

        assertTrue(result.isPresent(), "Книга должна быть найдена");
        assertEquals("The Great Adventure", result.get().title(), "Название книги должно совпадать");
    }

    @Test
    void testGetBooksByTitleLikeIgnoreCase_FoundMultiple() {
        String titleSubstring = "adventure";
        Set<Book> books = library.getBooksByTitleLikeIgnoreCase(titleSubstring);

        assertEquals(3, books.size(), "Должно быть найдено три книги");
        assertTrue(books.stream().anyMatch(book -> book.title().equals("The Great Adventure")), "Книга 'The Great Adventure' должна быть найдена");
        assertTrue(books.stream().anyMatch(book -> book.title().equals("Adventures in Wonderland")), "Книга 'Adventures in Wonderland' должна быть найдена");
        assertTrue(books.stream().anyMatch(book -> book.title().equals("Cooking Adventures")), "Книга 'Cooking Adventures' должна быть найдена");
    }

    @Test
    void testGetBooksByTitleLikeIgnoreCase_FoundSingle() {
        String titleSubstring = "wonderland";
        Set<Book> books = library.getBooksByTitleLikeIgnoreCase(titleSubstring);

        assertEquals(1, books.size(), "Должна быть найдена одна книга");
        assertTrue(books.stream().anyMatch(book -> book.title().equals("Adventures in Wonderland")), "Книга 'Adventures in Wonderland' должна быть найдена");
    }

    @Test
    void testGetBooksByTitleLikeIgnoreCase_NotFound() {
        String titleSubstring = "nonexistent";
        Set<Book> books = library.getBooksByTitleLikeIgnoreCase(titleSubstring);

        assertTrue(books.isEmpty(), "Не должно быть найдено книг");
    }

    @Test
    void testGetBooksByTitleLikeIgnoreCase_CaseInsensitive() {
        String titleSubstring = "ADVENTURE";
        Set<Book> books = library.getBooksByTitleLikeIgnoreCase(titleSubstring);

        assertEquals(3, books.size(), "Должно быть найдено три книги");
    }

    @Test
    public void testGetAllBooksSortedByReleaseDateDesc() {
        List<Book> result = library.getAllBooksSortedByReleaseDateDesc();

        List<String> titles = result.stream()
                .map(Book::title)
                .toList();

        assertEquals(List.of("Cooking Adventures", "Advanced Java","Adventures in Wonderland", "Spring Boot Guide",
                "The Great Adventure", "Java Basics", "Effective Java"), titles);
    }

    @Test
    public void testGetBooksByAuthorSortedByReleaseDateDesc() {
        List<Book> result = library.getBooksByAuthorSortedByReleaseDateDesc(author3.id());

        List<String> titles = result.stream()
                .map(Book::title)
                .toList();

        assertEquals(List.of("Advanced Java", "Java Basics", "Effective Java"), titles);
    }

    @Test
    void testFindMostExpensiveBookInCategory() {
        Optional<Book> result = library.findMostExpensiveBookInCategory("Java");

        assertTrue(result.isPresent());

        Book mostExpensiveBook = result.get();
        assertEquals("Effective Java", mostExpensiveBook.title());
        assertEquals(new BigDecimal("44.99"), mostExpensiveBook.price());
    }

    @Test
    void testFindAuthorsWithBooksInMultipleCategories() {
        Long expectedAuthorId = author3.id();
        Long expectedSize = 1L;
        Set<Author> authors = library.findAuthorsWithBooksInMultipleCategories(3);

        assertTrue(authors.stream().anyMatch(author -> author.id() == expectedAuthorId));
        assertEquals(expectedSize, authors.size());
    }

    @Test
    void testCalculateAveragePagesForAuthor() {
        Long authorId = author1.id();
        OptionalDouble averagePagesAuthor1 = library.calculateAveragePagesForAuthor(authorId);

        assertTrue(averagePagesAuthor1.isPresent());
        assertEquals(250.0, averagePagesAuthor1.getAsDouble(), 0.01);
    }

    @Test
    void testGetAllAuthors() {
        Set<Author> authors = library.getAllAuthors();

        assertEquals(3, authors.size());

        List<Long> authorsIds = authors.stream()
                .map(Author::id)
                .sorted()
                .toList();

        assertEquals(Stream.of(author1.id(), author2.id(), author3.id()).sorted().toList(), authorsIds);
    }
    @Test
    void testCalculateTotalPriceOfBooksByCategory() {
        Map<String, BigDecimal> totalPrices = library.calculateTotalPriceOfBooksByCategory();

        assertEquals(9, totalPrices.size());

        assertEquals(BigDecimal.valueOf(19.99), totalPrices.get("Adventure"));
        assertEquals(BigDecimal.valueOf(25.99), totalPrices.get("Fantasy"));
        assertEquals(BigDecimal.valueOf(15.99), totalPrices.get("Cooking"));
        assertEquals(BigDecimal.valueOf(69.98), totalPrices.get("Programming"));
    }

    @Test
    void testGetBooksByReleaseYear() {
        List<Book> books2021 = library.getBooksByReleaseYear(Year.of(2021));

        assertEquals(2, books2021.size());
        assertTrue(books2021.stream().anyMatch(x -> x.title().equals("The Great Adventure")));
        assertTrue(books2021.stream().anyMatch(x -> x.title().equals("Spring Boot Guide")));

        List<Book> books2022 = library.getBooksByReleaseYear(Year.of(2022));

        assertTrue(books2022.stream().anyMatch(x -> x.title().equals("Adventures in Wonderland")));
        assertTrue(books2022.stream().anyMatch(x -> x.title().equals("Advanced Java")));

        List<Book> books2025 = library.getBooksByReleaseYear(Year.of(2025));

        assertEquals(0, books2025.size());
    }

    @Test
    void testFindMostPopulatedCategory() {
        Optional<String> mostPopulatedCategory = library.findMostPopulatedCategory();

        assertTrue(mostPopulatedCategory.isPresent());
        assertEquals("Java", mostPopulatedCategory.get());
    }

    @Test
    void testAddAuthor_NewAuthor() {
        Author expectedAuthor = new Author(author1.id(), "Mark", "Twain", LocalDate.of(1835, 11, 30));
        Author actualAuthor = library.addAuthor("Mark", "Twain", LocalDate.of(1835, 11, 30));

        assertNotNull(actualAuthor, "Автор должен быть добавлен");
        assertEquals(expectedAuthor.firstName(), actualAuthor.firstName());
        assertEquals(expectedAuthor.lastName(), actualAuthor.lastName());
        assertEquals(expectedAuthor.birthDate(), actualAuthor.birthDate());
    }

    @Test
    void testAddAuthor_ExistingAuthor() {
        Author author1 = library.addAuthor("John", "Doe", LocalDate.of(1980, 1, 1));
        Author author2 = library.addAuthor("John", "Doe", LocalDate.of(1980, 1, 1));

        assertEquals(author1.id(), author2.id(), "Существующий автор должен быть возвращён");
    }

    @Test
    void testDeleteBook() {
        Optional<Book> deletedBook = library.deleteBook("978-3-16-148410-0");

        assertTrue(deletedBook.isPresent());
        assertEquals("The Great Adventure", deletedBook.get().title());

        Optional<Book> nonExistingBook = library.deleteBook("978-3-16-148410-0");
        assertFalse(nonExistingBook.isPresent());

        Optional<Book> notFoundBook = library.deleteBook("978-0-00-000000-0");
        assertFalse(notFoundBook.isPresent());
    }

    @Test
    void testDeleteExistingAuthor() {
        Author author4 = library.addAuthor("Drake", "Smith", LocalDate.of(1970, 11, 24));
        Optional<Author> deletedAuthor = library.deleteAuthor(author4.id());

        assertTrue(deletedAuthor.isPresent());
    }
    @Test
    void testDeleteNonExistingAuthor() {
        Long nonExistingAuthorId = Long.MAX_VALUE;
        Optional<Author> deletedAuthor = library.deleteAuthor(nonExistingAuthorId);

        assertFalse(deletedAuthor.isPresent());
    }

    @Test
    void testDeleteAuthorWithBooks() {
        assertThrows(IllegalArgumentException.class, () -> library.deleteAuthor(author1.id()));
    }

    @Test
    void testAddBook_InvalidBook() {
        assertThrows(IllegalArgumentException.class, () -> library.addBook(
                "", null,
                LocalDate.of(2021, 1, 1),
                Set.of("Adventure"),
                List.of(author1.id()),
                300,
                BigDecimal.valueOf(19.99))
        );
    }
}