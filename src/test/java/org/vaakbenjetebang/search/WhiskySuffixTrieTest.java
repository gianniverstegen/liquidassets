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

        // when
        List<WhiskyProduct> result = suffixTrie.search("a very");

        // then
        assertThat(result).singleElement().extracting(WhiskyProduct::getName).isEqualTo(FIRST_WHISKY);
    }

    @Test
    public void shouldRetrieveAllObjectsIfAllShareTheSamePattern() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        // when
        List<WhiskyProduct> result = suffixTrie.search("a");

        // then
        assertThat(result).hasSize(3)
                .extracting(WhiskyProduct::getName)
                .containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
    }

    @Test
    public void shouldRetrieveNoObjectsIfNoneContainThePattern() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        // when
        List<WhiskyProduct> result = suffixTrie.search("meow");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnTwoObjectsIfNamesAreTheSame() {
        // given
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, FIRST_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        // when
        List<WhiskyProduct> result = suffixTrie.search("a very");

        // then
        assertThat(result).hasSize(2)
                .extracting(WhiskyProduct::getName)
                .containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, FIRST_WHISKY));
    }

    @Test
    public void sizeShouldStartAtZeroAndIncreasePerAddCall() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        assertThat(suffixTrie.size()).isZero();

        suffixTrie.add(whisky("One"));
        assertThat(suffixTrie.size()).isEqualTo(1);

        suffixTrie.add(whisky("Two"));
        assertThat(suffixTrie.size()).isEqualTo(2);
    }

    @Test
    public void sizeShouldIncreaseWhenAddingViaAddAll() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        suffixTrie.addAll(getWhiskyProducts(List.of("One", "Two", "Three")));
        assertThat(suffixTrie.size()).isEqualTo(3);
    }

    @Test
    public void addShouldIndexAllSuffixesIncludingLastCharacter() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        suffixTrie.add(whisky("abc"));

        assertThat(suffixTrie.search("abc")).extracting(WhiskyProduct::getName).containsExactly("abc");
        assertThat(suffixTrie.search("bc")).extracting(WhiskyProduct::getName).containsExactly("abc");
        // Boundary: must include suffix starting at last character ("c")
        assertThat(suffixTrie.search("c")).extracting(WhiskyProduct::getName).containsExactly("abc");
    }

    @Test
    public void searchShouldBeCaseInsensitiveForBothStoredNameAndPattern() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        suffixTrie.add(whisky("AbC"));

        assertThat(suffixTrie.search("aBc")).extracting(WhiskyProduct::getName).containsExactly("AbC");
        assertThat(suffixTrie.search("BC")).extracting(WhiskyProduct::getName).containsExactly("AbC");
        assertThat(suffixTrie.search("c")).extracting(WhiskyProduct::getName).containsExactly("AbC");
    }

    private List<WhiskyProduct> getWhiskyProducts(List<String> names) {
        List<WhiskyProduct> whiskies = new ArrayList<>();
        for (String name : names) {
            whiskies.add(whisky(name));
        }
        return whiskies;
    }

    private WhiskyProduct whisky(String name) {
        WhiskyProduct whisky = new WhiskyProduct();
        whisky.setName(name);
        return whisky;
    }
}