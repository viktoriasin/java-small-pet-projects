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
    private final Set<Book> books = new HashSet<>();
    private final Set<Author> authors = new HashSet<>();
    private final int threshold;
    private final Map<String, Integer> methodsToCallsCount = new HashMap<>();

    private Map<String, Book> bookByIsbn;
    private Map<String, Set<Book>> bookByTitle;
    private Set<Book> setOfBookSortedByReleaseDay;
    private Map<String, Set<Book>> bookByCategory;
    private Map<Long, Set<Book>> bookByAuthor;
    private Map<Year, Set<Book>> bookByYear;


    public InMemoryLibrary(int threshold) {
        this.threshold = threshold;
        methodsToCallsCount.put("findBookByIsbn", 0);
        methodsToCallsCount.put("getBooksByTitleLikeIgnoreCase", 0);
        methodsToCallsCount.put("getBooksByReleaseYear", 0);
        methodsToCallsCount.put("getAllBooksSortedByReleaseDateDesc", 0);
        methodsToCallsCount.put("findMostExpensiveBookInCategory", 0);
        methodsToCallsCount.put("calculateTotalPriceOfBooksByCategory", 0);
        methodsToCallsCount.put("calculateAveragePagesForAuthor", 0);
        methodsToCallsCount.put("findMostPopulatedCategory", 0);
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        if (increaseMethodCallsAndCheckForThreshold("findBookByIsbn")) {
            if (bookByIsbn == null) {
                bookByIsbn = new HashMap<>();
                for (Book book : books) {
                    bookByIsbn.put(book.isbn(), book);
                }
            }
            if (bookByIsbn.containsKey(isbn)) {
                return Optional.of(bookByIsbn.get(isbn));
            } else {
                return Optional.empty();
            }
        }
        return books.stream()
                .filter(book -> book.isbn().equals(isbn))
                .findFirst();
    }

    @Override
    public Set<Book> getBooksByTitleLikeIgnoreCase(String title) {
        if (increaseMethodCallsAndCheckForThreshold("getBooksByTitleLikeIgnoreCase")) {
            String lowerCaseTitle = title.toLowerCase();
            if (bookByTitle == null) {
                bookByTitle = new HashMap<>();
                for (Book book : books) {
                    for (String wordFromTitle : book.title().toLowerCase().replaceAll("[^a-zA-Z]", "").split("\\s+")) {
                        bookByTitle.computeIfAbsent(wordFromTitle, k -> new HashSet<>()).add(book);
                    }
                }
            }

            Set<Book> foundBook = bookByTitle.getOrDefault(lowerCaseTitle, Collections.emptySet());
            if (!foundBook.isEmpty()) {
                return foundBook;
            }
        }
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
        if (increaseMethodCallsAndCheckForThreshold("getAllBooksSortedByReleaseDateDesc")) {
            if (setOfBookSortedByReleaseDay == null) {
                setOfBookSortedByReleaseDay = new TreeSet<>(Comparator.comparing(Book::releaseDate, Collections.reverseOrder()));
                setOfBookSortedByReleaseDay.addAll(books);
            }
            return setOfBookSortedByReleaseDay.stream().toList();
        }
        return books.stream()
                .sorted((book1, book2) -> book2.releaseDate().compareTo(book1.releaseDate()))
                .toList();
    }

    @Override
    public Optional<Book> findMostExpensiveBookInCategory(String category) {
        if (increaseMethodCallsAndCheckForThreshold("findMostExpensiveBookInCategory")) {
            if (bookByCategory == null) {
                bookByCategory = new HashMap<>();
                prepareBookByCategory();
            }
            if (bookByCategory.containsKey(category)) {
                Set<Book> booksByCategory = bookByCategory.get(category);
                return booksByCategory.stream().max(Comparator.comparing(Book::price));
            } else {
                return Optional.empty();
            }
        }
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
        if (increaseMethodCallsAndCheckForThreshold("calculateAveragePagesForAuthor")) {
            if (bookByAuthor == null) {
                bookByAuthor = new HashMap<>();
                for (Book book : books) {
                    for (Author author_ : book.authors()) {
                        bookByAuthor.computeIfAbsent(author_.id(), k -> new HashSet<>()).add(book);
                    }
                }
            }
            if (bookByAuthor.containsKey(authorId)) {
                return bookByAuthor.get(authorId).stream().mapToDouble(Book::pages)
                        .average();
            } else {
                return OptionalDouble.empty();
            }
        }
        return books.stream()
                .filter(book -> book.authors().stream().map(Author::id).anyMatch(id -> id == authorId))
                .mapToDouble(Book::pages)
                .average();
    }

    @Override
    public Map<String, BigDecimal> calculateTotalPriceOfBooksByCategory() {
        if (increaseMethodCallsAndCheckForThreshold("calculateTotalPriceOfBooksByCategory")) {
            if (bookByCategory == null) {
                bookByCategory = new HashMap<>();
                prepareBookByCategory();
            }
            return bookByCategory.entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                            .map(Book::price)
                            .reduce(BigDecimal::add)
                            .orElse(new BigDecimal(0))
                    ));
        }

        return books.stream()
                .map(book -> Pair.of(book.categories(), book.price()))
                .flatMap(pair -> pair.first().stream().map(category -> Pair.of(category, pair.second())))
                .collect(Collectors.toMap(Pair::first, Pair::second, BigDecimal::add));
    }

    @Override
    public List<Book> getBooksByReleaseYear(Year releaseYear) {
        if (increaseMethodCallsAndCheckForThreshold("getBooksByReleaseYear")) {
            if (bookByYear == null) {
                bookByYear = new HashMap<>();
                for (Book book : books) {
                    Year year = Year.of(book.releaseDate().getYear());
                    bookByYear.computeIfAbsent(year, k -> new HashSet<>()).add(book);
                }
            }
            return bookByYear.getOrDefault(releaseYear, Collections.emptySet()).stream().toList();
        }
        return books.stream()
                .filter(book -> book.releaseDate().getYear() == releaseYear.getValue())
                .toList();
    }

    @Override
    public Optional<String> findMostPopulatedCategory() {
        if (increaseMethodCallsAndCheckForThreshold("findMostPopulatedCategory")) {
            if (bookByCategory == null) {
                bookByCategory = new HashMap<>();
                prepareBookByCategory();
            }
            return bookByCategory.entrySet().stream()
                    .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                    .map(Map.Entry::getKey);
        }
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
                        .orElseThrow(IllegalArgumentException::new)
                )
                .toList();
        Book newBook = new Book(isbn, title, releaseDate, authorsNew, categoryNames, pages, price);
        if (books.contains(newBook)) {
            return null;
        }
        if (books.stream().anyMatch(book -> book.isbn().equals(isbn))) {
            throw new IllegalArgumentException("The book with this isbn has already exists in library.");
        }

        books.add(newBook);
        updateHelperStructures(newBook);

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
        if (foundBook.isPresent()) {
            deleteFromHelperStructures(foundBook.get());
            books.remove(foundBook.get());
        }
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

    private boolean increaseMethodCallsAndCheckForThreshold(String methodName) {
        if (methodsToCallsCount.get(methodName) > threshold) {
            return true;
        } else {
            methodsToCallsCount.put(methodName, methodsToCallsCount.get(methodName) + 1);
            return false;
        }
    }

    private void updateHelperStructures(Book newBook) {
        if (bookByIsbn != null) {
            bookByIsbn.put(newBook.isbn(), newBook);
        }
        if (bookByTitle != null) {
            for (String word : newBook.title().toLowerCase().replaceAll("[^a-zA-Z]", "").split("\\s+")) {
                bookByTitle.computeIfAbsent(word, k -> new HashSet<>()).add(newBook);
            }
        }
        if (setOfBookSortedByReleaseDay != null) {
            setOfBookSortedByReleaseDay.add(newBook);
        }
        if (bookByCategory != null) {
            for (String category : newBook.categories()) {
                bookByCategory.computeIfAbsent(category, k -> new HashSet<>()).add(newBook);
            }
        }
        if (bookByAuthor != null) {
            for (Author author : newBook.authors()) {
                bookByAuthor.computeIfAbsent(author.id(), k -> new HashSet<>()).add(newBook);
            }
        }
        if (bookByYear != null) {
            bookByYear.computeIfAbsent(Year.of(newBook.releaseDate().getYear()), k -> new HashSet<>()).add(newBook);
        }
    }

    private void deleteFromHelperStructures(Book bookToDelete) {
        if (bookByIsbn != null) {
            bookByIsbn.remove(bookToDelete.isbn());
        }
        if (bookByTitle != null) {
            bookByTitle.remove(bookToDelete.title());
            for (String wordFromTitle : bookToDelete.title().toLowerCase().replaceAll("[^a-zA-Z]", "").split("\\s+")) {
                bookByTitle.computeIfAbsent(wordFromTitle, k -> new HashSet<>()).remove(bookToDelete);
            }
        }
        if (setOfBookSortedByReleaseDay != null) {
            setOfBookSortedByReleaseDay.remove(bookToDelete);
        }
        if (bookByCategory != null) {
            for (String category : bookToDelete.categories()) {
                bookByCategory.computeIfAbsent(category, k -> Collections.emptySet()).remove(bookToDelete);
            }
        }
        if (bookByAuthor != null) {
            for (Author author : bookToDelete.authors()) {
                bookByAuthor.computeIfAbsent(author.id(), k -> Collections.emptySet()).remove(bookToDelete);
            }
        }
        if (bookByYear != null) {
            bookByYear.computeIfAbsent(Year.of(bookToDelete.releaseDate().getYear()), k -> Collections.emptySet()).remove(bookToDelete);
        }
    }

    private void prepareBookByCategory() {
        for (Book book : books) {
            for (String category_ : book.categories()) {
                bookByCategory.computeIfAbsent(category_, k -> new HashSet<>()).add(book);
            }
        }
    }
}
