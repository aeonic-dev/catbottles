package design.aeonic.catbottles.base.misc;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @return the function result
     */
    R apply(T t, U u, V v, W w);

    /**
     * Returns a composed function that first applies this function to its inputs, and then applies the {@code after}
     * function to the result. If evaluation of either function throws an exception, it is relayed to the caller of the
     * composed function.
     *
     * @param <C> the type of output of the {@code after} function, and of the composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <C> QuadFunction<T, U, V, W, C> andThen(final Function<? super R, ? extends C> after) {
        Objects.requireNonNull(after);
        return (final T t, final U u, final V v, final W w) -> after.apply(apply(t, u, v, w));
    }
}