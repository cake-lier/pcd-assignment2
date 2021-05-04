package it.unibo.pcd.assignment2.reactive.model.entities.impl;

import it.unibo.pcd.assignment2.reactive.model.entities.Page;

import java.util.Objects;

/**
 * An implementation of the {@link Page} interface.
 */
public class PageImpl implements Page {
    private final String text;

    /**
     * Default constructor.
     * @param text the textual content of this page
     */
    public PageImpl(final String text) {
        this.text = Objects.requireNonNull(text);
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PageImpl page = (PageImpl) o;
        return this.text.equals(page.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.text);
    }

    @Override
    public String toString() {
        return "PageImpl{text=" + this.text + "}";
    }
}
