package net.jacobpeterson.jet.common.util.nullable;

import net.jacobpeterson.jet.common.util.nullable.testclasses.InnerMarking;
import net.jacobpeterson.jet.common.util.nullable.testclasses.NoMarking;
import net.jacobpeterson.jet.common.util.nullable.testclasses.OuterMarking;
import net.jacobpeterson.jet.common.util.nullable.testclasses.superclass.Subclass;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullUnmarked
@SuppressWarnings({"ParameterMissingNullable", "DataFlowIssue"})
public final class NullableUtilTest {

    @Test
    public void requireNonNullFieldsSet() {
        doesNotThrow(null);

        doesNotThrow(noMarking(null, 0, null));
        doesNotThrow(noMarking(null, 0, ""));
        doesNotThrow(noMarking("", 1, null));
        doesNotThrow(noMarking("", 1, ""));

        doesThrow(noMarkingInner(null, 0, null));
        doesNotThrow(noMarkingInner(null, 0, ""));
        doesThrow(noMarkingInner("", 1, null));
        doesNotThrow(noMarkingInner("", 1, ""));

        doesThrow(outerMarking(null, 0, null));
        doesNotThrow(outerMarking(null, 0, ""));
        doesThrow(outerMarking("", 1, null));
        doesNotThrow(outerMarking("", 1, ""));

        doesThrow(outerMarkingInner(null, 0, null));
        doesThrow(outerMarkingInner(null, 0, ""));
        doesThrow(outerMarkingInner("", 1, null));
        doesNotThrow(outerMarkingInner("", 1, ""));

        doesNotThrow(innerMarking(null, 0, null));
        doesNotThrow(innerMarking(null, 0, ""));
        doesNotThrow(innerMarking("", 1, null));
        doesNotThrow(innerMarking("", 1, ""));

        doesThrow(innerMarkingInner(null, 0, null));
        doesNotThrow(innerMarkingInner(null, 0, ""));
        doesThrow(innerMarkingInner("", 1, null));
        doesNotThrow(innerMarkingInner("", 1, ""));

        doesThrow(nullMarkedPackageNoMarking(null, 0, null));
        doesNotThrow(nullMarkedPackageNoMarking(null, 0, ""));
        doesThrow(nullMarkedPackageNoMarking("", 1, null));
        doesNotThrow(nullMarkedPackageNoMarking("", 1, ""));

        doesThrow(nullMarkedPackageNoMarkingInner(null, 0, null));
        doesThrow(nullMarkedPackageNoMarkingInner(null, 0, ""));
        doesThrow(nullMarkedPackageNoMarkingInner("", 1, null));
        doesNotThrow(nullMarkedPackageNoMarkingInner("", 1, ""));

        doesThrow(nullMarkedPackageOuterMarking(null, 0, null));
        doesNotThrow(nullMarkedPackageOuterMarking(null, 0, ""));
        doesThrow(nullMarkedPackageOuterMarking("", 1, null));
        doesNotThrow(nullMarkedPackageOuterMarking("", 1, ""));

        doesThrow(nullMarkedPackageOuterMarkingInner(null, 0, null));
        doesThrow(nullMarkedPackageOuterMarkingInner(null, 0, ""));
        doesThrow(nullMarkedPackageOuterMarkingInner("", 1, null));
        doesNotThrow(nullMarkedPackageOuterMarkingInner("", 1, ""));

        doesThrow(nullMarkedPackageInnerMarking(null, 0, null));
        doesNotThrow(nullMarkedPackageInnerMarking(null, 0, ""));
        doesThrow(nullMarkedPackageInnerMarking("", 1, null));
        doesNotThrow(nullMarkedPackageInnerMarking("", 1, ""));

        doesThrow(nullMarkedPackageInnerMarkingInner(null, 0, null));
        doesNotThrow(nullMarkedPackageInnerMarkingInner(null, 0, ""));
        doesThrow(nullMarkedPackageInnerMarkingInner("", 1, null));
        doesNotThrow(nullMarkedPackageInnerMarkingInner("", 1, ""));

        doesNotThrow(nullUnmarkedPackageNoMarking(null, 0, null));
        doesNotThrow(nullUnmarkedPackageNoMarking(null, 0, ""));
        doesNotThrow(nullUnmarkedPackageNoMarking("", 1, null));
        doesNotThrow(nullUnmarkedPackageNoMarking("", 1, ""));

        doesThrow(nullUnmarkedPackageNoMarkingInner(null, 0, null));
        doesNotThrow(nullUnmarkedPackageNoMarkingInner(null, 0, ""));
        doesThrow(nullUnmarkedPackageNoMarkingInner("", 1, null));
        doesNotThrow(nullUnmarkedPackageNoMarkingInner("", 1, ""));

        doesThrow(nullUnmarkedPackageOuterMarking(null, 0, null));
        doesNotThrow(nullUnmarkedPackageOuterMarking(null, 0, ""));
        doesThrow(nullUnmarkedPackageOuterMarking("", 1, null));
        doesNotThrow(nullUnmarkedPackageOuterMarking("", 1, ""));

        doesThrow(nullUnmarkedPackageOuterMarkingInner(null, 0, null));
        doesThrow(nullUnmarkedPackageOuterMarkingInner(null, 0, ""));
        doesThrow(nullUnmarkedPackageOuterMarkingInner("", 1, null));
        doesNotThrow(nullUnmarkedPackageOuterMarkingInner("", 1, ""));

        doesNotThrow(nullUnmarkedPackageInnerMarking(null, 0, null));
        doesNotThrow(nullUnmarkedPackageInnerMarking(null, 0, ""));
        doesNotThrow(nullUnmarkedPackageInnerMarking("", 1, null));
        doesNotThrow(nullUnmarkedPackageInnerMarking("", 1, ""));

        doesThrow(nullUnmarkedPackageInnerMarkingInner(null, 0, null));
        doesNotThrow(nullUnmarkedPackageInnerMarkingInner(null, 0, ""));
        doesThrow(nullUnmarkedPackageInnerMarkingInner("", 1, null));
        doesNotThrow(nullUnmarkedPackageInnerMarkingInner("", 1, ""));

        doesThrow(new Subclass(null, 0, null));
        doesNotThrow(new Subclass(null, 0, ""));
        doesThrow(new Subclass("", 1, null));
        doesNotThrow(new Subclass("", 1, ""));
    }

