package org.vaakbenjetebang.search;

import org.junit.Test;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WhiskySuffixTrieTest {

    public static final String FIRST_WHISKY = "A very nice whisky";
    public static final String SECOND_WHISKY = "another whisky";
    public static final String THIRD_WHISKY = "last whisky";

    @Test
    public void shouldRetrieveSingleObjectIfPresentWithSpecificSuffix() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a very");
        assertThat(result).singleElement().extracting(WhiskyProduct::getName).isEqualTo(FIRST_WHISKY);
    }

    @Test
    public void shouldRetrieveAllObjectsIfAllShareTheSamePattern() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a");
        assertThat(result).hasSize(3).extracting(WhiskyProduct::getName)
                .containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
    }

    @Test
    public void shouldRetrieveNoObjectsIfNoneContainThePattern() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("meow");
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnTwoObjectsIfNamesAreTheSame() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, FIRST_WHISKY));

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a very");
        assertThat(result).hasSize(2).extracting(WhiskyProduct::getName).containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, FIRST_WHISKY));
    }

    private List<WhiskyProduct> getWhiskyProducts(List<String> names) {
        List<WhiskyProduct> whiskies = new ArrayList<>();

        for (String name : names) {
            WhiskyProduct whisky = new WhiskyProduct();
            whisky.setName(name);
            whiskies.add(whisky);
        }
        return whiskies;
    }


}
