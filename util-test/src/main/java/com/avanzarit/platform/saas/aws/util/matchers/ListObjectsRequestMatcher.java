package com.avanzarit.platform.saas.aws.util.matchers;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;

import static org.mockito.Matchers.argThat;

/**
 * Hamcrest {@link org.hamcrest.Matcher} that verifies whether a {@link ListObjectsRequest} has the right bucket and key
 * prefix configured.
 */
public class ListObjectsRequestMatcher extends BaseMatcher<ListObjectsRequest> {
    private String bucket;
    private String prefix;

    public ListObjectsRequestMatcher(String bucket, String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof ListObjectsRequest) {
            ListObjectsRequest listObjectsRequest = (ListObjectsRequest) o;

            return Objects.equals(listObjectsRequest.getBucketName(), bucket)
                    && Objects.equals(listObjectsRequest.getPrefix(), prefix);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A ListObjectsRequest with bucket " + bucket + " and prefix " + prefix);
    }

    /**
     * Matches a ListObjectsRequest based on a bucket name and prefix.
     *
     * @param bucket The expected bucket of the ListObjectsRequest.
     * @param prefix The expected key prefix of the ListObjectsRequest.
     * @return An object that wraps a Mockito matcher.
     */
    public static ListObjectsRequest listObjectsRequest(String bucket, String prefix) {
        return argThat(new ListObjectsRequestMatcher(bucket, prefix));
    }
}
