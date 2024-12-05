package skip_list_set;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkipListSetTest {
    private SkipListSet<Integer> skipListSet;

    @BeforeEach
    void setUp() {
        skipListSet = new SkipListSet<>();
    }

    @Test
    void testAdd() {
        assertTrue(skipListSet.add(10));
        assertFalse(skipListSet.add(10));
        assertTrue(skipListSet.add(20));
    }

    @Test
    void testRemove() {
        skipListSet.add(10);
        skipListSet.add(20);
        assertTrue(skipListSet.remove(10));
        assertFalse(skipListSet.remove(30));
    }

    @Test
    void testSize() {
        assertEquals(0, skipListSet.size());
        skipListSet.add(10);
        skipListSet.add(20);
        assertEquals(2, skipListSet.size());
        skipListSet.remove(10);
        assertEquals(1, skipListSet.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(skipListSet.isEmpty());
        skipListSet.add(10);
        assertFalse(skipListSet.isEmpty());
    }

    @Test
    void testContains() {
        skipListSet.add(10);
        assertTrue(skipListSet.contains(10));
        assertFalse(skipListSet.contains(20));
    }

    @Test
    void testClear() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.clear();
        assertTrue(skipListSet.isEmpty());
    }

    @Test
    void testComparator() {
        Comparator<? super Integer> comparator = skipListSet.comparator();
        assertNull(comparator);
    }

    @Test
    void testFirst() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(5, skipListSet.first());
    }

    @Test
    void testPollFirst() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(5, skipListSet.pollFirst());
        assertEquals(10, skipListSet.first());
    }

    @Test
    void testLast() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(20, skipListSet.last());
    }

    @Test
    void testPollLast() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(20, skipListSet.pollLast());
        assertEquals(10, skipListSet.last());
    }

    @Test
    void testLower() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(10, skipListSet.lower(15));
        assertNull(skipListSet.lower(5));
    }

    @Test
    void testFloor() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(10, skipListSet.floor(15));
        assertEquals(5, skipListSet.floor(5));
        assertNull(skipListSet.floor(1));
    }

    @Test
    void testCeiling() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(10, skipListSet.ceiling(10));
        assertEquals(20, skipListSet.ceiling(15));
        assertNull(skipListSet.ceiling(25));
    }

    @Test
    void testHigher() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        assertEquals(20, skipListSet.higher(15));
        assertNull(skipListSet.higher(20));
    }

    @Test
    void testIterator() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        Iterator<Integer> iterator = skipListSet.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.next());
        assertEquals(10, iterator.next());
        assertEquals(20, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testDescendingIterator() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();
        assertTrue(descendingIterator.hasNext());
        assertEquals(20, descendingIterator.next());
        assertEquals(10, descendingIterator.next());
        assertEquals(5, descendingIterator.next());
        assertFalse(descendingIterator.hasNext());
    }

    @Test
    void testDescendingSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        NavigableSet<Integer> descendingSet = skipListSet.descendingSet();
        assertEquals(20, descendingSet.first());
        assertEquals(5, descendingSet.last());
    }

    @Test
    void testSubSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        NavigableSet<Integer> subSet = skipListSet.subSet(5, true, 20, false);
        assertTrue(subSet.contains(5));
        assertTrue(subSet.contains(10));
        assertFalse(subSet.contains(20));
    }

    @Test
    void testHeadSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        NavigableSet<Integer> headSet = skipListSet.headSet(20, true);
        assertTrue(headSet.contains(5));
        assertTrue(headSet.contains(10));
        assertTrue(headSet.contains(20));
    }

    @Test
    void testTailSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(5);
        NavigableSet<Integer> tailSet = skipListSet.tailSet(10, true);
        assertTrue(tailSet.contains(10));
        assertTrue(tailSet.contains(20));
        assertFalse(tailSet.contains(5));
    }

    @Test
    void testEmptySet() {
        assertTrue(skipListSet.isEmpty());
        assertEquals(0, skipListSet.size());
        assertNull(skipListSet.pollFirst());
        assertNull(skipListSet.pollLast());
        assertFalse(skipListSet.contains(10));
        assertNull(skipListSet.lower(10));
        assertNull(skipListSet.floor(10));
        assertNull(skipListSet.ceiling(10));
        assertNull(skipListSet.higher(10));
    }

    @Test
    void testRepeatedAddAndRemove() {
        skipListSet.add(10);
        skipListSet.add(20);
        assertEquals(2, skipListSet.size());

        assertFalse(skipListSet.add(10));
        assertFalse(skipListSet.add(20));

        assertTrue(skipListSet.remove(10));
        assertEquals(1, skipListSet.size());
        assertFalse(skipListSet.remove(30));
    }

    @Test
    void testAddNull() {
        assertThrows(NullPointerException.class, () -> skipListSet.add(null));
    }

    @Test
    void testSubSetModification() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);

        NavigableSet<Integer> subSet = skipListSet.subSet(10, true, 30, false);
        assertTrue(subSet.contains(10));
        assertTrue(subSet.contains(20));
        assertFalse(subSet.contains(30));

        subSet.add(25);
        assertTrue(skipListSet.contains(25));
        assertTrue(subSet.contains(25));

        subSet.remove(20);
        assertFalse(skipListSet.contains(20));
    }

    @Test
    void testHeadSetModification() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> headSet = skipListSet.headSet(25, true);
        assertTrue(headSet.contains(10));
        assertTrue(headSet.contains(20));
        assertFalse(headSet.contains(30));

        headSet.add(5);
        assertTrue(skipListSet.contains(5));
        assertTrue(headSet.contains(5));

        headSet.remove(10);
        assertFalse(skipListSet.contains(10));
    }

    @Test
    void testTailSetModification() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> tailSet = skipListSet.tailSet(15, true);
        assertTrue(tailSet.contains(20));
        assertTrue(tailSet.contains(30));
        assertFalse(tailSet.contains(10));

        tailSet.add(25);
        assertTrue(skipListSet.contains(25));
        assertTrue(tailSet.contains(25));

        tailSet.remove(20);
        assertFalse(skipListSet.contains(20));
    }

    @Test
    void testDifferentRangeQueries() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);

        NavigableSet<Integer> subSet = skipListSet.subSet(10, true, 30, false);
        assertTrue(subSet.contains(10));
        assertTrue(subSet.contains(20));
        assertFalse(subSet.contains(30));

        NavigableSet<Integer> headSet = skipListSet.headSet(30, true);
        assertTrue(headSet.contains(10));
        assertTrue(headSet.contains(20));
        assertTrue(headSet.contains(30));

        NavigableSet<Integer> tailSet = skipListSet.tailSet(20, false);
        assertFalse(tailSet.contains(20));
        assertTrue(tailSet.contains(30));
        assertTrue(tailSet.contains(40));
    }

    @Test
    void testEmptySubSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> subSet = skipListSet.subSet(40, false, 50, false);
        assertTrue(subSet.isEmpty());
    }


    @Test
    void testDescendingSetModification() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> descendingSet = skipListSet.descendingSet();
        assertEquals(30, descendingSet.first());
        assertEquals(10, descendingSet.last());

        descendingSet.add(25);
        assertTrue(skipListSet.contains(25));

        descendingSet.remove(20);
        assertFalse(skipListSet.contains(20));
    }

    @Test
    void testLargeValues() {
        skipListSet.add(Integer.MAX_VALUE);
        skipListSet.add(Integer.MIN_VALUE);
        skipListSet.add(0);

        assertEquals(Integer.MIN_VALUE, skipListSet.first());
        assertEquals(Integer.MAX_VALUE, skipListSet.last());

        skipListSet.add(Integer.MAX_VALUE - 1);
        assertEquals(Integer.MAX_VALUE, skipListSet.pollLast());
    }

    @Test
    void testConstructorEmptySet() {
        SkipListSet<Integer> emptySet = new SkipListSet<>();
        assertTrue(emptySet.isEmpty());
        assertEquals(0, emptySet.size());
    }


    @Test
    void testLargeDataSet() {
        SkipListSet<Integer> largeSet = new SkipListSet<>();
        for (int i = 0; i < 10000; i++) {
            largeSet.add(i);
        }
        assertEquals(10000, largeSet.size());
        assertTrue(largeSet.contains(9999));
        assertEquals(0, largeSet.first());
        assertEquals(9999, largeSet.last());
    }

    @Test
    void testEmptySubSetV2() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> emptySubSet = skipListSet.subSet(40, true, 50, true);
        assertTrue(emptySubSet.isEmpty());
    }

    @Test
    void testHeadSetEmpty() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> headSetEmpty = skipListSet.headSet(5, true);
        assertTrue(headSetEmpty.isEmpty());
    }

    @Test
    void testTailSetEmpty() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> tailSetEmpty = skipListSet.tailSet(40, true);
        assertTrue(tailSetEmpty.isEmpty());
    }

    static class Person implements Comparable<Person> {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compareTo(Person other) {
            return Integer.compare(this.age, other.age);
        }

        @Override
        public String toString() {
            return name + ": " + age;
        }
    }

    @Test
    void testCustomObject() {
        SkipListSet<Person> personSet = new SkipListSet<>();
        Person p1 = new Person("Alice", 30);
        Person p2 = new Person("Bob", 25);
        Person p3 = new Person("Charlie", 35);

        personSet.add(p1);
        personSet.add(p2);
        personSet.add(p3);

        assertEquals(3, personSet.size());
        assertEquals(p2, personSet.first());
        assertEquals(p3, personSet.last());
    }

    @Test
    void testDescendingSetV2() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);

        NavigableSet<Integer> descendingSet = skipListSet.descendingSet();
        Iterator<Integer> descendingIterator = descendingSet.iterator();

        assertEquals(40, descendingIterator.next());
        assertEquals(30, descendingIterator.next());
        assertEquals(20, descendingIterator.next());
        assertEquals(10, descendingIterator.next());
    }

    @Test
    void testDescendingSetModification_1() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> descendingSet = skipListSet.descendingSet();
        descendingSet.add(40);

        assertTrue(skipListSet.contains(40));
    }

    @Test
    void testSubSetChangeThroughOriginalSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> subSet = skipListSet.subSet(10, true, 30, false);
        subSet.remove(10);

        assertFalse(skipListSet.contains(10));
    }

    @Test
    void testSubSetChangeThroughSubSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        NavigableSet<Integer> subSet = skipListSet.subSet(10, true, 30, false);
        subSet.add(25);

        assertTrue(skipListSet.contains(25));
    }

    @Test
    void testDuplicateAddAndRemove() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        assertFalse(skipListSet.add(10));
        assertFalse(skipListSet.add(20));
        assertFalse(skipListSet.add(30));

        assertTrue(skipListSet.remove(10));
        assertFalse(skipListSet.remove(10));
        assertTrue(skipListSet.remove(20));
        assertFalse(skipListSet.remove(20));
    }

    @Test
    void testBoundaryValues() {
        skipListSet.add(Integer.MIN_VALUE);
        skipListSet.add(Integer.MAX_VALUE);

        assertEquals(Integer.MIN_VALUE, skipListSet.first());
        assertEquals(Integer.MAX_VALUE, skipListSet.last());
    }

    @Test
    void testSubSetV2() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        SortedSet<Integer> subSet = skipListSet.subSet(20, 40);

        assertEquals(2, subSet.size());
        assertTrue(subSet.contains(20));
        assertTrue(subSet.contains(30));
        assertFalse(subSet.contains(40));

        SortedSet<Integer> emptySubSet = skipListSet.subSet(60, 70);
        assertTrue(emptySubSet.isEmpty());

        SortedSet<Integer> singleElementSubSet = skipListSet.subSet(30, 31);
        assertEquals(1, singleElementSubSet.size());
        assertTrue(singleElementSubSet.contains(30));
    }

    @Test
    void testHeadSetV2() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        SortedSet<Integer> headSet = skipListSet.headSet(30);

        assertEquals(2, headSet.size());
        assertTrue(headSet.contains(10));
        assertTrue(headSet.contains(20));
        assertFalse(headSet.contains(30));

        SortedSet<Integer> emptyHeadSet = skipListSet.headSet(10);
        assertTrue(emptyHeadSet.isEmpty());
    }

    @Test
    void testTailSetV2() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        SortedSet<Integer> tailSet = skipListSet.tailSet(30);

        assertEquals(3, tailSet.size());
        assertTrue(tailSet.contains(30));
        assertTrue(tailSet.contains(40));
        assertTrue(tailSet.contains(50));

        SortedSet<Integer> emptyTailSet = skipListSet.tailSet(60);
        assertTrue(emptyTailSet.isEmpty());
    }

    @Test
    void testSetModificationWithSubSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        SortedSet<Integer> subSet = skipListSet.subSet(20, 40);

        subSet.add(35);

        assertTrue(skipListSet.contains(35));
        assertTrue(subSet.contains(35));

        subSet.remove(30);

        assertFalse(skipListSet.contains(30));
        assertFalse(subSet.contains(30));
    }

    @Test
    void testEmptySubSetHeadSetTailSet() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);

        SortedSet<Integer> emptySubSet = skipListSet.subSet(40, 50);
        assertTrue(emptySubSet.isEmpty());

        SortedSet<Integer> emptyHeadSet = skipListSet.headSet(10);
        assertTrue(emptyHeadSet.isEmpty());

        SortedSet<Integer> emptyTailSet = skipListSet.tailSet(40);
        assertTrue(emptyTailSet.isEmpty());
    }


    @Test
    void testSubSetHeadSetTailSetWithModification() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        SortedSet<Integer> subSet = skipListSet.subSet(20, 40);
        subSet.add(25);
        assertTrue(skipListSet.contains(25));


        SortedSet<Integer> headSet = skipListSet.headSet(30);
        headSet.remove(10);
        assertFalse(skipListSet.contains(10));

        SortedSet<Integer> tailSet = skipListSet.tailSet(30);
        tailSet.add(35);
        assertTrue(skipListSet.contains(35));
    }

    @Test
    void testSubSetWithLargeData() {
        for (int i = 0; i < 1000; i++) {
            skipListSet.add(i);
        }

        SortedSet<Integer> subSet = skipListSet.subSet(100, 900);
        assertEquals(800, subSet.size());

        SortedSet<Integer> headSet = skipListSet.headSet(500);
        assertEquals(500, headSet.size());

        SortedSet<Integer> tailSet = skipListSet.tailSet(500);
        assertEquals(500, tailSet.size());
    }

    @Test
    void testDescendingIteratorOrder() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();

        assertTrue(descendingIterator.hasNext());
        assertEquals(50, descendingIterator.next());
        assertEquals(40, descendingIterator.next());
        assertEquals(30, descendingIterator.next());
        assertEquals(20, descendingIterator.next());
        assertEquals(10, descendingIterator.next());
        assertFalse(descendingIterator.hasNext());
    }

    @Test
    void testDescendingIteratorEmptySet() {
        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();
        assertFalse(descendingIterator.hasNext());
    }

    @Test
    void testDescendingIteratorSingleElement() {
        skipListSet.add(10);

        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();
        assertTrue(descendingIterator.hasNext());
        assertEquals(10, descendingIterator.next());
        assertFalse(descendingIterator.hasNext());
    }

    @Test
    void testDescendingIteratorRemove() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();
        assertEquals(50, descendingIterator.next());
        descendingIterator.remove();
        assertFalse(skipListSet.contains(50));


        assertEquals(40, descendingIterator.next());
        assertEquals(30, descendingIterator.next());


        descendingIterator.remove();
        assertFalse(skipListSet.contains(30));
    }

    @Test
    void testDescendingIteratorWithModification() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();
        assertEquals(50, descendingIterator.next());
        skipListSet.remove(50);
        assertEquals(40, descendingIterator.next());

        skipListSet.remove(40);
        assertEquals(30, descendingIterator.next());
    }

    @Test
    void testDescendingIteratorAfterRegularIterator() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        Iterator<Integer> iterator = skipListSet.iterator();
        iterator.next();
        iterator.next();
        iterator.remove();

        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();
        assertEquals(50, descendingIterator.next());
        assertEquals(40, descendingIterator.next());
        assertEquals(30, descendingIterator.next());
        assertEquals(10, descendingIterator.next());
        assertFalse(descendingIterator.hasNext());
    }

    @Test
    void testDescendingIteratorWithAddRemove() {
        skipListSet.add(10);
        skipListSet.add(20);
        skipListSet.add(30);
        skipListSet.add(40);
        skipListSet.add(50);

        Iterator<Integer> descendingIterator = skipListSet.descendingIterator();

        assertEquals(50, descendingIterator.next());
        skipListSet.add(60);
        assertEquals(40, descendingIterator.next());
        skipListSet.remove(40);
        assertEquals(30, descendingIterator.next());
    }
}

