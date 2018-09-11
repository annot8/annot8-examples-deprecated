package io.annot8.integration.tests.pipelines;

import io.annot8.common.implementations.pipelines.PipelineBuilder;
import io.annot8.common.implementations.registries.ContentBuilderFactoryRegistry;
import io.annot8.components.cyber.processors.Email;
import io.annot8.components.files.processors.TxtFileExtractor;
import io.annot8.components.mongo.data.MongoDocument;
import io.annot8.components.mongo.processors.CreateContentFromMongoDocument;
import io.annot8.components.mongo.resources.MongoConnectionSettings;
import io.annot8.components.mongo.resources.MongoFactory;
import io.annot8.components.mongo.sources.MongoSource;
import io.annot8.core.exceptions.BadConfigurationException;

import io.annot8.defaultimpl.Annot8PipelineApplication;
import io.annot8.integration.tests.pipelines.MongoDocumentBuilder.BuilderFactory;
import org.bson.Document;

public class MongoPipeline extends AbstractResourceDataPipeline {

  private String host;

  public MongoPipeline(String host){
    this.host = host;
  }

  @Override
  protected void configurePipeline(PipelineBuilder builder) throws BadConfigurationException {
    MongoConnectionSettings settings = new MongoConnectionSettings();
    settings.setConnection("mongodb://" + host);
    settings.setDatabase("testing");
    settings.setCollection("test");
    MongoFactory factory = new MongoFactory();

    builder.addResource("mongo", factory, settings);
    builder.addSource(new MongoSource());
    builder.addProcessor(new CreateContentFromMongoDocument());
    builder.addProcessor(new Email());
  }

  @Override
  protected void configureContentBuilders(ContentBuilderFactoryRegistry registry) {
    registry.register(MongoDocument.class, new MongoDocumentBuilder.BuilderFactory());
  }

}
