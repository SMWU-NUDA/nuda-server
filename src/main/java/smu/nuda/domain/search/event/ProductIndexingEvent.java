package smu.nuda.domain.search.event;

import smu.nuda.domain.search.document.ProductDocument;

import java.util.List;

public record ProductIndexingEvent(List<ProductDocument> docs) {
}
