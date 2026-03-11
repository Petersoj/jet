package net.jacobpeterson.jet.server.route.route.simple.pathregex;

import lombok.Value;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link PathRegexRouteMatch} is a {@link RouteMatch} for {@link PathRegexRoute}.
 */
@NullMarked
@Value
public class PathRegexRouteMatch implements RouteMatch {

    /**
     * The {@link Matcher} from {@link Pattern#matcher(CharSequence)}.
     */
    Matcher matcher;
}
