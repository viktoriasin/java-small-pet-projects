package library;

import library.model.Author;
import library.model.Book;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AuthorCategoryCollector implements Collector<Book,  Map<Author, Set<String>>, Map<Author, Set<String>>> {
    @Override
    public Supplier<Map<Author, Set<String>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Author, Set<String>>, Book> accumulator() {
        return (m, book) -> book.authors().contains();
    }

    @Override
    public BinaryOperator<?> combiner() {
        return null;
    }

    @Override
    public Function<?, Map<Author, Set<String>>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
