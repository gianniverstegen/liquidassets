package org.vaakbenjetebang.search;

import org.junit.Test;
import org.vaakbenjetebang.model.WhiskyProduct;

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

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts();

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a very");
        assertThat(result).singleElement().extracting(WhiskyProduct::getName).isEqualTo(FIRST_WHISKY);
    }

    @Test
    public void shouldRetrieveAllObjectsIfAllShareTheSamePattern() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts();

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a");
        assertThat(result).hasSize(3).extracting(WhiskyProduct::getName)
                .containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
    }

    @Test
    public void shouldRetrieveNoObjectsIfNoneContainThePattern() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts();

        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("meow");
        assertThat(result).isEmpty();
    }

    private List<WhiskyProduct> getWhiskyProducts() {
        WhiskyProduct whisky1 = new WhiskyProduct();
        whisky1.setName(FIRST_WHISKY);

        WhiskyProduct whisky2 = new WhiskyProduct();
        whisky2.setName(SECOND_WHISKY);

        WhiskyProduct whisky3 = new WhiskyProduct();
        whisky3.setName(THIRD_WHISKY);

        return List.of(whisky1, whisky2, whisky3);
    }


}
