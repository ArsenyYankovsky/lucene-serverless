package au.qut.edu.eresearch.serverlesssearch.handler;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public  class UUIDMatcher extends TypeSafeMatcher<String> {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

    @Override
    protected boolean matchesSafely(String s) {
        return s.matches(UUID_REGEX);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching the pattern of a UUID");
    }

    @Factory
    public static Matcher<String> matches() {
        return new UUIDMatcher();
    }

}