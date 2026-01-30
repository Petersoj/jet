package net.jacobpeterson.jet.common.util.jspecify.testclasses.superclass;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings({"RequireExplicitNullMarking", "NullAway"})
public class Superclass {

    @Nullable String string1;
    int primitive;
    String string2;

    public Superclass(final String string1, final int primitive, final String string2) {
        this.string1 = string1;
        this.primitive = primitive;
        this.string2 = string2;
    }
}
