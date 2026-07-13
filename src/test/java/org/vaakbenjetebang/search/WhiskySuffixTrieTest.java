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
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        // Despite the class name, behaviour is substring/prefix-like matching (not suffix-only).
        List<WhiskyProduct> result = suffixTrie.search("a very");
        assertThat(result).singleElement().extracting(WhiskyProduct::getName).isEqualTo(FIRST_WHISKY);
    }

    @Test
    public void shouldRetrieveAllObjectsIfAllShareTheSamePattern() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a");
        assertThat(result)
                .hasSize(3)
                .extracting(WhiskyProduct::getName)
                .containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
    }

    @Test
    public void shouldRetrieveNoObjectsIfNoneContainThePattern() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, SECOND_WHISKY, THIRD_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("meow");
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnTwoObjectsIfNamesAreTheSame() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();

        List<WhiskyProduct> whiskyProducts = getWhiskyProducts(List.of(FIRST_WHISKY, FIRST_WHISKY));
        suffixTrie.addAll(whiskyProducts);

        List<WhiskyProduct> result = suffixTrie.search("a very");
        assertThat(result)
                .hasSize(2)
                .extracting(WhiskyProduct::getName)
                .containsExactlyInAnyOrderElementsOf(List.of(FIRST_WHISKY, FIRST_WHISKY));
    }

    @Test
    public void shouldTrackSizeForAddsAndAddAll() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        assertThat(suffixTrie.size()).isZero();

        WhiskyProduct a = whiskyNamed("One");
        WhiskyProduct b = whiskyNamed("Two");
        WhiskyProduct c = whiskyNamed("Three");

        suffixTrie.add(a);
        assertThat(suffixTrie.size()).isEqualTo(1);

        suffixTrie.addAll(List.of(b, c));
        assertThat(suffixTrie.size()).isEqualTo(3);
    }

    @Test
    public void shouldBeCaseInsensitiveForSearchAndAdd() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        suffixTrie.add(whiskyNamed("MiXeD CaSe Whisky"));

        assertThat(suffixTrie.search("mixed")).extracting(WhiskyProduct::getName)
                .containsExactly("MiXeD CaSe Whisky");
        assertThat(suffixTrie.search("CASE")).extracting(WhiskyProduct::getName)
                .containsExactly("MiXeD CaSe Whisky");
        assertThat(suffixTrie.search("whISKy")).extracting(WhiskyProduct::getName)
                .containsExactly("MiXeD CaSe Whisky");
    }

    @Test
    public void shouldIndexAllSuffixesIncludingLastCharacter() {
        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        suffixTrie.add(whiskyNamed("ABC"));

        assertThat(suffixTrie.search("abc")).extracting(WhiskyProduct::getName).containsExactly("ABC");
        assertThat(suffixTrie.search("bc")).extracting(WhiskyProduct::getName).containsExactly("ABC");
        assertThat(suffixTrie.search("c")).extracting(WhiskyProduct::getName).containsExactly("ABC");
    }

    private List<WhiskyProduct> getWhiskyProducts(List<String> names) {
        List<WhiskyProduct> whiskies = new ArrayList<>();
        for (String name : names) {
            whiskies.add(whiskyNamed(name));
        }
        return whiskies;
    }

    private WhiskyProduct whiskyNamed(String name) {
        WhiskyProduct whisky = new WhiskyProduct();
        whisky.setName(name);
        return whisky;
    }
}