package org.vaakbenjetebang.search;

import org.vaakbenjetebang.model.WhiskyProduct;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
public class WhiskySuffixTrie {
    private final TrieNode root;
    private final ReadWriteLock lock;
    private long size;

    @Inject
    public WhiskySuffixTrie() {
        this.root = new TrieNode();
        this.lock = new ReentrantReadWriteLock();
        this.size = 0;
    }

    public void add(WhiskyProduct whisky) {
        lock.writeLock().lock();
        size++;
        try {
            String name = whisky.getName().toLowerCase();
            for (int i = 0; i < name.length(); i++) {
                insert(name.substring(i), whisky);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addAll(Collection<WhiskyProduct> whiskies) {
        lock.writeLock().lock();
        try {
            for (WhiskyProduct whisky : whiskies) {
                add(whisky);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<WhiskyProduct> search(String pattern) {
        lock.readLock().lock();
        try {
            pattern = pattern.toLowerCase();
            List<WhiskyProduct> result = new ArrayList<>();
            TrieNode node = searchNode(pattern);
            if (node != null) {
                collectAllObjects(node, result);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public long size() {
        return size;
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
