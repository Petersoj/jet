package net.jacobpeterson.jet.common.util.jspecify.testclasses.superclass;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings({"RequireExplicitNullMarking", "NullAway"})
public final class Subclass extends Superclass {

    @Nullable String subString1;
    int subPrimitive;
    String subString2;

    public Subclass(final String string1, final int primitive, final String string2) {
        super(string1, primitive, string2);
        this.subString1 = string1;
        this.subPrimitive = primitive;
        this.subString2 = string2;
    }
}
