package skip_list_set;

import java.util.*;

public class SkipListSet<T> extends AbstractSet<T> implements NavigableSet<T> {

    private final int MAX_LEVELS = 32;
    private final float PROP_NEXT_LEVEL = 0.25f;
    private Comparator<T> comparator;
    private final Random random = new Random();

    transient int size = 0;

    HeadNodeOfLevel<T> baseLevelHead;
    HeadNodeOfLevel<T> highestLevelHead;


    public SkipListSet() {
        baseLevelHead = new HeadNodeOfLevel<>(null, null);
        highestLevelHead = baseLevelHead;
    }

    public SkipListSet(Collection<T> values) {
        baseLevelHead = new HeadNodeOfLevel<>(null, null);
        highestLevelHead = baseLevelHead;
        this.addAll(values);
    }

    public SkipListSet(Comparator<T> comparator) {
        this.comparator = comparator;
        baseLevelHead = new HeadNodeOfLevel<>(null, null);
        highestLevelHead = baseLevelHead;
    }

    @Override
    public T lower(T t) {
        Node<T> prev = lowerNode(t, baseLevelHead);
        if (prev == null) {
            return null;
        }
        return prev.value;
    }

    @Override
    public T floor(T t) {

        Node<T> currNode = baseLevelHead.next;
        Node<T> prev = null;

        while (currNode != null && smartCompare(currNode.value, t) <= 0) {
            prev = currNode;
            currNode = currNode.next;
        }

        if (prev == null) {
            return null;
        }

        return prev.value;
    }

    @Override
    public T ceiling(T t) {
        Node<T> currNode = baseLevelHead.next;

        while (currNode != null && smartCompare(currNode.value, t) < 0) {
            currNode = currNode.next;
        }

        if (currNode == null) {
            return null;
        }

        return currNode.value;
    }

    @Override
    public T higher(T t) {
        Node<T> currNode = higherNode(t, baseLevelHead);

        while (currNode != null && smartCompare(currNode.value, t) <= 0) {
            currNode = currNode.next;
        }

        if (currNode == null) {
            return null;
        }

        return currNode.value;
    }

    public Node<T> higherNode(T t, HeadNodeOfLevel<T> currLevelHead) {
        Node<T> currNode = currLevelHead.next;

        while (currNode != null && smartCompare(currNode.value, t) <= 0) {
            currNode = currNode.next;
        }

        if (currNode == null) {
            return null;
        }

        return currNode;
    }

    @Override
    public T pollFirst() {
        if (isEmpty()) {
            return null;
        }
        T first = first();
        findNodeAndDoAction(first, true);
        return first;
    }