    private void doesNotThrow(final @Nullable Object object) {
        assertDoesNotThrow(requireNonNullFieldsSetExecutable(object));
    }

    private void doesThrow(final @Nullable Object object) {
        assertThrows(NullPointerException.class, requireNonNullFieldsSetExecutable(object));
    }

    private Executable requireNonNullFieldsSetExecutable(final @Nullable Object object) {
        return () -> NullableUtil.requireNonNullFieldsSet(object);
    }

    private NoMarking noMarking(final String string1, final int primitive, final String string2) {
        return new NoMarking(string1, primitive, string2);
    }

    private NoMarking.Inner noMarkingInner(final String string1, final int primitive, final String string2) {
        return new NoMarking.Inner(primitive, string1, string2);
    }

    private OuterMarking outerMarking(final String string1, final int primitive, final String string2) {
        return new OuterMarking(string1, primitive, string2);
    }

    private OuterMarking.Inner outerMarkingInner(final String string1, final int primitive, final String string2) {
        return new OuterMarking.Inner(primitive, string1, string2);
    }

    private InnerMarking innerMarking(final String string1, final int primitive, final String string2) {
        return new InnerMarking(string1, primitive, string2);
    }

    private InnerMarking.Inner innerMarkingInner(final String string1, final int primitive, final String string2) {
        return new InnerMarking.Inner(primitive, string1, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked.NoMarking
    nullMarkedPackageNoMarking(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked
                .NoMarking(string1, primitive, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked.NoMarking.Inner
    nullMarkedPackageNoMarkingInner(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked
                .NoMarking.Inner(primitive, string1, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked.OuterMarking
    nullMarkedPackageOuterMarking(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked
                .OuterMarking(string1, primitive, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked.OuterMarking.Inner
    nullMarkedPackageOuterMarkingInner(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked
                .OuterMarking.Inner(primitive, string1, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked.InnerMarking
    nullMarkedPackageInnerMarking(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked
                .InnerMarking(string1, primitive, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked.InnerMarking.Inner
    nullMarkedPackageInnerMarkingInner(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullmarked
                .InnerMarking.Inner(primitive, string1, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked.NoMarking
    nullUnmarkedPackageNoMarking(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked
                .NoMarking(string1, primitive, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked.NoMarking.Inner
    nullUnmarkedPackageNoMarkingInner(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked
                .NoMarking.Inner(primitive, string1, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked.OuterMarking
    nullUnmarkedPackageOuterMarking(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked
                .OuterMarking(string1, primitive, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked.OuterMarking.Inner
    nullUnmarkedPackageOuterMarkingInner(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked
                .OuterMarking.Inner(primitive, string1, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked.InnerMarking
    nullUnmarkedPackageInnerMarking(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked
                .InnerMarking(string1, primitive, string2);
    }

    private net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked.InnerMarking.Inner
    nullUnmarkedPackageInnerMarkingInner(final String string1, final int primitive, final String string2) {
        return new net.jacobpeterson.jet.common.util.nullable.testclasses.packageinfo.nullunmarked
                .InnerMarking.Inner(primitive, string1, string2);
    }
}
