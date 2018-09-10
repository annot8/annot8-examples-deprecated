package io.annot8.integration.tests.pipleines.processor;

import io.annot8.core.components.Processor;
import io.annot8.core.components.responses.ProcessorResponse;
import io.annot8.core.data.Item;
import io.annot8.core.exceptions.Annot8Exception;

import java.util.function.Consumer;

/**
 * Processor allowing observers to view Items processed by Annot8
 */
public class MonitoringProcessor implements Processor {

    private Consumer<Item> itemConsumer;

    public MonitoringProcessor(Consumer<Item> itemConsumer){
        this.itemConsumer = itemConsumer;
    }

    @Override
    public ProcessorResponse process(Item item) {
        try{
            itemConsumer.accept(item);
        }catch(Exception e){
            return ProcessorResponse.itemError();
        }
        return ProcessorResponse.ok();
    }
}