    @Override
    public T pollLast() {
        if (isEmpty()) {
            return null;
        }
        T last = last();
        findNodeAndDoAction(last, true);
        return last;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> currentNode = baseLevelHead;
            Node<T> prev = null;

            @Override
            public boolean hasNext() {
                return currentNode.next != null;
            }

            @Override
            public T next() {
                T value = currentNode.next.value;
                prev = currentNode;
                currentNode = currentNode.next;
                return value;
            }

            @Override
            public void remove() {
                findNodeAndDoAction(currentNode.value, true);
            }
        };
    }

    @Override
    public Iterator<T> descendingIterator() {
        Node<T> startOfReverseList = createReverseLinkedList(baseLevelHead.next);
        return new Iterator<T>() {
            Node<T> currentNode = new Node(null, null, startOfReverseList);
            final Node<T> head = currentNode;
            Node<T> prev;

            @Override
            public boolean hasNext() {
                return currentNode.next != null;
            }

            @Override
            public T next() {
                T valueToReturn = currentNode.next.value;
                prev = currentNode;
                currentNode = currentNode.next;
                return valueToReturn;
            }

            @Override
            public void remove() {
                if (prev == null) {
                    head.next = currentNode.next;
                } else {
                    prev.next = currentNode.next;
                }
            }
        };
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return null;
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        Node<T> start = findNode(fromElement);
        if (fromElement == null || toElement == null) {
            throw new NullPointerException("fromElement and toElement must not be null.");
        }
        if (smartCompare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement must be lower than toElement");
        }
        if (!fromInclusive) {
            if (start != null) {
                start = start.next;
            }
        }
        int resultTo = toInclusive ? 1 : 0;
        SkipListSet<T> newSkipList = new SkipListSet<>(this.comparator);
        while (start != null && smartCompare(start.value, toElement) < resultTo) {
            newSkipList.add(start.value);
            start = start.next;
        }
        return newSkipList;
    }

    private Node<T> findNode(T fromElement) {
        Node<T> targetNode = baseLevelHead.next;
        while (targetNode != null && smartCompare(targetNode.value, fromElement) != 0) {
            targetNode = targetNode.next;
        }
        return targetNode;
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        return subSet(baseLevelHead.next.value, true, toElement, inclusive);
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        return subSet(fromElement, inclusive, last(), true);
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, true);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public T first() {
        if (isEmpty()) {
            return null;
        }
        return baseLevelHead.next.value;
    }

    @Override
    public T last() {
        if (isEmpty()) {
            return null;
        }
        Node<T> current = baseLevelHead.next;
        Node<T> last = baseLevelHead.next.next;
        while (last != null) {
            current = last;
            last = current.next;
        }
        return current.value;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        boolean isAdded = addValue(t, baseLevelHead, null);
        if (isAdded) {
            size++;
        }
        return isAdded;
    }

    @Override
    public boolean contains(Object o) {
        T t = (T) o;
        return findNodeAndDoAction(t, false);
    }

    @Override
    public boolean remove(Object o) {
        T t = (T) o;
        return findNodeAndDoAction(t, true);
    }

    @Override
    public void clear() {
        baseLevelHead = null;
        highestLevelHead = null;
        size = 0;
    }

    @Override
    public String toString() {
        HeadNodeOfLevel<T> loopHead = highestLevelHead;
        StringBuilder stringBuilder = new StringBuilder();
        if (loopHead == null) {
            return stringBuilder.toString();
        }
        do {
            stringBuilder.append(levelToString(loopHead.next));
            stringBuilder.append("\n");
            loopHead = (HeadNodeOfLevel<T>) loopHead.lower;
        } while (loopHead != null);
        return stringBuilder.toString();
    }

    private boolean addValue(T t, HeadNodeOfLevel<T> currentLevelHead, Node<T> nodeFromLowerLevelToLinkWith) {
        // Проверка, чтобы не добавить уже существующий элемент
        if (currentLevelHead.equals(baseLevelHead) && contains(t)) {
            return false;
        }

        Node<T> currentNode = currentLevelHead.next;
        Node<T> newNode;
        // Если уровень пустой
        if (currentNode == null) {
            newNode = new Node<>(t, nodeFromLowerLevelToLinkWith);
            currentLevelHead.next = newNode;
            tryIncreaseToUpperLevel(newNode, currentLevelHead);
            return true;
        }

        Node<T> next = currentNode.next;
        T currentNodeValue = currentNode.value;
        Node<T> prev = null;

        while (smartCompare(currentNodeValue, t) <= 0) {
            if (smartCompare(currentNodeValue, t) == 0) {
                return false;
            }
            if (next != null) {
                prev = currentNode;
                currentNode = next;
                currentNodeValue = currentNode.value;
                next = next.next;
            } else {
                // достигли конца списка, нужно вставить в конец
                newNode = new Node<>(t, nodeFromLowerLevelToLinkWith);
                currentNode.next = newNode;
                tryIncreaseToUpperLevel(newNode, currentLevelHead);
                return true;
            }
        }

        // сюда заходим, если уже с 1 элемента на уровне все элементы больше или в середине списка нашли больше
        newNode = new Node<>(t, nodeFromLowerLevelToLinkWith);
        if (prev != null) {
            prev.next = newNode;
            newNode.next = currentNode;
        } else {
            currentLevelHead.next = newNode;
            newNode.next = currentNode;
        }

        tryIncreaseToUpperLevel(newNode, currentLevelHead);
        return true;
    }

    private void tryIncreaseToUpperLevel(Node<T> node, HeadNodeOfLevel<T> headOfLowerLevel) {
        if (random.nextDouble() <= PROP_NEXT_LEVEL) {
            if (headOfLowerLevel.getLevelNumber() >= MAX_LEVELS) {
                return;
            }
            increaseToNextLevel(node, headOfLowerLevel);
        }
    }

    private void increaseToNextLevel(Node<T> newNode, HeadNodeOfLevel<T> headOfLowerLevel) {
        HeadNodeOfLevel<T> headOfUpperLevel = findHeadOfLevelAbove(headOfLowerLevel);
        if (headOfUpperLevel == null) {
            highestLevelHead = new HeadNodeOfLevel<>(null, headOfLowerLevel);
            addValue(newNode.value, highestLevelHead, newNode);
        } else {
            addValue(newNode.value, headOfUpperLevel, newNode);
        }
    }

    private HeadNodeOfLevel<T> findHeadOfLevelAbove(HeadNodeOfLevel<T> headOfLowerLevel) {
        HeadNodeOfLevel<T> headOfLevelAbove = highestLevelHead;
        // Если уровень выше еще не создан
        if (headOfLevelAbove == headOfLowerLevel) {
            return null;
        }
        HeadNodeOfLevel<T> loopLowerLevelHead = (HeadNodeOfLevel<T>) highestLevelHead.lower;
//        if (loopLowerLevelHead == null) {
//            return null;
//        }
        while (loopLowerLevelHead != null && !loopLowerLevelHead.equals(headOfLowerLevel)) {
            headOfLevelAbove = loopLowerLevelHead;
            loopLowerLevelHead = (HeadNodeOfLevel<T>) loopLowerLevelHead.lower;
        }
        return headOfLevelAbove;
    }

    private boolean findNodeAndDoAction(T t, boolean delete) {
        if (isEmpty()) {
            return false;
        }

        if (t == null) {
            return false;
        }

        boolean isContain = false;
        HeadNodeOfLevel<T> currLevelHead = highestLevelHead;
        Node<T> targetNode = searchNodeOnLevel(t, currLevelHead.next);

        do {
            // на этом уровне нашли нужное значение
            if (targetNode != null && t.equals(targetNode.value)) {
                isContain = true;
                if (delete) {
                    deleteOnLevel(t, currLevelHead);
                    if (currLevelHead.equals(baseLevelHead)) {
                        size--;
                    }
                    // Если после удаления элемента уровень оказался пустой - то нужно удалить уровень весь
                    if (currLevelHead.next == null) {
                        highestLevelHead = (HeadNodeOfLevel<T>) currLevelHead.lower;
                    }

                    if (targetNode.lower == null) { // если мы на 1 уровне
                        return true;
                    }

                    currLevelHead = (HeadNodeOfLevel<T>) currLevelHead.lower;
                    targetNode = searchNodeOnLevel(t, targetNode.lower);
                    targetNode = targetNode;
                } else {
                    return true; // нашли на этом уровне
                }
            } else if (!t.equals(targetNode) && targetNode != null) {
                currLevelHead = (HeadNodeOfLevel<T>) currLevelHead.lower;
                if (targetNode.lower == null) { // если мы на 1 уровне
                    return false;
                }
                targetNode = searchNodeOnLevel(t, targetNode.lower);
                targetNode = targetNode;
            } else if (targetNode == null) { // ситуация, когда первый элемент уже больше и надо начать с первого элемента нижнего уровня
                currLevelHead = (HeadNodeOfLevel<T>) currLevelHead.lower;
                if (currLevelHead == null) {
                    return false;
                }
                targetNode = searchNodeOnLevel(t, currLevelHead.next);
                targetNode = targetNode;
            }
        } while (currLevelHead != null);

        return isContain;
    }

    private void deleteOnLevel(T t, HeadNodeOfLevel<T> currLevelHead) {
        Node<T> before = lowerNode(t, currLevelHead);
        Node<T> upper = higherNode(t, currLevelHead);
        if (before == null) {
            currLevelHead.next = upper;
        } else {
            before.next = upper;
        }
    }

    private Node<T> lowerNode(T t, Node<T> currLevelHead) {
        Node<T> currNode = currLevelHead.next;
        Node<T> prev = null;
        while (currNode != null && smartCompare(currNode.value, t) < 0) {
            prev = currNode;
            currNode = currNode.next;
        }
        return prev;
    }

    private Node<T> searchNodeOnLevel(T valueToFind, Node<T> nodeToStartSearchWith) {
        // Возвращает либо ноду, по которой надо спуститься вниз
        // Либо null, если нода, с которой мы начинаем == null
        // Либо null если первая же нода в поиске больше искомого и на более низком уровне нам надо начинать поиск с самой первой ноды
        if (nodeToStartSearchWith == null) {
            return null; // если достигли конца списка (спускаться вниз больше нельзя)
        }

        Node<T> currentNode = nodeToStartSearchWith;
        Node<T> next = currentNode.next;
        T currValue = currentNode.value;
        Node<T> prev = null;

        while (smartCompare(currValue, valueToFind) <= 0) {
            if (smartCompare(currValue, valueToFind) == 0) {
                return currentNode;
            }
            if (next != null) {
                prev = currentNode;
                currentNode = next;
                next = next.next;
                currValue = currentNode.value;
            } else {
                return currentNode; // достигли конца списка
            }
        }

        return prev; // если остановились на ноде, которая больше искомой
    }

    private int smartCompare(T t1, T t2) {
        if (comparator == null) {
            if (t1 instanceof Comparable t1Comparable) {
                return t1Comparable.compareTo(t2);
            } else {
                throw new ClassCastException();
            }
        } else {
            return comparator.compare(t1, t2);
        }
    }

    private String levelToString(Node<T> loopHead) {
        String result = "NULL --> ";
        if (loopHead == null) {
            return result + "NUll";
        }
        while (loopHead != null) {
            result += loopHead.value + " --> ";
            loopHead = loopHead.next;
        }
        return result + "NUll";
    }

    private Node<T> createReverseLinkedList(Node<T> head) {
        if (head == null) {
            return null;
        }

        if (head.next == null) {
            return new Node<T>(head.value, null);
        }

        Node<T> next = head.next;

        Node<T> newHead = new Node<T>(head.value, null);
        Node<T> newPrev = null;

        while (head != null) {

            newHead.next = newPrev;
            head = next;
            newPrev = newHead;
            if (next != null) {
                newHead = new Node<T>(head.value, null);
                next = next.next;
            }
        }

        return newPrev;
    }

    private class Node<T> {
        final T value;
        final Node<T> lower;
        Node<T> next;

        public Node(T value, Node<T> lower) {
            this.value = value;
            this.lower = lower;
        }

        public Node(T value, Node<T> lower, Node<T> next) {
            this.value = value;
            this.lower = lower;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", next=" + next +
                    '}';
        }
    }

    private class HeadNodeOfLevel<T> extends Node<T> {
        public static int totalLevelNumber = 1;
        public final int levelNumber = totalLevelNumber++;

        public int getLevelNumber() {
            return levelNumber;
        }

        public HeadNodeOfLevel(T value, Node<T> lower) {
            super(value, lower);
        }
    }
}