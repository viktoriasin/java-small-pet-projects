package library;

import library.model.Author;
import library.model.Book;
import library.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

public class InMemoryLibrary implements Library {
    private final List<Book> books = new ArrayList<>();
    private final List<Author> authors = new ArrayList<>();
    private final int threshold;

    public InMemoryLibrary(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.isbn().equals(isbn))
                .findFirst();
    }

    @Override
    public Set<Book> getBooksByTitleLikeIgnoreCase(String title) {
        return books.stream()
                .filter(book -> book.title().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Book> getBooksByAuthorSortedByReleaseDateDesc(long authorId) {
        return books.stream()
                .filter(book -> book.authors().stream().anyMatch(author -> author.id() == authorId))
                .sorted((book1, book2) -> book2.releaseDate().compareTo(book1.releaseDate()))
                .toList();

    }

    @Override
    public List<Book> getAllBooksSortedByReleaseDateDesc() {
        return books.stream()
                .sorted((book1, book2) -> book2.releaseDate().compareTo(book1.releaseDate()))
                .toList();
    }

    @Override
    public Optional<Book> findMostExpensiveBookInCategory(String category) {
        return books.stream()
                .filter(book -> book.categories().stream().anyMatch(thisCategory -> thisCategory.equals(category)))
                .max(Comparator.comparing(Book::price));
    }

    @Override
    public Set<Author> getAllAuthors() {
        return books.stream()
                .flatMap(book -> book.authors().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Author> findAuthorsWithBooksInMultipleCategories(int categoryCountThreshold) {
        return books.stream()
                .map(book -> Pair.of(book.authors(), book.categories()))
                .flatMap(pair -> pair.first().stream().map(author -> Pair.of(author, new HashSet<>(pair.second()))))
                .collect(Collectors.toMap(Pair::first, Pair::second, (Set<String> a, Set<String> b) -> {
                    a.addAll(b);
                    return a;
                }))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > categoryCountThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public OptionalDouble calculateAveragePagesForAuthor(long authorId) {
        return books.stream()
                .filter(book -> book.authors().stream().map(Author::id).anyMatch(id -> id == authorId))
                .mapToDouble(Book::pages)
                .average();
    }

    @Override
    public Map<String, BigDecimal> calculateTotalPriceOfBooksByCategory() {
        return books.stream()
                .map(book -> Pair.of(book.categories(), book.price()))
                .flatMap(pair -> pair.first().stream().map(category -> Pair.of(category, pair.second())))
                .collect(Collectors.toMap(Pair::first, Pair::second, BigDecimal::add));
    }

    @Override
    public List<Book> getBooksByReleaseYear(Year releaseYear) {
        return books.stream()
                .filter(book -> book.releaseDate().getYear() == releaseYear.getValue())
                .toList();
    }

    @Override
    public Optional<String> findMostPopulatedCategory() {
        return books.stream()
                .flatMap(book -> book.categories().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    @Override
    public Book addBook(String isbn, String title, LocalDate releaseDate, Set<String> categoryNames, List<Long> authorIds, int pages, BigDecimal price) throws IllegalArgumentException {
        if (isbn.isEmpty() || title.isEmpty() || releaseDate == null || price == null || categoryNames.isEmpty() || authorIds.isEmpty()) {
            throw new IllegalArgumentException("Book attributes is invalid.");
        }
        List<Author> authorsNew = authorIds.stream()
                .map(aLong -> authors.stream()
                        .filter(author -> author.id() == aLong)
                        .findFirst()
                        .orElseGet(() -> new Author(aLong, null, null, null)))
                .toList();
        Book newBook = new Book(isbn, title, releaseDate, authorsNew, categoryNames, pages, price);
        if (books.contains(newBook)) {
            return null;
        }
        if (books.stream().anyMatch(book -> book.isbn().equals(isbn))) {
            throw new IllegalArgumentException("The book with this isbn has already exists in library.");
        }
        books.add(newBook);
        return newBook;
    }

    @Override
    public Author addAuthor(String firstName, String lastName, LocalDate birthDate) {
        Optional<Author> authorOptional = authors.stream()
                .filter(author -> author.firstName().equals(firstName)
                        && author.lastName().equals(lastName)
                        && author.birthDate().equals(birthDate))
                .findAny();
        if (authorOptional.isPresent()) {
            return authorOptional.get();
        }
        Author newAuthor = new Author(randomUUID().getMostSignificantBits() & Long.MAX_VALUE, firstName, lastName, birthDate);

        authors.add(newAuthor);
        return newAuthor;
    }

    @Override
    public Optional<Book> deleteBook(String isbn) {
        Optional<Book> foundBook = books.stream().filter(book -> book.isbn().equals(isbn)).findFirst();
        foundBook.ifPresent(x -> books.remove(foundBook.get()));
        return foundBook;
    }

    @Override
    public Optional<Author> deleteAuthor(long authorId) throws IllegalArgumentException {
        Optional<Author> foundAuthor = authors.stream().filter(author -> author.id() == authorId).findFirst();
        if (foundAuthor.isPresent()) {
            if (books.stream().anyMatch(book -> book.authors().contains(foundAuthor.get()))) {
                throw new IllegalArgumentException("You can not delete the author with books in library.");
            }
        }
        foundAuthor.ifPresent(x -> authors.remove(foundAuthor.get()));
        return foundAuthor;
    }
}
