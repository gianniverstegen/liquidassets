package org.vaakbenjetebang.search;

import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhiskySuffixTrie {
    private final TrieNode root;

    public WhiskySuffixTrie() {
        this.root = new TrieNode();
    }

    public void add(WhiskyProduct whisky) {
        String name = whisky.getName().toLowerCase();
        for (int i = 0; i < name.length(); i++) {
            insert(name.substring(i), whisky);
        }
    }

    public void addAll(Collection<WhiskyProduct> whiskies) {
        for (WhiskyProduct whisky : whiskies) {
            add(whisky);
        }
    }

    public List<WhiskyProduct> search(String pattern) {
        pattern = pattern.toLowerCase();
        List<WhiskyProduct> result = new ArrayList<>();
        TrieNode node = searchNode(pattern);
        if (node != null) {
            collectAllObjects(node, result);
        }
        return result;
    }

    private void insert(String word, WhiskyProduct whiskyProduct) {
        TrieNode currentNode = root;
        for (char c : word.toCharArray()) {
            currentNode = currentNode.getChildren().computeIfAbsent(c, k -> new TrieNode());
        }
        currentNode.setEndOfWord(true);
        currentNode.getWhiskys().add(whiskyProduct);
    }


    private TrieNode searchNode(String pattern) {
        TrieNode currentNode = root;
        for (char c : pattern.toCharArray()) {
            currentNode = currentNode.getChildren().get(c);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode;
    }

    private void collectAllObjects(TrieNode node, List<WhiskyProduct> result) {
        if (node.isEndOfWord()) {
            result.addAll(node.getWhiskys());
        }
        for (TrieNode child : node.getChildren().values()) {
            collectAllObjects(child, result);
        }
    }

}
