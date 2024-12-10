package library;

import library.model.Author;
import library.model.Book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

public interface Library {

    /**
     * Finds a book by its ISBN.
     *
     * @param isbn the ISBN of the book to find
     * @return the found Book, or empty if no book is found
     */
    Optional<Book> findBookByIsbn(String isbn);

    /**
     * Retrieves a set of books whose titles contain the specified substring,
     * ignoring case differences.
     *
     * <p>This method performs a case-insensitive search on the titles of books
     * in the collection. It returns a set of books where each book's title contains
     * the specified substring.</p>
     *
     * @param title the substring to search for within each book's title
     * @return a set of books with titles that contain the specified substring,
     *         ignoring case; an empty set if no such books are found
     */
    Set<Book> getBooksByTitleLikeIgnoreCase(String title);

    /**
     * Retrieves all books by a specific author, sorted by release date in descending order.
     *
     * @param authorId the ID of the author
     * @return a list of books sorted by release date in descending order
     */
    List<Book> getBooksByAuthorSortedByReleaseDateDesc(long authorId);

    /**
     * Retrieves all books in the library, sorted by release date in descending order.
     *
     * @return a list of all books sorted by release date in descending order
     */
    List<Book> getAllBooksSortedByReleaseDateDesc();

    /**
     * Finds the most expensive book in a given category.
     *
     * @param category the category to search for the most expensive book
     * @return the most expensive Book, or empty if no books are found in the category
     */
    Optional<Book> findMostExpensiveBookInCategory(String category);

    /**
     * Retrieves all authors
     *
     * @return a set of all authors
     */
    Set<Author> getAllAuthors();

    /**
     * Finds authors who have books in multiple categories.
     *
     * @param categoryCountThreshold the minimum number of categories an author must have books in
     * @return a set of authors who meet the criteria
     */
    Set<Author> findAuthorsWithBooksInMultipleCategories(int categoryCountThreshold);

    /**
     * Calculates the average number of pages for books written by a specific author.
     *
     * @param authorId the ID of the author
     * @return the average number of pages, or empty if no books are present
     */
    OptionalDouble calculateAveragePagesForAuthor(long authorId);

    /**
     * Calculates the total price of books by category.
     *
     * @return a Map where the key is the category name and the value is the total price of books in that category
     */
    Map<String, BigDecimal> calculateTotalPriceOfBooksByCategory();

    /**
     * Retrieves all books released in a specific year.
     *
     * @param releaseYear the year of release for the books
     * @return a list of books released in the specified year
     */
    List<Book> getBooksByReleaseYear(Year releaseYear);

    /**
     * Finds one of the categories with the most number of books.
     *
     * @return the name of the category with the highest number of books,
     * or empty if no books are present across any category.
     */
    Optional<String> findMostPopulatedCategory();

    // *** Add methods ***

    /**
     * Adds a new book to the library and returns it or just returns
     * if the book already exists
     *
     * @param isbn          the ISBN of the book, unique across all books
     * @param title         the title of the book
     * @param releaseDate   the release date of the book
     * @param categoryNames the categories the book belongs to
     * @param authorIds     the list of author IDs for the book
     * @param pages         the number of pages in the book
     * @param price         the price of the book
     * @return the added Book or existent one
     * @throws IllegalArgumentException if a book with the requested {@code isbn}
     *                                  exists but some other fields differ
     */
    Book addBook(String isbn, String title, LocalDate releaseDate, Set<String> categoryNames,
                 List<Long> authorIds, int pages, BigDecimal price) throws IllegalArgumentException;

    /**
     * Adds a new author to the library or just returns if the author already exists
     *
     * @param firstName the first name of the author
     * @param lastName  the last name of the author
     * @param birthDate the birthdate of the author
     * @return the added Author with generated unique id or
     */
    Author addAuthor(String firstName, String lastName, LocalDate birthDate);

    // *** Delete methods ***

    /**
     * Deletes a book by its ISBN.
     *
     * @param isbn the ISBN of the book to delete
     * @return the deleted Book or empty if no book is found
     */
    Optional<Book> deleteBook(String isbn);

    /**
     * Deletes an author by their ID.
     *
     * @param authorId the ID of the author to delete
     * @return the deleted Author or empty if no author is found
     * @throws IllegalArgumentException if there is a book related to the author with {@code authorId}
     */
    Optional<Author> deleteAuthor(long authorId) throws IllegalArgumentException;
}