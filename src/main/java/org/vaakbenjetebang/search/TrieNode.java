package org.vaakbenjetebang.search;

import lombok.Getter;
import lombok.Setter;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
class TrieNode {
    private Map<Character, TrieNode> children = new HashMap<>();
    private boolean isEndOfWord = false;
    private List<WhiskyProduct> whiskys = new ArrayList<>();
}
