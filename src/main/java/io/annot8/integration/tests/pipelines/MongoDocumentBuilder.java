package io.annot8.integration.tests.pipelines;

import io.annot8.common.implementations.content.AbstractContentBuilder;
import io.annot8.common.implementations.content.AbstractContentBuilderFactory;
import io.annot8.common.implementations.stores.SaveCallback;
import io.annot8.components.mongo.data.MongoDocument;
import io.annot8.core.data.Content;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.IncompleteException;
import io.annot8.core.properties.ImmutableProperties;
import io.annot8.defaultimpl.stores.DefaultAnnotationStore;
import org.bson.Document;

import java.util.function.Supplier;

public class MongoDocumentBuilder extends AbstractContentBuilder<Document, MongoDocument> {

  public MongoDocumentBuilder(SaveCallback<MongoDocument, MongoDocument> saver) {
    super(saver);
  }

  @Override
  protected MongoDocument create(String id, String name, ImmutableProperties immutableProperties,
      Supplier<Document> supplier) throws IncompleteException {
    return new MongoDocument(id, name, new DefaultAnnotationStore(id), immutableProperties,
        supplier.get());
  }

  public static class BuilderFactory extends
      AbstractContentBuilderFactory<Document, MongoDocument> {

    public BuilderFactory() {
      super(Document.class, MongoDocument.class);
    }

    @Override
    public Content.Builder<MongoDocument, Document> create(Item item,
        SaveCallback<MongoDocument, MongoDocument> saveCallback) {
      return new MongoDocumentBuilder(saveCallback);
    }
  }

}
