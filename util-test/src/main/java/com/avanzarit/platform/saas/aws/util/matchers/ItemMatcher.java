package com.avanzarit.platform.saas.aws.util.matchers;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static org.mockito.Matchers.argThat;

/**
 * Hamcrest {@link org.hamcrest.Matcher} that verifies whether the object being checked is the same DynamoDb
 * {@link Item}.
 */
public class ItemMatcher extends BaseMatcher<Item> {
    private Item item;

    public ItemMatcher(Object expectedItem) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.item = Item.fromJSON(mapper.writeValueAsString(expectedItem));
    }

    @Override
    public boolean matches(Object o) {
        return item.equals(o);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(item.toString());
    }

    /**
     * Matches a DynamoDb item to the expected object.
     *
     * @param expectedObject The object that is supposed to match the DynamoDb item.
     * @return An object that wraps a Mockito matcher.
     */
    public static Item item(Object expectedObject) throws JsonProcessingException {
        return argThat(new ItemMatcher(expectedObject));
    }
}
