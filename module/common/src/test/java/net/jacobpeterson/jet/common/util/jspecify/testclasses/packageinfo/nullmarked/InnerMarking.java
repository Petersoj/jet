package net.jacobpeterson.jet.common.util.jspecify.testclasses.packageinfo.nullmarked;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"RequireExplicitNullMarking", "NullAway", "NullableProblems"})
public final class InnerMarking {

    @Nullable String string1;
    int primitive;
    String string2;

    public InnerMarking(final String string1, final int primitive, final String string2) {
        this.string1 = string1;
        this.primitive = primitive;
        this.string2 = string2;
    }

    @NullUnmarked
    public static class Inner {

        int primitive;
        String string1;
        @NonNull String string2;

        public Inner(final int primitive, final String string1, final String string2) {
            this.primitive = primitive;
            this.string1 = string1;
            this.string2 = string2;
        }
    }
}
