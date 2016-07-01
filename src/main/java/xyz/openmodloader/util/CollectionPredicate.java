package xyz.openmodloader.util;

import java.util.Collection;
import java.util.function.Predicate;

public class CollectionPredicate<T> implements Predicate<T> {
    private final Collection<Predicate<T>> collection;

    public CollectionPredicate(Collection<Predicate<T>> collection) {
        this.collection = collection;
    }
    
    public void add(Predicate<T> predicate) {
        collection.add(predicate);
    }

    @Override
    public boolean test(T t) {
        for (Predicate<T> predicate : collection) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }
}
